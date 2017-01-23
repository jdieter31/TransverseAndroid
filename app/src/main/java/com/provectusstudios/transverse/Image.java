package com.provectusstudios.transverse;

import android.opengl.GLES20;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Justin on 8/11/2014.
 */
public class Image {
    private static final int ATLAS_WIDTH = 514;
    private static final int ATLAS_HEIGHT = 473;


    private static List<String> images = new ArrayList<>();
    private static List<Integer> startX = new ArrayList<>();
    private static List<Integer> startY = new ArrayList<>();
    private static List<Integer> width = new ArrayList<>();
    private static List<Integer> height = new ArrayList<>();

    private static int textureHandle;

    public static void loadFromXML(int textureHandle, XmlPullParser xml) {
        Image.textureHandle = textureHandle;
        try {
            int eventType = xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xml.getName().equals("subtexture")) {
                        String name = xml.getAttributeValue(null, "name");
                        int startX = Integer.parseInt(xml.getAttributeValue(null, "x"));
                        int startY = Integer.parseInt(xml.getAttributeValue(null, "y"));
                        int width = Integer.parseInt(xml.getAttributeValue(null, "width"));
                        int height = Integer.parseInt(xml.getAttributeValue(null, "height"));
                        images.add(name);
                        Image.startX.add(startX);
                        Image.startY.add(startY);
                        Image.width.add(width);
                        Image.height.add(height);
                    }
                }
                eventType = xml.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float[] uvCoordinates;
    private float[] vertices;
    private short[] drawOrder;

    private String image;

    private FloatBuffer uvCoordinateBuffer;
    private FloatBuffer verticeBuffer;
    private ShortBuffer drawOrderBuffer;

    public void setImage(String image) {
        this.image = image;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public void refresh() {
        verticeBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticeBuffer.put(vertices).position(0);

        int index = images.indexOf(image);

        drawOrder = new short[] {
                0,1,2,0,2,3
        };
        uvCoordinates = new float[]{
                ((float) startX.get(index))/ATLAS_WIDTH, ((float) startY.get(index))/ATLAS_HEIGHT,
                ((float) startX.get(index))/ATLAS_WIDTH, ((float) startY.get(index) + height.get(index))/ATLAS_HEIGHT,
                ((float) startX.get(index) + width.get(index))/ATLAS_WIDTH, ((float) startY.get(index) + height.get(index))/ATLAS_HEIGHT,
                ((float) startX.get(index) + width.get(index))/ATLAS_WIDTH, ((float) startY.get(index))/ATLAS_HEIGHT,
        };

        drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        drawOrderBuffer.put(drawOrder).position(0);

        uvCoordinateBuffer = ByteBuffer.allocateDirect(uvCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        uvCoordinateBuffer.put(uvCoordinates).position(0);
    }

    public void draw(int verticeHandle, int uvCoordinateHandle, int textureVariableHandle) {

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(verticeHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(verticeHandle, 3,
                GLES20.GL_FLOAT, false,
                0, verticeBuffer);

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray (uvCoordinateHandle);

        // Prepare the texture coordinates
        GLES20.glVertexAttribPointer (uvCoordinateHandle, 2, GLES20.GL_FLOAT,
                false,
                0, uvCoordinateBuffer);

        GLES20.glUniform1i (textureVariableHandle, textureHandle);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(verticeHandle);
        GLES20.glDisableVertexAttribArray(uvCoordinateHandle);
    }
}
