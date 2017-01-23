package com.provectusstudios.transverse;

import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

/**
 * Created by Justin Dieter on 7/4/2016.
 */
public class StoreScreen {
    private static SolidRenderType whiteRenderType;
    private static SolidRenderType blueRenderType;

    {
        whiteRenderType = new SolidRenderType();
        whiteRenderType.setColor(.95f, .95f, .95f);
        whiteRenderType.setAlpha(1);
        blueRenderType = new SolidRenderType();
        blueRenderType.setColor(.204f, .553f, .686f);
        blueRenderType.setAlpha(1f);
    }

    private MainGameState mainGameState;
    private Rectangle backgroundRectangle;
    private Rectangle headerRectangle;

    private Circle backCircle;
    private Image backImage;
    private Text coinStoreText;
    private Coin coinStoreCoin;

    private Coin numCoinsCoin;
    private Text numCoinsText;

    public StoreScreen(MainGameState mainGameState) {
        this.mainGameState = mainGameState;
    }

    public void refreshDimensions(float width, float height) {
        backgroundRectangle = new Rectangle();
        backgroundRectangle.setOrigin(0, 0, 0);
        backgroundRectangle.setWidth(width);
        backgroundRectangle.setHeight(height);
        backgroundRectangle.refresh();

        headerRectangle = new Rectangle();
        headerRectangle.setWidth(width);
        headerRectangle.setHeight(height/5);
        headerRectangle.setOrigin(0,0,0);
        headerRectangle.refresh();

        backCircle = new Circle();
        backCircle.setPrecision(360);
        backCircle.setRadius(height/16);
        backCircle.setCenter(height/16 + width/30, height/10, 0);
        backCircle.refresh();
        
        float backImageCenterX = height/16 + width/30;
        float backImageCenterY = height/10;
        float backImageSize = 5*height/64;
        backImage = new Image();
        backImage.setImage("arrow");
        backImage.setVertices(new float[] {
                backImageCenterX - backImageSize/2, backImageCenterY - backImageSize/2, 0,
                backImageCenterX - backImageSize/2, backImageCenterY + backImageSize/2, 0,
                backImageCenterX + backImageSize/2, backImageCenterY + backImageSize/2, 0,
                backImageCenterX + backImageSize/2, backImageCenterY - backImageSize/2, 0
        });
        backImage.refresh();

        coinStoreCoin = new Coin();
        coinStoreCoin.setCenter(2*width/30 + height/8 + height/24, height/10);
        coinStoreCoin.setSize(height/20);
        coinStoreCoin.refresh();

        coinStoreText = new Text();
        coinStoreText.setFont("FFF Forward");
        coinStoreText.setText("Store");
        coinStoreText.setTextSize(height/8);
        coinStoreText.setOrigin(2*width/30 + height/8 + height/12 + width/60, height/10 - height/16, 0);
        coinStoreText.refresh();

        numCoinsCoin = new Coin();
        numCoinsCoin.setCenter(width - width/30 - height/20, height/10);
        numCoinsCoin.setSize(height/20);
        numCoinsCoin.refresh();

        numCoinsText = new Text();
        numCoinsText.setText("" + mainGameState.getNumberOfCoins());
        numCoinsText.setFont("FFF Forward");
        numCoinsText.setTextSize(height/8);
        numCoinsText.setOrigin(width - width/30 - height/10 - width/60 - numCoinsText.getWidth(), height/10 - height/16, 0);
        numCoinsText.refresh();
    }


    public void draw(float[] matrix) {
        whiteRenderType.setMatrix(matrix);
        whiteRenderType.drawShape(backgroundRectangle);
        drawHeader(matrix);
    }

    public void handleTouch(MotionEvent event) {
        float density = mainGameState.getMainRenderer().getContext().getResources().getDisplayMetrics().density;
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                int pointerIndex = MotionEventCompat.getActionIndex(event);
                int pointerID = MotionEventCompat.getPointerId(event, pointerIndex);
                float x = MotionEventCompat.getX(event, pointerIndex);
                float y = MotionEventCompat.getY(event, pointerIndex);
                float dpX = x / density;
                float dpY = y / density;
                if (backCircle.containsPoint(dpX, dpY)) {
                    mainGameState.toggleStore();
                }
        }
    }

    public void drawHeader(float[] matrix) {
        blueRenderType.setMatrix(matrix);
        blueRenderType.drawShape(headerRectangle);
        whiteRenderType.drawShape(backCircle);
        blueRenderType.drawImage(backImage);
        coinStoreCoin.draw(matrix);
        whiteRenderType.drawText(coinStoreText);
        numCoinsCoin.draw(matrix);
        whiteRenderType.drawText(numCoinsText);
    }

}
