package com.lawsgame.emishitactics.engine.geometry;

public class Vector {
	public float x;
	public float y;
	
	public Vector(float vx, float vy) {
		super();
		this.x = vx;
		this.y = vy;
	}
	
	public Vector(Vector copy){
		this(copy.x, copy.y);
	}
	
	public Vector duplicate(){
		return new Vector(this);
	}
	
	public void normalize(){
		float xtempo = x;
		if(x != 0 && y != 0){
			x = (float) (x/(Math.sqrt(x*x + y*y)));
			y = (float) (y/(Math.sqrt(xtempo*xtempo + y*y)));
		}else if( x != 0f){
			if(x < 0){
				x = -1;
			}else{
				x = 1;
			}
			y = 0;
		}else if( y != 0f){
			if(y < 0){
				y = -1;
			}else{
				y = 1;
			}
			x = 0;
		}else{
			y = 0;
			x = 0;
		}
	}
	
	public void multiply(float value){
		x *= value;
		y *= value;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Vector){
			Vector vector = (Vector)obj;
			return vector.x == x &&  vector.y == y;
		}
		return false;
	}

	public float length() {
		return (float)Math.sqrt(x*x + y*y);
	}

	public static float scalprod(Vector v, Vector vtempo) {
		return v.x*vtempo.x + v.y*vtempo.y;
	}
	
	public String toString(){
		return "("+x+","+y+")";
	}
	
}
