package com.provectusstudios.transverse;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 9/15/2014.
 */
public class Path implements AlphaShape {
    public static class Point {
        public float x;
        public float y;
    }

    public List<Point> points = new ArrayList<Point>();

    private float width;

    private float[] vertices = new float[0];
    private short[] drawOrder = new short[0];
    private float[] alpha = new float[0];

    private FloatBuffer verticeBuffer;
    private FloatBuffer alphaBuffer;
    private ShortBuffer drawOrderBuffer;
    private int drawOrderLength;

    private class Corner {
        private float[] outlineVertices;
        private float[] outlineAlpha;
        private float[] centerVertices;
        private float[] centerAlpha;
        private FloatBuffer outlineVerticeBuffer;
        private FloatBuffer outlineAlphaBuffer;
        private FloatBuffer centerVerticeBuffer;
        private FloatBuffer centerAlphaBuffer;
        private final int precision = 5;

        public Corner(float centerX, float centerY, float width) {
            float innerCircleRadius = width/4;
            centerVertices = new float[precision * 3 + 6];
            centerVertices[0] = centerX;
            centerVertices[1] = centerY;
            centerVertices[2] = 0;
            centerAlpha = new float[precision + 2];
            centerAlpha[0] = 1;
            for (int i = 0; i <= precision; i++) {
                centerVertices[(i + 1) * 3] = (float) (innerCircleRadius * Math.cos(((float) i) / precision * Math.PI * 2) + centerX);
                centerVertices[(i + 1) * 3 + 1] = (float) (innerCircleRadius * Math.sin(((float) i) / precision * Math.PI * 2) + centerY);
                centerVertices[(i + 1) * 3 + 2] = 0;
                centerAlpha[i + 1] = 1;
            }
            centerVerticeBuffer = ByteBuffer.allocateDirect(centerVertices.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            centerVerticeBuffer.put(centerVertices).position(0);
            centerAlphaBuffer = ByteBuffer.allocateDirect(centerAlpha.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            centerAlphaBuffer.put(centerAlpha).position(0);

            outlineVertices = new float[precision * 3 * 2 + 6];
            outlineAlpha = new float[precision*2 + 2];
            for (int i = 0; i <= precision; i++)  {
                outlineVertices[i * 3 * 2] = (float) ((innerCircleRadius) * Math.cos(((float) i) / precision * 2 * Math.PI) + centerX);
                outlineVertices[i * 3 * 2 + 1] = (float) ((innerCircleRadius) * Math.sin(((float) i) / precision * 2 * Math.PI) + centerY);
                outlineVertices[i * 3 * 2 + 2] = 0;
                outlineAlpha[i*2] = 1;
                outlineVertices[i * 3 * 2 + 3] = (float) ((width/2) * Math.cos(((float) i) / precision * 2 * Math.PI) + centerX);
                outlineVertices[i * 3 * 2 + 4] = (float) ((width/2) * Math.sin(((float) i) / precision * 2 * Math.PI) + centerY);
                outlineVertices[i * 3 * 2 + 5] = 0;
                outlineAlpha[i*2 + 1] = 0;
            }
            outlineVerticeBuffer = ByteBuffer.allocateDirect(outlineVertices.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            outlineVerticeBuffer.put(outlineVertices).position(0);
            outlineAlphaBuffer = ByteBuffer.allocateDirect(outlineAlpha.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            outlineAlphaBuffer.put(outlineAlpha).position(0);
        }


        public void draw(int verticeMatrixHandle, int alphaHandle) {
            GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                    0, centerVerticeBuffer);

            GLES20.glEnableVertexAttribArray(verticeMatrixHandle);

            GLES20.glVertexAttribPointer(alphaHandle, 1, GLES20.GL_FLOAT, false,
                    0, centerAlphaBuffer);

            GLES20.glEnableVertexAttribArray(alphaHandle);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, precision + 2);


            GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                    0, outlineVerticeBuffer);

            GLES20.glEnableVertexAttribArray(verticeMatrixHandle);

            GLES20.glVertexAttribPointer(alphaHandle, 1, GLES20.GL_FLOAT, false,
                    0, outlineAlphaBuffer);

            GLES20.glEnableVertexAttribArray(alphaHandle);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, precision*2 + 2);
        }
    }

    private Corner[] corners;

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
        float[] newVertices = new float[24*(points.size() - 1)];
        for (int i = 24*numOfPoints; i < vertices.length; i++) {
            newVertices[i - 24*numOfPoints] = vertices[i];
        }
        vertices = newVertices;
        verticeBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticeBuffer.put(vertices).position(0);

        short[] newDrawOrder = new short[18*(points.size()-1)];
        for (int i = 18*numOfPoints; i < drawOrder.length; i++) {
            newDrawOrder[i - 18*numOfPoints] = (short) (drawOrder[i] - 8*numOfPoints);
        }
        drawOrder = newDrawOrder;

        drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        drawOrderBuffer.put(drawOrder).position(0);
        drawOrderLength = drawOrder.length;

        float[] newAlpha = new float[8*(points.size() - 1)];
        for (int i = 8*numOfPoints; i < alpha.length; i++) {
            newAlpha[i - 8*numOfPoints] = alpha[i];
        }
        alpha = newAlpha;
        alphaBuffer = ByteBuffer.allocateDirect(alpha.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        alphaBuffer.put(alpha).position(0);

        if (points.size() > 2) {
            Corner[] newCorners = new Corner[points.size() - 2];
            for (int i = numOfPoints; i < corners.length; i++) {
                newCorners[i - numOfPoints] = corners[i];
            }
            corners = newCorners;
        } else {
            corners = new Corner[0];
        }
    }

    public void addTopPoint(Point point) {
        points.add(point);
        short[] newDrawOrder = new short[18*(points.size()-1)];
        for (int i = 0; i < drawOrder.length; i++) {
            newDrawOrder[i] = drawOrder[i];
        }
        drawOrder = newDrawOrder;
        float[] newVertices = new float[24*(points.size()-1)];
        for (int i = 0; i < vertices.length; i++) {
            newVertices[i] = vertices[i];
        }
        vertices = newVertices;
        float[] newAlpha = new float[8*(points.size() - 1)];
        for (int i = 0; i < alpha.length; i++) {
            newAlpha[i] = alpha[i];
        }
        alpha = newAlpha;
        if (points.size() > 2) {
            Corner[] newCorners = new Corner[points.size() - 2];
            for (int i = 0; i < corners.length; i++) {
                newCorners[i] = corners[i];
            }
            corners = newCorners;
        } else {
            corners = new Corner[0];
        }
        if (points.size() > 1) {
            insertSegment(points.size()-2);
        }

        verticeBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticeBuffer.put(vertices).position(0);

        alphaBuffer = ByteBuffer.allocateDirect(alpha.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        alphaBuffer.put(alpha).position(0);

        drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        drawOrderBuffer.put(drawOrder).position(0);
        drawOrderLength = drawOrder.length;

    }

    private void insertSegment(int index) {
        drawOrder[index*18] = (short) (index*8);
        drawOrder[index*18 + 1] = (short) (index*8 + 1);
        drawOrder[index*18 + 2] = (short) (index*8 + 3);
        drawOrder[index*18 + 3] = (short) (index*8);
        drawOrder[index*18 + 4] = (short) (index*8 + 3);
        drawOrder[index*18 + 5] = (short) (index*8 + 2);

        drawOrder[index*18 + 6] = (short) (index*8 + 2);
        drawOrder[index*18 + 7] = (short) (index*8 + 3);
        drawOrder[index*18 + 8] = (short) (index*8 + 5);
        drawOrder[index*18 + 9] = (short) (index*8 + 2);
        drawOrder[index*18 + 10] = (short) (index*8 + 5);
        drawOrder[index*18 + 11] = (short) (index*8 + 4);

        drawOrder[index*18 + 12] = (short) (index*8 + 4);
        drawOrder[index*18 + 13] = (short) (index*8 + 5);
        drawOrder[index*18 + 14] = (short) (index*8 + 7);
        drawOrder[index*18 + 15] = (short) (index*8 + 4);
        drawOrder[index*18 + 16] = (short) (index*8 + 7);
        drawOrder[index*18 + 17] = (short) (index*8 + 6);

        alpha[index*8] = 0;
        alpha[index*8 + 1] = 0;
        alpha[index*8 + 2] = 1;
        alpha[index*8 + 3] = 1;
        alpha[index*8 + 4] = 1;
        alpha[index*8 + 5] = 1;
        alpha[index*8 + 6] = 0;
        alpha[index*8 + 7] = 0;

        Point point = points.get(index);
        Point nextPoint = points.get(index + 1);
        float x = point.x;
        float y = point.y;
        float endX =  nextPoint.x;
        float endY = nextPoint.y;
        float widthX;
        float widthY;
        if ((endY - y) == 0) {
            widthY = 0;
            widthX = width/2;
        } else {
            float slope = -1f / ((endY - y) / (endX - x));
            widthX = (float) Math.sqrt(Math.pow(width / 2, 2) / (1 + Math.pow(slope, 2)));
            widthY = widthX * slope;
        }
        vertices[index*24] = x - widthX;
        vertices[index*24 + 1] = y - widthY;
        vertices[index*24 + 2] = 0;
        vertices[index*24 + 3] = endX - widthX;
        vertices[index*24 + 4] = endY - widthY;
        vertices[index*24 + 5] = 0;
        vertices[index*24 + 6] = x - widthX/2;
        vertices[index*24 + 7] = y - widthY/2;
        vertices[index*24 + 8] = 0;
        vertices[index*24 + 9] = endX - widthX/2;
        vertices[index*24 + 10] = endY - widthY/2;
        vertices[index*24 + 11] = 0;
        vertices[index*24 + 12] = x + widthX/2;
        vertices[index*24 + 13] = y + widthY/2;
        vertices[index*24 + 14] = 0;
        vertices[index*24 + 15] = endX + widthX/2;
        vertices[index*24 + 16] = endY + widthY/2;
        vertices[index*24 + 17] = 0;
        vertices[index*24 + 18] = x + widthX;
        vertices[index*24 + 19] = y + widthY;
        vertices[index*24 + 20] = 0;
        vertices[index*24 + 21] = endX + widthX;
        vertices[index*24 + 22] = endY + widthY;
        vertices[index*24 + 23] = 0;

        if (index != 0 && points.size() > 2) {
            Point lastPoint = points.get(index-1);
            boolean sameSlope = false;
            float lastSlope = (y - lastPoint.y)/(x - lastPoint.x);
            float slope = (endY - y)/(endX - x);
            if (lastSlope == 0 && slope == 0) {
                if (x - lastPoint.x == 0 && endX - x == 0) {
                    sameSlope = true;
                } else if (x - lastPoint.x != 0 && endX - x != 0) {
                    sameSlope = true;
                }
            } else if (slope == lastSlope) {
                sameSlope = true;
            }
            if (!sameSlope) {
                corners[index - 1] = new Corner(x, y, width);
            }
        }
    }

    public List<Point> getPoints() {
        return points;
    }

    public void refresh() {
        drawOrder = new short[18*(points.size()-1)];
        vertices = new float[24*(points.size()-1)];
        alpha = new float[8*(points.size() - 1)];
        if (points.size() > 2) {
            corners = new Corner[points.size() - 2];
        } else {
            corners = new Corner[0];
        }
        for (int i = 0; i < points.size() - 1; i++) {
            insertSegment(i);
        }

        verticeBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticeBuffer.put(vertices).position(0);

        alphaBuffer = ByteBuffer.allocateDirect(alpha.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        alphaBuffer.put(alpha).position(0);

        drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        drawOrderBuffer.put(drawOrder).position(0);
        drawOrderLength = drawOrder.length;
    }

    public void draw(int verticeMatrixHandle, int alphaHandle) {
        GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                0, verticeBuffer);

        GLES20.glEnableVertexAttribArray(verticeMatrixHandle);

        GLES20.glVertexAttribPointer(alphaHandle, 1, GLES20.GL_FLOAT, false,
                0, alphaBuffer);

        GLES20.glEnableVertexAttribArray(alphaHandle);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrderLength,
                GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);

        for (Corner corner : corners) {
            if (corner != null) {
                corner.draw(verticeMatrixHandle, alphaHandle);
            }
        }
    }
}

