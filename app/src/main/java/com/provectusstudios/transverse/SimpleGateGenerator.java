package com.provectusstudios.transverse;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Justin on 9/30/2014.
 */
public class SimpleGateGenerator implements  SingleGateGenerator {
    private List<Gate> gates = new ArrayList<Gate>();
    private float generateLength;

    @Override
    public void generateGates(Random random, float verticalStart, float startX, float width) {
        generateLength = 750f + random.nextFloat()*2000f;
        generateGates(random, verticalStart, startX, width, generateLength);
    }

    @Override
    public void generateGates(Random random, float verticalStart, float startX, float width, float generateLength) {
        this.generateLength = generateLength;
        float minSpacing = 150f;
        float spacingRange = 300f*random.nextFloat();
        float angleRange = (float) (random.nextFloat()*Math.PI/2);
        float positionDeviation = width/9 + random.nextFloat()*(2*width/9);
        float positionMean = startX + width/2;
        float lengthMinimum = 75f;
        float lengthRange = 200f;
        float lastGateY;

        Gate gate = new Gate();
        float gateX = (float) (positionMean + positionDeviation*random.nextGaussian());
        if (gateX > startX + width - 50) {
            gateX = startX + width - 150;
        } else if (gateX < startX + 50) {
            gateX = startX + 150;
        }
        float gateLength = lengthMinimum + lengthRange*random.nextFloat();
        gate.setLength(gateLength);
        float gateAngle = -angleRange + random.nextFloat()*2*angleRange;
        gate.setAngle(gateAngle);
        float gateWidth = gate.getWidth();
        if (gateX - gateWidth/2 - 5 < startX) {
            gate.setLength((float) (2*(gateX - (startX + 5))/Math.cos(gateAngle)));
        } else if (gateX + gateWidth/2 + 5 > startX + width) {
            gate.setLength((float) (2 * (startX + width - 5 - gateX) / Math.cos(gateAngle)));
        }
        float spacing = minSpacing + random.nextFloat()*spacingRange;
        float gateY = verticalStart - spacing - gate.getHeight()/2;
        gate.setCenter(gateX, gateY);
        gate.refresh();
        gates.add(gate);
        lastGateY = gateY - gate.getHeight()/2;
        while (true) {
            gate = new Gate();
            gateX = (float) (positionMean + positionDeviation*random.nextGaussian());
            if (gateX > startX + width - 50) {
                gateX = startX + width - 150;
            } else if (gateX < startX + 50) {
                gateX = startX + 150;
            }
            gateLength = lengthMinimum + lengthRange*random.nextFloat();
            gate.setLength(gateLength);
            gateAngle = -angleRange + random.nextFloat()*2*angleRange;
            gate.setAngle(gateAngle);
            gateWidth = gate.getWidth();
            if (gateX - gateWidth/2 - 5 < startX) {
                gate.setLength((float) (2*(gateX - (startX + 5))/Math.cos(gateAngle)));
            } else if (gateX + gateWidth/2 + 5 > startX + width) {
                gate.setLength((float) (2 * (startX + width - 5 - gateX) / Math.cos(gateAngle)));
            }
            spacing = minSpacing + random.nextFloat()*spacingRange;
            gateY = lastGateY - spacing - gate.getHeight()/2;
            gate.setCenter(gateX, gateY);
            lastGateY = gateY - gate.getHeight()/2;
            if (lastGateY <= verticalStart - generateLength) {
                break;
            } else {
                gate.refresh();
                gates.add(gate);
            }
        }
    }

    @Override
    public float getGenerateLength() {
        return generateLength;
    }

    @Override
    public List<Gate> getGates() {
        return gates;
    }
}
