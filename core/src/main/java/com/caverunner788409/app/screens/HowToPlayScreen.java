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

public class HowToPlayScreen implements Screen {

    private final MainGame           game;
    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage              stage;
    private final ShapeRenderer      sr;
    private final Texture            bgTexture;

    private final Rectangle rBack = new Rectangle();

    private static final GlyphLayout GL = new GlyphLayout();

    // Panel colours (R, G, B)
    private static final float[][] PANEL_CLR = {
        { 0.302f, 0.816f, 0.882f },  // #4DD0E1 cyan — JUMP
        { 1.000f, 0.435f, 0.000f },  // #FF6F00 orange — SLIDE
        { 0.009f, 0.467f, 0.741f },  // #0277BD blue — TURN
    };
    private static final String[] PANEL_TITLE = {
        "SWIPE UP TO JUMP",
        "SWIPE DOWN TO SLIDE",
        "SWIPE LEFT TO DODGE"
    };
    private static final String[] PANEL_DESC = {
        "Avoid low stalagmites rising from the floor",
        "Duck under stalactites hanging from the ceiling",
        "Brief invincibility dodge — use sparingly!"
    };

    private static final float PANEL_W = 700f;
    private static final float PANEL_H = 86f;
    private static final float PANEL_X = (Constants.WORLD_WIDTH - PANEL_W) / 2f;
    private static final float[] PANEL_Y = { 320f, 210f, 100f };

    public HowToPlayScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        game.manager.load("backgrounds/bg_main.png",         Texture.class);
        game.manager.load("sounds/sfx/sfx_button_back.ogg",  Sound.class);
        game.manager.load("sounds/sfx/sfx_button_click.ogg", Sound.class);
        game.manager.finishLoading();

        bgTexture = game.manager.get("backgrounds/bg_main.png", Texture.class);

        rBack.set(20f, 20f, Constants.BTN_WIDTH_SECONDARY, Constants.BTN_HEIGHT_SECONDARY);

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.playSound("sounds/sfx/sfx_button_back.ogg");
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int sx, int sy, int pointer, int button) {
                Vector2 c = stage.screenToStageCoordinates(new Vector2(sx, sy));
                if (rBack.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_back.ogg");
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));

        game.playMusic("sounds/music/music_menu.ogg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.102f, 0.102f, 0.180f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);

        // Background + title
        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        game.fontHeader.setColor(1f, 1f, 1f, 1f);
        String title = "HOW TO PLAY";
        GL.setText(game.fontHeader, title);
        game.fontHeader.draw(game.batch, title,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 455f);
        game.batch.end();

        // Colour panels
        sr.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 3; i++) {
            float[] c = PANEL_CLR[i];
            sr.setColor(c[0], c[1], c[2], 0.85f);
            sr.rect(PANEL_X, PANEL_Y[i], PANEL_W, PANEL_H);
        }
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Panel text
        game.batch.begin();
        for (int i = 0; i < 3; i++) {
            float py = PANEL_Y[i];

            game.fontBody.setColor(1f, 1f, 1f, 1f);
            game.fontBody.draw(game.batch, PANEL_TITLE[i],
                    PANEL_X + 16f, py + PANEL_H - 14f);

            game.fontSmall.setColor(1f, 1f, 1f, 0.88f);
            game.fontSmall.draw(game.batch, PANEL_DESC[i],
                    PANEL_X + 16f, py + 28f);
        }
        game.batch.end();

        // Back button
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "BACK", rBack.x, rBack.y, rBack.width, rBack.height);

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
