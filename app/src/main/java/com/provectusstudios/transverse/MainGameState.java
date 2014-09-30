package com.provectusstudios.transverse;

import android.opengl.Matrix;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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
    private float lastLeftVerticalChange;
    private float lastRightX;
    private float lastRightY;
    private float lastRightVerticalChange;

    private int score = 0;

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
    private float speed = 200;

    private long lastTouchCalculationLeft = -1;
    private long lastTouchCalculationRight = -1;

    private boolean circlesInView = true;

    private ConcurrentLinkedQueue<Path.Point> leftPointsToAdd = new ConcurrentLinkedQueue<Path.Point>();
    private ConcurrentLinkedQueue<Path.Point> rightPointsToAdd = new ConcurrentLinkedQueue<Path.Point>();

    private class LineSegment {
        public float startX;
        public float startY;
        public float endX;
        public float endY;
    }

    private ConcurrentLinkedQueue<LineSegment> leftTouchChanges = new ConcurrentLinkedQueue<LineSegment>();
    private ConcurrentLinkedQueue<LineSegment> rightTouchChanges = new ConcurrentLinkedQueue<LineSegment>();

    private Rectangle backgroundRectangle;
    private RoundedRectangle scoreRectangle;
    private Text scoreText;

    private RenderType backgroundRenderType;
    private RenderType scoreRectangleRenderType;

    private Random random = new Random();

    private List<Gate> leftGates = new ArrayList<Gate>();
    private List<Gate> rightGates = new ArrayList<Gate>();

    private long nextAddLeftGate;
    private long nextAddRightGate;

    private long lastAddLeftGate = -1;
    private long lastAddRightGate = -1;

    private long lastSpeedCalculation = -1;

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
                            point.y = dpY;
                            leftPointsToAdd.add(point);
                            LineSegment touchChange = new LineSegment();
                            touchChange.startX = lastLeftX;
                            touchChange.startY = lastLeftY + lastLeftVerticalChange;
                            touchChange.endX = dpX;
                            touchChange.endY = dpY + verticalChange;
                            leftTouchChanges.add(touchChange);
                            lastLeftVerticalChange = verticalChange;
                            lastLeftX = dpX;
                            lastLeftY = dpY;
                            lastTouchCalculationLeft = System.currentTimeMillis();
                        } else if (pointerID == rightPointer && dpX != lastRightX && dpY != lastRightY) {
                            Path.Point point = new Path.Point();
                            point.x = dpX;
                            point.y = dpY;
                            rightPointsToAdd.add(point);
                            LineSegment touchChange = new LineSegment();
                            touchChange.startX = lastRightX;
                            touchChange.startY = lastRightY + lastRightVerticalChange;
                            touchChange.endX = dpX;
                            touchChange.endY = dpY + verticalChange;
                            rightTouchChanges.add(touchChange);
                            lastRightVerticalChange = verticalChange;
                            lastRightX = dpX;
                            lastRightY = dpY;
                            lastTouchCalculationRight = System.currentTimeMillis();
                        }
                    }
                }
                break;
        }
    }

    private void refreshScore() {
        scoreText.setText("" + score);
        scoreText.setOrigin(width/2 - scoreText.getWidth()/2, height/2 - height/4, 0);
        scoreText.refresh();
    }

    private void handleGatesTouch(float endX, float endY, float startX, float startY, List<Gate> gates) {
        for (Gate gate : gates) {
            if (!gate.isPassed() && gate.lineCrosses(startX, startY, endX, endY)) {
                score++;
                refreshScore();
                gate.setPassed(true);
            }
        }
    }

    private void checkGatesTouch() {
        LineSegment touchChange;
        while ((touchChange = leftTouchChanges.poll()) != null) {
            handleGatesTouch(touchChange.endX, touchChange.endY, touchChange.startX, touchChange.startY, leftGates);
        }
        while ((touchChange = rightTouchChanges.poll()) != null) {
            handleGatesTouch(touchChange.endX, touchChange.endY, touchChange.startX, touchChange.startY, rightGates);
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
        if (lastTouchCalculationLeft != -1 && ((float) (time - lastTouchCalculationLeft))*speed/1000f > 5) {
            LineSegment pathChange = new LineSegment();
            pathChange.startX = lastLeftX;
            pathChange.endX = lastLeftX;
            pathChange.startY = lastLeftY + lastLeftVerticalChange;
            pathChange.endY = lastLeftY + verticalChange;
            leftTouchChanges.add(pathChange);
            lastLeftVerticalChange = verticalChange;
            lastTouchCalculationLeft = time;
        }
        if (lastTouchCalculationRight != -1 && ((float) (time - lastTouchCalculationRight))*speed/1000f > 5) {
            LineSegment pathChange = new LineSegment();
            pathChange.startX = lastRightX;
            pathChange.endX = lastRightX;
            pathChange.startY = lastRightY + lastRightVerticalChange;
            pathChange.endY = lastRightY + verticalChange;
            rightTouchChanges.add(pathChange);
            lastRightVerticalChange = verticalChange;
        }
        Iterator<Gate> leftGateIterator = leftGates.iterator();
        while (leftGateIterator.hasNext()) {
            Gate gate = leftGateIterator.next();
            if (gate.getCenterY() - gate.getWidth()/2 - 5 > (height + verticalChange)) {
                leftGateIterator.remove();
            }
        }
        Iterator<Gate> rightGateIterator = rightGates.iterator();
        while (rightGateIterator.hasNext()) {
            Gate gate = rightGateIterator.next();
            if (gate.getCenterY() - gate.getWidth()/2 - 5 > (height + verticalChange)) {
                rightGateIterator.remove();
            }
        }
        lastMoveCalc = time;
    }

    public void addNewPoints() {
        Path.Point newPoint;
        while ((newPoint = leftPointsToAdd.poll()) != null) {
            leftPath.addTopPoint(newPoint);
        }
        int pointsToRemove = 0;
        float totalDistance = 0;
        Path.Point prevPoint = leftPath.getPoints().get(leftPath.points.size()-1);
        for (int i = leftPath.points.size() - 2; i >= 0; i--) {
            Path.Point point = leftPath.points.get(i);
            totalDistance += Math.sqrt(Math.pow(point.x - prevPoint.x, 2) + Math.pow(point.y - prevPoint.y, 2));
            if (totalDistance >= 100) {
                leftPath.setAlpha(i, (200f - totalDistance)/100f);
            }
            if (totalDistance >= 200) {
                pointsToRemove = i + 1;
                break;
            }
            prevPoint = point;
        }
        leftPath.removeBottomPoints(pointsToRemove);
        while ((newPoint = rightPointsToAdd.poll()) != null) {
            rightPath.addTopPoint(newPoint);
        }
        pointsToRemove = 0;
        totalDistance = 0;
        prevPoint = rightPath.getPoints().get(rightPath.points.size()-1);
        for (int i = rightPath.points.size() - 2; i >= 0; i--) {
            Path.Point point = rightPath.points.get(i);
            totalDistance += Math.sqrt(Math.pow(point.x - prevPoint.x, 2) + Math.pow(point.y - prevPoint.y, 2));
            if (totalDistance >= 100) {
                rightPath.setAlpha(i, (200f - totalDistance)/100f);
            }
            if (totalDistance >= 200) {
                pointsToRemove = i + 1;
                break;
            }
            prevPoint = point;
        }
        rightPath.removeBottomPoints(pointsToRemove);
    }

    public void addNewGates() {
        long time = System.currentTimeMillis();
        long leftGateDT = time - lastAddLeftGate;
        boolean sane = true;
        if (lastAddLeftGate == -1 || leftGateDT < 0) {
            lastAddLeftGate = time;
            nextAddLeftGate = (long) (random.nextFloat() * 3000 + 2000);
            sane = false;
        }
        long rightGateDT = time - lastAddRightGate;
        if (lastAddRightGate == -1 || rightGateDT < 0) {
            lastAddRightGate = time;
            nextAddRightGate = (long) (random.nextFloat() * 3000 + 2000);
            sane = false;
        }
        if (!sane) {
            return;
        }
        if (leftGateDT >= nextAddLeftGate) {
            lastAddLeftGate = time;
            nextAddLeftGate = (long) (random.nextFloat() * 3000 + 2000);
            Gate gate = randomGate();
            gate.setRenderType(leftRenderType);
            gate.refresh();
            leftGates.add(gate);
        }
        if (rightGateDT >= nextAddRightGate) {
            lastAddRightGate = time;
            nextAddRightGate = (long) (random.nextFloat() * 3000 + 2000);
            Gate gate = randomGate();
            gate.setRenderType(rightRenderType);
            gate.refresh();
            rightGates.add(gate);
        }
    }

    private Gate randomGate() {
        Gate gate = new Gate();
        float angle = (float) (random.nextFloat()*Math.PI);
        gate.setAngle(angle);
        float gateWidth = random.nextFloat()*(width/3) + (width/8);
        gate.setWidth(gateWidth);
        float gateX = random.nextFloat()*(height - 10 - gateWidth) + gateWidth/2 + 5;
        float gateY = verticalChange - gateWidth/2 - 5;
        gate.setCenter(gateX, gateY);
        return gate;
    }

    private void adjustSpeed() {
        long time = System.currentTimeMillis();
        long dt = time - lastSpeedCalculation;
        if (lastSpeedCalculation == -1 || dt < 0) {
            lastSpeedCalculation = time;
            return;
        }
        speed += ((float) dt)*(1f/100f);
        lastSpeedCalculation = time;
    }

    @Override
    public void onDrawFrame() {
        backgroundRenderType.setMatrix(viewProjectionMatrix);
        backgroundRenderType.setAlpha(1);
        backgroundRenderType.drawShape(backgroundRectangle);
        scoreRectangleRenderType.setMatrix(viewProjectionMatrix);
        scoreRectangleRenderType.setAlpha(1);
        scoreRectangleRenderType.drawShape(scoreRectangle);
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
            checkGatesTouch();
            addNewGates();
            adjustSpeed();
            float[] verticalTranslateMVP = new float[16];
            Matrix.multiplyMM(verticalTranslateMVP, 0, viewProjectionMatrix, 0, verticalTranslate, 0);
            leftRenderType.setMatrix(verticalTranslateMVP);
            rightRenderType.setMatrix(verticalTranslateMVP);
            if (circlesInView) {
                leftRenderType.drawShape(leftCircle);
                rightRenderType.drawShape(rightCircle);
            }
            leftRenderType.setMatrix(viewProjectionMatrix);
            rightRenderType.setMatrix(viewProjectionMatrix);
            leftRenderType.drawAlphaShape(leftPath);
            rightRenderType.drawAlphaShape(rightPath);
            leftRenderType.setMatrix(verticalTranslateMVP);
            rightRenderType.setMatrix(verticalTranslateMVP);
            for (Gate gate : leftGates) {
                gate.draw();
            }
            for (Gate gate : rightGates) {
                gate.draw();
            }
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
        refreshScore();
    }
}
