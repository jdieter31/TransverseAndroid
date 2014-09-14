package com.provectusstudios.transverse;

import android.view.MotionEvent;

/**
 * Created by Justin on 8/28/2014.
 */
public interface GameState {
    public void handleTouchEvent(MotionEvent event);

    public void onDrawFrame();

    public void refreshDimensions(float width, float height, float[] viewProjectionMatrix);
}
