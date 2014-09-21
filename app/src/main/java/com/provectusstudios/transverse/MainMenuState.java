package com.provectusstudios.transverse;

import android.support.v4.view.MotionEventCompat;
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

    private Text titleText;
    private RoundedRectangle testButton;

    public MainMenuState(MainRenderer mainRenderer) {
        this.mainRenderer = mainRenderer;
        titleText = new Text();
        titleText.setFont("Arial");
        titleText.setText("Main Menu");
        titleText.setTextSize(30);
        solidRenderType = new SolidRenderType();
        solidRenderType.setColor(.204f, .588f, .753f);
        solidRenderType.setAlpha(1);
        testButton = new RoundedRectangle();
        testButton.setWidth(150);
        testButton.setHeight(75);
        testButton.setCornerRadius(10);
        testButton.setPrecision(90);
    }

    @Override
    public void handleTouchEvent(MotionEvent event) {
        float density = mainRenderer.getContext().getResources().getDisplayMetrics().density;
        int pointer = MotionEventCompat.getActionIndex(event);
        float x = MotionEventCompat.getX(event, pointer);
        float y = MotionEventCompat.getY(event, pointer);
        float dpX = event.getX()/density;
        float dpY = event.getY()/density;
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (testButton.containsPoint(dpX, dpY)) {
                    mainRenderer.changeGameState(new MainGameState(mainRenderer));
                }
        }
    }

    @Override
    public void onDrawFrame() {
        solidRenderType.drawText(titleText);
        solidRenderType.drawShape(testButton);
    }

    @Override
    public void refreshDimensions(float width, float height, float[] viewProjectionMatrix) {
        this.width = width;
        this.height = height;
        this.viewProjectionMatrix = viewProjectionMatrix;
        titleText.setOrigin(width/2- titleText.getWidth()/2, height/2 - 15, 0);
        titleText.refresh();
        testButton.setCenter(width/2, height/2 + 70, 0);
        testButton.refresh();
        solidRenderType.setMatrix(viewProjectionMatrix);
    }

    public void createGraphics() {

    }

}
