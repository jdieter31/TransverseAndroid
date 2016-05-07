package com.provectusstudios.transverse;

import java.util.Random;

public interface SubSection {
    void draw(RenderType renderType);
    void refresh();
    boolean handleTouchMove(float startX, float endX, float startY, float endY);
    void generate(Random random, float width, float startX, float startY);
    void generate(Random random, float width, float startX, float startY, float length);
    float getLength();
    void setDifficulty(float difficulty);
    SubSection copy();
    void flip();
    void setOrigin(float startX, float startY);
    void empty();
}
