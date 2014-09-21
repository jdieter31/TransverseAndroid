package com.provectusstudios.transverse;

/**
 * Created by Justin on 8/16/2014.
 */
public interface RenderType {
    public void drawText(Text text);
    public void drawShape(Shape shape);
    public void setMatrix(float[] matrix);
    public void drawImage(Image image);
    public void drawAlphaShape(AlphaShape line);
    public void setAlpha(float alpha);
}
