package com.provectusstudios.transverse;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Justin on 10/5/2014.
 */
public class GateSubSection implements SubSection {
    private float length;

    private float startX;
    private float startY;
    private float width;

    private List<Gate> gates = new ArrayList<Gate>();

    private boolean inverted;

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
    public void handleTouchMove(float startX, float endX, float startY, float endY) {

    }

    @Override
    public void generate(Random random, float width, float startX, float startY) {
        length = 1000 + random.nextFloat()*1500;
        generate(random, width, startX, startY, length);
    }

    @Override
    public void generate(Random random, float width, float startX, float startY, float length) {
        this.startX = startX;
        this.width = width;
        this.startY = startY;
        this.length = length;
        float minSpacing = 200f;
        float spacingRange = 200f*random.nextFloat();
        float angleRange = (float) (random.nextFloat()*Math.PI/8);
        float positionDeviation = width / 9 + random.nextFloat() * (2 * width / 9);;
        float lengthMinimum;
        float lengthRange;
        if (inverted) {
            lengthMinimum = 3*width/5;
            lengthRange = width/5;
        } else {
            lengthMinimum = 2*width/5;
            lengthRange = random.nextFloat() * width/4;
        }
        float positionMean = startX + width/2;
        float lastGateY;

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
        if (gateX - gateWidth / 2 - width/5 < startX && gateX + gateWidth / 2 + width/5 > startX + width) {
            Log.d("", "Gate goes over edges");
            if (adjustRight) {
                gateX = startX + width/5 + gateWidth/2;
            } else {
                gateX = startX + width - gateWidth/2 - width/5;
            }
        }
        if (gateX - gateWidth / 2 < startX) {
            Log.d("", "Gate goes over left edge");
            gate.setGateLength((float) (2 * (gateX - (startX)) / Math.cos(gateAngle)));
        }
        if (gateX + gateWidth / 2 > startX + width) {
            gate.setGateLength((float) (2 * (startX + width - gateX) / Math.cos(gateAngle)));
        }
        float spacing = minSpacing + random.nextFloat()*spacingRange;
        float gateY;
        if (gateAngle > 0) {
            gateY = (float) (startY - spacing - Math.abs((gateX - startX) * Math.tan(gateAngle)));
        } else {
            gateY = (float) (startY - spacing - Math.abs((startX + width - gateX) * Math.tan(gateAngle)));
        }
        gate.setGateCenter(gateX, gateY);
        if (inverted) {
            gate.setInverted(true);
        }
        gates.add(gate);
        lastGateY = startY - spacing - gate.getHeight();
        while (true) {
            gate = new Gate();
            gateX = (float) (positionMean + positionDeviation*random.nextGaussian());
            if (gateX > startX + width - 50) {
                gateX = startX + width - 150;
            } else if (gateX < startX + 50) {
                gateX = startX + 150;
            }
            gateLength = lengthMinimum + lengthRange*random.nextFloat();
            gate.setGateLength(gateLength);
            gateAngle = -angleRange + random.nextFloat()*2*angleRange;
            gate.setAngle(gateAngle);
            gate.setEndXPoints(startX, startX + width);
            gateWidth = (float) (gateLength * Math.cos(-gateAngle));
            adjustRight = random.nextFloat() > 0.5f;
            if (gateX - gateWidth / 2 - width/5 < startX && gateX + gateWidth / 2 + width/5 > startX + width) {
                if (adjustRight) {
                    gateX = startX + width/5 + gateWidth/2;
                } else {
                    gateX = startX + width - gateWidth/2 - width/5;
                }
            }
            if (gateX - gateWidth / 2 < startX) {
                gate.setGateLength((float) (2 * (gateX - (startX)) / Math.cos(gateAngle)));
            }
            if (gateX + gateWidth / 2 > startX + width) {
                gate.setGateLength((float) (2 * (startX + width - gateX) / Math.cos(gateAngle)));
            }

            spacing = minSpacing + random.nextFloat()*spacingRange;
            if (gateAngle > 0) {
                gateY = (float) (lastGateY - spacing - Math.abs((gateX - startX) * Math.tan(gateAngle)));
            } else {
                gateY = (float) (lastGateY - spacing - Math.abs((startX + width - gateX) * Math.tan(gateAngle)));
            }
            gate.setGateCenter(gateX, gateY);
            if (inverted) {
                gate.setInverted(true);
            }
            lastGateY = lastGateY - spacing - gate.getHeight();
            if (lastGateY - minSpacing <= startY - length) {
                break;
            } else {
                gates.add(gate);
            }
        }
    }

    @Override
    public float getLength() {
        return length;
    }

    @Override
    public SubSection copy() {
        GateSubSection copy = new GateSubSection();
        copy.setLength(length);
        List<Gate> gatesCopy = new ArrayList<Gate>();
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
