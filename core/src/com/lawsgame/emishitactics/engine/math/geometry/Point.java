package com.lawsgame.emishitactics.engine.math.geometry;


/**
 * Created by jfwee on 28/09/2017.
 */

public class Point implements Shape {
    public float x;
    public float y;

    public Point(float x, float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean contains(float xPoint, float yPoint) {
        return xPoint == x && yPoint == y;
    }

    @Override
    public boolean intersect(Shape shape) {
        if(shape instanceof Point){
            Point p = (Point)shape;
            return this.contains(p.x, p.y);
        }else{
            shape.intersect(this);
        }
        return false;
    }

    @Override
    public float getXCenter() {
        return x;
    }

    @Override
    public float getYCenter() {
        return y;
    }

    @Override
    public void setXCenter(float x) {
        this.x = x;
    }

    @Override
    public void setYCenter(float y) {
        this.y = y;
    }

    @Override
    public Point getCenter() {
        return new Point(x,y);
    }

    @Override
    public float getArea() {
        return 0;
    }

    @Override
    public float getPerimeter() {
        return 0;
    }

    @Override
    public void translate(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public void rotate(float radiansClockwise) { }

    @Override
    public void scale(float factor) { }

    @Override
    public void scale(float factor, Point origin) {
        Vector.vectorUtils.x = x - origin.x;
        Vector.vectorUtils.y = y - origin.y;
        Vector.vectorUtils.multiply(factor);
        setXCenter(Vector.vectorUtils.x + origin.x);
        setYCenter(Vector.vectorUtils.y + origin.y);
    }

    @Override
    public Shape duplicate() {
        return new Point(x,y);
    }
}
