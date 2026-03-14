package com.caverunner788409.app.screens;

import com.caverunner788409.app.Constants;
import com.caverunner788409.app.MainGame;

/**
 * Ice Cave — Hard biome.
 * Higher starting speed makes obstacle timing tighter from the first second.
 */
public class IceCaveScreen extends BaseCaveScreen {

    public IceCaveScreen(MainGame game) {
        super(game);
    }

    @Override
    protected String getBgAssetPath() {
        return "backgrounds/bg_ice_cave.png";
    }

    @Override
    protected float getInitialSpeed() {
        return 360f;  // significantly faster than Lava biome
    }

    @Override
    protected int getBiomeIndex() {
        return 2;
    }
}
