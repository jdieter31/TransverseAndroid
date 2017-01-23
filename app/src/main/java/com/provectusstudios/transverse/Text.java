package com.provectusstudios.transverse;

import android.opengl.GLES20;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

public class Text {

    private static Map<String, Font> fonts = new HashMap<>();

    private String text;

    private float originX;
    private float originY;
    private float originZ;

    private Font font;

    private float textSize;

    private float[] uvCoordinates;
    private float[] vertices;
    private short[] drawOrder;

    private int textureHandle;

    private FloatBuffer uvCoordinateBuffer;
    private FloatBuffer verticeBuffer;
    private ShortBuffer drawOrderBuffer;

    public void setText(String text) {
        this.text = text;
    }

    public void setOrigin(float x, float y, float z) {
        originX = x;
        originY = y;
        originZ = z;
    }

    public void setFont(String font) {
        this.font = fonts.get(font);
    }

    public float getWidth() {
        return font.widthOfString(text, textSize);
    }

    public float getHeight() {
        return font.heightOfString(text, textSize);
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void refresh() {
        textureHandle = font.textureHandle;
        font.calculateDataForString(text, textSize, originX, originY, originZ);
        uvCoordinates = font.getUVCoordinates();
        drawOrder = font.getDrawOrder();
        vertices = font.getVertices();

        verticeBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticeBuffer.put(vertices).position(0);

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

    public static void loadFont(XmlPullParser xml, int textureHandle, String name) {
        fonts.put(name, new Font(xml, textureHandle));
    }

    private static class Font {
        private int textureHandle;

        private int width;
        private int height;
        private int baseFontSize;

        private char[] chars;
        private int[] charHeights;
        private int[] charWidths;
        private int[] charX;
        private int[] charY;
        private int[] charXOffset;
        private int[] charYOffset;
        private int[] charXAdvance;

        private char[] kerningFirstChar = new char[0];
        private char[] kerningSecondChar = new char[0];
        private int[] kerningValue = new int[0];

        private float[] vertices;
        private float[] uvCoordinates;
        private short[] drawOrder;

        public Font(XmlPullParser xml, int textureHandle) {
            this.textureHandle = textureHandle;
            parseXML(xml);
        }

        private void parseXML(XmlPullParser xml) {
            int charIndex = 0;
            int kerningIndex = 0;
            try {
                int eventType = xml.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xml.getName().equals("chars")) {
                            int numOfChars = Integer.parseInt(xml.getAttributeValue(null, "count"));
                            chars = new char[numOfChars];
                            charHeights = new int[numOfChars];
                            charWidths = new int[numOfChars];
                            charX = new int[numOfChars];
                            charY = new int[numOfChars];
                            charXOffset = new int[numOfChars];
                            charYOffset = new int[numOfChars];
                            charXAdvance = new int[numOfChars];
                        }
                        if (xml.getName().equals("kernings")) {
                            int numOfKernings = Integer.parseInt(xml.getAttributeValue(null, "count"));
                            kerningFirstChar = new char[numOfKernings];
                            kerningSecondChar = new char[numOfKernings];
                            kerningValue = new int[numOfKernings];
                        } else if (xml.getName().equals("common")) {
                            width = Integer.parseInt(xml.getAttributeValue(null, "scaleW"));
                            height = Integer.parseInt(xml.getAttributeValue(null, "scaleH"));
                        } else if (xml.getName().equals("info")) {
                            baseFontSize = Integer.parseInt(xml.getAttributeValue(null, "size"));
                        } else if (xml.getName().equals("char")) {
                            chars[charIndex] = (char) Integer.parseInt(xml.getAttributeValue(null, "id"));
                            charHeights[charIndex] = Integer.parseInt(xml.getAttributeValue(null, "height"));
                            charWidths[charIndex] = Integer.parseInt(xml.getAttributeValue(null, "width"));
                            charX[charIndex] = Integer.parseInt(xml.getAttributeValue(null, "x"));
                            charY[charIndex] = Integer.parseInt(xml.getAttributeValue(null, "y"));
                            charXOffset[charIndex] = Integer.parseInt(xml.getAttributeValue(null, "xoffset"));
                            charYOffset[charIndex] = Integer.parseInt(xml.getAttributeValue(null, "yoffset"));
                            charXAdvance[charIndex] = Integer.parseInt(xml.getAttributeValue(null, "xadvance"));
                            charIndex++;
                        } else if (xml.getName().equals("kerning")) {
                            kerningFirstChar[kerningIndex] = (char) Integer.parseInt(xml.getAttributeValue(null, "first"));
                            kerningSecondChar[kerningIndex] = (char) Integer.parseInt(xml.getAttributeValue(null, "second"));
                            kerningValue[kerningIndex] = Integer.parseInt(xml.getAttributeValue(null, "amount"));
                            kerningIndex++;
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


        public int getTextureHandle() {
            return textureHandle;
        }

        private int kerningForChars(char firstChar, char secondChar) {
            for (int i = 0; i < kerningFirstChar.length; i++) {
                if (kerningFirstChar[i] == firstChar && kerningSecondChar[i] == secondChar) {
                    return kerningValue[i];
                }
            }
            return 0;
        }

        private int indexOfChar(char character) {
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == character) {
                    return i;
                }
            }
            return -1;
        }

        public float widthOfString(String string, float fontSize) {
            char[] charArray = string.toCharArray();
            int nonScaledWidth = 0;
            char prevChar = 0;
            for (int i = 0; i < charArray.length; i++) {
                char character = charArray[i];
                int indexOfChar = indexOfChar(character);
                if (indexOfChar != -1) {
                    if (i != charArray.length - 1) {
                        nonScaledWidth += charXAdvance[indexOfChar];
                    } else {
                        nonScaledWidth += charWidths[indexOfChar];
                    }
                    if (i != 0) {
                        int kerning = kerningForChars(prevChar, character);
                        nonScaledWidth += kerning;
                    }
                }
                prevChar = character;
            }
            return ((float) nonScaledWidth) * fontSize / ((float) baseFontSize);
        }

        public float heightOfString(String string, float fontSize) {
            char[] charArray = string.toCharArray();
            int nonScaledHeight = 0;
            for (char character : charArray) {
                int indexOfChar = indexOfChar(character);
                if (charHeights[indexOfChar] > nonScaledHeight) {
                    nonScaledHeight = charHeights[indexOfChar];
                }
            }
            return ((float) nonScaledHeight) * fontSize / ((float) baseFontSize);
        }

        public void calculateDataForString(String string, float fontSize, float originX, float originY, float originZ) {

            char[] charArray = string.toCharArray();
            //Three dimensions, four points per square
            vertices = new float[charArray.length*4*3];
            //Two dimensions, four points per square
            uvCoordinates = new float[charArray.length*4*2];
            //3 points per triangle, 2 triangles per square
            drawOrder = new short[charArray.length*6];

            char prevChar = 0;
            int xPosition = 0;
            for (int i = 0; i < charArray.length; i++) {
                char character = charArray[i];
                int characterIndex = indexOfChar(character);

                uvCoordinates[i*4*2] = ((float) charX[characterIndex])/((float) width);
                uvCoordinates[i*4*2 + 1] = ((float) charY[characterIndex])/((float) height);

                uvCoordinates[i*4*2 + 2] = ((float) charX[characterIndex])/((float) width);
                uvCoordinates[i*4*2 + 3] = ((float) (charY[characterIndex] + charHeights[characterIndex]))/((float) height);

                uvCoordinates[i*4*2 + 4] = ((float) (charX[characterIndex] + charWidths[characterIndex]))/((float) width);
                uvCoordinates[i*4*2 + 5] = ((float) (charY[characterIndex] + charHeights[characterIndex]))/((float) height);

                uvCoordinates[i*4*2 + 6] = ((float) (charX[characterIndex] + charWidths[characterIndex]))/((float) width);
                uvCoordinates[i*4*2 + 7] = ((float) charY[characterIndex])/((float) height);
                drawOrder[i*6] = (short) (i*4);
                drawOrder[i*6 + 1] = (short) (i*4 + 1);
                drawOrder[i*6 + 2] = (short) (i*4 + 2);
                drawOrder[i*6 + 3] = (short) (i*4);
                drawOrder[i*6 + 4] = (short) (i*4 + 2);
                drawOrder[i*6 + 5] = (short) (i*4 + 3);
                if (i != 0) {
                    int kerning = kerningForChars(prevChar, character);
                    xPosition += kerning;
                }
                vertices[i*4*3] = originX + (xPosition + charXOffset[characterIndex]) * (fontSize / ((float) (baseFontSize)));
                vertices[i*4*3 + 1] = originY + charYOffset[characterIndex] * (fontSize / ((float) (baseFontSize)));
                vertices[i*4*3 + 2] = originZ;
                vertices[i*4*3 + 3] = originX + (xPosition + charXOffset[characterIndex]) * (fontSize / ((float) (baseFontSize)));
                vertices[i*4*3 + 4] = originY + (charYOffset[characterIndex] + charHeights[characterIndex]) * (fontSize / ((float) (baseFontSize)));
                vertices[i*4*3 + 5] = originZ;
                vertices[i*4*3 + 6] = originX + (xPosition + charXOffset[characterIndex] + charWidths[characterIndex]) * (fontSize / ((float) (baseFontSize)));
                vertices[i*4*3 + 7] = originY + (charYOffset[characterIndex] + charHeights[characterIndex]) * (fontSize / ((float) (baseFontSize)));
                vertices[i*4*3 + 8] = originZ;
                vertices[i*4*3 + 9] = originX + (xPosition + charXOffset[characterIndex] + charWidths[characterIndex]) * (fontSize / ((float) (baseFontSize)));
                vertices[i*4*3 + 10] = originY + charYOffset[characterIndex] * (fontSize / ((float) (baseFontSize)));
                vertices[i*4*3 + 11] = originZ;
                xPosition += charXAdvance[characterIndex];
                prevChar = character;
            }
        }

        public float[] getVertices() {
            return vertices;
        }

        public float[] getUVCoordinates() {
            return uvCoordinates;
        }

        public short[] getDrawOrder() {
            return drawOrder;
        }
    }
}
