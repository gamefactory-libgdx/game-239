package com.caverunner788409.app.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
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

public class MainMenuScreen implements Screen {

    private final MainGame          game;
    private final OrthographicCamera camera;
    private final StretchViewport   viewport;
    private final Stage             stage;
    private final ShapeRenderer     sr;

    private final Texture bgTexture;

    // Button hit-rectangles (world coords)
    private final Rectangle rPlay        = new Rectangle();
    private final Rectangle rLeaderboard = new Rectangle();
    private final Rectangle rSettings    = new Rectangle();

    private static final GlyphLayout GL = new GlyphLayout();

    // Palette
    private static final float[] BG_CLR  = {0.102f, 0.102f, 0.180f}; // #1A1A2E
    private static final float[] TXT_CLR = {1f, 1f, 1f};

    public MainMenuScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        // ── Load assets ───────────────────────────────────────────────
        game.manager.load("backgrounds/bg_main.png",           Texture.class);
        game.manager.load("sounds/music/music_menu.ogg",       Music.class);
        game.manager.load("sounds/sfx/sfx_button_click.ogg",   Sound.class);
        game.manager.load("sounds/sfx/sfx_button_back.ogg",    Sound.class);
        game.manager.finishLoading();

        bgTexture = game.manager.get("backgrounds/bg_main.png", Texture.class);

        // ── Button layout ──────────────────────────────────────────────
        float bw = Constants.BTN_WIDTH_MAIN;
        float bh = Constants.BTN_HEIGHT_MAIN;
        float cx = (Constants.WORLD_WIDTH - bw) / 2f;

        rPlay.set(cx, 270, bw, bh);
        rLeaderboard.set(cx, 200, bw, bh);
        rSettings.set(cx, 130, bw, bh);

        // ── Input ──────────────────────────────────────────────────────
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) return true; // already at root — do nothing
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                Vector2 c = stage.screenToStageCoordinates(new Vector2(screenX, screenY));
                if (rPlay.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    game.setScreen(new BiomeSelectScreen(game));
                    return true;
                }
                if (rLeaderboard.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    game.setScreen(new LeaderboardScreen(game));
                    return true;
                }
                if (rSettings.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    game.setScreen(new SettingsScreen(game));
                    return true;
                }
                return false;
            }
        }));

        game.playMusic("sounds/music/music_menu.ogg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(BG_CLR[0], BG_CLR[1], BG_CLR[2], 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);

        // ── Background + title text ────────────────────────────────────
        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Title
        game.fontTitle.setColor(TXT_CLR[0], TXT_CLR[1], TXT_CLR[2], 1f);
        String title = "CAVE RUNNER";
        GL.setText(game.fontTitle, title);
        game.fontTitle.draw(game.batch, title,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 450f);

        // Subtitle
        game.fontSmall.setColor(1f, 1f, 1f, 0.8f);
        String sub = "ENDLESS RUNNER";
        GL.setText(game.fontSmall, sub);
        game.fontSmall.draw(game.batch, sub,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 415f);

        game.batch.end();

        // ── Pixel buttons ──────────────────────────────────────────────
        sr.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "PLAY",         rPlay.x,        rPlay.y,        rPlay.width,        rPlay.height);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "LEADERBOARD",  rLeaderboard.x, rLeaderboard.y, rLeaderboard.width, rLeaderboard.height);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "SETTINGS",     rSettings.x,    rSettings.y,    rSettings.width,    rSettings.height);

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
