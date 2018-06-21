package com.lawsgame.emishitactics.engine.math;

import com.lawsgame.emishitactics.engine.math.geometry.Point;

public class MathLGUtils {

    public static float dist(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt((x1 - x2)*(x1 - x2) + (y1 -y2)*(y1 - y2));
    }

    public static float dist(Point p1, Point p2){
        return dist(p1.x , p1.y, p2.x, p2.y);
    }

    public static float abs( float x){
        if(x < 0){
            x = -x;
        }
        return x;
    }
}
