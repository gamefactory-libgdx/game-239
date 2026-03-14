package com.caverunner788409.app.screens;

import com.caverunner788409.app.Constants;
import com.caverunner788409.app.MainGame;

/**
 * Lava Cave — Medium biome.
 * Standard starting speed matching the design spec defaults.
 */
public class LavaCaveScreen extends BaseCaveScreen {

    public LavaCaveScreen(MainGame game) {
        super(game);
    }

    @Override
    protected String getBgAssetPath() {
        return "backgrounds/bg_lava_cave.png";
    }

    @Override
    protected float getInitialSpeed() {
        return Constants.PLAYER_SPEED_INIT;  // 280f — default
    }

    @Override
    protected int getBiomeIndex() {
        return 1;
    }
}
