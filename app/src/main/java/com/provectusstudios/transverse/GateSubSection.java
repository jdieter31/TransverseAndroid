package com.provectusstudios.transverse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GateSubSection implements SubSection {
    private float length;

    private float startX;
    private float startY;
    private float width;

    private List<Gate> gates = new ArrayList<>();

    private boolean inverted;

    private float difficulty = .5f;

    private float minSpacing;
    private float angleRange;
    private float spacingRange;
    private float positionDeviation;
    private float positionMean;
    private float lengthMinimum;
    private float lengthRange;

    @Override
    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void draw(RenderType renderType) {
        for (Gate gate : gates) {
            gate.draw(renderType);
        }
    }

    @Override
    public void refresh() {
        for (Gate gate : gates) {
            gate.refresh();
        }
    }

    @Override
    public boolean handleTouchMove(float startX, float endX, float startY, float endY) {
        for (Gate gate : gates) {
            if (gate.lineSegmentCrosses(startX, startY, endX, endY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void generate(Random random, float width, float startX, float startY) {
        length = Math.max(500, 750 + 750*difficulty + (float) random.nextGaussian()*difficulty*750);
        generate(random, width, startX, startY, length);
    }

    @Override
    public void generate(Random random, float width, float startX, float startY, float length) {
        this.startX = startX;
        this.width = width;
        this.startY = startY;
        this.length = length;

        minSpacing = 200 - 100*difficulty;
        spacingRange = 100*(1-difficulty)*random.nextFloat();

        angleRange = 0;
        if (random.nextFloat() > .4) {
            angleRange = (float) (random.nextFloat()*Math.PI/8);
        }

        positionDeviation = width / 9 + random.nextFloat() * difficulty * (1/3f);
        positionMean = startX + width/2;


        if (inverted) {
            lengthMinimum = 2*width/5 + difficulty * width/3;
            lengthRange = difficulty * width/4;
        } else {
            lengthMinimum = width/5 + (1 - difficulty) * width/3;
            lengthRange = random.nextFloat() * difficulty * width/4;
        }

        float lastGateY;

        float spacing = minSpacing + random.nextFloat()*spacingRange;
        Gate gate = genGate(random, startY - spacing);
        lastGateY = startY - spacing - gate.getHeight();
        do {
            gates.add(gate);


            spacing = minSpacing + random.nextFloat()*spacingRange;
            gate = genGate(random, lastGateY - spacing);
            lastGateY = lastGateY - spacing - gate.getHeight();
        } while (lastGateY - spacing >= startY - length);
        lastGateY += gate.getHeight();
        lastGateY += spacing;
        if (lastGateY - 220f >= startY - length) {
            gate = genGate(random, (startY - length + lastGateY)/2);
            gate.setAngle(0);
            gate.setGateCenter(gate.getGateCenterX(), startY - length + 2 * spacing / 3f);
            gates.add(gate);
        }
    }

    private Gate genGate(Random random, float gateStartY) {
        Gate gate = new Gate();
        float gateX = (float) (positionMean + positionDeviation*random.nextGaussian());
        if (gateX > startX + width - 50) {
            gateX = startX + width - 150;
        } else if (gateX < startX + 50) {
            gateX = startX + 150;
        }
        float gateLength = lengthMinimum + lengthRange*random.nextFloat();
        gate.setGateLength(gateLength);
        float gateAngle = -angleRange + random.nextFloat()*2*angleRange;
        gate.setAngle(gateAngle);
        gate.setEndXPoints(startX, startX + width);
        float gateWidth = (float) (gateLength * Math.cos(-gateAngle));
        boolean adjustRight = random.nextFloat() > 0.5f;
        if (gateX - gateWidth / 2 - width/3 < startX && gateX + gateWidth / 2 + width/3 > startX + width) {
            if (adjustRight) {
                gateX = startX + width/3 + gateWidth/2;
            } else {
                gateX = startX + width - gateWidth/2 - width/3;
            }
        }
        if (gateX - gateWidth / 2 < startX) {
            gate.setGateLength((float) (2 * (gateX - (startX)) / Math.cos(gateAngle)));
        }
        if (gateX + gateWidth / 2 > startX + width) {
            gate.setGateLength((float) (2 * (startX + width - gateX) / Math.cos(gateAngle)));
        }

        float gateY;
        if (gateAngle > 0) {
            gateY = (float) (gateStartY - Math.abs((gateX - startX) * Math.tan(gateAngle)));
        } else {
            gateY = (float) (gateStartY - Math.abs((startX + width - gateX) * Math.tan(gateAngle)));
        }
        gate.setGateCenter(gateX, gateY);
        if (inverted) {
            gate.setInverted(true);
        }
        return gate;
    }

    @Override
    public float getLength() {
        return length;
    }

    @Override
    public SubSection copy() {
        GateSubSection copy = new GateSubSection();
        copy.setLength(length);
        List<Gate> gatesCopy = new ArrayList<>();
        for (Gate gate : gates) {
            Gate gateCopy = new Gate();
            gateCopy.setAngle(gate.getAngle());
            gateCopy.setEndXPoints(startX, startX + width);
            gateCopy.setGateCenter(gate.getGateCenterX(), gate.getGateCenterY());
            gateCopy.setGateLength(gate.getGateLength());
            gatesCopy.add(gateCopy);
            gateCopy.setInverted(inverted);
        }
        copy.setGates(gatesCopy);
        copy.setDimensions(startX, startY, width);
        copy.setInverted(inverted);
        return copy;
    }

    @Override
    public void flip() {
        for (Gate gate : gates) {
            gate.setAngle(-gate.getAngle());
            float newCenterX = startX + width - (gate.getGateCenterX() - startX);
            float newCenterY = gate.getGateCenterY();
            gate.setGateCenter(newCenterX, newCenterY);
        }
    }

    @Override
    public void setOrigin(float startX, float startY) {
        for (Gate gate: gates) {
            gate.setGateCenter(startX + (gate.getGateCenterX() - this.startX), startY + (gate.getGateCenterY() - this.startY));
            gate.setEndXPoints(startX, startX + width);
        }
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    //Empties the List of gates
    public void empty() {
        gates = new ArrayList<>();
    }

    private void setGates(List<Gate> newGates) {
        gates = newGates;
    }

    private void setLength(float length) {
        this.length = length;
    }

    private void setDimensions(float startX, float startY, float width) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

}
