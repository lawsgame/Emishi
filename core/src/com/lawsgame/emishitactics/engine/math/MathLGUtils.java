package com.lawsgame.emishitactics.engine.math;



public class MathLGUtils {

    public static float dist(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt((x1 - x2)*(x1 - x2) + (y1 -y2)*(y1 - y2));
    }
}
