package com.caverunner788409.app.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
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

public class BiomeSelectScreen implements Screen {

    private final MainGame           game;
    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage              stage;
    private final ShapeRenderer      sr;
    private final Texture            bgTexture;

    // Biome card hit areas
    private final Rectangle rCrystal = new Rectangle();
    private final Rectangle rLava    = new Rectangle();
    private final Rectangle rIce     = new Rectangle();

    // Bottom-row button hit areas
    private final Rectangle rBack       = new Rectangle();
    private final Rectangle rShop       = new Rectangle();
    private final Rectangle rHowToPlay  = new Rectangle();

    private static final GlyphLayout GL = new GlyphLayout();

    // Biome card colours  (fill R,G,B)
    private static final float[] CLR_CRYSTAL = { 0.302f, 0.816f, 0.882f };  // #4DD0E1
    private static final float[] CLR_LAVA    = { 1.000f, 0.435f, 0.000f };  // #FF6F00
    private static final float[] CLR_ICE     = { 0.009f, 0.467f, 0.741f };  // #0277BD

    private static final float CARD_W = 240f;
    private static final float CARD_H = 300f;
    // Cards centred: (854 - 3*240 - 2*27) / 2 = 40; gaps 27px
    private static final float CARD_Y = 90f;
    private static final float CARD1_X = 40f;
    private static final float CARD2_X = CARD1_X + CARD_W + 27f;
    private static final float CARD3_X = CARD2_X + CARD_W + 27f;

    public BiomeSelectScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        game.manager.load("backgrounds/bg_biome_select.png",  Texture.class);
        game.manager.load("sounds/sfx/sfx_button_click.ogg",  Sound.class);
        game.manager.load("sounds/sfx/sfx_button_back.ogg",   Sound.class);
        game.manager.finishLoading();

        bgTexture = game.manager.get("backgrounds/bg_biome_select.png", Texture.class);

        // Biome card rectangles
        rCrystal.set(CARD1_X, CARD_Y, CARD_W, CARD_H);
        rLava   .set(CARD2_X, CARD_Y, CARD_W, CARD_H);
        rIce    .set(CARD3_X, CARD_Y, CARD_W, CARD_H);

        // Bottom buttons: BACK | SHOP | HOW TO PLAY
        rBack     .set(20f,  20f, 120f, 48f);
        rShop     .set(160f, 20f, 160f, 48f);
        rHowToPlay.set(340f, 20f, 200f, 48f);

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
                if (rCrystal.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    saveLastBiome(0);
                    game.setScreen(new CrystalCaveScreen(game));
                    return true;
                }
                if (rLava.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    saveLastBiome(1);
                    game.setScreen(new LavaCaveScreen(game));
                    return true;
                }
                if (rIce.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    saveLastBiome(2);
                    game.setScreen(new IceCaveScreen(game));
                    return true;
                }
                if (rBack.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_back.ogg");
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                if (rShop.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    game.setScreen(new CharacterScreen(game));
                    return true;
                }
                if (rHowToPlay.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    game.setScreen(new HowToPlayScreen(game));
                    return true;
                }
                return false;
            }
        }));

        game.playMusic("sounds/music/music_menu.ogg");
    }

    private void saveLastBiome(int index) {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        prefs.putInteger(Constants.PREF_LAST_BIOME, index);
        prefs.flush();
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
        String title = "SELECT BIOME";
        GL.setText(game.fontHeader, title);
        game.fontHeader.draw(game.batch, title,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 455f);
        game.batch.end();

        // Biome cards (filled + border)
        sr.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        drawCard(sr, rCrystal, CLR_CRYSTAL);
        drawCard(sr, rLava,    CLR_LAVA);
        drawCard(sr, rIce,     CLR_ICE);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Card labels
        game.batch.begin();
        drawCardLabel("CRYSTAL", "EASY",   rCrystal);
        drawCardLabel("LAVA",    "MEDIUM", rLava);
        drawCardLabel("ICE",     "HARD",   rIce);
        game.batch.end();

        // Bottom buttons
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "BACK",       rBack.x,      rBack.y,      rBack.width,      rBack.height);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "SHOP",       rShop.x,      rShop.y,      rShop.width,      rShop.height);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "HOW TO PLAY",rHowToPlay.x, rHowToPlay.y, rHowToPlay.width, rHowToPlay.height);

        stage.act(delta);
        stage.draw();
    }

    private void drawCard(ShapeRenderer sr, Rectangle r, float[] clr) {
        // Border (white at low alpha)
        sr.setColor(1f, 1f, 1f, 0.25f);
        sr.rect(r.x - 2, r.y - 2, r.width + 4, r.height + 4);
        // Fill
        sr.setColor(clr[0], clr[1], clr[2], 0.75f);
        sr.rect(r.x, r.y, r.width, r.height);
    }

    private void drawCardLabel(String name, String difficulty, Rectangle r) {
        // Biome name
        game.fontBody.setColor(1f, 1f, 1f, 1f);
        GL.setText(game.fontBody, name);
        game.fontBody.draw(game.batch, name,
                r.x + (r.width - GL.width) / 2f, r.y + r.height - 20f);

        // Difficulty
        game.fontSmall.setColor(1f, 1f, 1f, 0.8f);
        GL.setText(game.fontSmall, difficulty);
        game.fontSmall.draw(game.batch, difficulty,
                r.x + (r.width - GL.width) / 2f, r.y + r.height - 60f);
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
