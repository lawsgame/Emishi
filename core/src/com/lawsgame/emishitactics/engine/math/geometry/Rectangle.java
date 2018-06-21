package com.lawsgame.emishitactics.engine.math.geometry;

public class Rectangle implements Shape{
	protected float xCenter;
	protected float yCenter;
	protected float width;
	protected float height;
	protected float radiansAngle;
	
	public Rectangle(float xCenter, float yCenter, float width, float heigth) {
		super();
		this.xCenter = xCenter;
		this.yCenter = yCenter;
		this.width = width;
		this.height = heigth;
	}
	
	public Rectangle(){
		this(0,0,0,0);
	}
	
	@Override
	public Shape duplicate() {
		return new Rectangle(xCenter, yCenter,width, height);
	}
	
	
	public float getXCenter() {
		return xCenter;
	}
	public void setXCenter(float x) {
		this.xCenter = x;
	}

	public float getYCenter() {
		return yCenter;
	}
	public void setYCenter(float y) {
		this.yCenter = y;
	}

	@Override
	public Point getCenter() {
		return new Point(xCenter, yCenter);
	}

	@Override
	public float getArea() {
		return width * height;
	}

	@Override
	public float getPerimeter() {
		return width*2 + height *2;
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
		boolean res = xCenter - width/2f <= xPoint && xCenter + width/2f >= xPoint && yCenter - height/2f <= yPoint && yPoint <= yCenter + height/2f;

		if(radiansAngle != 0){
			Point[] vertexes = getVertexes();
			Vector vp = new Vector(xPoint - xCenter, yPoint - yCenter);
			Vector vr0 = new Vector( vertexes[0].x - xCenter, vertexes[0].y - yCenter);
			Vector vr1 = new Vector( vertexes[1].x - xCenter, vertexes[1].y - yCenter);
			Vector vr2 = new Vector( vertexes[2].x - xCenter, vertexes[2].y - yCenter);
			Vector vr3 = new Vector( vertexes[3].x - xCenter, vertexes[3].y - yCenter);

			vp.rotate(-radiansAngle);
			vr0.rotate(-radiansAngle);
			vr1.rotate(-radiansAngle);
			vr2.rotate(-radiansAngle);
			vr3.rotate(-radiansAngle);


			//TODO:
			return false;
		}

		return res;
	}

	@Override
	public boolean intersect(Shape shape) {
		if(shape instanceof Point){
			Point p = (Point)shape;
			return this.contains(p.x, p.y);
		}else if (shape instanceof  Rectangle){
			//TODO:




		}else if(shape instanceof  Circle){
			//TODO:


		}else if (shape instanceof  Segment){
		    //TODO:


        }else{
            shape.intersect(this);
        }
		return false;
	}

	@Override
	public void translate(float dx, float dy) {
		xCenter += dx;
		yCenter += dy;
		
	}

	@Override
	public void rotate(float radiansClockwise) {
		radiansAngle = radiansClockwise;
	}

	@Override
	public void scale(float factor) {
		width *= factor;
		height *=factor;
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

	public Point[] getVertexes(){
		return new Point[4];
	}

	@Override
	public String toString() {
		return "Rectangle [xCenter=" + xCenter + ", yCenter=" + yCenter + ", width=" + width + ", height=" + height + "] rotate with an angle of "+radiansAngle+ " PI";
	}
	
}
