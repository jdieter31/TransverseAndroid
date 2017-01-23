package com.provectusstudios.transverse;

/**
 * Created by Justin Dieter on 7/4/2016.
 */
public class Coin {
    private static SolidRenderType coinRenderType;
    private static SolidRenderType coinTextRenderType;

    static {
        coinRenderType = new SolidRenderType();
        coinRenderType.setAlpha(1);
        coinRenderType.setColor(.773f, .702f, .345f);

        coinTextRenderType = new SolidRenderType();
        coinTextRenderType.setAlpha(1);
        coinTextRenderType.setColor(.95f, .95f, .95f);
    }

    private Circle circle;
    private Circle whiteOutline;
    private Text text;

    private float centerX;
    private float centerY;

    private float radius;

    public boolean containsPoint(float x, float y) {
        return circle.containsPoint(x, y);
    }

    public void setCenter(float x, float y) {
        centerX = x;
        centerY = y;
    }

    public void setSize(float radius) {
        this.radius = radius;
    }


    public void refresh() {
        circle = new Circle();
        circle.setCenter(centerX, centerY, 0);
        circle.setRadius(radius);
        circle.setPrecision(180);
        circle.refresh();

        whiteOutline = new Circle();
        whiteOutline.setCenter(centerX, centerY, 0);
        whiteOutline.setIsOutline(true);
        whiteOutline.setOutlineWidth(radius/7);
        whiteOutline.setRadius(radius - 3*radius/14);
        whiteOutline.setPrecision(180);
        whiteOutline.refresh();

        text = new Text();
        text.setText("C");
        text.setTextSize(radius);
        text.setFont("FFF Forward");
        text.setOrigin(centerX - text.getWidth()/2, centerY - radius/2, 0);
        text.refresh();
    }

    public void draw(float[] matrix) {
        coinRenderType.setMatrix(matrix);
        coinTextRenderType.setMatrix(matrix);

        coinRenderType.drawShape(circle);
        coinTextRenderType.drawText(text);
        coinTextRenderType.drawShape(whiteOutline);
    }


}
