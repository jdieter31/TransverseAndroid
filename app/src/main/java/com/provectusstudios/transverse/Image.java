package com.provectusstudios.transverse;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Justin on 8/11/2014.
 */
public class Image {
    private float[] uvCoordinates;
    private float[] vertices;
    private short[] drawOrder;
    private int textureHandle;

    private FloatBuffer uvCoordinateBuffer;
    private FloatBuffer verticeBuffer;
    private ShortBuffer drawOrderBuffer;

    public void setTextureHandle(int textureHandle) {
        this.textureHandle = textureHandle;
    }

    public void setDrawOrder(short[] drawOrder) {
        this.drawOrder = drawOrder;
    }

    public void setUVCoordinates(float[] uvCoordinates) {
        this.uvCoordinates = uvCoordinates;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public void refresh() {
        verticeBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticeBuffer.put(vertices).position(0);

        drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        drawOrderBuffer.put(drawOrder).position(0);

        uvCoordinateBuffer = ByteBuffer.allocateDirect(uvCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        uvCoordinateBuffer.put(uvCoordinates).position(0);
    }

    public void draw(int verticeHandle, int uvCoordinateHandle, int textureVariableHandle) {

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(verticeHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(verticeHandle, 3,
                GLES20.GL_FLOAT, false,
                0, verticeBuffer);

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray (uvCoordinateHandle);

        // Prepare the texture coordinates
        GLES20.glVertexAttribPointer (uvCoordinateHandle, 2, GLES20.GL_FLOAT,
                false,
                0, uvCoordinateBuffer);

        GLES20.glUniform1i (textureVariableHandle, textureHandle);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(verticeHandle);
        GLES20.glDisableVertexAttribArray(uvCoordinateHandle);
    }
}
