package com.provectusstudios.transverse;

import java.util.Random;

/**
 * Created by Justin on 10/5/2014.
 */
public class MirroredSection implements Section {
    private RenderType renderType;
    private float length;

    private Rectangle centerDivider;
    private Rectangle rightWall;
    private Rectangle leftWall;

    private SubSection section;
    private SubSection mirroredSection;

    private float startX;
    private float width;
    private float startY;

    private Rectangle firstLeftGateLeft;
    private Rectangle firstLeftGateRight;
    private Rectangle firstRightGateLeft;
    private Rectangle firstRightGateRight;

    public MirroredSection(SubSection sectionToMirror) {
        section = sectionToMirror;
    }

    @Override
    public void draw(float[] matrix) {
        renderType.setMatrix(matrix);
        renderType.drawShape(centerDivider);
        renderType.drawShape(leftWall);
        renderType.drawShape(rightWall);
        section.draw(renderType);
        mirroredSection.draw(renderType);
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
        section.refresh();
        mirroredSection.refresh();
        firstLeftGateLeft.refresh();
        firstLeftGateRight.refresh();
        firstRightGateLeft.refresh();
        firstRightGateRight.refresh();
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
            if (mirroredSection.handleTouchMove(startX, endX, startY, endY)) {
                return true;
            }
        } else {
            if (section.handleTouchMove(startX, endX, startY, endY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setDifficulty(float difficulty) {

    }

    @Override
    public void generate(Random random, float startX, float width, float startY) {
        this.startX = startX;
        this.width = width;
        this.startY = startY;
        section.generate(random, (width-45)/2, startX + 15, startY);
        length = section.getLength();
        mirroredSection = section.copy();
        section.flip();
        mirroredSection.setOrigin(startX + 30 + (width-45)/2, startY);
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
    public void setRenderType(RenderType renderType) {
        this.renderType = renderType;
    }

    @Override
    public boolean isSplit() {
        return true;
    }
}
