package com.caverunner788409.app.screens;

import com.caverunner788409.app.Constants;
import com.caverunner788409.app.MainGame;

/**
 * Crystal Cave — Easy biome.
 * Slower start speed and gentler ramp make this the most approachable biome.
 */
public class CrystalCaveScreen extends BaseCaveScreen {

    public CrystalCaveScreen(MainGame game) {
        super(game);
    }

    @Override
    protected String getBgAssetPath() {
        return "backgrounds/bg_crystal_cave.png";
    }

    @Override
    protected float getInitialSpeed() {
        // Slower than default; Lava/Ice are faster
        return 230f;
    }

    @Override
    protected int getBiomeIndex() {
        return 0;
    }
}
