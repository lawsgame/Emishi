package com.lawsgame.emishitactics.engine.datastructures;

import java.util.HashMap;

public class MatrixMap<A,B,C> {
	private HashMap<A, HashMap<B,C>> matrixmap;
	
	public MatrixMap(){
		this.matrixmap = new HashMap<A, HashMap<B,C>>();
	}
	
	public void put(A a, B b, C c){
		if(matrixmap.get(a) == null){
			matrixmap.put(a, new HashMap<B,C>());
		}
		matrixmap.get(a).put(b, c);
	}
	
	public C get(A a, B b){
		return matrixmap.get(a).get(b);
	}
}
