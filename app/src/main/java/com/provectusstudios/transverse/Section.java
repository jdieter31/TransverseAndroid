package com.provectusstudios.transverse;

import java.util.Random;

public interface Section {
    void draw(float[] matrix, RenderType renderType);
    void refresh();
    boolean handleTouchMove(float startX, float endX, float startY, float endY, boolean rightSide);
    void setDifficulty(float difficulty);
    void generate(Random random, float startX, float width, float startY);
    float getStartX();
    float getStartY();
    float getWidth();
    float getLength();
    boolean isSplit();
}
