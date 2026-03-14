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

/**
 * Shop screen — power-ups and character skins.
 * Layout (landscape 854×480):
 *   Title "SHOP"          y=450
 *   Coin balance          y=415
 *   "POWER-UPS" header    y=375
 *   3 power-up cards      y=280 (h=80)
 *   "SKINS" header        y=260
 *   3 skin cards          y=120 (h=120)
 *   BACK button           y=20
 */
public class CharacterScreen implements Screen {

    private final MainGame           game;
    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage              stage;
    private final ShapeRenderer      sr;
    private final Texture            bgTexture;

    // Skin preview sprites
    private final Texture skinBlue, skinGreen, skinPink;

    // Card rectangles (power-ups)
    private final Rectangle rShield = new Rectangle();
    private final Rectangle rMagnet = new Rectangle();
    private final Rectangle rDouble = new Rectangle();

    // Card rectangles (skins)
    private final Rectangle rSkin0 = new Rectangle();
    private final Rectangle rSkin1 = new Rectangle();
    private final Rectangle rSkin2 = new Rectangle();

    // Back button
    private final Rectangle rBack = new Rectangle();

    private static final GlyphLayout GL = new GlyphLayout();

    // Card layout
    private static final float CARD_W = 250f;
    private static final float CARD_GAP = 27f;
    private static final float CARD_START_X;
    static {
        CARD_START_X = (Constants.WORLD_WIDTH - 3 * CARD_W - 2 * CARD_GAP) / 2f;
    }

    // Colour constants
    private static final float[] BG_CLR  = { 0.102f, 0.102f, 0.180f };
    // Primary #FF6F00
    private static final float PR = 1f, PG = 0.435f, PB = 0f;
    // Accent #B2FF59
    private static final float AR = 0.698f, AG = 1f, AB = 0.349f;

    public CharacterScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        game.manager.load("backgrounds/bg_main.png",         Texture.class);
        game.manager.load("sprites/player_idle.png",         Texture.class);
        game.manager.load("sprites/player_idle_green.png",   Texture.class);
        game.manager.load("sprites/player_idle_pink.png",    Texture.class);
        game.manager.load("sounds/sfx/sfx_button_click.ogg", Sound.class);
        game.manager.load("sounds/sfx/sfx_button_back.ogg",  Sound.class);
        game.manager.load("sounds/sfx/sfx_coin.ogg",         Sound.class);
        game.manager.load("sounds/sfx/sfx_error.ogg",        Sound.class);
        game.manager.load("sounds/sfx/sfx_power_up.ogg",     Sound.class);
        game.manager.finishLoading();

        bgTexture = game.manager.get("backgrounds/bg_main.png",       Texture.class);
        skinBlue  = game.manager.get("sprites/player_idle.png",       Texture.class);
        skinGreen = game.manager.get("sprites/player_idle_green.png",  Texture.class);
        skinPink  = game.manager.get("sprites/player_idle_pink.png",   Texture.class);

        // Power-up card positions
        float puY = 280f, puH = 80f;
        rShield.set(CARD_START_X,                    puY, CARD_W, puH);
        rMagnet.set(CARD_START_X + CARD_W + CARD_GAP,puY, CARD_W, puH);
        rDouble.set(CARD_START_X + 2*(CARD_W+CARD_GAP), puY, CARD_W, puH);

        // Skin card positions
        float skY = 120f, skH = 120f;
        rSkin0.set(CARD_START_X,                       skY, CARD_W, skH);
        rSkin1.set(CARD_START_X + CARD_W + CARD_GAP,  skY, CARD_W, skH);
        rSkin2.set(CARD_START_X + 2*(CARD_W+CARD_GAP),skY, CARD_W, skH);

        rBack.set(20f, 20f, Constants.BTN_WIDTH_SECONDARY, Constants.BTN_HEIGHT_SECONDARY);

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.playSound("sounds/sfx/sfx_button_back.ogg");
                    game.setScreen(new BiomeSelectScreen(game));
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int sx, int sy, int pointer, int button) {
                Vector2 c = stage.screenToStageCoordinates(new Vector2(sx, sy));

                if (rBack.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_back.ogg");
                    game.setScreen(new BiomeSelectScreen(game));
                    return true;
                }

                // Power-up purchases
                if (rShield.contains(c.x, c.y)) {
                    tryBuyPowerUp(Constants.PREF_SHIELD_OWNED, Constants.SHOP_SHIELD_COST);
                    return true;
                }
                if (rMagnet.contains(c.x, c.y)) {
                    tryBuyPowerUp(Constants.PREF_MAGNET_OWNED, Constants.SHOP_MAGNET_COST);
                    return true;
                }
                if (rDouble.contains(c.x, c.y)) {
                    tryBuyPowerUp(Constants.PREF_DOUBLE_OWNED, Constants.SHOP_DOUBLE_SCORE_COST);
                    return true;
                }

                // Skin selection
                if (rSkin0.contains(c.x, c.y)) { equipSkin(0); return true; }
                if (rSkin1.contains(c.x, c.y)) { tryBuySkin(1, Constants.SHOP_SKIN_GREEN_COST); return true; }
                if (rSkin2.contains(c.x, c.y)) { tryBuySkin(2, Constants.SHOP_SKIN_PINK_COST);  return true; }

                return false;
            }
        }));

        game.playMusic("sounds/music/music_menu.ogg");
    }

    // ── Shop helpers ──────────────────────────────────────────────────

    private Preferences prefs() {
        return Gdx.app.getPreferences(Constants.PREFS_NAME);
    }

    private int coins() {
        return prefs().getInteger(Constants.PREF_COINS, 0);
    }

    private void spendCoins(int amount) {
        Preferences p = prefs();
        p.putInteger(Constants.PREF_COINS, Math.max(0, coins() - amount));
        p.flush();
    }

    private void tryBuyPowerUp(String prefKey, int cost) {
        Preferences p = prefs();
        if (p.getBoolean(prefKey, false)) {
            // Already owned for next run — play confirm
            game.playSound("sounds/sfx/sfx_button_click.ogg");
            return;
        }
        if (coins() >= cost) {
            spendCoins(cost);
            p.putBoolean(prefKey, true);
            p.flush();
            game.playSound("sounds/sfx/sfx_power_up.ogg");
        } else {
            game.playSound("sounds/sfx/sfx_error.ogg");
        }
    }

    private void tryBuySkin(int skinIndex, int cost) {
        String ownedKey = "skinOwned_" + skinIndex;
        Preferences p = prefs();
        if (p.getBoolean(ownedKey, false)) {
            equipSkin(skinIndex);
            return;
        }
        if (coins() >= cost) {
            spendCoins(cost);
            p.putBoolean(ownedKey, true);
            p.flush();
            equipSkin(skinIndex);
            game.playSound("sounds/sfx/sfx_coin.ogg");
        } else {
            game.playSound("sounds/sfx/sfx_error.ogg");
        }
    }

    private void equipSkin(int skinIndex) {
        Preferences p = prefs();
        p.putInteger(Constants.PREF_SKIN, skinIndex);
        p.flush();
        game.playSound("sounds/sfx/sfx_button_click.ogg");
    }

    // ── Render ───────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(BG_CLR[0], BG_CLR[1], BG_CLR[2], 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        sr.setProjectionMatrix(camera.combined);

        Preferences p     = prefs();
        int totalCoins    = p.getInteger(Constants.PREF_COINS, 0);
        int equippedSkin  = p.getInteger(Constants.PREF_SKIN, 0);
        boolean shOwned   = p.getBoolean(Constants.PREF_SHIELD_OWNED, false);
        boolean mgOwned   = p.getBoolean(Constants.PREF_MAGNET_OWNED, false);
        boolean dbOwned   = p.getBoolean(Constants.PREF_DOUBLE_OWNED, false);
        boolean sk1Owned  = p.getBoolean("skinOwned_1", false);
        boolean sk2Owned  = p.getBoolean("skinOwned_2", false);

        // Background
        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Title
        game.fontHeader.setColor(1f, 1f, 1f, 1f);
        String title = "SHOP";
        GL.setText(game.fontHeader, title);
        game.fontHeader.draw(game.batch, title,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 455f);

        // Coin balance
        game.fontSmall.setColor(AR, AG, AB, 1f);
        String coinStr = "COINS: " + totalCoins;
        GL.setText(game.fontSmall, coinStr);
        game.fontSmall.draw(game.batch, coinStr,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 418f);

        // Section headers
        game.fontSmall.setColor(1f, 1f, 1f, 0.75f);
        game.fontSmall.draw(game.batch, "POWER-UPS", CARD_START_X, 378f);
        game.fontSmall.draw(game.batch, "SKINS",     CARD_START_X, 258f);
        game.batch.end();

        // Draw card backgrounds
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Power-up cards
        drawCardBg(sr, rShield);
        drawCardBg(sr, rMagnet);
        drawCardBg(sr, rDouble);
        // Skin cards
        drawCardBg(sr, rSkin0);
        drawCardBg(sr, rSkin1);
        drawCardBg(sr, rSkin2);

        // Highlight equipped skin
        sr.setColor(PR, PG, PB, 0.35f);
        Rectangle eq = equippedSkin == 0 ? rSkin0 : (equippedSkin == 1 ? rSkin1 : rSkin2);
        sr.rect(eq.x, eq.y, eq.width, eq.height);

        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Power-up labels + action text
        game.batch.begin();
        drawPowerUpCard("SHIELD",  "5s invincibility", Constants.SHOP_SHIELD_COST, shOwned, rShield);
        drawPowerUpCard("MAGNET",  "Auto-collect 10s",  Constants.SHOP_MAGNET_COST, mgOwned, rMagnet);
        drawPowerUpCard("2x SCORE","Double pts 1 run",  Constants.SHOP_DOUBLE_SCORE_COST, dbOwned, rDouble);

        // Skin previews + labels
        drawSkinCard("BLUE",  skinBlue,  0,    true,    equippedSkin == 0, rSkin0);
        drawSkinCard("GREEN", skinGreen, Constants.SHOP_SKIN_GREEN_COST, sk1Owned, equippedSkin == 1, rSkin1);
        drawSkinCard("PINK",  skinPink,  Constants.SHOP_SKIN_PINK_COST,  sk2Owned, equippedSkin == 2, rSkin2);
        game.batch.end();

        // Back button
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "BACK", rBack.x, rBack.y, rBack.width, rBack.height);

        stage.act(delta);
        stage.draw();
    }

    private void drawCardBg(ShapeRenderer sr, Rectangle r) {
        sr.setColor(0f, 0f, 0f, 0.55f);
        sr.rect(r.x, r.y, r.width, r.height);
        sr.setColor(PR, PG, PB, 0.9f);
        sr.rect(r.x, r.y, r.width, 2f);
        sr.rect(r.x, r.y + r.height - 2f, r.width, 2f);
        sr.rect(r.x, r.y, 2f, r.height);
        sr.rect(r.x + r.width - 2f, r.y, 2f, r.height);
    }

    private void drawPowerUpCard(String name, String desc, int cost, boolean owned, Rectangle r) {
        game.fontBody.setColor(AR, AG, AB, 1f);
        GL.setText(game.fontBody, name);
        game.fontBody.draw(game.batch, name,
                r.x + (r.width - GL.width) / 2f, r.y + r.height - 12f);

        game.fontTiny.setColor(1f, 1f, 1f, 0.8f);
        GL.setText(game.fontTiny, desc);
        game.fontTiny.draw(game.batch, desc,
                r.x + (r.width - GL.width) / 2f, r.y + r.height - 42f);

        String actionStr = owned ? "READY" : cost + " COINS";
        game.fontSmall.setColor(owned ? AR : 1f, owned ? AG : 1f, owned ? AB : 1f, 1f);
        GL.setText(game.fontSmall, actionStr);
        game.fontSmall.draw(game.batch, actionStr,
                r.x + (r.width - GL.width) / 2f, r.y + 22f);
    }

    private void drawSkinCard(String label, Texture tex, int cost, boolean owned,
                               boolean equipped, Rectangle r) {
        // Sprite preview (centred, 48x64)
        float imgW = 48f, imgH = 64f;
        game.batch.draw(tex,
                r.x + (r.width - imgW) / 2f, r.y + r.height - imgH - 8f, imgW, imgH);

        // Name
        game.fontTiny.setColor(1f, 1f, 1f, 1f);
        GL.setText(game.fontTiny, label);
        game.fontTiny.draw(game.batch, label,
                r.x + (r.width - GL.width) / 2f, r.y + 52f);

        // Status
        String status = equipped ? "EQUIPPED" : (owned ? "EQUIP" : cost + " COINS");
        game.fontSmall.setColor(equipped ? AR : 1f, equipped ? AG : 1f, equipped ? AB : 1f, 1f);
        GL.setText(game.fontSmall, status);
        game.fontSmall.draw(game.batch, status,
                r.x + (r.width - GL.width) / 2f, r.y + 22f);
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
