package com.provectusstudios.transverse;

import java.util.List;
import java.util.Random;

/**
 * Created by Justin on 9/30/2014.
 */
public interface SingleGateGenerator {
    public void generateGates(Random random, float verticalStart, float startX, float width);
    public void generateGates(Random random, float verticalStart, float startX, float width, float generateLength);
    public float getGenerateLength();
    public List<Gate> getGates();
}
