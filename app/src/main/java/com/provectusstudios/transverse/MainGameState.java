package com.provectusstudios.transverse;

import android.opengl.Matrix;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Justin on 9/15/2014.
 */
public class MainGameState implements GameState {
    private MainRenderer mainRenderer;

    private SolidRenderType leftRenderType;
    private SolidRenderType rightRenderType;
    private SolidRenderType greyRenderType;

    private int leftPointer;
    private int rightPointer;

    private float lastLeftX;
    private float lastLeftY;
    private float lastRightX;
    private float lastRightY;

    private boolean leftDown;
    private boolean rightDown;

    private Circle leftCircle;
    private Circle rightCircle;

    private float[] verticalTranslate;

    private Path leftPath = new Path();
    private Path rightPath = new Path();

    private float[] viewProjectionMatrix;
    private float width;
    private float height;

    private float verticalChange = 0;

    private boolean started;

    private long lastMoveCalc;
    private float speed = 100;

    private long lastTouchCalculationLeft = -1;
    private long lastTouchCalculationRight = -1;

    private boolean circlesInView = true;

    private ConcurrentLinkedQueue<Path.Point> leftPointsToAdd = new ConcurrentLinkedQueue<Path.Point>();
    private ConcurrentLinkedQueue<Path.Point> rightPointsToAdd = new ConcurrentLinkedQueue<Path.Point>();

    private Rectangle backgroundRectangle;
    private RoundedRectangle scoreRectangle;
    private Text scoreText;

    private RenderType backgroundRenderType;
    private RenderType scoreRectangleRenderType;

    public MainGameState(MainRenderer mainRenderer) {
        this.mainRenderer = mainRenderer;
        leftCircle = new Circle();
        leftCircle.setPrecision(360);
        leftCircle.setRadius(40);
        rightCircle = new Circle();
        rightCircle.setPrecision(360);
        rightCircle.setRadius(40);
        greyRenderType = new SolidRenderType();
        greyRenderType.setAlpha(1);
        greyRenderType.setColor(.4f, .4f, .4f);
        leftRenderType = new SolidRenderType();
        leftRenderType.setAlpha(1);
        leftRenderType.setColor(.31f, .902f, .09f);
        rightRenderType = new SolidRenderType();
        rightRenderType.setAlpha(1);
        rightRenderType.setColor(.114f, .514f, .753f);
        verticalTranslate = new float[16];
        Matrix.setIdentityM(verticalTranslate, 0);
        leftPath.setWidth(5f);
        rightPath.setWidth(5f);
        backgroundRenderType = new SolidRenderType();
        ((SolidRenderType) backgroundRenderType).setColor(.95f, .95f, .95f);
        scoreRectangleRenderType = new SolidRenderType();
        ((SolidRenderType) scoreRectangleRenderType).setColor(1f,1f,1f);
    }

    @Override
    public void handleTouchEvent(MotionEvent event) {
        float density = mainRenderer.getContext().getResources().getDisplayMetrics().density;
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                int pointerIndex = MotionEventCompat.getActionIndex(event);
                int pointerID = MotionEventCompat.getPointerId(event, pointerIndex);
                float x = MotionEventCompat.getX(event, pointerIndex);
                float y = MotionEventCompat.getY(event, pointerIndex);
                float dpX = x/density;
                float dpY = y/density;
                if (!started) {
                    if (!rightDown && rightCircle.containsPoint(dpX, dpY)) {
                        rightDown = true;
                        rightPointer = pointerID;
                    }
                    if (!leftDown && leftCircle.containsPoint(dpX, dpY)) {
                        leftDown = true;
                        leftPointer = pointerID;
                    }
                    if (rightDown && leftDown) {
                        started = true;
                        lastMoveCalc = System.currentTimeMillis();
                        Path.Point leftPoint = new Path.Point();
                        leftPoint.x = MotionEventCompat.getX(event, MotionEventCompat.findPointerIndex(event, leftPointer))/density;
                        leftPoint.y = MotionEventCompat.getY(event, MotionEventCompat.findPointerIndex(event, leftPointer))/density;
                        leftPointsToAdd.add(leftPoint);
                        Path.Point rightPoint = new Path.Point();
                        rightPoint.x = MotionEventCompat.getX(event, MotionEventCompat.findPointerIndex(event, rightPointer))/density;
                        rightPoint.y = MotionEventCompat.getY(event, MotionEventCompat.findPointerIndex(event, rightPointer))/density;
                        rightPointsToAdd.add(rightPoint);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                pointerIndex = MotionEventCompat.getActionIndex(event);
                pointerID = MotionEventCompat.getPointerId(event, pointerIndex);
                if (!started) {
                    if (rightDown && pointerID == rightPointer) {
                        rightDown = false;
                        rightPointer = 0;
                    }
                    if (leftDown && pointerID == leftPointer) {
                        leftDown = false;
                        leftPointer = 0;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (started) {
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        pointerID = MotionEventCompat.getPointerId(event, i);
                        x = MotionEventCompat.getX(event, i);
                        y = MotionEventCompat.getY(event, i);
                        dpX = x/density;
                        dpY = y/density;
                        if (pointerID == leftPointer && dpX != lastLeftX && dpY != lastLeftY) {
                            Path.Point point = new Path.Point();
                            point.x = dpX;
                            point.y = dpY + verticalChange;
                            leftPointsToAdd.add(point);
                            lastLeftX = dpX;
                            lastLeftY = dpY;
                            lastTouchCalculationLeft = System.currentTimeMillis();
                        } else if (pointerID == rightPointer && dpX != lastRightX && dpY != lastRightY) {
                            Path.Point point = new Path.Point();
                            point.x = dpX;
                            point.y = dpY + verticalChange;
                            rightPointsToAdd.add(point);
                            lastRightX = dpX;
                            lastRightY = dpY;
                            lastTouchCalculationRight = System.currentTimeMillis();
                        }
                    }
                }
                break;
        }
    }

    private void calculateMove() {
        long time = System.currentTimeMillis();
        long dt = time - lastMoveCalc;
        if (dt < 0) {
            lastMoveCalc = time;
            return;
        }
        float newVerticalChange = (((float) dt) / 1000f) * speed;
        verticalChange -= newVerticalChange;
        Matrix.translateM(verticalTranslate, 0, 0, newVerticalChange, 0);
        List<Path.Point> leftPoints = leftPath.getPoints();
        int numOfLeftPointsToRemove = 0;
        for (int i = 0; i < leftPoints.size(); i++) {
            Path.Point point = leftPoints.get(i);
            if (point.y > (height + 20 + verticalChange)) {
                numOfLeftPointsToRemove += 1;
            } else {
                break;
            }
        }
        if (numOfLeftPointsToRemove != 0) {
            leftPath.removeBottomPoints(numOfLeftPointsToRemove);
        }
        List<Path.Point> rightPoints = rightPath.getPoints();
        int numOfRightPointsToRemove = 0;
        for (int i = 0; i < rightPoints.size(); i++) {
            Path.Point point = rightPoints.get(i);
            if (point.y > (height + 20 + verticalChange)) {
                numOfRightPointsToRemove += 1;
            } else {
                break;
            }
        }
        if (numOfRightPointsToRemove != 0) {
            rightPath.removeBottomPoints(numOfRightPointsToRemove);
        }
        if (lastTouchCalculationLeft != -1 && ((float) (time - lastTouchCalculationLeft))*speed/1000f > 7) {
            Path.Point newLeftPoint = new Path.Point();
            newLeftPoint.x = lastLeftX;
            newLeftPoint.y = lastLeftY + verticalChange;
            leftPointsToAdd.add(newLeftPoint);
            lastTouchCalculationLeft = time;
        }
        if (lastTouchCalculationRight != -1 && ((float) (time - lastTouchCalculationRight))*speed/1000f > 7) {
            Path.Point newRightPoint = new Path.Point();
            newRightPoint.x = lastRightX;
            newRightPoint.y = lastRightY + verticalChange;
            rightPointsToAdd.add(newRightPoint);
            lastTouchCalculationRight = time;
        }
        lastMoveCalc = time;
    }

    public void addNewPoints() {
        Path.Point newPoint;
        while ((newPoint = leftPointsToAdd.poll()) != null) {
            leftPath.addTopPoint(newPoint);
        }
        while ((newPoint = rightPointsToAdd.poll()) != null) {
            rightPath.addTopPoint(newPoint);
        }
    }

    @Override
    public void onDrawFrame() {
        backgroundRenderType.setMatrix(viewProjectionMatrix);
        backgroundRenderType.setAlpha(1);
        backgroundRenderType.drawShape(backgroundRectangle);
        scoreRectangleRenderType.setMatrix(viewProjectionMatrix);
        scoreRectangleRenderType.setAlpha(1);
        scoreRectangleRenderType.drawShape(scoreRectangle);
        scoreText.setText("" + (System.currentTimeMillis() % 11000)/1000);
        scoreText.setOrigin(width/2 - scoreText.getWidth()/2, height/2 - height/4, 0);
        scoreText.refresh();
        backgroundRenderType.drawText(scoreText);
        if (!started) {
            greyRenderType.setMatrix(viewProjectionMatrix);
            leftRenderType.setMatrix(viewProjectionMatrix);
            rightRenderType.setMatrix(viewProjectionMatrix);
            if (leftDown) {
                leftRenderType.drawShape(leftCircle);
            } else {
                greyRenderType.drawShape(leftCircle);
            }
            if (rightDown) {
                rightRenderType.drawShape(rightCircle);
            } else {
                greyRenderType.drawShape(rightCircle);
            }
        } else {
            addNewPoints();
            calculateMove();
            float[] verticalTranslateMVP = new float[16];
            Matrix.multiplyMM(verticalTranslateMVP, 0, viewProjectionMatrix, 0, verticalTranslate, 0);
            leftRenderType.setMatrix(verticalTranslateMVP);
            rightRenderType.setMatrix(verticalTranslateMVP);
            if (circlesInView) {
                leftRenderType.drawShape(leftCircle);
                rightRenderType.drawShape(rightCircle);
            }
            leftRenderType.drawAlphaShape(leftPath);
            rightRenderType.drawAlphaShape(rightPath);
        }
    }

    @Override
    public void refreshDimensions(float width, float height, float[] viewProjectionMatrix) {
        if (!started) {
            leftCircle.setCenter(width/4, height/2, 0);
            rightCircle.setCenter(3*width/4, height/2, 0);
            leftCircle.refresh();
            rightCircle.refresh();
        }
        this.viewProjectionMatrix = viewProjectionMatrix;
        this.height = height;
        this.width = width;
        backgroundRectangle = new Rectangle();
        backgroundRectangle.setWidth(width);
        backgroundRectangle.setHeight(height);
        backgroundRectangle.setOrigin(0, 0, 0);
        backgroundRectangle.refresh();
        scoreRectangle = new RoundedRectangle();
        scoreRectangle.setWidth(width/2);
        scoreRectangle.setHeight(height / 2);
        scoreRectangle.setCenter(width / 2, height / 2, 0);
        scoreRectangle.setCornerRadius(20);
        scoreRectangle.setPrecision(40);
        scoreRectangle.refresh();
        scoreText = new Text();
        scoreText.setFont("FFF Forward");
        scoreText.setTextSize(height/2);
        scoreText.setText("10");
        scoreText.setOrigin(width/2 - scoreText.getWidth()/2, height/2 - height/4, 0);
        scoreText.refresh();
    }
}
