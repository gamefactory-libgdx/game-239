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
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.caverunner788409.app.Constants;
import com.caverunner788409.app.MainGame;
import com.caverunner788409.app.UiFactory;

public class LeaderboardScreen implements Screen {

    private final MainGame           game;
    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage              stage;
    private final ShapeRenderer      sr;
    private final Texture            bgTexture;

    /** Loaded top-10 scores, descending order. */
    private final int[] scores;

    private final Rectangle rMenu = new Rectangle();

    private static final GlyphLayout GL = new GlyphLayout();

    private static final float[] BG_CLR = {0.102f, 0.102f, 0.180f};

    // Row highlight colours (alpha-blended over the background)
    private static final float[] GOLD_TINT   = {1.000f, 0.843f, 0.000f, 0.12f};
    private static final float[] SILVER_TINT = {0.753f, 0.753f, 0.753f, 0.10f};
    private static final float[] BRONZE_TINT = {0.804f, 0.498f, 0.196f, 0.10f};
    private static final float[] ALT_TINT    = {1.000f, 1.000f, 1.000f, 0.05f};

    // ── Static helper ─────────────────────────────────────────────────

    /**
     * Inserts a score into the persistent top-10 leaderboard stored in SharedPreferences.
     * Safe to call from any libGDX thread after the framework is initialised.
     */
    public static void addScore(int newScore) {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        String raw = prefs.getString(Constants.PREF_LEADERBOARD, "");

        IntArray list = new IntArray();
        if (!raw.isEmpty()) {
            for (String token : raw.split(",")) {
                try { list.add(Integer.parseInt(token.trim())); }
                catch (NumberFormatException ignored) {}
            }
        }
        list.add(newScore);

        // Sort descending
        list.sort();
        list.reverse();

        // Keep top-N
        while (list.size > Constants.LEADERBOARD_SIZE) {
            list.removeIndex(list.size - 1);
        }

        // Serialise back
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size; i++) {
            if (i > 0) sb.append(',');
            sb.append(list.get(i));
        }
        prefs.putString(Constants.PREF_LEADERBOARD, sb.toString());
        prefs.flush();
    }

    // ── Constructor ───────────────────────────────────────────────────

    public LeaderboardScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        // ── Load assets ───────────────────────────────────────────────
        game.manager.load("backgrounds/bg_main.png",         Texture.class);
        game.manager.load("sounds/sfx/sfx_button_click.ogg", Sound.class);
        game.manager.finishLoading();

        bgTexture = game.manager.get("backgrounds/bg_main.png", Texture.class);

        // ── Read leaderboard ──────────────────────────────────────────
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        String raw = prefs.getString(Constants.PREF_LEADERBOARD, "");
        IntArray list = new IntArray();
        if (!raw.isEmpty()) {
            for (String token : raw.split(",")) {
                try { list.add(Integer.parseInt(token.trim())); }
                catch (NumberFormatException ignored) {}
            }
        }
        list.sort();
        list.reverse();
        scores = new int[Math.min(list.size, Constants.LEADERBOARD_SIZE)];
        for (int i = 0; i < scores.length; i++) scores[i] = list.get(i);

        // ── Button layout ─────────────────────────────────────────────
        float bw = Constants.BTN_WIDTH_SECONDARY;
        float bh = Constants.BTN_HEIGHT_SECONDARY;
        rMenu.set((Constants.WORLD_WIDTH - bw) / 2f, 30f, bw, bh);

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
                if (rMenu.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    // ── Rendering ─────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(BG_CLR[0], BG_CLR[1], BG_CLR[2], 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);

        // ── Background ────────────────────────────────────────────────
        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Title
        game.fontHeader.setColor(1f, 1f, 1f, 1f);
        String titleStr = "LEADERBOARD";
        GL.setText(game.fontHeader, titleStr);
        game.fontHeader.draw(game.batch, titleStr,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 460f);

        game.batch.end();

        // ── Row tint bars ─────────────────────────────────────────────
        float rowH   = 32f;
        float rowW   = 600f;
        float rowX   = (Constants.WORLD_WIDTH - rowW) / 2f;
        float startY = 415f;    // top of first row (in world coords, bottom-left origin)

        sr.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < scores.length; i++) {
            float ry = startY - i * (rowH + 4f);
            float[] tint = rowTint(i);
            sr.setColor(tint[0], tint[1], tint[2], tint[3]);
            sr.rect(rowX, ry - rowH, rowW, rowH);
        }
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // ── Row text ──────────────────────────────────────────────────
        game.batch.begin();
        for (int i = 0; i < scores.length; i++) {
            float ry = startY - i * (rowH + 4f) - rowH / 2f + lineAscent() / 2f;

            // Rank number  (grey)
            game.fontSmall.setColor(0.75f, 0.75f, 0.75f, 1f);
            String rank = (i + 1) + ".";
            game.fontSmall.draw(game.batch, rank, rowX + 8f, ry);

            // Score  (accent colour #B2FF59)
            game.fontSmall.setColor(0.698f, 1.0f, 0.349f, 1f);
            String scoreStr = String.valueOf(scores[i]);
            GL.setText(game.fontSmall, scoreStr);
            game.fontSmall.draw(game.batch, scoreStr,
                    rowX + rowW - GL.width - 8f, ry);
        }

        if (scores.length == 0) {
            game.fontBody.setColor(1f, 1f, 1f, 0.6f);
            String empty = "No scores yet — play to set a record!";
            GL.setText(game.fontBody, empty);
            game.fontBody.draw(game.batch, empty,
                    (Constants.WORLD_WIDTH - GL.width) / 2f, 360f);
        }

        game.batch.end();

        // ── Main Menu button ──────────────────────────────────────────
        sr.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "MAIN MENU", rMenu.x, rMenu.y, rMenu.width, rMenu.height);

        stage.act(delta);
        stage.draw();
    }

    private float[] rowTint(int rank) {
        if (rank == 0) return GOLD_TINT;
        if (rank == 1) return SILVER_TINT;
        if (rank == 2) return BRONZE_TINT;
        return (rank % 2 == 0) ? ALT_TINT : new float[]{0, 0, 0, 0};
    }

    private float lineAscent() {
        GL.setText(game.fontSmall, "0");
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
