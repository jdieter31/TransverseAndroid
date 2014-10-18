package com.provectusstudios.transverse;

/**
 * Created by Justin on 9/21/2014.
 */
public class Gate {

    private float angle;
    private float gateLength;

    private float centerGateX;
    private float centerGateY;

    private float startX;
    private float endX;

    private boolean inverted;

    private Line leftLine;
    private Line rightLine;

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setGateLength(float gateLength) {
        this.gateLength = gateLength;
    }

    public void setGateCenter(float x, float y) {
        this.centerGateX = x;
        this.centerGateY = y;
    }

    public void setEndXPoints(float startX, float endX) {
        this.startX = startX;
        this.endX = endX;
    }

    public float getGateCenterX() {
        return centerGateX;
    }

    public float getGateCenterY() {
        return centerGateY;
    }

    public float getGateLength() {
        return gateLength;
    }

    public float getAngle() {
        return angle;
    }

    public float getHeight() {
        return (float) Math.abs((endX - startX) * Math.tan(angle));
    }

    public float getWidth() {
        return endX - startX;
    }

    public float getStartX() {
        return startX;
    }

    public float getMinY() {
        return (float) (centerGateY - Math.abs(Math.tan(angle) * (centerGateX - startX)));
    }

    public void refresh() {
        if (!inverted) {
            leftLine = new Line();
            leftLine.setStartPoint(startX - 2.5f, (float) (centerGateY - (centerGateX - startX) * Math.tan(-angle)), 0);
            leftLine.setEndPoint((float) (centerGateX - gateLength / 2 * Math.cos(-angle)), (float) (centerGateY - gateLength / 2 * Math.sin(-angle)));
            leftLine.setWidth(5f);
            leftLine.refresh();

            rightLine = new Line();
            rightLine.setStartPoint((float) (centerGateX + gateLength / 2 * Math.cos(-angle)), (float) (centerGateY + gateLength / 2 * Math.sin(-angle)), 0);
            rightLine.setEndPoint(endX + 2.5f, (float) (centerGateY + (endX - centerGateX) * Math.tan(-angle)));
            rightLine.setWidth(5f);
            rightLine.refresh();
        } else {
            leftLine = new Line();
            leftLine.setStartPoint((float) (centerGateX - gateLength / 2 * Math.cos(-angle)), (float) (centerGateY - gateLength / 2 * Math.sin(-angle)), 0);
            leftLine.setEndPoint((float) (centerGateX + gateLength / 2 * Math.cos(-angle)), (float) (centerGateY + gateLength / 2 * Math.sin(-angle)));
            leftLine.setWidth(5f);
            leftLine.refresh();
        }
    }

    public void draw(RenderType renderType) {
        renderType.drawAlphaShape(leftLine);
        if (!inverted) {
            renderType.drawAlphaShape(rightLine);
        }
    }

    public boolean lineSegmentCrosses(float startX, float startY, float endX, float endY) {
        if (leftLine.lineSegmentCrosses(startX, startY, endX, endY)) {
            return true;
        }
        if (!inverted) {
            if (rightLine.lineSegmentCrosses(startX, startY, endX, endY)) {
                return true;
            }
        }
        return false;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

}
