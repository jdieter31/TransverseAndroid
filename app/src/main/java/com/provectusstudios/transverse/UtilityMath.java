package com.provectusstudios.transverse;

/**
 * Created by Justin on 10/17/2014.
 */
public class UtilityMath {
    public static boolean lineSegmentsCross(float startX, float startY, float endX, float endY,
                                            float secondStartX, float secondStartY,
                                            float secondEndX, float secondEndY) {
        float dx = endX - startX;
        float dy = endY - startY;
        float leftCornerLineBoxX = Math.min(startX, endX);
        float leftCornerLineBoxY = Math.min(startY, endY);
        float rightCornerLineBoxX = Math.max(startX, endX);
        float rightCornerLineBoxY = Math.max(startY, endY);
        float leftCornerGateBoxX = Math.min(secondStartX, secondEndX);
        float leftCornerGateBoxY = Math.min(secondStartY, secondEndY);
        float rightCornerGateBoxX = Math.max(secondStartX, secondEndX);
        float rightCornerGateBoxY = Math.max(secondStartY, secondEndY);
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
        float gateDX = secondEndX - secondStartX;
        float gateDY = secondEndY - secondStartY;
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
            if ((startX == secondStartX && startY == secondStartY)|| (endX == secondStartX && endY == secondStartY)) {
                return true;
            }
            return false;
        }
        float xIntersectLine;
        float yIntersectLine;
        float gateYIntercept = secondStartY - gateSlope*secondStartX;
        float lineYIntercept = startY - slope*startX;
        if (dx == 0) {
            xIntersectLine = startX;
            yIntersectLine = startX*gateSlope + gateYIntercept;
        } else if (gateDX == 0) {
            xIntersectLine = secondStartX;
            yIntersectLine = secondStartX*slope + lineYIntercept;
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

    public static boolean lineSegmentCrossesCircle(float startX, float startY, float endX, float endY,
                                             float centerX, float centerY, float radius) {
        //TODO
        return false;
    }
}
