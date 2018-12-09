package com.lawsgame.emishitactics.engine.datastructures.maps;

import java.util.HashMap;

public class Map2<A,B,C> {
	private HashMap<A, HashMap<B,C>> matrixmap;
	
	public Map2(){
		this.matrixmap = new HashMap<A, HashMap<B,C>>();
	}
	
	public void put(A a, B b, C c){
		if(a != null && b != null) {
			if (matrixmap.get(a) == null)
				matrixmap.put(a, new HashMap<B, C>());
			matrixmap.get(a).put(b, c);
		}
	}
	
	public C get(A a, B b){
	    if(matrixmap.get(a) != null) {
            return matrixmap.get(a).get(b);
        }
        return null;
	}
}
