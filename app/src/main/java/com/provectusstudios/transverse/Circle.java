package com.provectusstudios.transverse;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Circle implements Shape {
    private float radius;
    private float centerX;
    private float centerY;
    private float centerZ;
    private int precision;

    private float startAngle = 0;
    private float endAngle = (float) (2 * Math.PI);

    private boolean isOutline;
    private float outlineWidth;

    private float[] outlineVerticeArray;
    private FloatBuffer outlineVerticeBuffer;

    private float[] verticeArray;
    private FloatBuffer verticeBuffer;

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setCenter(float x, float y, float z) {
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
    }

    public void setAngle(float startAngle, float endAngle) {
        this.startAngle = startAngle;
        this.endAngle = endAngle;
    }

    public void setIsOutline(boolean isOutline) {
        this.isOutline = isOutline;
    }

    public void setOutlineWidth(float outlineWidth) {
        this.outlineWidth = outlineWidth;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public void refresh() {
        if (!isOutline) {
            verticeArray = new float[precision * 3 + 6];
            verticeArray[0] = centerX;
            verticeArray[1] = centerY;
            verticeArray[2] = centerZ;
            //Make all angles negatives since y is inverted and we want angle to go clockwise
            float totalAngle = -(endAngle - startAngle);
            for (int i = 0; i <= precision; i++) {
                verticeArray[(i + 1) * 3] = (float) (radius * Math.cos(((float) i) / precision * totalAngle - startAngle) + centerX);
                verticeArray[(i + 1) * 3 + 1] = (float) (radius * Math.sin(((float) i) / precision * totalAngle - startAngle) + centerY);
                verticeArray[(i + 1) * 3 + 2] = centerZ;
            }
            verticeBuffer = ByteBuffer.allocateDirect(verticeArray.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();

            verticeBuffer.put(verticeArray).position(0);
        } else {
            outlineVerticeArray = new float[precision * 3 * 2 + 6];
            float totalAngle = -(endAngle - startAngle);
            for (int i = 0; i <= precision; i++)  {
                outlineVerticeArray[i * 3 * 2] = (float) ((radius - outlineWidth/2) * Math.cos(((float) i) / precision * totalAngle - startAngle) + centerX);
                outlineVerticeArray[i * 3 * 2 + 1] = (float) ((radius - outlineWidth/2) * Math.sin(((float) i) / precision * totalAngle - startAngle) + centerY);
                outlineVerticeArray[i * 3 * 2 + 2] = centerZ;
                outlineVerticeArray[i * 3 * 2 + 3] = (float) ((radius + outlineWidth/2) * Math.cos(((float) i) / precision * totalAngle - startAngle) + centerX);
                outlineVerticeArray[i * 3 * 2 + 4] = (float) ((radius + outlineWidth/2) * Math.sin(((float) i) / precision * totalAngle - startAngle) + centerY);
                outlineVerticeArray[i * 3 * 2 + 5] = centerZ;
            }
            outlineVerticeBuffer = ByteBuffer.allocateDirect(outlineVerticeArray.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            outlineVerticeBuffer.put(outlineVerticeArray).position(0);
        }
    }

    public void draw(int verticeMatrixHandle) {
        if (isOutline) {
            GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                    0, outlineVerticeBuffer);
            GLES20.glEnableVertexAttribArray(verticeMatrixHandle);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, precision*2 + 2);
        } else {
            GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                    0, verticeBuffer);
            GLES20.glEnableVertexAttribArray(verticeMatrixHandle);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, precision + 2);
        }
    }

    public boolean containsPoint(float x, float y) {
        float dx = x - centerX;
        float dy = y - centerY;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) <= radius;
    }

    public boolean lineSegmentCrosses(float startX, float startY, float endX, float endY) {
        return UtilityMath.lineSegmentCrossesCircle(startX, startY, endX, endY, centerX, centerY, radius);
    }
}
