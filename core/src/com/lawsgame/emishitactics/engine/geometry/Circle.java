package com.lawsgame.emishitactics.engine.geometry;

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
	public float getX() {
		return xCenter;
	}

	@Override
	public float getY() {
		return yCenter;
	}

	@Override
	public void setX(float x) {
		this.xCenter = x;
	}

	@Override
	public void setY(float y) {
		this.yCenter = y;
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
	public String toString() {
		return "Circle [radius=" + radius + ", xCenter=" + xCenter + ", yCenter=" + yCenter + "]";
	}

	
	

}
