package com.provectusstudios.transverse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * Created by Justin on 8/11/2014.
 */
public class Textures {

    public static int fffForwardFontTexture;
    public static int trophyTexture;
    public static int leaderboardTexture;
    public static int particleTexture;
    public static int fbTexture;
    public static int twitterTexture;
    public static int muteTexture;

    private static int maxTextureSize;

    public static void loadTextures(Context context) {
        int[] max = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, max, 0);
        maxTextureSize = max[0];
        Log.d("Transverse", "Max Texture Size: " + maxTextureSize);

        Log.d("Transverse", "Loading font texture");
        fffForwardFontTexture = loadTextureFromResource(R.drawable.forward, context);
        Log.d("Transverse", "Loading trophy texture");
        trophyTexture = loadTextureFromResource(R.drawable.trophy, context);
        Log.d("Transverse", "Loading leaderboard texture");
        leaderboardTexture = loadTextureFromResource(R.drawable.leaderboard, context);
        Log.d("Transverse", "Loading particle texture");
        particleTexture = loadTextureFromResource(R.drawable.particle, context);
        Log.d("Transverse", "Loading fb texture");
        fbTexture = loadTextureFromResource(R.drawable.facebook, context);
        Log.d("Transverse", "Loading twitter texture");
        twitterTexture = loadTextureFromResource(R.drawable.twitter, context);
        Log.d("Transverse", "Loading mute texture");
        muteTexture = loadTextureFromResource(R.drawable.mute, context);
    }

    private static int loadTextureFromBitmap(Bitmap bitmap) {
        if (bitmap.getHeight() >= maxTextureSize || bitmap.getWidth() >= maxTextureSize) {
            Log.d("Transverse", "Texture too big scaling down");
            float scaleFactor;
            if (bitmap.getHeight() > bitmap.getWidth()) {
                scaleFactor = ((float) maxTextureSize - 1)/((float) bitmap.getHeight());
            } else {
                scaleFactor = ((float) maxTextureSize - 1)/((float) bitmap.getWidth());
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()*scaleFactor), (int) (bitmap.getHeight()*scaleFactor), true);
        }

        int[] newTextureName = new int[1];
        GLES20.glGenTextures(1, newTextureName, 0);
        int textureName = newTextureName[0];
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureName);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        // Set wrapping mode
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // We are done using the bitmap so we should recycle it.
        bitmap.recycle();
        return textureName;
    }

    private static int loadTextureFromResource(int resourceID, Context context) {
        return loadTextureFromBitmap(
                BitmapFactory.decodeResource(context.getResources(), resourceID));
    }

}
