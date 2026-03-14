package com.caverunner788409.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.caverunner788409.app.screens.MainMenuScreen;

public class MainGame extends Game {

    public SpriteBatch  batch;
    public AssetManager manager;

    // Shared fonts — generated once, used across all screens
    public BitmapFont fontTitle;   // Born2bSporty.otf — headers / scores
    public BitmapFont fontHeader;  // Born2bSporty.otf — screen titles
    public BitmapFont fontBody;    // MotionControlBold.otf — buttons / labels
    public BitmapFont fontSmall;   // MotionControlBold.otf — small labels
    public BitmapFont fontTiny;    // MotionControlBold.otf — hints

    // Audio state
    public boolean musicEnabled = true;
    public boolean sfxEnabled   = true;
    public Music   currentMusic = null;

    @Override
    public void create() {
        batch   = new SpriteBatch();
        manager = new AssetManager();

        generateFonts();

        setScreen(new MainMenuScreen(this));
    }

    private void generateFonts() {
        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Born2bSporty.otf"));
        FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/MotionControlBold.otf"));

        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();

        p.size  = Constants.FONT_SIZE_TITLE;
        fontTitle = titleGen.generateFont(p);

        p.size  = Constants.FONT_SIZE_HEADER;
        fontHeader = titleGen.generateFont(p);

        p.size  = Constants.FONT_SIZE_BODY;
        fontBody = bodyGen.generateFont(p);

        p.size  = Constants.FONT_SIZE_SMALL;
        fontSmall = bodyGen.generateFont(p);

        p.size  = Constants.FONT_SIZE_TINY;
        fontTiny = bodyGen.generateFont(p);

        titleGen.dispose();
        bodyGen.dispose();
    }

    /** Start looping music. Skips restart if the same track is already playing. */
    public void playMusic(String path) {
        Music requested = manager.get(path, Music.class);
        if (requested == currentMusic && currentMusic.isPlaying()) return;
        if (currentMusic != null) currentMusic.stop();
        currentMusic = requested;
        currentMusic.setLooping(true);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    /** Play a one-shot music track (game over jingle — never loops). */
    public void playMusicOnce(String path) {
        if (currentMusic != null) currentMusic.stop();
        currentMusic = manager.get(path, Music.class);
        currentMusic.setLooping(false);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    /** Play a sound effect if SFX is enabled. */
    public void playSound(String path) {
        if (sfxEnabled && manager.isLoaded(path, Sound.class)) {
            manager.get(path, Sound.class).play(1.0f);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        manager.dispose();
        fontTitle.dispose();
        fontHeader.dispose();
        fontBody.dispose();
        fontSmall.dispose();
        fontTiny.dispose();
    }
}
