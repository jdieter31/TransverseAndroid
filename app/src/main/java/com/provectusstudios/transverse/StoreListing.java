package com.provectusstudios.transverse;

/**
 * Created by Justin Dieter on 7/7/2016.
 */
public class StoreListing {
    private float centerX;
    private float centerY;

    private float heigth;
    private float width;

    public void setCenter(float centerX, float centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.heigth = height;
    }

}
