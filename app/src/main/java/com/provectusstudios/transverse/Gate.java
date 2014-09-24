package com.provectusstudios.transverse;

/**
 * Created by Justin on 9/21/2014.
 */
public class Gate {
    private float angle;
    private float width;

    private float centerX;
    private float centerY;

    private RenderType renderType;

    private Circle leftEnd;
    private Circle rightEnd;
    private Line gateLine;

    private boolean passed;

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setCenter(float x, float y) {
        this.centerX = x;
        this.centerY = y;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getWidth() {
        return width;
    }

    public void setRenderType(RenderType renderType) {
        this.renderType = renderType;
    }

    public void refresh() {
        gateLine = new Line();
        gateLine.setStartPoint((float) (centerX - width/2 * Math.cos(-angle)), (float) (centerY - width/2 * Math.sin(-angle)), 0);
        gateLine.setEndPoint((float) (centerX + width/2 * Math.cos(-angle)), (float) (centerY + width/2 * Math.sin(-angle)));
        gateLine.setWidth(3f);
        gateLine.refresh();
        leftEnd = new Circle();
        leftEnd.setPrecision(90);
        leftEnd.setRadius(4f);
        leftEnd.setCenter((float) (centerX - width/2 * Math.cos(-angle)), (float) (centerY - width/2 * Math.sin(-angle)), 0);
        leftEnd.refresh();
        rightEnd = new Circle();
        rightEnd.setPrecision(90);
        rightEnd.setRadius(4f);
        rightEnd.setCenter((float) (centerX + width/2 * Math.cos(-angle)), (float) (centerY + width/2 * Math.sin(-angle)), 0);
        rightEnd.refresh();
    }

    public void draw() {
        if (!passed) {
            renderType.drawAlphaShape(gateLine);
        }
        renderType.drawShape(leftEnd);
        renderType.drawShape(rightEnd);
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public boolean isPassed() {
        return passed;
    }

    public boolean lineCrosses(float startX, float startY, float endX, float endY) {
        float dx = endX - startX;
        float dy = endY - startY;
        float gateEndX = (float) (centerX + width/2 * Math.cos(-angle));
        float gateEndY = (float) (centerY + width/2 * Math.sin(-angle));
        float gateStartX = (float) (centerX - width/2 * Math.cos(-angle));
        float gateStartY = (float) (centerY - width/2 * Math.sin(-angle));
        float leftCornerLineBoxX = Math.min(startX, endX);
        float leftCornerLineBoxY = Math.min(startY, endY);
        float rightCornerLineBoxX = Math.max(startX, endX);
        float rightCornerLineBoxY = Math.max(startY, endY);
        float leftCornerGateBoxX = Math.min(gateStartX, gateEndX);
        float leftCornerGateBoxY = Math.min(gateStartY, gateEndY);
        float rightCornerGateBoxX = Math.max(gateStartX, gateEndX);
        float rightCornerGateBoxY = Math.max(gateStartY, gateEndY);
        float intersectRectangleLeftX;
        float intersectRectangleLeftY;
        float intersectRectangleRightX;
        float intersectRectangleRightY;
        if (!(leftCornerLineBoxX < rightCornerGateBoxX && rightCornerLineBoxX > leftCornerGateBoxX
                && leftCornerLineBoxY < rightCornerGateBoxY && rightCornerLineBoxY > leftCornerGateBoxY)) {
            return false;
        } else {
            intersectRectangleLeftX = Math.max(leftCornerLineBoxX, leftCornerGateBoxX);
            intersectRectangleLeftY = Math.max(leftCornerLineBoxY, leftCornerGateBoxY);
            intersectRectangleRightX = Math.min(rightCornerGateBoxX, rightCornerLineBoxX);
            intersectRectangleRightY = Math.min(rightCornerGateBoxY, rightCornerLineBoxY);
        }
        float gateDX = gateEndX - gateStartX;
        float gateDY = gateEndY - gateStartY;
        boolean equalSlopes = false;
        float gateSlope = gateDY/gateDX;
        float slope = dy/dx;
        if (dy == 0 && gateDY == 0) {
            equalSlopes = true;
        } else if (gateDX == 0 && dx == 0) {
            equalSlopes = true;
        } else if (slope == gateSlope) {
            equalSlopes = true;
        }
        if (equalSlopes) {
            if ((startX == gateStartX && startY == gateStartY)|| (endX == gateStartX && endY == gateStartY)) {
                return true;
            }
            return false;
        }
        float xIntersectLine;
        float yIntersectLine;
        float gateYIntercept = gateStartY - gateSlope*gateStartX;
        float lineYIntercept = startY - slope*startX;
        if (dx == 0) {
            xIntersectLine = startX;
            yIntersectLine = startX*gateSlope + gateYIntercept;
        } else if (gateDX == 0) {
            xIntersectLine = gateStartX;
            yIntersectLine = gateStartX*slope + lineYIntercept;
        } else {
            xIntersectLine = (gateYIntercept - lineYIntercept)/(slope - gateSlope);
            yIntersectLine = xIntersectLine*slope + lineYIntercept;
        }
        if (xIntersectLine >= intersectRectangleLeftX && xIntersectLine <= intersectRectangleRightX
                && yIntersectLine >= intersectRectangleLeftY && yIntersectLine <= intersectRectangleRightY) {
            return true;
        }
        return false;
    }

}
