package com.lawsgame.emishitactics.engine.geometry;

public interface Shape {
	boolean contains(float xPoint, float yPoint);
	float getX();
	float getY();
	void setX(float x);
	void setY(float y);
	void translate(float dx, float dy);
	Shape duplicate();
}
