package com.provectusstudios.transverse;

import android.opengl.GLES20;

public class SolidRenderType implements RenderType {
    private float red;
    private float green;
    private float blue;
    private float alpha;

    private float red2;
    private float green2;
    private float blue2;

    private float width;

    private boolean isDualColor = false;

    private float[] matrix;

    @Override
    public void drawText(Text text) {
        GLES20.glUseProgram(Shaders.solidImageProgram);

        // get handle to vertex shader's vPosition member
        int positionHandle = GLES20.glGetAttribLocation(Shaders.solidImageProgram, "vPosition");

        int mTexCoordLoc = GLES20.glGetAttribLocation(Shaders.solidImageProgram,
                "a_texCoord" );

        int mSamplerLoc = GLES20.glGetUniformLocation (Shaders.solidImageProgram,
                "s_texture" );

        // Get handle to shape's transformation matrix
        int matrixHandle = GLES20.glGetUniformLocation(Shaders.solidImageProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);

        int colorHandle = GLES20.glGetUniformLocation(Shaders.solidImageProgram, "vColor");

        GLES20.glUniform4f(colorHandle, red, green, blue, alpha);

        text.draw(positionHandle, mTexCoordLoc, mSamplerLoc);
    }

    public void drawImage(Image image) {
        GLES20.glUseProgram(Shaders.solidImageProgram);

        // get handle to vertex shader's vPosition member
        int positionHandle = GLES20.glGetAttribLocation(Shaders.solidImageProgram, "vPosition");

        int mTexCoordLoc = GLES20.glGetAttribLocation(Shaders.solidImageProgram,
                "a_texCoord" );

        int mSamplerLoc = GLES20.glGetUniformLocation (Shaders.solidImageProgram,
                "s_texture" );

        // Get handle to shape's transformation matrix
        int matrixHandle = GLES20.glGetUniformLocation(Shaders.solidImageProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);

        int colorHandle = GLES20.glGetUniformLocation(Shaders.solidImageProgram, "vColor");

        GLES20.glUniform4f(colorHandle, red, green, blue, alpha);

        image.draw(positionHandle, mTexCoordLoc, mSamplerLoc);
    }

    @Override
    public void drawAlphaShape(AlphaShape alphaShape) {
        if (isDualColor) {
            drawDualColorAlphaShape(alphaShape);
            return;
        }
        GLES20.glUseProgram(Shaders.solidLineProgram);

        int positionHandle = GLES20.glGetAttribLocation(Shaders.solidLineProgram, "vPosition");

        // Get handle to shape's transformation matrix
        int matrixHandle = GLES20.glGetUniformLocation(Shaders.solidLineProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);

        int colorHandle = GLES20.glGetUniformLocation(Shaders.solidLineProgram, "vColor");

        GLES20.glUniform4f(colorHandle, red, green, blue, alpha);

        int alphaHandle = GLES20.glGetAttribLocation(Shaders.solidLineProgram, "aAlpha");

        alphaShape.draw(positionHandle, alphaHandle);
    }

    private void drawDualColorAlphaShape(AlphaShape alphaShape) {
        GLES20.glUseProgram(Shaders.dualColorAlphaProgram);

        int positionHandle = GLES20.glGetAttribLocation(Shaders.dualColorAlphaProgram, "vPosition");

        // Get handle to shape's transformation matrix
        int matrixHandle = GLES20.glGetUniformLocation(Shaders.dualColorAlphaProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);

        int colorHandle = GLES20.glGetUniformLocation(Shaders.dualColorAlphaProgram, "vColor");

        GLES20.glUniform4f(colorHandle, red, green, blue, alpha);

        int colorHandle2 = GLES20.glGetUniformLocation(Shaders.dualColorAlphaProgram, "vColor2");

        GLES20.glUniform4f(colorHandle2, red2, green2, blue2, alpha);

        int alphaHandle = GLES20.glGetAttribLocation(Shaders.dualColorAlphaProgram, "aAlpha");

        int widthHandle = GLES20.glGetUniformLocation(Shaders.dualColorAlphaProgram, "width");

        GLES20.glUniform1f(widthHandle, width);

        alphaShape.draw(positionHandle, alphaHandle);
    }

    @Override
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public void drawShape(Shape shape) {
        if (isDualColor) {
            drawShapeDual(shape);
            return;
        }
        GLES20.glUseProgram(Shaders.solidColorProgram);

        int positionHandle = GLES20.glGetAttribLocation(Shaders.solidColorProgram, "vPosition");

        // Get handle to shape's transformation matrix
        int matrixHandle = GLES20.glGetUniformLocation(Shaders.solidColorProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);

        int colorHandle = GLES20.glGetUniformLocation(Shaders.solidColorProgram, "vColor");

        GLES20.glUniform4f(colorHandle, red, green, blue, alpha);

        shape.draw(positionHandle);
    }

    private void drawShapeDual(Shape shape) {
        GLES20.glUseProgram(Shaders.dualColorProgram);

        int positionHandle = GLES20.glGetAttribLocation(Shaders.dualColorProgram, "vPosition");

        // Get handle to shape's transformation matrix
        int matrixHandle = GLES20.glGetUniformLocation(Shaders.dualColorProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);

        int colorHandle = GLES20.glGetUniformLocation(Shaders.dualColorProgram, "vColor");

        GLES20.glUniform4f(colorHandle, red, green, blue, alpha);

        int colorHandle2 = GLES20.glGetUniformLocation(Shaders.dualColorProgram, "vColor2");

        GLES20.glUniform4f(colorHandle2, red2, green2, blue2, alpha);

        int widthHandle = GLES20.glGetUniformLocation(Shaders.dualColorProgram, "width");

        GLES20.glUniform1f(widthHandle, width);

        shape.draw(positionHandle);
    }


    @Override
    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

    public float getRed() {
        return red;
    }

    public float getBlue() {
        return blue;
    }

    public float getGreen() {
        return green;
    }

    public float getRed2() {
        return red2;
    }

    public float getBlue2() {
        return blue2;
    }

    public float getGreen2() {
        return green2;
    }

    public boolean isDualColor() {
        return isDualColor;
    }


    public void setColor(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public void setDualColor(float red2, float green2, float blue2, float width) {
        isDualColor = true;
        this.red2 = red2;
        this.green2 = green2;
        this.blue2 = blue2;
        this.width = width;
    }


}
