package com.provectusstudios.transverse;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class Path {
    public static class Point {
        public float x;
        public float y;
    }

    public List<Point> points = new ArrayList<>();

    private float width;

    private static final int CAPACITY = 2000;

    int numOfVertices = 0;

    private FloatBuffer verticeBuffer = ByteBuffer.allocateDirect(CAPACITY * 3 * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
    private FloatBuffer alphaBuffer = ByteBuffer.allocateDirect(CAPACITY * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();

    private int bufferPosition = 0;

    public Path() {

    }

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
        if (numOfPoints == 0) {
            return;
        }

        int pointsRemoved = 0;
        for (Point point : points) {
            int index = -1;
            float prevValue = Float.NaN;
            verticeBuffer.position(bufferPosition*3);
            for (int i = 0; i < numOfVertices*3; i++) {
                float value = verticeBuffer.get();
                if (prevValue == point.x && value == point.y) {
                    index = (i-1)/3;
                    break;
                }
                prevValue = value;
            }
            bufferPosition += index + 1;
            numOfVertices -= index + 1;
            pointsRemoved++;
            if (pointsRemoved == numOfPoints) {
                break;
            }
        }

        points.subList(0, numOfPoints).clear();

    }

    public void addTopPoint(Point point) {
        points.add(point);

        if (points.size() == 1) {
            insertToBuffer(points.get(0).x, points.get(0).y, 0f, 1f);
        } else {
            insertSegment(points.size() - 2, points.size() - 1);
        }

    }

    public void setAlphaVertice(int verticeIndex, float alphaValue) {
        alphaBuffer.put(bufferPosition + verticeIndex/3, alphaValue);
    }


    private void insertSegment(int index1, int index2) {
        Point point1 = points.get(index1);
        Point point2 = points.get(index2);
        double distance = Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2));
        int numOfPoints = (int) Math.max(1, distance);
        for (int i = 1; i <= numOfPoints; i++) {
            float ratio = ((float) i)/((float) numOfPoints);
            float x = point1.x + (point2.x - point1.x)*ratio;
            float y = point1.y + (point2.y - point1.y)*ratio;
            float z = 0f;
            insertToBuffer(x, y, z, 1f);
        }

    }

    public List<Point> getPoints() {
        return points;
    }

    public void refresh() {

        numOfVertices = 0;

        verticeBuffer = ByteBuffer.allocateDirect(CAPACITY * 3 * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        alphaBuffer = ByteBuffer.allocateDirect(CAPACITY * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        bufferPosition = 0;

        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {
                insertToBuffer(points.get(0).x, points.get(0).y, 0f, 1f);
            } else {
                insertSegment(i - 1, i);
            }

        }
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


    public void draw(int verticeMatrixHandle, int alphaHandle, int pointSizeLocation) {
        verticeBuffer.position(bufferPosition * 3);
        alphaBuffer.position(bufferPosition);
        GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                0, verticeBuffer);

        GLES20.glEnableVertexAttribArray(verticeMatrixHandle);

        GLES20.glVertexAttribPointer(alphaHandle, 1, GLES20.GL_FLOAT, false,
                0, alphaBuffer);

        GLES20.glEnableVertexAttribArray(alphaHandle);

        GLES20.glUniform1f(pointSizeLocation, width);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, numOfVertices);
    }

    public float[] getVertices() {
        float[] vertices = new float[numOfVertices*3];
        verticeBuffer.position(bufferPosition*3);
        verticeBuffer.get(vertices);
        return vertices;
    }

}

