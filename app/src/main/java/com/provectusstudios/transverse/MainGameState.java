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


public class MainGameState implements GameState {

    private MainRenderer mainRenderer;

    private SolidRenderType lineRenderType;
    private SolidRenderType greyRenderType;
    private SolidRenderType sectionRenderType;
    private SolidRenderType titleRenderType;

    private int leftPointer;
    private int rightPointer;

    private int score = 0;
    private Text scoreText;

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
    private float speed = 150;

    private boolean circlesInView = true;
    private boolean wallsInView = true;

    private float endCurrentGenerate = 0;

    private List<Section> sectionsInView = new ArrayList<>();

    private volatile float leftX;
    private volatile float leftY;
    private volatile float rightX;
    private volatile float rightY;
    private float lastLeftX;
    private float lastLeftY;
    private float lastRightX;
    private float lastRightY;
    private float lastVerticalChange;

    private RenderType redRenderType;
    private boolean inLossMenu;
    private boolean animatingLoss;
    private long timeOfLoss;
    private RoundedRectangle loseScoreRectangle;
    private Text loseScoreNumberText;
    private Text loseScoreText;

    private Rectangle backgroundRectangle;

    private Rectangle leftWall;
    private Rectangle centerDivider;
    private Rectangle rightWall;

    private RenderType backgroundRenderType;
    private RenderType scoreRectangleRenderType;

    private Random random = new Random();

    private long lastSpeedCalculation = -1;

    private RoundedRectangle titleRectangle;
    private Text titleText;
    private RoundedRectangle tapToStartRectangle;
    private Text tapAndHoldText;
    private Text toStartText;

    private boolean titleInView = true;

    private float titleAlpha = 1;
    private long lastTitleCalculation = -1;

    private float sectionToPass = 0;
    private float nextSectionToPass = 0;

    public MainGameState(MainRenderer mainRenderer) {
        this.mainRenderer = mainRenderer;
        greyRenderType = new SolidRenderType();
        greyRenderType.setAlpha(1);
        greyRenderType.setColor(.4f, .4f, .4f);
        lineRenderType = new SolidRenderType();
        lineRenderType.setAlpha(1);
        lineRenderType.setColor(.322f, .808f, 1f);
        sectionRenderType = new SolidRenderType();
        sectionRenderType.setAlpha(1);
        sectionRenderType.setColor(.4f, .4f, .4f);
        verticalTranslate = new float[16];
        Matrix.setIdentityM(verticalTranslate, 0);
        leftPath.setWidth(20f);
        rightPath.setWidth(20f);
        backgroundRenderType = new SolidRenderType();
        ((SolidRenderType) backgroundRenderType).setColor(.95f, .95f, .95f);
        scoreRectangleRenderType = new SolidRenderType();
        ((SolidRenderType) scoreRectangleRenderType).setColor(1f,1f,1f);
        redRenderType = new SolidRenderType();
        ((SolidRenderType) redRenderType).setColor(1f, .275f, .2f);
        redRenderType.setAlpha(1f);
        titleRenderType = new SolidRenderType();
        titleRenderType.setColor(.204f, .553f, .686f);
        titleRenderType.setAlpha(1f);
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
                        rightX = dpX;
                        rightY = dpY;
                        lastRightX = rightX;
                        lastRightY = rightY;
                    }
                    if (!leftDown && leftCircle.containsPoint(dpX, dpY)) {
                        leftX = dpX;
                        leftY = dpY;
                        lastLeftX = leftX;
                        lastLeftY = leftY;
                        leftDown = true;
                        leftPointer = pointerID;
                    }
                    if (rightDown && leftDown) {
                        started = true;
                        lastMoveCalc = System.currentTimeMillis();
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
                } else if (!inLossMenu) {
                    if (pointerID == leftPointer || pointerID == rightPointer) {
                        handleLoss();
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
                        if (pointerID == leftPointer) {
                            leftX = dpX;
                            leftY = dpY;
                        } else if (pointerID == rightPointer) {
                            rightX = dpX;
                            rightY = dpY;
                        }
                    }
                }
                break;
        }
    }

    private void handleLoss() {
        inLossMenu = true;
        animatingLoss = true;
        timeOfLoss = System.currentTimeMillis();
        for (Section section : sectionsInView) {
            section.setRenderType(redRenderType);
        }
        loseScoreRectangle = new RoundedRectangle();
        loseScoreRectangle.setCenter(width/4, height/2, 0);
        loseScoreRectangle.setWidth(width / 3);
        loseScoreRectangle.setHeight(2 * height / 3);
        loseScoreRectangle.setCornerRadius(10f);
        loseScoreRectangle.setPrecision(60);
        loseScoreRectangle.refresh();
        loseScoreNumberText = new Text();
        loseScoreNumberText.setFont("FFF Forward");
        loseScoreNumberText.setTextSize(height / 3);
        loseScoreNumberText.setText("" + score);
        loseScoreNumberText.setOrigin(width / 4 - loseScoreNumberText.getWidth() / 2, height / 2 - 2 * height / 9, 0);
        loseScoreNumberText.refresh();
        loseScoreText = new Text();
        loseScoreText.setFont("FFF Forward");
        loseScoreText.setText("POINTS");
        loseScoreText.setTextSize((2*height/3)/5);
        loseScoreText.setOrigin(width/4 - loseScoreText.getWidth()/2, height/2 + height / 8, 0);
        loseScoreText.refresh();
    }

    private void handleSectionTouch() {
        boolean leftPast = false;
        Path.Point newLeftPoint = new Path.Point();
        newLeftPoint.x = leftX;
        newLeftPoint.y = leftY + verticalChange;
        leftPath.addTopPoint(newLeftPoint);
        if (wallsInView) {
            if (leftWall.lineSegmentCrosses(lastLeftX, lastLeftY + lastVerticalChange, leftX, leftY + verticalChange)
                    || rightWall.lineSegmentCrosses(lastLeftX, lastLeftY + lastVerticalChange, leftX, leftY + verticalChange)
                    || centerDivider.lineSegmentCrosses(lastLeftX, lastLeftY + lastVerticalChange, leftX, leftY + verticalChange)) {
                handleLoss();
            }
        }
        for (Section section : sectionsInView) {
            if (section.handleTouchMove(lastLeftX, leftX, lastLeftY + lastVerticalChange, leftY + verticalChange, false)) {
                handleLoss();
            }
        }
        if (leftY + verticalChange <= sectionToPass) {
            leftPast = true;
        }
        boolean rightPast = false;
        Path.Point newRightPoint = new Path.Point();
        newRightPoint.x = rightX;
        newRightPoint.y = rightY + verticalChange;
        rightPath.addTopPoint(newRightPoint);
        if (wallsInView) {
            if (leftWall.lineSegmentCrosses(lastRightX, lastRightY + lastVerticalChange, rightX, rightY + verticalChange)
                    || rightWall.lineSegmentCrosses(lastRightX, lastRightY + lastVerticalChange, rightX, rightY + verticalChange)
                    || centerDivider.lineSegmentCrosses(lastRightX, lastRightY + lastVerticalChange, rightX, rightY + verticalChange)) {
                handleLoss();
            }
        }
        for (Section section : sectionsInView) {
            if(section.handleTouchMove(lastRightX, rightX, lastRightY + lastVerticalChange, rightY + verticalChange, true)) {
                handleLoss();
            }
        }
        if (rightY + verticalChange <= sectionToPass) {
            rightPast = true;
        }
        lastLeftX = leftX;
        lastLeftY = leftY;
        lastRightX = rightX;
        lastRightY = rightY;
        lastVerticalChange = verticalChange;
        if (leftPast && rightPast) {
            score += 1;
            refreshScore();
            sectionToPass = nextSectionToPass;
        }
        int pointsToRemove = 0;
        float totalDistance = 0;
        if (leftPath.points.size() > 2) {
            Path.Point prevPoint = leftPath.getPoints().get(leftPath.points.size() - 1);
            for (int i = leftPath.points.size() - 2; i >= 0; i--) {
                Path.Point point = leftPath.points.get(i);
                totalDistance += Math.sqrt(Math.pow(point.x - prevPoint.x, 2) + Math.pow(point.y - prevPoint.y, 2));
                if (totalDistance >= height/6) {
                    leftPath.setAlpha(i, (height/3 - totalDistance) / (height/6));
                }
                if (totalDistance >= height/3) {
                    pointsToRemove = i + 1;
                    break;
                }
                prevPoint = point;
            }
            leftPath.removeBottomPoints(pointsToRemove);
        }
        pointsToRemove = 0;
        totalDistance = 0;
        if (rightPath.points.size() > 2) {
            Path.Point prevPoint = rightPath.getPoints().get(rightPath.points.size() - 1);
            for (int i = rightPath.points.size() - 2; i >= 0; i--) {
                Path.Point point = rightPath.points.get(i);
                totalDistance += Math.sqrt(Math.pow(point.x - prevPoint.x, 2) + Math.pow(point.y - prevPoint.y, 2));
                if (totalDistance >= height/6) {
                    rightPath.setAlpha(i, (height/3 - totalDistance) / (height/6));
                }
                if (totalDistance >= height/3) {
                    pointsToRemove = i + 1;
                    break;
                }
                prevPoint = point;
            }
            rightPath.removeBottomPoints(pointsToRemove);
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
        Iterator<Section> sectionIterator = sectionsInView.iterator();
        while (sectionIterator.hasNext()) {
            Section section = sectionIterator.next();
            if (section.getStartY() - section.getLength() > verticalChange + height) {
                sectionIterator.remove();
            }
        }
        if (wallsInView) {
            if (verticalChange < -height) {
                wallsInView = false;
                leftWall = null;
                rightWall = null;
                centerDivider = null;
            }
        }
        if (circlesInView) {
            if (verticalChange < -height/2 - height/10) {
                leftCircle = null;
                rightCircle = null;
                circlesInView = false;
            }
        }
        lastMoveCalc = time;
    }

    private void adjustSpeed() {
        long time = System.currentTimeMillis();
        long dt = time - lastSpeedCalculation;
        if (lastSpeedCalculation == -1 || dt < 0) {
            lastSpeedCalculation = time;
            return;
        }
        speed += ((float) dt)*(1f/500f)*(1f + 7*Math.log1p(score/10f));
        lastSpeedCalculation = time;
    }

    private Section getSection() {
        GateSubSection subSection = new GateSubSection();
        if (random.nextFloat() > .5f) {
            subSection.setInverted(true);
        }
        Section section = new MirroredSection(subSection);
        SolidRenderType sectionRenderType = new SolidRenderType();
        float hue = random.nextFloat();
        float luminance = random.nextFloat()*0.5f;
        float saturation = random.nextFloat();
        float q;
        if (luminance < 0.5)
            q = luminance * (1 + saturation);
        else
            q = (luminance + saturation) - (saturation * luminance);

        float p = 2 * luminance - q;
        float red = Math.max(0, hueToRGB(p, q, hue + (1.0f / 3.0f)));
        float green = Math.max(0, hueToRGB(p, q, hue));
        float blue = Math.max(0, hueToRGB(p, q, hue - (1.0f / 3.0f)));
        sectionRenderType.setColor(red, green, blue);
        sectionRenderType.setAlpha(1);
        section.setRenderType(sectionRenderType);
        return section;
    }


    private float hueToRGB(float p, float q, float h)
    {
        if (h < 0) h += 1;

        if (h > 1 ) h -= 1;

        if (6 * h < 1)
        {
            return p + ((q - p) * 6 * h);
        }

        if (2 * h < 1 )
        {
            return  q;
        }

        if (3 * h < 2)
        {
            return p + ( (q - p) * 6 * ((2.0f / 3.0f) - h) );
        }

        return p;
    }

    private void generateSections() {
        if (endCurrentGenerate >= verticalChange - 10) {
            Section section = getSection();
            section.generate(random, 0, width, endCurrentGenerate);
            section.refresh();
            endCurrentGenerate = endCurrentGenerate - section.getLength();
            nextSectionToPass = endCurrentGenerate;
            sectionsInView.add(section);
        }
    }

    @Override
    public void onDrawFrame() {

        backgroundRenderType.setMatrix(viewProjectionMatrix);
        backgroundRenderType.setAlpha(1);
        backgroundRenderType.drawShape(backgroundRectangle);
        scoreRectangleRenderType.setMatrix(viewProjectionMatrix);
        scoreRectangleRenderType.setAlpha(1);
        titleRenderType.setMatrix(viewProjectionMatrix);
        if (!started) {
            greyRenderType.setMatrix(viewProjectionMatrix);
            lineRenderType.setMatrix(viewProjectionMatrix);
            greyRenderType.drawShape(leftWall);
            greyRenderType.drawShape(centerDivider);
            greyRenderType.drawShape(rightWall);
            titleRenderType.drawShape(tapToStartRectangle);
            titleRenderType.drawShape(titleRectangle);
            backgroundRenderType.drawText(titleText);
            backgroundRenderType.drawText(tapAndHoldText);
            backgroundRenderType.drawText(toStartText);

            if (leftDown) {
                lineRenderType.drawShape(leftCircle);
            } else {
                greyRenderType.drawShape(leftCircle);
            }
            if (rightDown) {
                lineRenderType.drawShape(rightCircle);
            } else {
                greyRenderType.drawShape(rightCircle);
            }
        } else if (inLossMenu) {
            redRenderType.setAlpha(1);
            float[] verticalTranslateMVP = new float[16];
            Matrix.multiplyMM(verticalTranslateMVP, 0, viewProjectionMatrix, 0, verticalTranslate, 0);
            redRenderType.setMatrix(verticalTranslateMVP);
            redRenderType.setAlpha(.8f);
            if (wallsInView) {
                redRenderType.drawShape(leftWall);
                redRenderType.drawShape(centerDivider);
                redRenderType.drawShape(rightWall);
            }
            for (Section section : sectionsInView) {
                section.draw(verticalTranslateMVP);
            }
            redRenderType.setMatrix(viewProjectionMatrix);
            redRenderType.setAlpha(1f);
            backgroundRenderType.setAlpha(1f);
            if (animatingLoss) {
                long dt = System.currentTimeMillis() - timeOfLoss;
                float alpha = dt*(1f/1000f);
                if (alpha > 1) {
                    animatingLoss = false;
                }
                redRenderType.setAlpha(alpha);
                redRenderType.drawShape(loseScoreRectangle);
                backgroundRenderType.drawText(loseScoreNumberText);
                backgroundRenderType.drawText(loseScoreText);
            } else {
                redRenderType.drawShape(loseScoreRectangle);
                backgroundRenderType.drawText(loseScoreNumberText);
                backgroundRenderType.drawText(loseScoreText);
            }
        } else {
            calculateMove();
            adjustSpeed();
            generateSections();
            handleSectionTouch();
            float[] verticalTranslateMVP = new float[16];
            Matrix.multiplyMM(verticalTranslateMVP, 0, viewProjectionMatrix, 0, verticalTranslate, 0);
            lineRenderType.setMatrix(verticalTranslateMVP);
            if (wallsInView) {
                greyRenderType.setMatrix(verticalTranslateMVP);
                greyRenderType.drawShape(leftWall);
                greyRenderType.drawShape(centerDivider);
                greyRenderType.drawShape(rightWall);
            }
            if (titleInView) {
                long time = System.currentTimeMillis();
                long dt = time - lastTitleCalculation;
                boolean sane = true;
                if (lastTitleCalculation == -1 || dt <= 0) {
                    lastTitleCalculation = time;
                    sane = false;
                    titleRenderType.setAlpha(titleAlpha);
                    titleRenderType.drawShape(titleRectangle);
                    backgroundRenderType.setAlpha(titleAlpha);
                    backgroundRenderType.drawText(titleText);
                    titleRenderType.drawShape(tapToStartRectangle);
                    backgroundRenderType.drawText(tapAndHoldText);
                    backgroundRenderType.drawText(toStartText);
                }
                if (sane) {
                    float alphaChange = dt * (1/500f);
                    titleAlpha -= alphaChange;
                    if (titleAlpha < 0) {
                       titleInView = false;
                       titleAlpha = 0;
                    }
                    lastTitleCalculation = time;
                    titleRenderType.setAlpha(titleAlpha);
                    titleRenderType.drawShape(titleRectangle);
                    titleRenderType.drawShape(tapToStartRectangle);
                    backgroundRenderType.setAlpha(titleAlpha);
                    backgroundRenderType.drawText(titleText);
                    backgroundRenderType.drawText(tapAndHoldText);
                    backgroundRenderType.drawText(toStartText);
                    backgroundRenderType.setAlpha(1f);
                    if (!titleInView) {
                        titleRectangle = null;
                        titleText = null;
                        tapToStartRectangle = null;
                        tapAndHoldText = null;
                        toStartText = null;
                    }
                }
            }
            if (circlesInView) {
                lineRenderType.drawShape(leftCircle);
                lineRenderType.drawShape(rightCircle);
            }
            lineRenderType.setMatrix(verticalTranslateMVP);
            lineRenderType.drawAlphaShape(leftPath);
            lineRenderType.drawAlphaShape(rightPath);
            for (Section section : sectionsInView) {
                section.draw(verticalTranslateMVP);
            }
            greyRenderType.setMatrix(viewProjectionMatrix);
            greyRenderType.drawText(scoreText);
        }
    }

    @Override
    public void refreshDimensions(float width, float height, float[] viewProjectionMatrix) {
        if (circlesInView) {
            leftCircle = new Circle();
            leftCircle.setPrecision(360);
            leftCircle.setRadius(height/10);
            rightCircle = new Circle();
            rightCircle.setPrecision(360);
            rightCircle.setRadius(height/10);
            leftCircle.setCenter(width/4, height/2, 0);
            rightCircle.setCenter(3*width/4, height/2, 0);
            leftCircle.refresh();
            rightCircle.refresh();
        }
        if (wallsInView) {
            leftWall = new Rectangle();
            leftWall.setOrigin(0, 0, 0);
            leftWall.setWidth(15f);
            leftWall.setHeight(height);
            leftWall.refresh();
            centerDivider = new Rectangle();
            centerDivider.setOrigin(width/2 - 7.5f, 0, 0);
            centerDivider.setWidth(15f);
            centerDivider.setHeight(height);
            centerDivider.refresh();
            rightWall = new Rectangle();
            rightWall.setOrigin(width - 15, 0, 0);
            rightWall.setWidth(15f);
            rightWall.setHeight(height);
            rightWall.refresh();
        }
        if (titleInView) {
            titleRectangle = new RoundedRectangle();
            float titleRectangleWidth = 2*width/3;
            titleRectangle.setWidth(titleRectangleWidth);
            float titleRectangleY = height/5;
            float titleRectangleHeight = height/5;
            titleRectangle.setCenter(width/2, titleRectangleY, 0);
            titleRectangle.setHeight(titleRectangleHeight);
            titleRectangle.setCornerRadius(10f);
            titleRectangle.setPrecision(60);
            titleRectangle.refresh();
            titleText = new Text();
            titleText.setFont("FFF Forward");
            titleText.setText("Transverse");
            titleText.setTextSize(titleRectangleHeight - 10);
            titleText.setOrigin(width/2 - titleText.getWidth()/2, titleRectangleY - (titleRectangleHeight - 10)/2, 0);
            titleText.refresh();
            tapToStartRectangle = new RoundedRectangle();
            tapToStartRectangle.setWidth(titleRectangleWidth);
            tapToStartRectangle.setCenter(width/2, height/2, 0);
            tapToStartRectangle.setHeight(height/4);
            tapToStartRectangle.setCornerRadius(10f);
            tapToStartRectangle.setPrecision(60);
            tapToStartRectangle.refresh();
            tapAndHoldText = new Text();
            tapAndHoldText.setFont("FFF Forward");
            tapAndHoldText.setText("Tap and hold");
            tapAndHoldText.setTextSize((height/4)/3);
            tapAndHoldText.setOrigin(width/2 - tapAndHoldText.getWidth()/2, height/2 - (height/4)/3, 0);
            tapAndHoldText.refresh();
            toStartText = new Text();
            toStartText.setFont("FFF Forward");
            toStartText.setText("to start!");
            toStartText.setTextSize((height/4)/3);
            toStartText.setOrigin(width/2 - toStartText.getWidth()/2, height/2, 0);
            toStartText.refresh();
        }
        this.viewProjectionMatrix = viewProjectionMatrix;
        this.height = height;
        this.width = width;
        backgroundRectangle = new Rectangle();
        backgroundRectangle.setWidth(width);
        backgroundRectangle.setHeight(height);
        backgroundRectangle.setOrigin(0, 0, 0);
        backgroundRectangle.refresh();
        scoreText = new Text();
        scoreText.setFont("FFF Forward");
        scoreText.setTextSize(50f);
        refreshScore();
    }

    private void refreshScore() {
        scoreText.setText(score + "");
        scoreText.setOrigin(width - 25 - scoreText.getWidth(), 3, 0);
        scoreText.refresh();
    }

}
