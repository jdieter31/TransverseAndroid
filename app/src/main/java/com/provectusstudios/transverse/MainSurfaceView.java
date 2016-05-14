package com.provectusstudios.transverse;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by Justin on 8/9/2014.
 */
public class MainSurfaceView extends GLSurfaceView {
    private MainRenderer renderer;

    public MainSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(new MultisampleConfigChooser());
        renderer = new MainRenderer(context);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void onPause() {
        super.onPause();
        renderer.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        renderer.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        renderer.handleTouchEvent(event);
        return true;
    }

}
