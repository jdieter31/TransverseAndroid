package com.provectusstudios.transverse;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ColorFingerTrail implements FingerTrail {
    protected float red;
    protected float blue;
    protected float green;

    private float width;
    private float length;

    private static final int CAPACITY = 2000;

    int numOfVertices = 0;

    private FloatBuffer verticeBuffer = ByteBuffer.allocateDirect(CAPACITY * 3 * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
    private FloatBuffer alphaBuffer = ByteBuffer.allocateDirect(CAPACITY * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();

    private int bufferPosition = 0;

    public ColorFingerTrail(float width, float length) {
        this.width = width;
        this.length = length;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public void addTopPoint(Point point) {

        if (numOfVertices == 0) {
            insertToBuffer(point.x, point.y, 0f, 1f);
        } else {
            verticeBuffer.position(bufferPosition*3 + (numOfVertices - 1)*3);
            float lastX = verticeBuffer.get();
            float lastY = verticeBuffer.get();
            insertSegment(lastX, lastY, point.x, point.y);
        }

    }

    private void updateAlphaAndRemove() {
        int safeVertices = 0;
        float x;
        float y = 0;
        float lastX = Float.NaN;
        float lastY = Float.NaN;
        float totalDistance = 0;
        for (int i = numOfVertices*3 - 1; i >= 0; i--) {
            verticeBuffer.position(bufferPosition*3 + i);
            float vertex = verticeBuffer.get();
            if (i % 3 == 1) {
                y = vertex;
            } else if (i % 3 == 0) {
                x = vertex;
                if (!Float.isNaN(lastX)) {
                    totalDistance += Math.sqrt(Math.pow(x - lastX, 2) + Math.pow(y - lastY, 2));
                    if (totalDistance >= length/2) {
                        alphaBuffer.put(bufferPosition + i/3, (length - totalDistance) / (length/2));
                    }
                }
                safeVertices++;
                if (totalDistance >= length) {
                    break;
                }
                lastX = x;
                lastY = y;
            }
        }

        bufferPosition += (numOfVertices - safeVertices);
        numOfVertices = safeVertices;
    }

    private void insertSegment(float startX, float startY, float endX, float endY) {
        double distance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        int numOfPoints = (int) Math.max(1, distance);
        for (int i = 1; i <= numOfPoints; i++) {
            float ratio = ((float) i)/((float) numOfPoints);
            float x = startX + (endX - startX)*ratio;
            float y = startY + (endY - startY)*ratio;
            float z = 0f;
            insertToBuffer(x, y, z, 1f);
        }
        updateAlphaAndRemove();
    }

    public void insertToBuffer(float x, float y, float z, float alpha) {
        if (numOfVertices + bufferPosition > CAPACITY - 15) {
            alphaBuffer.position(bufferPosition);
            alphaBuffer.compact();
            verticeBuffer.position(bufferPosition*3);
            verticeBuffer.compact();
            bufferPosition = 0;
        }
        verticeBuffer.position(bufferPosition*3 + numOfVertices*3);
        verticeBuffer.put(x);
        verticeBuffer.put(y);
        verticeBuffer.put(z);
        alphaBuffer.position(bufferPosition + numOfVertices);
        alphaBuffer.put(alpha);
        verticeBuffer.position(bufferPosition*3);
        alphaBuffer.position(bufferPosition);
        numOfVertices++;
    }

    @Override
    public void draw(float[] matrix) {
        GLES20.glUseProgram(Shaders.pathProgram);

        int positionHandle = GLES20.glGetAttribLocation(Shaders.pathProgram, "vPosition");

        // Get handle to shape's transformation matrix
        int matrixHandle = GLES20.glGetUniformLocation(Shaders.pathProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);

        int colorHandle = GLES20.glGetUniformLocation(Shaders.pathProgram, "vColor");

        GLES20.glUniform4f(colorHandle, red, green, blue, 1);

        int alphaHandle = GLES20.glGetAttribLocation(Shaders.pathProgram, "aAlpha");

        int mSamplerLoc = GLES20.glGetUniformLocation(Shaders.pathProgram, "texture");

        GLES20.glUniform1i(mSamplerLoc, Textures.particleTexture);

        int pointSizeLoc = GLES20.glGetUniformLocation(Shaders.pathProgram, "pointSize");

        verticeBuffer.position(bufferPosition * 3);
        alphaBuffer.position(bufferPosition);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,
                0, verticeBuffer);

        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(alphaHandle, 1, GLES20.GL_FLOAT, false,
                0, alphaBuffer);

        GLES20.glEnableVertexAttribArray(alphaHandle);

        GLES20.glUniform1f(pointSizeLoc, width);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, numOfVertices);
    }

    public void setColor(float red, float green, float blue) {
        this.red = red;
        this.blue = blue;
        this.green = green;
    }
}
