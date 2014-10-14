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

    private FloatBuffer verticeBuffer = ByteBuffer.allocateDirect(0)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
    private FloatBuffer alphaBuffer = ByteBuffer.allocateDirect(0)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
    private ShortBuffer drawOrderBuffer = ByteBuffer.allocateDirect(0)
            .order(ByteOrder.nativeOrder()).asShortBuffer();
    private int drawOrderLength;

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
            drawOrder = new short[0];
            drawOrderBuffer = ByteBuffer.allocateDirect(0)
                    .order(ByteOrder.nativeOrder()).asShortBuffer();
            drawOrderLength = 0;
            return;
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

    public void setAlpha(int pointIndex, float alphaValue) {
        float prevAlpha = 0;
        if (pointIndex != 0) {
            prevAlpha = alpha[(pointIndex - 1)*8 + 3];
        }
        alpha[pointIndex*8] = 0;
        alpha[pointIndex*8 + 1] = 0;
        alpha[pointIndex*8 + 2] = prevAlpha;
        alpha[pointIndex*8 + 3] = alphaValue;
        alpha[pointIndex*8 + 4] = prevAlpha;
        alpha[pointIndex*8 + 5] = alphaValue;
        alpha[pointIndex*8 + 6] = 0;
        alpha[pointIndex*8 + 7] = 0;
        alphaBuffer.put(pointIndex*8 + 2, prevAlpha);
        alphaBuffer.put(pointIndex*8 + 3, alphaValue);
        alphaBuffer.put(pointIndex*8 + 4, prevAlpha);
        alphaBuffer.put(pointIndex*8 + 5, alphaValue);
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
        float magnitude = (float) Math.sqrt(Math.pow(endX - x, 2) + Math.pow(endY - y, 2));
        float directionX = (endX - x)/magnitude;
        float directionY = (endY - y)/magnitude;
        float perpendicularX = -directionY;
        float perpendicularY = directionX;
        if (index != 0) {
            vertices[index*24] = vertices[(index-1)*24 + 3];
            vertices[index*24 + 1] = vertices[(index - 1)*24 + 4];
            vertices[index*24 + 2] = 0;
            vertices[index*24 + 6] = vertices[(index-1)*24 + 9];
            vertices[index*24 + 7] = vertices[(index - 1)*24 + 10];
            vertices[index*24 + 8] = 0;
            vertices[index*24 + 12] = vertices[(index-1)*24 + 15];
            vertices[index*24 + 13] = vertices[(index - 1)*24 + 16];
            vertices[index*24 + 14] = 0;
            vertices[index*24 + 18] = vertices[(index-1)*24 + 21];
            vertices[index*24 + 19] = vertices[(index - 1)*24 + 22];
            vertices[index*24 + 20] = 0;
        } else {
            vertices[index*24] = x - perpendicularX*width/2;
            vertices[index*24 + 1] = y - perpendicularY*width/2;
            vertices[index*24 + 2] = 0;
            vertices[index*24 + 6] = x - perpendicularX*width/4;
            vertices[index*24 + 7] = y - perpendicularY*width/4;
            vertices[index*24 + 8] = 0;
            vertices[index*24 + 12] = x + perpendicularX*width/4;
            vertices[index*24 + 13] = y + perpendicularY*width/4;
            vertices[index*24 + 14] = 0;
            vertices[index*24 + 18] = x + perpendicularX*width/2;
            vertices[index*24 + 19] = y + perpendicularY*width/2;
            vertices[index*24 + 20] = 0;
        }
        vertices[index*24 + 3] = endX - perpendicularX*width/2;
        vertices[index*24 + 4] = endY - perpendicularY*width/2;
        vertices[index*24 + 5] = 0;
        vertices[index*24 + 9] = endX - perpendicularX*width/4;
        vertices[index*24 + 10] = endY - perpendicularY*width/4;
        vertices[index*24 + 11] = 0;
        vertices[index*24 + 15] = endX + perpendicularX*width/4;
        vertices[index*24 + 16] = endY + perpendicularY*width/4;
        vertices[index*24 + 17] = 0;
        vertices[index*24 + 21] = endX + perpendicularX*width/2;
        vertices[index*24 + 22] = endY + perpendicularY*width/2;
        vertices[index*24 + 23] = 0;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void refresh() {
        drawOrder = new short[18*(points.size()-1)];
        vertices = new float[24*(points.size()-1)];
        alpha = new float[8*(points.size() - 1)];
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

    public void setInverted(boolean inverted) {

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
    }
}

