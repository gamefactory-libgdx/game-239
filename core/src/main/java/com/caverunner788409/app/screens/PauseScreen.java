package com.caverunner788409.app.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
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

/**
 * Pause screen — drawn as a dark overlay with three action buttons.
 *
 * @param gameScreen   The live game screen to resume (unchanged state).
 * @param biomeIndex   0=Crystal, 1=Lava, 2=Ice — used to create a fresh restart.
 */
public class PauseScreen implements Screen {

    private final MainGame           game;
    private final Screen             gameScreen;
    private final int                biomeIndex;
    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage              stage;
    private final ShapeRenderer      sr;

    private final Rectangle rResume  = new Rectangle();
    private final Rectangle rRestart = new Rectangle();
    private final Rectangle rMenu    = new Rectangle();

    private static final GlyphLayout GL = new GlyphLayout();

    // Primary #FF6F00 (for panel border)
    private static final float PR = 1f, PG = 0.435f, PB = 0f;

    public PauseScreen(MainGame game, Screen gameScreen, int biomeIndex) {
        this.game        = game;
        this.gameScreen  = gameScreen;
        this.biomeIndex  = biomeIndex;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        game.manager.load("sounds/sfx/sfx_button_click.ogg", Sound.class);
        game.manager.load("sounds/sfx/sfx_button_back.ogg",  Sound.class);
        game.manager.finishLoading();

        // Stop music while paused
        if (game.currentMusic != null) game.currentMusic.pause();

        // Three stacked buttons, centred
        float bw = Constants.BTN_WIDTH_MAIN, bh = Constants.BTN_HEIGHT_MAIN;
        float cx = (Constants.WORLD_WIDTH  - bw) / 2f;
        float cy = (Constants.WORLD_HEIGHT - bh) / 2f;

        rResume .set(cx, cy + 80f, bw, bh);
        rRestart.set(cx, cy,       bw, bh);
        rMenu   .set(cx, cy - 80f, bw, bh);

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    resume();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int sx, int sy, int pointer, int button) {
                Vector2 c = stage.screenToStageCoordinates(new Vector2(sx, sy));
                if (rResume.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    resumeGame();
                    return true;
                }
                if (rRestart.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    restartGame();
                    return true;
                }
                if (rMenu.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_back.ogg");
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    private void resumeGame() {
        // Resume music before handing back to game screen
        if (game.currentMusic != null && game.musicEnabled) {
            game.currentMusic.play();
        }
        game.setScreen(gameScreen);
    }

    private void restartGame() {
        switch (biomeIndex) {
            case 1:  game.setScreen(new LavaCaveScreen(game));    break;
            case 2:  game.setScreen(new IceCaveScreen(game));     break;
            default: game.setScreen(new CrystalCaveScreen(game)); break;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        sr.setProjectionMatrix(camera.combined);

        // Dark overlay panel
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.102f, 0.102f, 0.180f, 0.95f);
        sr.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        // Panel border
        sr.setColor(PR, PG, PB, 1f);
        float px = 200f, py = 100f, pw = Constants.WORLD_WIDTH - 400f, ph = Constants.WORLD_HEIGHT - 200f;
        sr.rect(px - 2, py - 2, pw + 4, ph + 4);
        sr.setColor(0.102f, 0.102f, 0.180f, 1f);
        sr.rect(px, py, pw, ph);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // "PAUSED" title
        game.batch.begin();
        game.fontHeader.setColor(1f, 1f, 1f, 1f);
        String title = "PAUSED";
        GL.setText(game.fontHeader, title);
        game.fontHeader.draw(game.batch, title,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 400f);
        game.batch.end();

        // Buttons
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "RESUME",    rResume.x,  rResume.y,  rResume.width,  rResume.height);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "RESTART",   rRestart.x, rRestart.y, rRestart.width, rRestart.height);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "MAIN MENU", rMenu.x,    rMenu.y,    rMenu.width,    rMenu.height);

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
