package com.provectusstudios.transverse;

import java.util.List;
import java.util.Random;

/**
 * Created by Justin on 9/30/2014.
 */
public class DualGateGenerator implements GateGenerator {
    private List<Gate> leftGates;
    private List<Gate> rightGates;
    private float generateLength;
    private SingleGateGenerator leftGen;
    private SingleGateGenerator rightGen;

    public DualGateGenerator(SingleGateGenerator leftGen, SingleGateGenerator rightGen) {
        this.leftGen = leftGen;
        this.rightGen = rightGen;
    }

    @Override
    public void generateGates(Random random, float verticalStart, float screenWidth) {
        leftGen.generateGates(random, verticalStart, 0, screenWidth/2);
        rightGen.generateGates(random, verticalStart, screenWidth/2, screenWidth/2, leftGen.getGenerateLength());
        generateLength = leftGen.getGenerateLength();
        leftGates = leftGen.getGates();
        rightGates = rightGen.getGates();
    }

    @Override
    public void generateGates(Random random, float verticalStart, float screenWidth, float generateLength) {
        leftGen.generateGates(random, verticalStart, 0, screenWidth/2, generateLength);
        rightGen.generateGates(random, verticalStart, screenWidth/2, screenWidth/2, generateLength);
        this.generateLength = generateLength;
        leftGates = leftGen.getGates();
        rightGates = rightGen.getGates();
    }

    @Override
    public List<Gate> getLeftGates() {
        return leftGates;
    }

    @Override
    public List<Gate> getRightGates() {
        return rightGates;
    }

    @Override
    public float getGenerateLength() {
        return generateLength;
    }
}
