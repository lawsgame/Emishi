package com.lawsgame.emishitactics.engine.datastructures.maps;

import java.util.HashMap;

public class Map2<A,B,C> {
    private A stdA;
    private B stdB;
    private HashMap<A, HashMap<B,C>> innerMap;

    public Map2(){
        this.innerMap = new HashMap<A, HashMap<B,C>>();
    }

    public Map2(A a, B b){
        this();
        this.stdA = a;
        this.stdB = b;
        boolean successfullyAdded = put(a, b, null);
        if(!successfullyAdded){
            try {
                throw new Exception("at least one of the standard arguments is NULL");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
	
	public boolean put(A a, B b, C c){
		if(a != null && b != null) {
			if (innerMap.get(a) == null)
				innerMap.put(a, new HashMap<B, C>());
			innerMap.get(a).put(b, c);
			return true;
		}
		return false;
	}
	
	public C get(A a, B b){
	    if(innerMap.get(a) == null) {
	        return (a == stdA) ?  get(stdA, b) : null;
        }else{
            if(innerMap.get(a).get(b) == null) {
                return (b == stdB) ? get(a, stdB) : null;
            }else {
                return innerMap.get(a).get(b);
            }
        }
	}


	public C getStandard(){
        return get(stdA, stdB);
    }

    public A getStdA() {
        return stdA;
    }

    public B getStdB() {
        return stdB;
    }
}
