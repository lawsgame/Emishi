package com.lawsgame.emishitactics.engine.geometry;


/**
 * Created by jfwee on 28/09/2017.
 */

public class Point implements Shape {
    protected float x;
    protected float y;

    public Point(float x, float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean contains(float xPoint, float yPoint) {
        return xPoint == x && yPoint == y;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void translate(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public Shape duplicate() {
        return new Point(x,y);
    }
}
