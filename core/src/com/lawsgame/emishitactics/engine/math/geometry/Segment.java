package com.lawsgame.emishitactics.engine.math.geometry;


import com.lawsgame.emishitactics.engine.math.MathLGUtils;

public class Segment implements Shape{
    public Point p1;
    public Point p2;

    public Segment(Point p1, Point p2){
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public float getXCenter() {
        return (p1.x + p2.x)/2;
    }

    @Override
    public float getYCenter() {
        return (p1.y + p2.y)/2;
    }

    @Override
    public void setXCenter(float x) {
        float dx = x - getXCenter();
        p1.x += dx;
    }

    @Override
    public void setYCenter(float y) {
        float dy = y - getYCenter();
        p1.x += dy;
    }

    @Override
    public Point getCenter() {
        return new Point(getXCenter(), getYCenter());
    }

    @Override
    public float getArea() {
        return 0;
    }

    @Override
    public float getPerimeter() {
        return (float)Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
    }

    public float getSlope(){
        return (p2.y - p1.y)/(p2.x - p1.x);
    }


    public float getBase(){
        return p2.y - getSlope()*p2.x;
    }

    @Override
    public boolean contains(float xPoint, float yPoint) {
        boolean withinDomain = true;
        if(p2.y >p1.y){
            withinDomain = withinDomain && p1.y <= yPoint && yPoint <= p2.y;
        }else{
            withinDomain = withinDomain && p1.y >= yPoint && yPoint >= p2.y;
        }
        return withinDomain && getBase() + xPoint*getSlope() == yPoint ;
    }

    @Override
    public boolean intersect(Shape shape) {
        if(shape instanceof Point){
            Point p = (Point)shape;
            contains(p.x, p.y);
        }else if(shape instanceof Circle){
            Circle c = (Circle)shape;
            if(MathLGUtils.dist(p1.x, p1.y, c.getXCenter(), c.getYCenter()) <= c.radius || MathLGUtils.dist(p2.x, p2.y, c.getXCenter(), c.getYCenter()) <= c.radius){
                // at least one of the summits is within the circle
                return true;
            }else {
                float dist = (float) (Math.abs(-getSlope() * c.getXCenter() + getYCenter() - getBase()) / Math.sqrt(c.getXCenter() * c.getYCenter() + c.getYCenter() * c.getYCenter()));
                if (dist > c.radius) {
                    return false;
                } else {
                    // TODO:
                    // treat the last use case when neither of the summit are within the circle but the distance
                    // between the center of the circle and neareat point of the associated straight line are below the radius requiredRange.
                    return false;
                }
            }
        }else if(shape instanceof Segment){
            Segment s = (Segment)shape;
            float thisSlope = getSlope();
            float sslope = s.getSlope();
            if(sslope == thisSlope){
                if(contains(s.p1.x, s.p1.y) || contains(s.p2.x, s.p2.y)){
                    //confound
                    return true;
                }else{
                    //parallel
                    return false;
                }
            }else{
                // non parallel
                boolean res = true;
                float xInterP = (s.getBase() - getBase()) / (thisSlope - sslope);
                if(p1.x < p2.x){
                    res = res && p1.x <= xInterP && xInterP <= p2.x;
                }else{
                    res = res && p2.x <= xInterP && xInterP <= p1.x;
                }
                if(s.p1.x < s.p2.x){
                    res = res && s.p1.x <= xInterP && xInterP <= s.p2.x;
                }else{
                    res = res && s.p2.x <= xInterP && xInterP <= s.p1.x;
                }
                return res;
            }
        }else{
            shape.intersect(this);
        }
        return false;
    }

    @Override
    public void translate(float dx, float dy) {
        p1.x += dx;
        p2.x += dx;
        p1.y += dy;
        p2.y += dy;
    }

    @Override
    public void rotate(float radiansClockwise) {
        Vector.vectorUtils.x = p2.x - p1.x;
        Vector.vectorUtils.y = p2.y - p2.y;
        Vector.vectorUtils.rotate(radiansClockwise);
        Vector.vectorUtils.multiply(0.5f);

        float xCenter = getXCenter();
        float yCenter = getYCenter();

        p2.x = Vector.vectorUtils.x + xCenter;
        p2.y = Vector.vectorUtils.y + yCenter;
        p1.x = - Vector.vectorUtils.x + xCenter;
        p1.y = - Vector.vectorUtils.y + yCenter;
    }

    @Override
    public void scale(float factor) {
        Vector.vectorUtils.x = p2.x - p1.x;
        Vector.vectorUtils.y = p2.y - p2.y;
        Vector.vectorUtils.multiply(factor/2f);

        float xCenter = getXCenter();
        float yCenter = getYCenter();

        p2.x = Vector.vectorUtils.x + xCenter;
        p2.y = Vector.vectorUtils.y + yCenter;
        p1.x = - Vector.vectorUtils.x + xCenter;
        p1.y = - Vector.vectorUtils.y + yCenter;
    }

    @Override
    public void scale(float factor, Point origin) {
        scale(factor);

        Vector.vectorUtils.x = getXCenter() - origin.x;
        Vector.vectorUtils.y = getYCenter() - origin.y;
        Vector.vectorUtils.multiply(factor);
        setXCenter(Vector.vectorUtils.x + origin.x);
        setYCenter(Vector.vectorUtils.y + origin.y);
    }

    @Override
    public Shape duplicate() {
        return new Segment((Point)p1.duplicate(), (Point)p2.duplicate());
    }
}
