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

    private static final int CAPACITY = 2000;

    private FloatBuffer verticeBuffer = ByteBuffer.allocateDirect(CAPACITY * 3 * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
    private FloatBuffer alphaBuffer = ByteBuffer.allocateDirect(CAPACITY * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();

    private int bufferPosition = 0;

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
            int i = 0;
            Float prevValue = Float.NaN;
            for (Float value : verticeList) {
                if (prevValue.equals(point.x) && value.equals(point.y)) {
                    index = (i-1)/3;
                    break;
                }
                prevValue = value;
                i++;
            }
            for (int k = 0; k <= index; k++) {
                verticeList.remove(0);
                verticeList.remove(0);
                verticeList.remove(0);
                bufferPosition++;
            }
            pointsRemoved++;
            if (pointsRemoved == numOfPoints) {
                break;
            }
        }

        for (int i = 0; i < numOfPoints; i++) {
            points.remove(0);
        }

    }

    public void addTopPoint(Point point) {
        points.add(point);

        if (points.size() == 1) {
            verticeList.add(points.get(0).x);
            verticeList.add(points.get(0).y);
            verticeList.add(0f);
            insertToBuffer(points.get(0).x, points.get(0).y, 0f, 1f);
        } else {
            insertSegment(points.size() - 2, points.size() - 1);
        }



    }

    public void setAlpha(int pointIndex, float alphaValue) {
        if (pointIndex == 0) {
            alphaBuffer.put(bufferPosition, alphaValue);
        } else {
            Point prevPoint = points.get(pointIndex - 1);
            Point point = points.get(pointIndex);
            boolean settingAlpha = false;
            Float prevValue = Float.NaN;
            int i = 0;
            for (Float value : verticeList) {
                if (prevValue.equals(prevPoint.x) && value.equals(prevPoint.y)) {
                    settingAlpha = true;
                }
                if (settingAlpha && i%3 == 0) {
                    alphaBuffer.put(bufferPosition + i/3, alphaValue);
                }
                if (prevValue.equals(point.x) && value.equals(point.y)) {
                    break;
                }
                prevValue = value;
                i++;
            }
        }
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
            verticeList.add(x);
            verticeList.add(y);
            verticeList.add(z);
            insertToBuffer(x, y, z, 1f);
        }

    }

    public List<Point> getPoints() {
        return points;
    }

    public void refresh() {
        Log.d("Transverse", "Refreshing");

        verticeList = new LinkedList<>();

        verticeBuffer = ByteBuffer.allocateDirect(CAPACITY * 3 * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        alphaBuffer = ByteBuffer.allocateDirect(CAPACITY * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        bufferPosition = 0;

        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {
                verticeList.add(points.get(0).x);
                verticeList.add(points.get(0).y);
                verticeList.add(0f);
                verticeList.add(1f);
                insertToBuffer(points.get(0).x, points.get(0).y, 0f, 1f);
            } else {
                insertSegment(i - 1, i);
            }

        }
    }

    public void insertToBuffer(float x, float y, float z, float alpha) {
        if (verticeList.size()/3 + bufferPosition > CAPACITY - 15) {
            alphaBuffer.position(bufferPosition);
            alphaBuffer.compact();
            verticeBuffer.position(bufferPosition*3);
            verticeBuffer.compact();
            bufferPosition = 0;
        }
        verticeBuffer.position(bufferPosition*3 + verticeList.size());
        verticeBuffer.put(x);
        verticeBuffer.put(y);
        verticeBuffer.put(z);
        alphaBuffer.position(bufferPosition + verticeList.size()/3);
        alphaBuffer.put(alpha);
        verticeBuffer.position(bufferPosition*3);
        alphaBuffer.position(bufferPosition);
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

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, verticeList.size()/3);
    }
}

