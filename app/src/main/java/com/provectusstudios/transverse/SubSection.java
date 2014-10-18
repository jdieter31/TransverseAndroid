package com.provectusstudios.transverse;

import java.util.Random;

/**
 * Created by Justin on 10/5/2014.
 */
public interface SubSection {
    public void draw(RenderType renderType);
    public void refresh();
    public boolean handleTouchMove(float startX, float endX, float startY, float endY);
    public void generate(Random random, float width, float startX, float startY);
    public void generate(Random random, float width, float startX, float startY, float length);
    public float getLength();
    public SubSection copy();
    public void flip();
    public void setOrigin(float startX, float startY);
}
