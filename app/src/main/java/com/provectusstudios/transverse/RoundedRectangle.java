package com.provectusstudios.transverse;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Justin on 8/10/2014.
 */
public class RoundedRectangle implements Shape {
    private float cornerRadius;
    private float centerX;
    private float centerY;
    private float centerZ;
    private int precision;
    private float width;
    private float height;

    private float[] trianglesVertice;
    private short[] trianglesDrawOrder;

    private float[] topLeftVertices;
    private float[] topRightVertices;
    private float[] bottomLeftVertices;
    private float[] bottomRightVertices;

    private FloatBuffer topLeftVerticeBuffer;
    private FloatBuffer topRightVerticeBuffer;
    private FloatBuffer bottomLeftVerticeBuffer;
    private FloatBuffer bottomRightVerticeBuffer;

    private FloatBuffer trianglesVerticeBuffer;
    private ShortBuffer trianglesDrawOrderBuffer;

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public void setCenter(float x, float y, float z) {
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public void refresh() {
        trianglesDrawOrder = new short[] {
                0, 1, 2, 0, 2, 3, //left edge, right edge and center square
                4, 5, 6, 4, 6, 7, //top edge
                8, 9, 10, 8, 10, 11 //bottom edge
        };

        trianglesVertice = new float[] {
                centerX - width/2, centerY + height/2 - cornerRadius, centerZ,
                centerX - width/2, centerY - height/2 + cornerRadius, centerZ,
                centerX + width/2, centerY - height/2 + cornerRadius, centerZ,
                centerX + width/2, centerY + height/2 - cornerRadius, centerZ,

                centerX - width/2 + cornerRadius, centerY + height/2, centerZ,
                centerX - width/2 + cornerRadius, centerY + height/2 - cornerRadius, centerZ,
                centerX + width/2 - cornerRadius, centerY + height/2 - cornerRadius, centerZ,
                centerX + width/2 - cornerRadius, centerY + height/2, centerZ,

                centerX - width/2 + cornerRadius, centerY - height/2, centerZ,
                centerX - width/2 + cornerRadius, centerY - height/2 + cornerRadius, centerZ,
                centerX + width/2 - cornerRadius, centerY - height/2 + cornerRadius, centerZ,
                centerX + width/2 - cornerRadius, centerY - height/2, centerZ
        };
        trianglesVerticeBuffer = ByteBuffer.allocateDirect(trianglesVertice.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        trianglesVerticeBuffer.put(trianglesVertice).position(0);
        trianglesDrawOrderBuffer = ByteBuffer.allocateDirect(trianglesDrawOrder.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        trianglesDrawOrderBuffer.put(trianglesDrawOrder).position(0);

        topLeftVertices = new float[precision*3 + 3];
        topLeftVertices[0] = centerX - width/2 + cornerRadius;
        topLeftVertices[1] = centerY + height/2 - cornerRadius;
        topLeftVertices[2] = centerZ;
        for (int i = 0; i < precision; i++) {
            topLeftVertices[i*3 + 3] = (float) (cornerRadius * Math.cos(((float) i)/(precision - 1) * Math.PI / 2f + Math.PI / 2f) + topLeftVertices[0]);
            topLeftVertices[i*3 + 4] = (float) (cornerRadius * Math.sin(((float) i) /(precision - 1) * Math.PI / 2f + Math.PI / 2f) + topLeftVertices[1]);
            topLeftVertices[i*3 + 5] = centerZ;
        }
        topLeftVerticeBuffer = ByteBuffer.allocateDirect(topLeftVertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        topLeftVerticeBuffer.put(topLeftVertices).position(0);

        topRightVertices = new float[precision*3 + 3];
        topRightVertices[0] = centerX + width/2 - cornerRadius;
        topRightVertices[1] = centerY + height/2 - cornerRadius;
        topRightVertices[2] = centerZ;
        for (int i = 0; i < precision; i++) {
            topRightVertices[i*3 + 3] = (float) (cornerRadius * Math.cos(((float) i)/(precision - 1) * Math.PI / 2f) + topRightVertices[0]);
            topRightVertices[i*3 + 4] = (float) (cornerRadius * Math.sin(((float) i) /(precision - 1) * Math.PI / 2f) + topRightVertices[1]);
            topRightVertices[i*3 + 5] = centerZ;
        }
        topRightVerticeBuffer = ByteBuffer.allocateDirect(topRightVertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        topRightVerticeBuffer.put(topRightVertices).position(0);

        bottomLeftVertices = new float[precision*3 + 3];
        bottomLeftVertices[0] = centerX - width/2 + cornerRadius;
        bottomLeftVertices[1] = centerY - height/2 + cornerRadius;
        bottomLeftVertices[2] = centerZ;
        for (int i = 0; i < precision; i++) {
            bottomLeftVertices[i*3 + 3] = (float) (cornerRadius * Math.cos(((float) i)/(precision - 1) * Math.PI / 2f + Math.PI) + bottomLeftVertices[0]);
            bottomLeftVertices[i*3 + 4] = (float) (cornerRadius * Math.sin(((float) i) /(precision - 1) * Math.PI / 2f + Math.PI) + bottomLeftVertices[1]);
            bottomLeftVertices[i*3 + 5] = centerZ;
        }
        bottomLeftVerticeBuffer = ByteBuffer.allocateDirect(bottomLeftVertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        bottomLeftVerticeBuffer.put(bottomLeftVertices).position(0);

        bottomRightVertices = new float[precision*3 + 3];
        bottomRightVertices[0] = centerX + width/2 - cornerRadius;
        bottomRightVertices[1] = centerY - height/2 + cornerRadius;
        bottomRightVertices[2] = centerZ;
        for (int i = 0; i < precision; i++) {
            bottomRightVertices[i*3 + 3] = (float) (cornerRadius * Math.cos(((float) i)/(precision - 1) * Math.PI / 2f + 3f * Math.PI / 2f) + bottomRightVertices[0]);
            bottomRightVertices[i*3 + 4] = (float) (cornerRadius * Math.sin(((float) i) /(precision - 1) * Math.PI / 2f + 3f * Math.PI / 2f) + bottomRightVertices[1]);
            bottomRightVertices[i*3 + 5] = centerZ;
        }
        bottomRightVerticeBuffer = ByteBuffer.allocateDirect(bottomRightVertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        bottomRightVerticeBuffer.put(bottomRightVertices).position(0);
    }

    public void draw(int verticeMatrixHandle) {
        GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                0, trianglesVerticeBuffer);

        GLES20.glEnableVertexAttribArray(verticeMatrixHandle);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, trianglesDrawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, trianglesDrawOrderBuffer);

        GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                0, topLeftVerticeBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, precision + 1);

        GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                0, topRightVerticeBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, precision + 1);

        GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                0, bottomLeftVerticeBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, precision + 1);

        GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                0, bottomRightVerticeBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, precision + 1);
    }

    @Override
    public boolean containsPoint(float x, float y) {
        float topLeftX = centerX - width/2;
        float topLeftY = centerY - height/2;
        float bottomRightX = centerX + width/2;
        float bottomRightY = centerY + height/2;
        if (x >= topLeftX && x <= bottomRightX && y >= topLeftY + cornerRadius && y <= bottomRightY - cornerRadius) {
            return true;
        } else if (x >= topLeftX + cornerRadius && x <= bottomRightX - cornerRadius && y >= topLeftY && y <= bottomRightY) {
            return true;
        } else {
            float dx = Math.min(Math.abs((topLeftX + cornerRadius) - x), Math.abs((bottomRightX - cornerRadius) - x));
            float dy = Math.min(Math.abs((topLeftY + cornerRadius) - y), Math.abs((bottomRightY - cornerRadius) - y));
            if (Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) <= cornerRadius) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean lineSegmentCrosses(float startX, float startY, float endX, float endY) {
        float topLeftX = centerX - width/2;
        float topLeftY = centerY - height/2;
        float bottomRightX = centerX + width/2;
        float bottomRightY = centerY + height/2;
        if (UtilityMath.lineSegmentsCross(startX, startY, endX, endY, topLeftX + cornerRadius, topLeftY, bottomRightX - cornerRadius, topLeftY)
                || UtilityMath.lineSegmentsCross(startX, startY, endX, endY, topLeftX + cornerRadius, bottomRightY, bottomRightX - cornerRadius, bottomRightY)
                || UtilityMath.lineSegmentsCross(startX, startY, endX, endY, topLeftX, topLeftY + cornerRadius, topLeftX, bottomRightY - cornerRadius)
                || UtilityMath.lineSegmentsCross(startX, startY, endX, endY, bottomRightX, topLeftY + cornerRadius, bottomRightX, bottomRightY - cornerRadius)
                || UtilityMath.lineSegmentCrossesCircle(startX, startY, endX, endY, topLeftX + cornerRadius, topLeftY + cornerRadius, cornerRadius)
                || UtilityMath.lineSegmentCrossesCircle(startX, startY, endX, endY, bottomRightX - cornerRadius, topLeftY + cornerRadius, cornerRadius)
                || UtilityMath.lineSegmentCrossesCircle(startX, startY, endX, endY, topLeftX + cornerRadius, bottomRightY - cornerRadius, cornerRadius)
                || UtilityMath.lineSegmentCrossesCircle(startX, startY, endX, endY, bottomRightX - cornerRadius, bottomRightY - cornerRadius, cornerRadius)) {
            return true;
        }
        return false;
    }


}
