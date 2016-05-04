package com.provectusstudios.transverse;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Path implements AlphaShape {
    public static class Point {
        public float x;
        public float y;
    }

    public List<Point> points = new ArrayList<>();

    private float width;

    private float[] vertices = new float[0];
    private float[] alpha = new float[0];

    private FloatBuffer verticeBuffer = ByteBuffer.allocateDirect(0)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
    private FloatBuffer alphaBuffer = ByteBuffer.allocateDirect(0)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();

    public void setWidth(float width) {
        this.width = width;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void removePoint(Point point) {
        points.remove(point);
    }

    public void removeBottomPoints(int numOfPoints) {
        for (int i = 0; i < numOfPoints; i++) {
            points.remove(0);
        }
        if (points.size() == 0) {
            vertices = new float[0];
            verticeBuffer = ByteBuffer.allocateDirect(0)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            alpha = new float[0];
            alphaBuffer = ByteBuffer.allocateDirect(0)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            return;
        }
        float[] newVertices = new float[3*points.size()];
        System.arraycopy(vertices, 3 * numOfPoints, newVertices, 0, vertices.length - 3 * numOfPoints);
        vertices = newVertices;
        verticeBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticeBuffer.put(vertices).position(0);


        float[] newAlpha = new float[points.size()];
        System.arraycopy(alpha, numOfPoints, newAlpha, 0, alpha.length - numOfPoints);
        alpha = newAlpha;
        alphaBuffer = ByteBuffer.allocateDirect(alpha.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        alphaBuffer.put(alpha).position(0);

    }

    public void addTopPoint(Point point) {
        points.add(point);
        float[] newVertices = new float[3*points.size()];
        System.arraycopy(vertices, 0, newVertices, 0, vertices.length);
        vertices = newVertices;
        float[] newAlpha = new float[points.size()];
        System.arraycopy(alpha, 0, newAlpha, 0, alpha.length);
        alpha = newAlpha;

        insertSegment(points.size() - 1);

        verticeBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticeBuffer.put(vertices).position(0);

        alphaBuffer = ByteBuffer.allocateDirect(alpha.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        alphaBuffer.put(alpha).position(0);


    }

    public void setAlpha(int pointIndex, float alphaValue) {
         alpha[pointIndex] = alphaValue;
    }

    private void insertSegment(int index) {

        alpha[index] = 1;
        vertices[3*index] = points.get(index).x;
        vertices[3*index + 1] = points.get(index).y;
        vertices[3*index + 2] = 0;

    }

    public List<Point> getPoints() {
        return points;
    }

    public void refresh() {
        vertices = new float[points.size()];
        alpha = new float[points.size()];
        for (int i = 0; i < points.size() - 1; i++) {
            insertSegment(i);
        }

        verticeBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticeBuffer.put(vertices).position(0);

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

        GLES20.glLineWidth(width);

        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertices.length/3);
    }
}

