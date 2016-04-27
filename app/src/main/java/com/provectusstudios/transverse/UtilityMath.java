package com.provectusstudios.transverse;

import android.util.Log;

/**
 * Created by Justin on 10/17/2014.
 */
public class UtilityMath {
    public static boolean lineSegmentsCross(float startX, float startY, float endX, float endY,
                                            float secondStartX, float secondStartY,
                                            float secondEndX, float secondEndY) {
        float firstXMin = Math.min(startX, endX);
        float secondXMin = Math.min(secondStartX, secondEndX);
        float firstXMax = Math.max(startX, endX);
        float secondXMax = Math.max(secondStartX, secondEndX);

        float firstYMin = Math.min(startY, endY);
        float secondYMin = Math.min(secondStartY, secondEndY);
        float firstYMax = Math.max(startY, endY);
        float secondYMax = Math.max(secondStartY, secondEndY);

        float xSolution;
        float usableSlope;
        float usableIntercept;

        float slope1 = (endY - startY) / (endX - startX);
        float intercept1 = startY - slope1 * startX;

        float slope2 = (secondEndY - secondStartY) / (secondEndX - secondStartX);
        float intercept2 = secondStartY - slope2 * secondStartX;

        if (endX - startX == 0) {
            if (secondEndX - secondStartX == 0) {
                if (endX == secondEndX) {
                    return true;
                }
                return false;
            }
            xSolution = startX;
            usableSlope = slope2;
            usableIntercept = intercept2;
        } else if (secondEndX - secondStartX == 0) {
            xSolution = secondStartX;
            usableSlope = slope1;
            usableIntercept = intercept1;
        } else {

            if (slope1 == slope2) {
                if (intercept1 == intercept2) {
                    return true;
                }
                return false;
            }
            xSolution = (intercept1 - intercept2) / (slope2 - slope1);

            usableIntercept = intercept1;
            usableSlope = slope1;
        }
        float ySolution = xSolution * usableSlope + usableIntercept;

        if (xSolution >= Math.max(firstXMin, secondXMin) && xSolution <= Math.min(firstXMax, secondXMax)) {
            if (ySolution >= Math.max(firstYMin, secondYMin) && ySolution <= Math.min(firstYMax, secondYMax)) {
                return true;
            }
        }
        return false;
    }

    public static boolean lineSegmentCrossesCircle(float startX, float startY, float endX, float endY,
                                             float centerX, float centerY, float radius) {
        //TODO
        return false;
    }
}
