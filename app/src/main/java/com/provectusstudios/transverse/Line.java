package com.provectusstudios.transverse;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Justin on 8/18/2014.
 */
public class Line implements AlphaShape {
    private float x;
    private float y;
    private float z;
    private float endX;
    private float endY;
    private float width;

    private float[] vertices;
    private float[] alpha;

    private FloatBuffer verticeBuffer;
    private FloatBuffer alphaBuffer;

    public void setStartPoint(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setEndPoint(float x, float y) {
        this.endX = x;
        this.endY = y;
    }

    public float getStartPointX() {
        return x;
    }

    public float getStartPointY() {
        return y;
    }

    public float getEndPointX() {
        return endX;
    }

    public float getEndPointY() {
        return endY;
    }

    public void setWidth(float width) {
        this.width = width;
    }


    public void refresh() {
        float widthX;
        float widthY;
        if (endY - y != 0) {
            float slope = -1f / ((endY - y) / (endX - x));
            widthX = (float) Math.sqrt(Math.pow(width / 2, 2) / (1 + Math.pow(slope, 2)));
            widthY = widthX * slope;
        } else {
            widthX = 0;
            widthY = width/2;
        }
        float alphaY = Math.min(widthY/2, 1f);
        float alphaX = Math.min(widthX/2, 1f);
        vertices = new float[] {
                x - widthX, y - widthY, z,
                endX - widthX, endY - widthY, z,
                x - widthX + alphaX, y - widthY + alphaY, z,
                endX - widthX + alphaX, endY - widthY + alphaY, z,
                x + widthX - alphaX, y + widthY - alphaY, z,
                endX + widthX - alphaX, endY + widthY - alphaY, z,
                x + widthX, y + widthY, z,
                endX + widthX, endY + widthY, z
        };
        verticeBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        verticeBuffer.put(vertices).position(0);

        alpha = new float[] {
                0,0,1,1,1,1,0,0
        };
        alphaBuffer = ByteBuffer.allocateDirect(alpha.length * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
        alphaBuffer.put(alpha).position(0);

    }

    public void draw(int verticeMatrixHandle, int alphaHandle) {
        GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                0, verticeBuffer);

        GLES20.glEnableVertexAttribArray(verticeMatrixHandle);

        GLES20.glVertexAttribPointer(alphaHandle, 1, GLES20.GL_FLOAT, false,
                0, alphaBuffer);

        GLES20.glEnableVertexAttribArray(alphaHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 8);
    }
}
