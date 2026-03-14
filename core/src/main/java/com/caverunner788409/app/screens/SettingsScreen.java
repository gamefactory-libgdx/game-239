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

public class SettingsScreen implements Screen {

    private final MainGame           game;
    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage              stage;
    private final ShapeRenderer      sr;
    private final Texture            bgTexture;

    private boolean musicOn;
    private boolean sfxOn;

    // Toggle button rects (ON / OFF for each row)
    private final Rectangle rMusicOn  = new Rectangle();
    private final Rectangle rMusicOff = new Rectangle();
    private final Rectangle rSfxOn    = new Rectangle();
    private final Rectangle rSfxOff   = new Rectangle();
    private final Rectangle rMenu     = new Rectangle();

    private static final GlyphLayout GL = new GlyphLayout();

    // Palette
    private static final float[] BG_CLR  = {0.102f, 0.102f, 0.180f};
    // Active toggle: primary #FF6F00 border stays, fill greenish to signal ON
    // Inactive toggle: grey border. We reuse UiFactory.drawButton for the active state
    // and draw a dimmed variant manually for the OFF state.

    private static final float TW = 100f;   // toggle button width
    private static final float TH = Constants.BTN_HEIGHT_SECONDARY;

    public SettingsScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        // ── Load assets ───────────────────────────────────────────────
        game.manager.load("backgrounds/bg_main.png",         Texture.class);
        game.manager.load("sounds/sfx/sfx_button_click.ogg", Sound.class);
        game.manager.load("sounds/sfx/sfx_toggle.ogg",       Sound.class);
        game.manager.finishLoading();

        bgTexture = game.manager.get("backgrounds/bg_main.png", Texture.class);

        // ── Read saved prefs ──────────────────────────────────────────
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        musicOn = prefs.getBoolean(Constants.PREF_MUSIC, true);
        sfxOn   = prefs.getBoolean(Constants.PREF_SFX,   true);
        game.musicEnabled = musicOn;
        game.sfxEnabled   = sfxOn;

        // ── Layout ───────────────────────────────────────────────────
        // Row centres: y=310 (music), y=230 (sfx)
        // ON button left of label centre, OFF button right
        float labelX   = 180f;   // right edge of label column
        float toggleX  = labelX + 20f;

        rMusicOn .set(toggleX,       310f, TW, TH);
        rMusicOff.set(toggleX + TW + 10f, 310f, TW, TH);
        rSfxOn   .set(toggleX,       230f, TW, TH);
        rSfxOff  .set(toggleX + TW + 10f, 230f, TW, TH);

        float bw = Constants.BTN_WIDTH_SECONDARY;
        float bh = Constants.BTN_HEIGHT_SECONDARY;
        rMenu.set((Constants.WORLD_WIDTH - bw) / 2f, 120f, bw, bh);

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

                if (rMusicOn.contains(c.x, c.y) && !musicOn) {
                    toggleMusic(true);
                    return true;
                }
                if (rMusicOff.contains(c.x, c.y) && musicOn) {
                    toggleMusic(false);
                    return true;
                }
                if (rSfxOn.contains(c.x, c.y) && !sfxOn) {
                    toggleSfx(true);
                    return true;
                }
                if (rSfxOff.contains(c.x, c.y) && sfxOn) {
                    toggleSfx(false);
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
    }

    // ── Toggle helpers ─────────────────────────────────────────────────

    private void toggleMusic(boolean on) {
        musicOn = on;
        game.musicEnabled = on;
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        prefs.putBoolean(Constants.PREF_MUSIC, on);
        prefs.flush();
        if (game.currentMusic != null) {
            if (on) game.currentMusic.play();
            else    game.currentMusic.pause();
        }
        game.playSound("sounds/sfx/sfx_toggle.ogg");
    }

    private void toggleSfx(boolean on) {
        sfxOn = on;
        game.sfxEnabled = on;
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        prefs.putBoolean(Constants.PREF_SFX, on);
        prefs.flush();
        game.playSound("sounds/sfx/sfx_toggle.ogg");
    }

    // ── Rendering ─────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(BG_CLR[0], BG_CLR[1], BG_CLR[2], 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);

        // Background + text labels
        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Screen title
        game.fontHeader.setColor(1f, 1f, 1f, 1f);
        String titleStr = "SETTINGS";
        GL.setText(game.fontHeader, titleStr);
        game.fontHeader.draw(game.batch, titleStr,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 440f);

        // Row labels
        game.fontBody.setColor(1f, 1f, 1f, 1f);
        game.fontBody.draw(game.batch, "MUSIC", 80f,
                rMusicOn.y + (rMusicOn.height + labelAscent(game.fontBody, "MUSIC")) / 2f);
        game.fontBody.draw(game.batch, "SFX",   80f,
                rSfxOn.y   + (rSfxOn.height   + labelAscent(game.fontBody, "SFX"))   / 2f);

        game.batch.end();

        // Toggle buttons (use UiFactory for active selection; dim inactive)
        sr.setProjectionMatrix(camera.combined);

        drawToggle(musicOn, rMusicOn,  rMusicOff, "ON", "OFF");
        drawToggle(sfxOn,   rSfxOn,    rSfxOff,   "ON", "OFF");

        // Main menu button
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "MAIN MENU", rMenu.x, rMenu.y, rMenu.width, rMenu.height);

        stage.act(delta);
        stage.draw();
    }

    /** Active state uses UiFactory (full colour). Inactive uses a dimmed grey border. */
    private void drawToggle(boolean stateOn,
                            Rectangle rOn, Rectangle rOff,
                            String labelOn, String labelOff) {
        if (stateOn) {
            // ON button: full primary style
            UiFactory.drawButton(sr, game.batch, game.fontBody,
                    labelOn, rOn.x, rOn.y, rOn.width, rOn.height);
            // OFF button: dimmed
            drawDimButton(rOff.x, rOff.y, rOff.width, rOff.height, labelOff);
        } else {
            // ON button: dimmed
            drawDimButton(rOn.x, rOn.y, rOn.width, rOn.height, labelOn);
            // OFF button: full primary style
            UiFactory.drawButton(sr, game.batch, game.fontBody,
                    labelOff, rOff.x, rOff.y, rOff.width, rOff.height);
        }
    }

    /** Draws a muted/inactive toggle option (grey border, dark fill, grey text). */
    private void drawDimButton(float x, float y, float w, float h, String label) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.35f, 0.35f, 0.35f, 1f);   // grey border
        sr.rect(x, y, w, h);
        sr.setColor(0f, 0f, 0f, 0.6f);
        sr.rect(x + 3f, y + 3f, w - 6f, h - 6f);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.batch.begin();
        GL.setText(game.fontBody, label);
        game.fontBody.setColor(0.55f, 0.55f, 0.55f, 1f);  // muted text
        game.fontBody.draw(game.batch, label,
                x + (w - GL.width) / 2f,
                y + (h + GL.height) / 2f);
        game.fontBody.setColor(1f, 1f, 1f, 1f);
        game.batch.end();
    }

    private float labelAscent(com.badlogic.gdx.graphics.g2d.BitmapFont font, String text) {
        GL.setText(font, text);
        return GL.height;
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
