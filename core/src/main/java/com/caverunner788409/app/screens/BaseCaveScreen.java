package com.caverunner788409.app.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.caverunner788409.app.Constants;
import com.caverunner788409.app.MainGame;

/**
 * Shared endless-runner gameplay logic for all three cave biomes.
 *
 * <p>Physics summary:
 * <ul>
 *   <li>Player fixed at x=PLAYER_START_X; world scrolls leftward.</li>
 *   <li>Swipe UP  → jump (JUMP_VELOCITY applied once, gravity pulls back).</li>
 *   <li>Swipe DOWN → slide (player height shrinks for SLIDE_DURATION).</li>
 *   <li>Swipe LEFT → 0.5 s dodge invincibility.</li>
 *   <li>LOW obstacle  (stalagmite, from ground): player must jump.</li>
 *   <li>HIGH obstacle (stalactite, from ceiling): player must slide.</li>
 * </ul>
 *
 * <p>Obstacle collision rectangles are deliberately inset so the feel is fair.
 * <pre>
 *  Standing player coll rect: playerY→playerY+56 (vs PLAYER_HEIGHT=64)
 *  Sliding  player coll rect: playerY→playerY+24 (vs SLIDE_HEIGHT=32)
 *
 *  Low  obs (stalagmite): y=80..150  — blocks standing (top=56+80=136>80) ✓
 *                                       cleared by jump (playerY>150)        ✓
 *  High obs (stalactite): y=120..320 — blocks standing (136>120)             ✓
 *                                       cleared by slide  (24+80=104 < 120)  ✓
 * </pre>
 */
public abstract class BaseCaveScreen extends ScreenAdapter {

    // ── Obstacle geometry ─────────────────────────────────────────────
    private static final float LOW_OBS_Y       = Constants.GROUND_Y;  // 80
    private static final float LOW_OBS_H       = 70f;                 // stalagmite height
    private static final float HIGH_OBS_BOTTOM = 120f;               // stalactite bottom edge
    private static final float HIGH_OBS_H      = 200f;               // stalactite coll height
    private static final float OBS_W           = Constants.OBSTACLE_WIDTH; // 40
    private static final float SWIPE_VEL       = 200f;               // min fling velocity

    // ── Player collision geometry ──────────────────────────────────────
    private static final float PCOLL_INSET_X = 8f;
    private static final float PCOLL_W       = Constants.PLAYER_WIDTH - 16f;  // 32
    private static final float PCOLL_H_STAND = Constants.PLAYER_HEIGHT - 8f;  // 56
    private static final float PCOLL_H_SLIDE = Constants.SLIDE_HEIGHT  - 8f;  // 24

    // ── Coin geometry ─────────────────────────────────────────────────
    private static final float COIN_W = Constants.COIN_SIZE;
    private static final float COIN_H = Constants.COIN_SIZE;

    // ── Inner data classes ────────────────────────────────────────────

    private static final class ObstacleData {
        float x;
        boolean isHigh;            // true = stalactite (slide under)
        final Rectangle coll = new Rectangle();
    }

    private static final class CoinData {
        float x, y;
        final Rectangle coll = new Rectangle();
    }

    // ── Fields ────────────────────────────────────────────────────────

    protected final MainGame game;

    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage              stage;
    private final ShapeRenderer      sr;

    private InputMultiplexer inputMultiplexer;

    // Assets (all loaded in constructor)
    private Texture bgTexture;
    private Texture obsSpikeTex;     // low obstacle (stalagmite)
    private TextureRegion stalTex;   // high obstacle (flipped spike = stalactite)
    private Texture coinTex;
    private Texture pauseIconTex;
    // Player skins: [skinIndex][frame] — 0=idle, 1=walk1, 2=walk2, 3=jump, 4=hurt
    private final Texture[][] playerTex = new Texture[3][5];

    // Player state
    private float playerY   = Constants.PLAYER_START_Y;
    private float velY      = 0f;
    private boolean isSliding     = false;
    private float   slideTimer    = 0f;
    private float   dodgeTimer    = 0f;
    private boolean gameOver      = false;
    private boolean paused        = false;
    private float   hurtFlashTime = 0f;  // visual flash on death

    // Animation
    private float walkAnimTime = 0f;

    // World
    private float worldSpeed;
    private float gameTime    = 0f;
    private float bgScrollX   = 0f;

    // Score
    private int score         = 0;
    private int coinsThisRun  = 0;

    // Power-ups (activated at run start, consumed from prefs)
    private boolean shieldActive = false;
    private float   shieldTimer  = 0f;
    private boolean magnetActive = false;
    private float   magnetTimer  = 0f;
    private boolean doubleScore  = false;

    // Obstacle pool
    private final Array<ObstacleData> obstacles = new Array<>();
    private float obstacleTimer  = 0f;
    private float nextObstDelay  = 1.5f;  // initial delay in seconds

    // Coin pool
    private final Array<CoinData> coins       = new Array<>();
    private float coinTimer       = 0f;
    private float nextCoinDelay   = 2.0f;

    // HUD rectangles
    private final Rectangle rPauseBtn = new Rectangle();
    private final Rectangle playerRect = new Rectangle();

    private static final GlyphLayout GL = new GlyphLayout();

    // Selected skin index (0/1/2)
    private int skinIndex = 0;

    // ── Abstract contract ─────────────────────────────────────────────

    /** Asset path for this biome's background, e.g. "backgrounds/bg_crystal_cave.png". */
    protected abstract String getBgAssetPath();

    /** Initial world scroll speed for this biome (difficulty). */
    protected abstract float getInitialSpeed();

    /** Biome index: 0=Crystal, 1=Lava, 2=Ice. Used by PauseScreen restart. */
    protected abstract int getBiomeIndex();

    // ── Constructor ───────────────────────────────────────────────────

    protected BaseCaveScreen(MainGame game) {
        this.game  = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        loadAssets();
        activatePowerUps();

        // Pause button: top-right
        float iconSize = 40f;
        rPauseBtn.set(Constants.WORLD_WIDTH - iconSize - 8f,
                      Constants.WORLD_HEIGHT - iconSize - 8f,
                      iconSize, iconSize);

        worldSpeed = getInitialSpeed();

        // Seed first obstacle and coin delays
        nextObstDelay = MathUtils.random(1.0f, 1.8f);
        nextCoinDelay = MathUtils.random(1.5f, 3.0f);

        buildInputProcessor();
    }

    // ── Asset loading ─────────────────────────────────────────────────

    private void loadAssets() {
        game.manager.load(getBgAssetPath(),                        Texture.class);
        game.manager.load("sprites/obstacle_spike.png",            Texture.class);
        game.manager.load("sprites/coin_gold.png",                 Texture.class);
        game.manager.load("sprites/icon_pause.png",                Texture.class);
        // All three skin variants
        String[] idlePaths  = {"sprites/player_idle.png",   "sprites/player_idle_green.png",  "sprites/player_idle_pink.png"};
        String[] walk1Paths = {"sprites/player_walk1.png",  "sprites/player_walk1_green.png", "sprites/player_walk1_pink.png"};
        String[] walk2Paths = {"sprites/player_walk2.png",  "sprites/player_walk2_green.png", "sprites/player_walk2_pink.png"};
        String[] jumpPaths  = {"sprites/player_jump.png",   "sprites/player_jump_green.png",  "sprites/player_jump_pink.png"};
        String[] hurtPaths  = {"sprites/player_hurt.png",   "sprites/player_hurt_green.png",  "sprites/player_hurt_pink.png"};
        for (int i = 0; i < 3; i++) {
            game.manager.load(idlePaths[i],  Texture.class);
            game.manager.load(walk1Paths[i], Texture.class);
            game.manager.load(walk2Paths[i], Texture.class);
            game.manager.load(jumpPaths[i],  Texture.class);
            game.manager.load(hurtPaths[i],  Texture.class);
        }
        game.manager.load("sounds/music/music_gameplay.ogg",      Music.class);
        game.manager.load("sounds/sfx/sfx_jump.ogg",              Sound.class);
        game.manager.load("sounds/sfx/sfx_coin.ogg",              Sound.class);
        game.manager.load("sounds/sfx/sfx_hit.ogg",               Sound.class);
        game.manager.load("sounds/sfx/sfx_game_over.ogg",         Sound.class);
        game.manager.load("sounds/sfx/sfx_power_up.ogg",          Sound.class);
        game.manager.load("sounds/sfx/sfx_button_click.ogg",      Sound.class);
        game.manager.finishLoading();

        bgTexture   = game.manager.get(getBgAssetPath(),             Texture.class);
        obsSpikeTex = game.manager.get("sprites/obstacle_spike.png", Texture.class);
        coinTex     = game.manager.get("sprites/coin_gold.png",      Texture.class);
        pauseIconTex= game.manager.get("sprites/icon_pause.png",     Texture.class);

        // Build flipped texture region for stalactite rendering
        stalTex = new TextureRegion(obsSpikeTex);
        stalTex.flip(false, true);

        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        skinIndex = prefs.getInteger(Constants.PREF_SKIN, 0);
        skinIndex = MathUtils.clamp(skinIndex, 0, 2);

        for (int i = 0; i < 3; i++) {
            playerTex[i][0] = game.manager.get(idlePaths[i],  Texture.class);
            playerTex[i][1] = game.manager.get(walk1Paths[i], Texture.class);
            playerTex[i][2] = game.manager.get(walk2Paths[i], Texture.class);
            playerTex[i][3] = game.manager.get(jumpPaths[i],  Texture.class);
            playerTex[i][4] = game.manager.get(hurtPaths[i],  Texture.class);
        }
    }

    /** Consume one-shot power-ups from SharedPreferences. */
    private void activatePowerUps() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);

        if (prefs.getBoolean(Constants.PREF_SHIELD_OWNED, false)) {
            shieldActive = true;
            shieldTimer  = Constants.SHIELD_DURATION;
            prefs.putBoolean(Constants.PREF_SHIELD_OWNED, false);
        }
        if (prefs.getBoolean(Constants.PREF_MAGNET_OWNED, false)) {
            magnetActive = true;
            magnetTimer  = Constants.MAGNET_DURATION;
            prefs.putBoolean(Constants.PREF_MAGNET_OWNED, false);
        }
        if (prefs.getBoolean(Constants.PREF_DOUBLE_OWNED, false)) {
            doubleScore = true;
            prefs.putBoolean(Constants.PREF_DOUBLE_OWNED, false);
        }
        prefs.flush();
    }

    // ── Input ─────────────────────────────────────────────────────────

    private void buildInputProcessor() {
        GestureDetector gd = new GestureDetector(new GestureDetector.GestureAdapter() {
            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                if (gameOver || paused) return false;
                float absX = Math.abs(velocityX);
                float absY = Math.abs(velocityY);
                if (absY > absX && absY > SWIPE_VEL) {
                    if (velocityY < 0) onJump();   // swipe up   → jump
                    else               onSlide();  // swipe down → slide
                } else if (absX > absY && absX > SWIPE_VEL) {
                    if (velocityX < 0) onDodge();  // swipe left → dodge
                }
                return true;
            }
        });

        InputAdapter backAdapter = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    goToPause();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int sx, int sy, int pointer, int button) {
                Vector2 c = stage.screenToStageCoordinates(new Vector2(sx, sy));
                if (rPauseBtn.contains(c.x, c.y)) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    goToPause();
                    return true;
                }
                return false;
            }
        };

        inputMultiplexer = new InputMultiplexer(stage, gd, backAdapter);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        game.playMusic("sounds/music/music_gameplay.ogg");
    }

    private void goToPause() {
        paused = true;
        game.setScreen(new PauseScreen(game, this, getBiomeIndex()));
    }

    // ── Player actions ────────────────────────────────────────────────

    private void onJump() {
        if (playerY <= Constants.GROUND_Y + 1f && !isSliding) {
            velY = Constants.JUMP_VELOCITY;
            game.playSound("sounds/sfx/sfx_jump.ogg");
        }
    }

    private void onSlide() {
        if (playerY <= Constants.GROUND_Y + 1f) {
            isSliding  = true;
            slideTimer = Constants.SLIDE_DURATION;
        }
    }

    private void onDodge() {
        dodgeTimer = 0.5f;
        game.playSound("sounds/sfx/sfx_power_up.ogg");
    }

    // ── Obstacle spawning ─────────────────────────────────────────────

    private void spawnObstacle() {
        ObstacleData obs = new ObstacleData();
        obs.x      = Constants.OBSTACLE_SPAWN_X;
        obs.isHigh = MathUtils.randomBoolean(0.45f); // 45% chance of stalactite
        if (obs.isHigh) {
            obs.coll.set(obs.x, HIGH_OBS_BOTTOM, OBS_W, HIGH_OBS_H);
        } else {
            obs.coll.set(obs.x, LOW_OBS_Y, OBS_W, LOW_OBS_H);
        }
        obstacles.add(obs);
    }

    /** Spawn a horizontal arc of 3–5 coins at a random height. */
    private void spawnCoins() {
        float cy = MathUtils.random(
                Constants.GROUND_Y + 10f,
                Constants.GROUND_Y + 80f);
        float cx  = Constants.WORLD_WIDTH + 60f;
        int   cnt = MathUtils.random(3, 5);
        float gap = 36f;
        for (int i = 0; i < cnt; i++) {
            CoinData c = new CoinData();
            c.x = cx + i * gap;
            c.y = cy;
            coins.add(c);
        }
    }

    // ── Update ────────────────────────────────────────────────────────

    private void update(float delta) {
        if (paused || gameOver) return;

        gameTime += delta;

        // World speed ramp
        worldSpeed = Math.min(
                getInitialSpeed() + Constants.PLAYER_SPEED_INC * gameTime,
                Constants.PLAYER_SPEED_MAX);

        // Player physics
        if (playerY > Constants.GROUND_Y || velY > 0f) {
            velY      += Constants.GRAVITY * delta;
            playerY   += velY * delta;
            if (playerY <= Constants.GROUND_Y) {
                playerY    = Constants.GROUND_Y;
                velY       = 0f;
            }
        }

        // Slide countdown
        if (isSliding) {
            slideTimer -= delta;
            if (slideTimer <= 0f) isSliding = false;
        }

        // Dodge invincibility
        if (dodgeTimer > 0f) dodgeTimer -= delta;

        // Power-up timers
        if (shieldActive) { shieldTimer -= delta; if (shieldTimer <= 0f) shieldActive = false; }
        if (magnetActive) { magnetTimer -= delta; if (magnetTimer <= 0f) magnetActive = false; }

        // Walk animation
        walkAnimTime += delta;

        // Background parallax scroll
        bgScrollX -= worldSpeed * Constants.PARALLAX_BACK_SPEED * delta;
        if (bgScrollX <= -Constants.WORLD_WIDTH) bgScrollX += Constants.WORLD_WIDTH;

        // Score: time-based + coin bonuses
        int baseScore = (int)(gameTime * Constants.SCORE_PER_SECOND)
                      + coinsThisRun * Constants.COIN_SCORE_VALUE;
        score = doubleScore ? baseScore * 2 : baseScore;

        // Player collision rectangle
        float pCollH = isSliding ? PCOLL_H_SLIDE : PCOLL_H_STAND;
        playerRect.set(Constants.PLAYER_START_X + PCOLL_INSET_X,
                       playerY,
                       PCOLL_W, pCollH);

        // Spawn obstacles
        obstacleTimer += delta;
        if (obstacleTimer >= nextObstDelay) {
            spawnObstacle();
            obstacleTimer = 0f;
            float gap = MathUtils.random(Constants.OBSTACLE_MIN_GAP, Constants.OBSTACLE_MAX_GAP);
            nextObstDelay = gap / worldSpeed;
        }

        // Move obstacles + collision check
        for (int i = obstacles.size - 1; i >= 0; i--) {
            ObstacleData obs = obstacles.get(i);
            obs.x        -= worldSpeed * delta;
            obs.coll.x    = obs.x;
            if (obs.x + OBS_W < 0f) {
                obstacles.removeIndex(i);
                continue;
            }
            boolean immune = shieldActive || dodgeTimer > 0f;
            if (!immune && playerRect.overlaps(obs.coll)) {
                triggerGameOver();
                return;
            }
        }

        // Spawn coins
        coinTimer += delta;
        if (coinTimer >= nextCoinDelay) {
            spawnCoins();
            coinTimer     = 0f;
            nextCoinDelay = MathUtils.random(1.8f, 3.5f);
        }

        // Move coins + magnet + collect
        for (int i = coins.size - 1; i >= 0; i--) {
            CoinData coin = coins.get(i);

            if (magnetActive) {
                float dx   = Constants.PLAYER_START_X - coin.x;
                float dy   = Constants.GROUND_Y       - coin.y;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist < Constants.MAGNET_RADIUS) {
                    float speed = worldSpeed * 2.5f;
                    coin.x += (dx / dist) * speed * delta;
                    coin.y += (dy / dist) * speed * delta;
                } else {
                    coin.x -= worldSpeed * delta;
                }
            } else {
                coin.x -= worldSpeed * delta;
            }

            if (coin.x + COIN_W < 0f) {
                coins.removeIndex(i);
                continue;
            }

            coin.coll.set(coin.x, coin.y, COIN_W, COIN_H);
            if (playerRect.overlaps(coin.coll)) {
                coinsThisRun++;
                game.playSound("sounds/sfx/sfx_coin.ogg");
                coins.removeIndex(i);
            }
        }
    }

    private void triggerGameOver() {
        gameOver      = true;
        hurtFlashTime = 0.8f;
        game.playSound("sounds/sfx/sfx_hit.ogg");
        game.playSound("sounds/sfx/sfx_game_over.ogg");

        // Persist coins earned this run
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int total = prefs.getInteger(Constants.PREF_COINS, 0) + coinsThisRun;
        prefs.putInteger(Constants.PREF_COINS, total);
        prefs.flush();

        // Defer screen switch to next frame (give sfx a moment to register)
    }

    // ── Render ────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        update(delta);

        // After game-over, show the hurt player for hurtFlashTime then switch
        if (gameOver) {
            hurtFlashTime -= delta;
            if (hurtFlashTime <= 0f) {
                game.setScreen(new GameOverScreen(game, score, coinsThisRun));
                return;
            }
        }

        Gdx.gl.glClearColor(0.102f, 0.102f, 0.180f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        sr.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // Scrolling background (two copies for seamless loop)
        game.batch.draw(bgTexture, bgScrollX,                    0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.draw(bgTexture, bgScrollX + Constants.WORLD_WIDTH, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Coins
        for (CoinData c : coins) {
            game.batch.draw(coinTex, c.x, c.y, COIN_W, COIN_H);
        }

        // Obstacles
        for (ObstacleData obs : obstacles) {
            if (obs.isHigh) {
                // Stalactite: flipped spike hanging from ceiling area
                float drawH = HIGH_OBS_H + 20f;
                float drawY = HIGH_OBS_BOTTOM;
                game.batch.draw(stalTex, obs.x, drawY, OBS_W, drawH);
            } else {
                // Stalagmite: spike rising from ground
                game.batch.draw(obsSpikeTex, obs.x, LOW_OBS_Y, OBS_W, LOW_OBS_H + 10f);
            }
        }

        // Player
        boolean onGround = playerY <= Constants.GROUND_Y + 1f;
        Texture pTex;
        if (gameOver) {
            pTex = playerTex[skinIndex][4]; // hurt
        } else if (isSliding) {
            pTex = playerTex[skinIndex][0]; // idle (crouched)
        } else if (!onGround) {
            pTex = playerTex[skinIndex][3]; // jump
        } else {
            int frame = ((int)(walkAnimTime / 0.15f)) % 2;
            pTex = playerTex[skinIndex][frame == 0 ? 1 : 2]; // walk1/walk2
        }
        float pH = isSliding ? Constants.SLIDE_HEIGHT : Constants.PLAYER_HEIGHT;
        float pW = Constants.PLAYER_WIDTH;

        // Dodge flicker: skip every other draw cycle
        boolean showPlayer = !(dodgeTimer > 0f && ((int)(dodgeTimer * 12f) % 2) == 0);
        // Shield flicker when nearly expired
        boolean shieldFlicker = shieldActive && shieldTimer < 1f
                && ((int)(shieldTimer * 8f) % 2) == 0;

        if (showPlayer && !shieldFlicker) {
            if (shieldActive) {
                // slight cyan tint for shield
                game.batch.setColor(0.5f, 1f, 1f, 1f);
            }
            game.batch.draw(pTex, Constants.PLAYER_START_X, playerY, pW, pH);
            game.batch.setColor(1f, 1f, 1f, 1f);
        }

        // HUD: score (top-left)
        game.fontSmall.setColor(1f, 1f, 1f, 1f);
        game.fontSmall.draw(game.batch, "SCORE: " + score,
                Constants.HUD_PAD, Constants.WORLD_HEIGHT - Constants.HUD_PAD);

        // HUD: coins (top-right area, left of pause icon)
        game.fontSmall.setColor(0.698f, 1f, 0.349f, 1f);
        String coinStr = "x" + coinsThisRun;
        GL.setText(game.fontSmall, coinStr);
        game.batch.draw(coinTex,
                Constants.WORLD_WIDTH - 90f,
                Constants.WORLD_HEIGHT - 36f,
                22f, 22f);
        game.fontSmall.draw(game.batch, coinStr,
                Constants.WORLD_WIDTH - 64f,
                Constants.WORLD_HEIGHT - Constants.HUD_PAD);
        game.fontSmall.setColor(1f, 1f, 1f, 1f);

        // HUD: swipe hints (bottom centre, semi-transparent)
        game.fontTiny.setColor(1f, 1f, 1f, 0.40f);
        String hint = "Swipe UP=JUMP   DOWN=SLIDE   LEFT=DODGE";
        GL.setText(game.fontTiny, hint);
        game.fontTiny.draw(game.batch, hint,
                (Constants.WORLD_WIDTH - GL.width) / 2f, 22f);

        // HUD: pause icon
        game.batch.draw(pauseIconTex,
                rPauseBtn.x, rPauseBtn.y, rPauseBtn.width, rPauseBtn.height);

        // Active power-up indicators
        float indicX = 10f;
        float indicY = Constants.WORLD_HEIGHT - 60f;
        if (shieldActive) {
            game.fontTiny.setColor(0.5f, 1f, 1f, 1f);
            game.fontTiny.draw(game.batch, "SHIELD " + (int)shieldTimer + "s", indicX, indicY);
            indicY -= 20f;
        }
        if (magnetActive) {
            game.fontTiny.setColor(1f, 0.84f, 0f, 1f);
            game.fontTiny.draw(game.batch, "MAGNET " + (int)magnetTimer + "s", indicX, indicY);
            indicY -= 20f;
        }
        if (doubleScore) {
            game.fontTiny.setColor(0.698f, 1f, 0.349f, 1f);
            game.fontTiny.draw(game.batch, "2x SCORE", indicX, indicY);
        }

        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void pause()  { paused = true; }
    @Override public void resume() { paused = false; }
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
        sr.dispose();
    }
}
