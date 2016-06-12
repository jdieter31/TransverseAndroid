package com.provectusstudios.transverse;

public interface RenderType {
    void drawText(Text text);
    void drawShape(Shape shape);
    void setMatrix(float[] matrix);
    void drawImage(Image image);
    void drawAlphaShape(AlphaShape line);
    void setAlpha(float alpha);
}
