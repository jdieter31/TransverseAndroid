package com.provectusstudios.transverse;

import java.util.Random;

/**
 * Created by Justin on 10/3/2014.
 */
public interface Section {
    public void draw(float[] matrix);
    public void refresh();
    public boolean handleTouchMove(float startX, float endX, float startY, float endY, boolean rightSide);
    public void setDifficulty(float difficulty);
    public void generate(Random random, float startX, float width, float startY);
    public float getStartX();
    public float getStartY();
    public float getWidth();
    public float getLength();
    public void setRenderType(RenderType renderType);
    public boolean isSplit();
}
