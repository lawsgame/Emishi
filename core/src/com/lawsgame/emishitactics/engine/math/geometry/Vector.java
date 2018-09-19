package com.lawsgame.emishitactics.engine.math.geometry;

public class Vector {
	public float x;
	public float y;

	static Vector vectorUtils = new Vector(0,0);

	public Vector(){
		this(0, 0);
	}

	public Vector(float vx, float vy) {
		super();
		this.x = vx;
		this.y = vy;
	}

	public Vector(Point originPoint, Point endPoint){
		this(endPoint.x - originPoint.x, endPoint.y - originPoint.y);
	}
	
	public Vector(Vector original){
		this(original.x, original.y);
	}
	
	public Vector normalize(){
		float xtempo = x;
		if(!(x == 0 && y == 0)){
			x = (float) (x/(Math.sqrt(x*x + y*y)));
			y = (float) (y/(Math.sqrt(xtempo*xtempo + y*y)));
		}else{
			y = 0;
			x = 0;
		}
		return this;
	}
	
	public Vector multiply(float value){
		x *= value;
		y *= value;
		return this;
	}

	public void apply(Matrix m){
        float xtempo = x;
        float ytempo = y;
        x = m.getValue(0,0)*xtempo + m.getValue(0,1)*ytempo;
        y = m.getValue(1,0)*xtempo + m.getValue(1,1)*ytempo;
    }

	public void rotate(float radiansClockwise){
        apply(Matrix.getRotationMatrix(radiansClockwise));
	}

	public float length() {
		return (float)Math.sqrt(x*x + y*y);
	}

	public static float scalprod(Vector v, Vector vtempo) {
		return v.x*vtempo.x + v.y*vtempo.y;
	}

	public static boolean colinear(Vector v1, Vector v2){
		return v1.x*v2.y - v2.x*v1.y == 0;
	}

	public static float vectprod(Vector v1, Vector v2){
		return v1.x*v2.y - v2.x*v1.y;
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof Vector){
			Vector vector = (Vector)obj;
			return vector.x == x &&  vector.y == y;
		}
		return false;
	}
	
	public String toString(){
		return "("+x+","+y+")";
	}
	
}
