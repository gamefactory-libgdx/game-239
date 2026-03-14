package com.caverunner788409.app;

public class Constants {

    // World dimensions (landscape)
    public static final float WORLD_WIDTH  = 854f;
    public static final float WORLD_HEIGHT = 480f;

    // Player
    public static final float PLAYER_WIDTH       = 48f;
    public static final float PLAYER_HEIGHT      = 64f;
    public static final float PLAYER_START_X     = 120f;
    public static final float PLAYER_START_Y     = 80f;
    public static final float PLAYER_SPEED_INIT  = 280f;
    public static final float PLAYER_SPEED_MAX   = 600f;
    public static final float PLAYER_SPEED_INC   = 12f;  // units/s added per second

    // Physics
    public static final float GRAVITY            = -1400f;
    public static final float JUMP_VELOCITY      = 820f;
    public static final float GROUND_Y           = 80f;
    public static final float SLIDE_HEIGHT       = 32f;   // crouched player height
    public static final float SLIDE_DURATION     = 0.6f;  // seconds

    // Obstacles
    public static final float OBSTACLE_WIDTH     = 40f;
    public static final float OBSTACLE_HEIGHT    = 64f;
    public static final float OBSTACLE_LOW_H     = 40f;   // low rock / stalagmite
    public static final float OBSTACLE_HIGH_H    = 80f;   // stalactite hanging from ceiling
    public static final float OBSTACLE_SPAWN_X   = WORLD_WIDTH + 50f;
    public static final float OBSTACLE_MIN_GAP   = 300f;  // min horizontal gap between spawns
    public static final float OBSTACLE_MAX_GAP   = 520f;

    // Coins
    public static final float COIN_SIZE          = 28f;
    public static final float COIN_FLOAT_SPEED   = 60f;   // gentle bob amplitude per second
    public static final int   COIN_SCORE_VALUE   = 10;

    // Ground / Platform
    public static final float GROUND_HEIGHT      = 24f;

    // Camera / Scroll
    public static final float PARALLAX_BACK_SPEED  = 0.3f;
    public static final float PARALLAX_MID_SPEED   = 0.6f;

    // UI sizes
    public static final float BTN_WIDTH_MAIN     = 240f;
    public static final float BTN_HEIGHT_MAIN    = 56f;
    public static final float BTN_WIDTH_SECONDARY = 120f;
    public static final float BTN_HEIGHT_SECONDARY = 48f;
    public static final float BTN_ROUND_SIZE     = 60f;
    public static final float BIOME_CARD_WIDTH   = 200f;
    public static final float BIOME_CARD_HEIGHT  = 240f;
    public static final float SKIN_CARD_WIDTH    = 120f;
    public static final float SKIN_CARD_HEIGHT   = 140f;
    public static final float HUD_PAD            = 10f;

    // Font sizes
    public static final int FONT_SIZE_TITLE      = 56;
    public static final int FONT_SIZE_HEADER     = 48;
    public static final int FONT_SIZE_BODY       = 28;
    public static final int FONT_SIZE_SMALL      = 20;
    public static final int FONT_SIZE_TINY       = 16;
    public static final int FONT_SIZE_SCORE      = 24;

    // Score
    public static final int SCORE_PER_SECOND     = 5;

    // Shop / Power-ups
    public static final int  SHOP_SHIELD_COST        = 20;
    public static final int  SHOP_MAGNET_COST        = 30;
    public static final int  SHOP_DOUBLE_SCORE_COST  = 50;
    public static final int  SHOP_SKIN_GREEN_COST    = 100;
    public static final int  SHOP_SKIN_PINK_COST     = 150;
    public static final float SHIELD_DURATION        = 5f;
    public static final float MAGNET_DURATION        = 10f;
    public static final float MAGNET_RADIUS          = 120f;
    public static final int   DOUBLE_SCORE_RUNS      = 1;   // lasts 1 run

    // Leaderboard
    public static final int LEADERBOARD_SIZE     = 10;

    // SharedPreferences keys
    public static final String PREFS_NAME        = "GamePrefs";
    public static final String PREF_HIGH_SCORE   = "highScore";
    public static final String PREF_COINS        = "coins";
    public static final String PREF_MUSIC        = "musicEnabled";
    public static final String PREF_SFX          = "sfxEnabled";
    public static final String PREF_VIBRATION    = "vibrationEnabled";
    public static final String PREF_SKIN         = "selectedSkin";
    public static final String PREF_SHIELD_OWNED = "shieldOwned";
    public static final String PREF_MAGNET_OWNED = "magnetOwned";
    public static final String PREF_DOUBLE_OWNED = "doubleScoreOwned";
    public static final String PREF_LEADERBOARD  = "leaderboard";
    public static final String PREF_LAST_BIOME   = "lastBiome";
}
