package com.provectusstudios.transverse;

import android.opengl.Matrix;
import android.view.MotionEvent;

/**
 * Created by Justin on 8/28/2014.
 */
public class MainMenuState implements GameState {
    private float width;
    private float height;
    private float[] viewProjectionMatrix;

    private MainRenderer mainRenderer;

    public MainMenuState(MainRenderer mainRenderer) {
        this.mainRenderer = mainRenderer;
    }

    @Override
    public void handleTouchEvent(MotionEvent event) {

    }

    @Override
    public void onDrawFrame() {
    }

    @Override
    public void refreshDimensions(float width, float height, float[] viewProjectionMatrix) {
        this.width = width;
        this.height = height;
        this.viewProjectionMatrix = viewProjectionMatrix;
    }
}
