package com.provectusstudios.transverse;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Justin on 8/9/2014.
 */
public class MainRenderer implements GLSurfaceView.Renderer {

    private Context context;

    private GameState gameState;

    private boolean initialized = false;

    private float width;
    private float height;

    private int fps;
    private long lastFpsCalc;

    private boolean noAds = false;

    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] viewAndProjectionMatrix = new float[16];

    private static final boolean SHOULD_LOG_FPS = false;

    public MainRenderer(Context context) {
        this.context = context;
    }

    public void changeGameState(GameState newState) {
        gameState = newState;
        newState.refreshDimensions(width, height, viewAndProjectionMatrix);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        GLES20.glClearColor(1f, 1f, 1f, 1f);

        Shaders.loadShaders();

        Log.d("Transverse", "Loading Textures");
        Textures.loadTextures(context);

        Log.d("Transverse", "Loading font from XML");
        Text.loadFont(context.getResources().getXml(R.xml.forward), Textures.fffForwardFontTexture, "FFF Forward");

        GLES20.glEnable(GLES20.GL_DITHER);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        if (!initialized) {

            gameState = new MainGameState(this);
            if (noAds) {
                ((MainGameState) gameState).setNoAds();
            }
            initialized = true;
        }

        if (SHOULD_LOG_FPS) {
            lastFpsCalc = System.currentTimeMillis();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int screenWidth, int screenHeight) {

        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, screenWidth, screenHeight);

        //Get density and set height/width variable to dip
        float density = context.getResources().getDisplayMetrics().density;
        width = ((float) screenWidth)/density;
        height = ((float) screenHeight)/density;

        // Clear our matrices
        for(int i = 0; i < 16; i++)
        {
            projectionMatrix[i] = 0.0f;
            viewMatrix[i] = 0.0f;
            viewAndProjectionMatrix[i] = 0.0f;
        }

        //Set orthogonal projection matrix with height flipped so that the top of the screen
        //has y 0 and the bottom has y of the total height
        Matrix.orthoM(projectionMatrix, 0, 0.0f, width, height, 0.0f, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(viewAndProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        if (gameState != null) {
            gameState.refreshDimensions(width, height, viewAndProjectionMatrix);
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        if (SHOULD_LOG_FPS) {
            fps += 1;
            if (System.currentTimeMillis() - lastFpsCalc > 1000) {
                lastFpsCalc = System.currentTimeMillis();
                Log.d("Transverse", "FPS: " + fps);
                fps = 0;
            }
        }

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        if (gameState != null) {
            gameState.onDrawFrame();
        }
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void handleTouchEvent(MotionEvent event) {
        if (gameState != null) {
            gameState.handleTouchEvent(event);
        }
    }

    public void setNoAds() {
        noAds = true;
        if (gameState != null) {
            ((MainGameState) gameState).setNoAds();
        }
    }

}
