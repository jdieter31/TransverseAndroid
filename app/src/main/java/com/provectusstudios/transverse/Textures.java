package com.provectusstudios.transverse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by Justin on 8/11/2014.
 */
public class Textures {

    public static int arialFontTexture;
    public static int fffForwardFontTexture;
    public static int trophyTexture;
    public static int leaderboardTexture;

    public static void loadTextures(Context context) {
        arialFontTexture = loadTextureFromResource(R.drawable.arial, context);
        fffForwardFontTexture = loadTextureFromResource(R.drawable.forward, context);
        trophyTexture = loadTextureFromResource(R.drawable.trophy, context);
        leaderboardTexture = loadTextureFromResource(R.drawable.leaderboard, context);
    }

    private static int loadTextureFromBitmap(Bitmap bitmap) {
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
