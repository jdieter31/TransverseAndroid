package com.provectusstudios.transverse;

/**
 * Created by Justin on 8/16/2014.
 */
public interface Shape {
    public void refresh();
    public void draw(int verticeMatrixHandle);
    public boolean containsPoint(float x, float y);
    public boolean lineSegmentCrosses(float startX, float startY, float endX, float endY);
}
