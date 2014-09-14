package com.provectusstudios.transverse;

import android.opengl.GLES20;

/**
 * Created by Justin on 8/10/2014.
 */
public class Shaders {

    public static int solidColorProgram;
    public static int imageProgram;
    public static int solidImageProgram;
    public static int solidLineProgram;

    public static int vsSolidColorHandle;
    public static int fsSolidColorHandle;
    public static int vsImageHandle;
    public static int fsImageHandle;
    public static int vsSolidImageHandle;
    public static int fsSolidImageHandle;
    public static int vsSolidLineHandle;
    public static int fsSolidLineHandle;

    /* SHADER Solid
     *
     * This shader is for rendering a colored primitive.
     *
     */
    public static final String vsSolidColorSource =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    public static final String fsSolidColorSource =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    public static final String vsImageSource =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 a_texCoord;" +
            "varying vec2 v_texCoord;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  v_texCoord = a_texCoord;" +
            "}";

    public static final String fsImageSource =
            "precision mediump float;" +
            "varying vec2 v_texCoord;" +
            "uniform sampler2D s_texture;" +
            "void main() {" +
            "  gl_FragColor = texture2D( s_texture, v_texCoord );" +
            "}";

    public static final String vsSolidImageSource =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 a_texCoord;" +
                    "varying vec2 v_texCoord;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  v_texCoord = a_texCoord;" +
                    "}";
    public static final String fsSolidImageSource =
            "precision mediump float;" +
            "varying vec2 v_texCoord;" +
            "uniform sampler2D s_texture;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  vec4 bitmapColor = texture2D( s_texture, v_texCoord );" +
            "  vec4 invertedBitmapColor = vec4(1.0 - bitmapColor.x, 1.0 -bitmapColor.y, 1.0 - bitmapColor.z, bitmapColor.w);" +
            "  gl_FragColor = invertedBitmapColor * vColor;" +
            "}";

    public static final String vsSolidLineSource =
            "uniform mat4 uMVPMatrix;" +     // A constant representing the combined model/view/projection matrix.
            "attribute vec4 vPosition;" +     // Per-vertex position information we will pass in.
            "attribute float aAlpha;" +
            "varying float vAlpha;" +     // This will be passed into the fragment shader.
            "void main() {" +     // The entry point for our vertex shader.
            "  vAlpha = aAlpha;" +    // Pass the color through to the fragment shader.
            "  gl_Position = uMVPMatrix * vPosition;" +    // gl_Position is a special variable used to store the final position.
            "}";    // normalized screen coordinates.

    public static final String fsSolidLineSource =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "varying float vAlpha;" +
            "void main() {" +
            "  gl_FragColor = vec4(vColor.x, vColor.y, vColor.z, vColor.w * vAlpha);" +
            "}";

    private static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // return the shader
        return shader;
    }

    public static void loadShaders() {
        // Create the shaders
        vsSolidColorHandle = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, vsSolidColorSource);
        fsSolidColorHandle = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, fsSolidColorSource);

        solidColorProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(solidColorProgram, vsSolidColorHandle);   // add the vertex shader to program
        GLES20.glAttachShader(solidColorProgram, fsSolidColorHandle); // add the fragment shader to program
        GLES20.glLinkProgram(solidColorProgram);                  // creates OpenGL ES program executables

        // Create the shaders
        vsImageHandle = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, vsImageSource);
        fsImageHandle = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, fsImageSource);

        imageProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(imageProgram, vsImageHandle);   // add the vertex shader to program
        GLES20.glAttachShader(imageProgram, fsImageHandle); // add the fragment shader to program
        GLES20.glLinkProgram(imageProgram);                  // creates OpenGL ES program executables

        // Create the shaders
        vsSolidImageHandle = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, vsSolidImageSource);
        fsSolidImageHandle = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, fsSolidImageSource);

        solidImageProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(solidImageProgram, vsSolidImageHandle);   // add the vertex shader to program
        GLES20.glAttachShader(solidImageProgram, fsSolidImageHandle); // add the fragment shader to program
        GLES20.glLinkProgram(solidImageProgram);                  // creates OpenGL ES program executables

        // Create the shaders
        vsSolidLineHandle = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, vsSolidLineSource);
        fsSolidLineHandle = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, fsSolidLineSource);

        solidLineProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(solidLineProgram, vsSolidLineHandle);   // add the vertex shader to program
        GLES20.glAttachShader(solidLineProgram, fsSolidLineHandle); // add the fragment shader to program
        GLES20.glLinkProgram(solidLineProgram);                  // creates OpenGL ES program executables
    }
}