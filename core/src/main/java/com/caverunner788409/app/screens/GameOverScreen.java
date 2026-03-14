package com.caverunner788409.app.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.caverunner788409.app.Constants;
import com.caverunner788409.app.MainGame;
import com.caverunner788409.app.UiFactory;

public class GameOverScreen implements Screen {

    private final MainGame           game;
    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage              stage;
    private final ShapeRenderer      sr;
    private final Texture            bgTexture;

    private final int score;
    private final int coinsEarned;  // extra param — coins collected this run
    private final int bestScore;

    private final Rectangle rRetry = new Rectangle();
    private final Rectangle rMenu  = new Rectangle();

    private static final GlyphLayout GL = new GlyphLayout();

    private static final float[] BG_CLR = {0.102f, 0.102f, 0.180f};

    /**
     * @param game        The main game instance.
     * @param score       Score achieved this run.
     * @param coinsEarned Coins collected this run (displayed; already added to total by caller).
     */
    public GameOverScreen(MainGame game, int score, int coinsEarned) {
        this.game         = game;
        this.score        = score;
        this.coinsEarned  = coinsEarned;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        // ── Load assets ───────────────────────────────────────────────
        game.manager.load("backgrounds/bg_main.png",           Texture.class);
        game.manager.load("sounds/music/music_game_over.ogg",  Music.class);
        game.manager.load("sounds/sfx/sfx_button_click.ogg",   Sound.class);
        game.manager.load("sounds/sfx/sfx_game_over.ogg",      Sound.class);
        game.manager.finishLoading();

        bgTexture = game.manager.get("backgrounds/bg_main.png", Texture.class);

        // ── Update leaderboard and personal best ──────────────────────
        LeaderboardScreen.addScore(score);

        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int saved = prefs.getInteger(Constants.PREF_HIGH_SCORE, 0);
        if (score > saved) {
            prefs.putInteger(Constants.PREF_HIGH_SCORE, score);
            prefs.flush();
            bestScore = score;
        } else {
            bestScore = saved;
        }

        // ── Button layout ─────────────────────────────────────────────
        float bw = Constants.BTN_WIDTH_MAIN;
        float bh = Constants.BTN_HEIGHT_MAIN;
        float gap = 20f;
        float totalW = bw * 2 + gap;
        float startX = (Constants.WORLD_WIDTH - totalW) / 2f;

        rRetry.set(startX,          140f, bw, bh);
        rMenu .set(startX + bw + gap, 140f, bw, bh);

        // ── Input ──────────────────────────────────────────────────────
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                Vector2 c = stage.screenToStageCoordinates(new Vector2(screenX, screenY));
                if (rRetry.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    game.setScreen(new BiomeSelectScreen(game));
                    return true;
                }
                if (rMenu.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));

        // Game-over music plays once — no looping
        game.playMusicOnce("sounds/music/music_game_over.ogg");
        game.playSound("sounds/sfx/sfx_game_over.ogg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(BG_CLR[0], BG_CLR[1], BG_CLR[2], 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);

        // ── Background ────────────────────────────────────────────────
        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Semi-transparent dark overlay
        game.batch.setColor(0f, 0f, 0f, 0.55f);
        // (use a white pixel if available; here we use background color already set)
        game.batch.setColor(1f, 1f, 1f, 1f);

        // ── Text content ──────────────────────────────────────────────
        // "GAME OVER"
        game.fontTitle.setColor(1f, 1f, 1f, 1f);
        String goStr = "GAME OVER";
        GL.setText(game.fontTitle, goStr);
        game.fontTitle.draw(game.batch, goStr,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 430f);

        // Score
        game.fontHeader.setColor(1f, 1f, 1f, 1f);
        String scoreStr = "SCORE: " + score;
        GL.setText(game.fontHeader, scoreStr);
        game.fontHeader.draw(game.batch, scoreStr,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 360f);

        // Coins earned (accent colour #B2FF59)
        game.fontBody.setColor(0.698f, 1.0f, 0.349f, 1f);
        String coinStr = "COINS EARNED: +" + coinsEarned;
        GL.setText(game.fontBody, coinStr);
        game.fontBody.draw(game.batch, coinStr,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 310f);

        // Best score
        game.fontBody.setColor(1f, 1f, 1f, 0.75f);
        String bestStr = "BEST: " + bestScore;
        GL.setText(game.fontBody, bestStr);
        game.fontBody.draw(game.batch, bestStr,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 270f);

        game.batch.end();

        // ── Buttons ───────────────────────────────────────────────────
        sr.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "RETRY", rRetry.x, rRetry.y, rRetry.width, rRetry.height);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "MENU",  rMenu.x,  rMenu.y,  rMenu.width,  rMenu.height);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void show()   {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
        sr.dispose();
    }
}
