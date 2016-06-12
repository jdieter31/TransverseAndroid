package com.provectusstudios.transverse;

public interface FingerTrail {
    class Point {
        public float x;
        public float y;
    }

    void addTopPoint(Point point);
    void draw(float[] matrix);
}
