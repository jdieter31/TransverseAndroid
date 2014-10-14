package com.provectusstudios.transverse;

/**
 * Created by Justin on 9/21/2014.
 */
public class Gate {

    private float angle;
    private float gateLength;

    private float gateThickness = 5f;

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
            leftLine.setWidth(gateThickness);
            leftLine.refresh();

            rightLine = new Line();
            rightLine.setStartPoint((float) (centerGateX + gateLength / 2 * Math.cos(-angle)), (float) (centerGateY + gateLength / 2 * Math.sin(-angle)), 0);
            rightLine.setEndPoint(endX + 2.5f, (float) (centerGateY + (endX - centerGateX) * Math.tan(-angle)));
            rightLine.setWidth(gateThickness);
            rightLine.refresh();
        } else {
            leftLine = new Line();
            leftLine.setStartPoint((float) (centerGateX - gateLength / 2 * Math.cos(-angle)), (float) (centerGateY - gateLength / 2 * Math.sin(-angle)), 0);
            leftLine.setEndPoint((float) (centerGateX + gateLength / 2 * Math.cos(-angle)), (float) (centerGateY + gateLength / 2 * Math.sin(-angle)));
            leftLine.setWidth(gateThickness);
            leftLine.refresh();
        }
    }

    public void draw(RenderType renderType) {
        renderType.drawAlphaShape(leftLine);
        if (!inverted) {
            renderType.drawAlphaShape(rightLine);
        }
    }

    public boolean lineCrosses(float startX, float startY, float endX, float endY) {
        float dx = endX - startX;
        float dy = endY - startY;
        float gateEndX = (float) (centerGateX + gateLength /2 * Math.cos(-angle));
        float gateEndY = (float) (centerGateY + gateLength /2 * Math.sin(-angle));
        float gateStartX = (float) (centerGateX - gateLength /2 * Math.cos(-angle));
        float gateStartY = (float) (centerGateY - gateLength /2 * Math.sin(-angle));
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

    public void setGateThickness(float thickness) {
        this.gateThickness = thickness;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

}
