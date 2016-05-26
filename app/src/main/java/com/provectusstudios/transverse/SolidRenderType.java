package com.provectusstudios.transverse;

import android.opengl.GLES20;

public class SolidRenderType implements RenderType {
    private float red;
    private float green;
    private float blue;
    private float alpha;

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

    @Override
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public void drawPath(Path path) {
        GLES20.glUseProgram(Shaders.pathProgram);

        int positionHandle = GLES20.glGetAttribLocation(Shaders.pathProgram, "vPosition");

        // Get handle to shape's transformation matrix
        int matrixHandle = GLES20.glGetUniformLocation(Shaders.pathProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);

        int colorHandle = GLES20.glGetUniformLocation(Shaders.pathProgram, "vColor");

        GLES20.glUniform4f(colorHandle, red, green, blue, alpha);

        int alphaHandle = GLES20.glGetAttribLocation(Shaders.pathProgram, "aAlpha");

        int mSamplerLoc = GLES20.glGetUniformLocation(Shaders.pathProgram, "texture");

        GLES20.glUniform1i(mSamplerLoc, Textures.particleTexture);

        int pointSizeLoc = GLES20.glGetUniformLocation(Shaders.pathProgram, "pointSize");

        path.draw(positionHandle, alphaHandle, pointSizeLoc);
    }

    @Override
    public void drawShape(Shape shape) {
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

    public void setColor(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

}
