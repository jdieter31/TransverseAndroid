package com.provectusstudios.transverse;

import java.util.Random;

public class DoubleSection implements Section {
    private float length;

    private Rectangle centerDivider;
    private Rectangle rightWall;
    private Rectangle leftWall;

    private SubSection section1;
    private SubSection section2;

    private float startX;
    private float width;
    private float startY;

    private Rectangle firstLeftGateLeft;
    private Rectangle firstLeftGateRight;
    private Rectangle firstRightGateLeft;
    private Rectangle firstRightGateRight;

    private float difficulty;

    @Override
    public void draw(float[] matrix, RenderType renderType) {
        renderType.setMatrix(matrix);
        renderType.drawShape(centerDivider);
        renderType.drawShape(leftWall);
        renderType.drawShape(rightWall);
        section1.draw(renderType);
        section2.draw(renderType);
        renderType.drawShape(firstLeftGateLeft);
        renderType.drawShape(firstLeftGateRight);
        renderType.drawShape(firstRightGateLeft);
        renderType.drawShape(firstRightGateRight);
    }

    @Override
    public void refresh() {
        leftWall.refresh();
        rightWall.refresh();
        centerDivider.refresh();
        section1.refresh();
        section2.refresh();
        firstLeftGateLeft.refresh();
        firstLeftGateRight.refresh();
        firstRightGateLeft.refresh();
        firstRightGateRight.refresh();
    }

    @Override
    public void empty() {
        section1.empty();
        section2.empty();
    }

    @Override
    public boolean handleTouchMove(float startX, float endX, float startY, float endY, boolean rightSide) {
        if (firstLeftGateLeft.lineSegmentCrosses(startX, startY, endX, endY)
                || firstLeftGateRight.lineSegmentCrosses(startX, startY, endX, endY)
                || firstRightGateRight.lineSegmentCrosses(startX, startY, endX, endY)
                || firstRightGateLeft.lineSegmentCrosses(startX, startY, endX, endY)
                || rightWall.lineSegmentCrosses(startX, startY, endX, endY)
                || leftWall.lineSegmentCrosses(startX, startY, endX, endY)
                || centerDivider.lineSegmentCrosses(startX, startY, endX, endY)) {
            return true;
        }
        if (rightSide) {
            if (section2.handleTouchMove(startX, endX, startY, endY)) {
                return true;
            }
        } else {
            if (section1.handleTouchMove(startX, endX, startY, endY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void generate(Random random, float startX, float width, float startY) {
        this.startX = startX;
        this.width = width;
        this.startY = startY;
        section1 = getSubSection(random);
        section1.setDifficulty(difficulty);
        section1.generate(random, (width-45)/2, startX + 15, startY);
        length = section1.getLength();

        if (random.nextFloat() < .7 - .5 * difficulty) {
            section2 = section1.copy();
            if (random.nextFloat() < .5) {
                section1.flip();
            }
            section2.setOrigin(startX + 30 + (width-45)/2, startY);
        } else {
            section2 = getSubSection(random);
            section2.setDifficulty(difficulty);
            section2.generate(random, (width-45)/2, startX + 30 + (width-45)/2, startY, section1.getLength());
        }

        centerDivider = new Rectangle();
        centerDivider.setOrigin(startX + 15 + (width-45)/2, startY - length, 0);
        centerDivider.setWidth(15);
        centerDivider.setHeight(length);
        leftWall = new Rectangle();
        leftWall.setOrigin(startX, startY - length, 0);
        leftWall.setWidth(15);
        leftWall.setHeight(length);
        rightWall = new Rectangle();
        rightWall.setOrigin(startX + width - 15, startY - length, 0);
        rightWall.setWidth(15);
        rightWall.setHeight(length);
        firstLeftGateLeft = new Rectangle();
        firstLeftGateLeft.setOrigin(startX + 15, startY - 15, 0);
        firstLeftGateLeft.setHeight(15);
        firstLeftGateLeft.setWidth((width-45)/8);
        firstLeftGateRight = new Rectangle();
        firstLeftGateRight.setOrigin(3*(width-45)/8 + 15, startY - 15, 0);
        firstLeftGateRight.setHeight(15);
        firstLeftGateRight.setWidth((width - 45)/8);
        firstRightGateLeft = new Rectangle();
        firstRightGateLeft.setOrigin((width-45)/2 + 30, startY - 15, 0);
        firstRightGateLeft.setHeight(15);
        firstRightGateLeft.setWidth((width - 45)/8);
        firstRightGateRight = new Rectangle();
        firstRightGateRight.setOrigin(30 + 7*(width-45)/8, startY - 15, 0);
        firstRightGateRight.setHeight(15);
        firstRightGateRight.setWidth((width - 45)/8);

    }

    private SubSection getSubSection(Random random) {
        GateSubSection subSection = new GateSubSection();
        if (random.nextFloat() > .5f) {
            subSection.setInverted(true);
        }
        return subSection;
    }

    @Override
    public float getStartX() {
        return startX;
    }

    @Override
    public float getStartY() {
        return startY;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getLength() {
        return length;
    }

    @Override
    public boolean isSplit() {
        return true;
    }
}
