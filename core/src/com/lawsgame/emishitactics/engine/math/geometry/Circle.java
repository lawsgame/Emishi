package com.lawsgame.emishitactics.engine.math.geometry;

import com.badlogic.gdx.math.MathUtils;

public class Circle implements Shape{
	protected float radius;
	protected float xCenter;
	protected float yCenter;
	
	

	public Circle() {
		this(0,0,0);
	}

	public Circle(float radius, float xCenter, float yCenter) {
		super();
		this.radius = radius;
		this.xCenter = xCenter;
		this.yCenter = yCenter;
	}
	
	@Override
	public Shape duplicate() {
		return new Circle(radius, xCenter, yCenter);
	}

	@Override
	public boolean contains(float xPoint, float yPoint) {
		return (xPoint - xCenter)*(xPoint - xCenter) + (yPoint - yCenter)*(yPoint - yCenter) < radius*radius;
	}

	@Override
	public boolean intersect(Shape shape) {
		if(shape instanceof Point){
			Point p = (Point)shape;
			return this.contains(p.x, p.y);
		}else if(shape instanceof  Circle){
			Circle c = new Circle();
			Vector v = new Vector(c.xCenter - xCenter, c.yCenter - yCenter);
			return (c.radius + radius) - v.length() >= 0;
		}else {
            shape.intersect(this);
        }
		return false;
	}

	@Override
	public float getXCenter() {
		return xCenter;
	}

	@Override
	public float getYCenter() {
		return yCenter;
	}

	@Override
	public void setXCenter(float x) {
		this.xCenter = x;
	}

	@Override
	public void setYCenter(float y) {
		this.yCenter = y;
	}

	@Override
	public Point getCenter() {
		return new Point(xCenter, yCenter);
	}

	@Override
	public float getArea() {
		return MathUtils.PI*radius*radius;
	}

	@Override
	public float getPerimeter() {
		return MathUtils.PI*2*radius;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	@Override
	public void translate(float dx, float dy) {
		xCenter += dx;
		yCenter += dy;
		
	}

	@Override
	public void rotate(float radiansClockwise) { }

	@Override
	public void scale(float factor) {
		radius *= factor;
	}

	@Override
	public void scale(float factor, Point origin) {
		scale(factor);
        Vector.vectorUtils.x = xCenter - origin.x;
        Vector.vectorUtils.y = yCenter - origin.y;
        Vector.vectorUtils.multiply(factor);
        setXCenter(Vector.vectorUtils.x + origin.x);
        setYCenter(Vector.vectorUtils.y + origin.y);
	}

	@Override
	public String toString() {
		return "Circle [radius=" + radius + ", xCenter=" + xCenter + ", yCenter=" + yCenter + "]";
	}

	
	

}
