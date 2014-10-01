package com.provectusstudios.transverse;

import java.util.List;
import java.util.Random;

/**
 * Created by Justin on 9/30/2014.
 */
public interface GateGenerator {
    public void generateGates(Random random, float verticalStart, float screenWidth);
    public void generateGates(Random random, float verticalStart, float screenWidth, float generateLength);
    public List<Gate> getLeftGates();
    public List<Gate> getRightGates();
    public float getGenerateLength();
}
