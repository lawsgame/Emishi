package com.lawsgame.emishitactics.engine.math.geometry;

@Deprecated
public interface Shape {

	float getXCenter();
	float getYCenter();
	void setXCenter(float x);
	void setYCenter(float y);

	Point getCenter();
	float getArea();
	float getPerimeter();

	boolean contains(float xPoint, float yPoint);
	boolean intersect(Shape shape);

	void translate(float dx, float dy);
	void rotate(float radiansClockwise);
	void scale(float factor);
	void scale(float factor, Point origin);

	Shape duplicate();
}
