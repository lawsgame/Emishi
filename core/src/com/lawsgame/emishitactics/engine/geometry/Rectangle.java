package com.lawsgame.emishitactics.engine.geometry;

public class Rectangle implements Shape{
	protected float x;
	protected float y;
	protected float width;
	protected float height;
	
	
	
	public Rectangle(float x, float y, float width, float heigth) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = heigth;
	}
	
	public Rectangle(){
		this(0,0,0,0);
	}
	
	@Override
	public Shape duplicate() {
		return new Rectangle(x,y,width, height);
	}
	
	
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeigth() {
		return height;
	}
	public void setHeigth(float heigth) {
		this.height = heigth;
	}

	@Override
	public boolean contains(float xPoint, float yPoint) {
		return x <= xPoint && x + width >= xPoint && y <= yPoint && yPoint <= y + height;
	}

	@Override
	public void translate(float dx, float dy) {
		x += dx;
		y += dy;
		
	}

	@Override
	public String toString() {
		return "Rectangle [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
	}
	
}
