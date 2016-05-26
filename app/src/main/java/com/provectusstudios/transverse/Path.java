package com.provectusstudios.transverse;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

public class Path {
    public static class Point {
        public float x;
        public float y;
    }

    public List<Point> points = new LinkedList<>();

    private float width;

    List<Float> verticeList = new LinkedList<Float>();
    List<Float> alphaList= new LinkedList<Float>();

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
        if (numOfPoints == 0) {
            return;
        }

        for (int i = 0; i < numOfPoints; i++) {
            Point point = points.get(i);
            int index = -1;
            for (int j = 0; j < verticeList.size()/3; j++) {
                if (verticeList.get(3*j).equals(point.x) && verticeList.get(3*j + 1).equals(point.y)) {
                    index = j;
                    break;
                }
            }
            for (int k = 0; i <= index; i++) {
                verticeList.remove(0);
                verticeList.remove(0);
                verticeList.remove(0);
                alphaList.remove(0);
            }
        }

        for (int i = 0; i < numOfPoints; i++) {
            points.remove(0);
        }

        verticeBuffer = ByteBuffer.allocateDirect(verticeList.size() * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticeBuffer.put(toArray(verticeList)).position(0);

        alphaBuffer = ByteBuffer.allocateDirect(alphaList.size() * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        alphaBuffer.put(toArray(alphaList)).position(0);

    }

    public void addTopPoint(Point point) {
        points.add(point);

        if (points.size() == 1) {
            verticeList.add(points.get(0).x);
            verticeList.add(points.get(0).y);
            verticeList.add(0f);
            alphaList.add(1f);
        } else {
            insertSegment(points.size() - 2, points.size() - 1);
        }
        verticeBuffer = ByteBuffer.allocateDirect(verticeList.size() * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticeBuffer.put(toArray(verticeList)).position(0);

        alphaBuffer = ByteBuffer.allocateDirect(alphaList.size() * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        alphaBuffer.put(toArray(alphaList)).position(0);


    }

    public void setAlpha(int pointIndex, float alphaValue) {
        if (pointIndex == 0) {
            alphaList.set(0, alphaValue);
        } else {
            Point prevPoint = points.get(pointIndex - 1);
            int prevPointIndex = -1;
            for (int j = 0; j < (verticeList.size() / 3); j++) {
                if (verticeList.get(3 * j).equals(prevPoint.x) && verticeList.get(3 * j + 1).equals(prevPoint.y)) {
                    prevPointIndex = j;
                    break;
                }
            }
            Point point = points.get(pointIndex);
            int curPointIndex = -1;
            for (int j = 0; j < (verticeList.size() / 3); j++) {
                if (verticeList.get(3 * j).equals(point.x) && verticeList.get(3 * j + 1).equals(point.y)) {
                    pointIndex = j;
                    break;
                }
            }
            for (int i = (prevPointIndex + 1); i <= pointIndex; i++) {
                alphaList.set(i, alphaValue);
            }
        }
        alphaBuffer = ByteBuffer.allocateDirect(alphaList.size() * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        alphaBuffer.put(toArray(alphaList)).position(0);
    }

    private void insertSegment(int index1, int index2) {
        Point point1 = points.get(index1);
        Point point2 = points.get(index2);
        double distance = Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2));
        int numOfPoints = (int) Math.max(1, distance);
        for (int i = 1; i <= numOfPoints; i++) {
            float ratio = ((float) i)/((float) numOfPoints);
            verticeList.add(point1.x + (point2.x - point1.x)*ratio);
            verticeList.add(point1.y + (point2.y - point1.y)*ratio);
            verticeList.add(0f);
            alphaList.add(1f);
        }

    }

    public List<Point> getPoints() {
        return points;
    }

    public void refresh() {
        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {
                verticeList.add(points.get(0).x);
                verticeList.add(points.get(0).y);
                verticeList.add(0f);
                alphaList.add(1f);
            } else {
                insertSegment(i - 1, i);
            }

        }

        verticeBuffer = ByteBuffer.allocateDirect(verticeList.size() * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticeBuffer.put(toArray(verticeList)).position(0);

        alphaBuffer = ByteBuffer.allocateDirect(alphaList.size() * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        alphaBuffer.put(toArray(alphaList)).position(0);

    }

    public void draw(int verticeMatrixHandle, int alphaHandle, int pointSizeLocation) {
        GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                0, verticeBuffer);

        GLES20.glEnableVertexAttribArray(verticeMatrixHandle);

        GLES20.glVertexAttribPointer(alphaHandle, 1, GLES20.GL_FLOAT, false,
                0, alphaBuffer);

        GLES20.glEnableVertexAttribArray(alphaHandle);

        GLES20.glUniform1f(pointSizeLocation, width);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, verticeList.size()/3);
    }

    private static float[] toArray(List<Float> list) {
        float[] floatArray = new float[list.size()];
        int i = 0;

        for (Float f : list) {
            floatArray[i++] = (f != null ? f : Float.NaN);
        }

        return floatArray;
    }
}

