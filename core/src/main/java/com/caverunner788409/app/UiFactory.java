package com.caverunner788409.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Pixel-style button factory.
 * Border: 3px solid #FF6F00 (primary). Fill: semi-transparent black (0,0,0,0.6).
 * Label: centered, accent color #B2FF59. No corner rounding.
 *
 * Usage: call sr.setProjectionMatrix(camera.combined) once before any drawButton calls.
 * Assumes SpriteBatch is NOT active (between batch.end() and the next batch.begin()).
 */
public final class UiFactory {

    // Primary  #FF6F00
    private static final float PR = 1.000f, PG = 0.435f, PB = 0.000f;
    // Accent   #B2FF59
    private static final float AR = 0.698f, AG = 1.000f, AB = 0.349f;

    private static final GlyphLayout LAYOUT = new GlyphLayout();

    private UiFactory() {}

    /**
     * Draws a single pixel-style button.
     *
     * @param sr    ShapeRenderer with projection matrix already set; must NOT be in begin/end block.
     * @param batch SpriteBatch shared by the game; must NOT be active when this is called.
     * @param font  BitmapFont used for the label (typically game.fontBody).
     * @param label Button text (e.g. "PLAY").
     * @param x     Left edge in world coordinates.
     * @param y     Bottom edge in world coordinates.
     * @param w     Button width.
     * @param h     Button height.
     */
    public static void drawButton(ShapeRenderer sr, SpriteBatch batch, BitmapFont font,
                                  String label, float x, float y, float w, float h) {
        // ── Shape pass ──────────────────────────────────────────────────
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        // 3-px border in primary colour
        sr.setColor(PR, PG, PB, 1f);
        sr.rect(x, y, w, h);
        // semi-transparent black fill inside the border
        sr.setColor(0f, 0f, 0f, 0.6f);
        sr.rect(x + 3f, y + 3f, w - 6f, h - 6f);
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // ── Text pass ───────────────────────────────────────────────────
        batch.begin();
        LAYOUT.setText(font, label);
        float savedR = font.getColor().r, savedG = font.getColor().g,
              savedB = font.getColor().b, savedA = font.getColor().a;
        font.setColor(AR, AG, AB, 1f);
        font.draw(batch, label,
                x + (w - LAYOUT.width)  / 2f,
                y + (h + LAYOUT.height) / 2f);
        font.setColor(savedR, savedG, savedB, savedA);
        batch.end();
    }
}
