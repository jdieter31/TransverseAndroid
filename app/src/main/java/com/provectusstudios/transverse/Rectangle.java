package com.provectusstudios.transverse;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Justin on 8/16/2014.
 */
public class Rectangle implements Shape {
    private float x;
    private float y;
    private float z;
    private float width;
    private float height;

    private float[] vertices;
    private short[] drawOrder;

    private FloatBuffer verticeBuffer;
    private ShortBuffer drawOrderBuffer;

    public void setOrigin(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public void refresh() {
        drawOrder = new short[] {
                0,1,2,0,2,3
        };
        vertices = new float[] {
                x, y, z,
                x, y + height, z,
                x + width, y + height, z,
                x + width, y, z
        };
        verticeBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        verticeBuffer.put(vertices).position(0);

        drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();

        drawOrderBuffer.put(drawOrder).position(0);
    }

    @Override
    public void draw(int verticeMatrixHandle) {
        GLES20.glVertexAttribPointer(verticeMatrixHandle, 3, GLES20.GL_FLOAT, false,
                0, verticeBuffer);

        GLES20.glEnableVertexAttribArray(verticeMatrixHandle);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);
    }
}
