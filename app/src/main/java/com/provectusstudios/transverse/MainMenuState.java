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
    private SolidRenderType solidRenderType;

    private Text testText;

    public MainMenuState(MainRenderer mainRenderer) {
        this.mainRenderer = mainRenderer;
        testText = new Text();
        testText.setFont("Orbitron");
        testText.setText("transverseTRANSVERSE");
        testText.setTextSize(30);
        solidRenderType = new SolidRenderType();
        solidRenderType.setColor(.204f, .588f, .753f);
        solidRenderType.setAlpha(1);
    }

    @Override
    public void handleTouchEvent(MotionEvent event) {

    }

    @Override
    public void onDrawFrame() {
        solidRenderType.drawText(testText);
    }

    @Override
    public void refreshDimensions(float width, float height, float[] viewProjectionMatrix) {
        this.width = width;
        this.height = height;
        this.viewProjectionMatrix = viewProjectionMatrix;
        testText.setOrigin(width/2-testText.getWidth()/2, height/2 - 15, 0);
        testText.refresh();
        solidRenderType.setMatrix(viewProjectionMatrix);
    }
}
