package com.lawsgame.emishitactics.engine.datastructures;

import java.util.HashMap;

public class Map3<A,B,C,D> {
    private HashMap<A, HashMap<B,HashMap<C, D>>> innerMap;

    public Map3(){
        this.innerMap = new HashMap<A, HashMap<B,HashMap<C, D>>>();
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @param d : can value NULL
     */
    public void put(A a, B b, C c, D d){
        if(a != null && b != null && c != null){
            if(innerMap.get(a) == null)
                innerMap.put(a, new HashMap<B, HashMap<C, D>>());
            if(innerMap.get(a).get(b) == null)
                innerMap.get(a).put(b, new HashMap<C, D>());
            innerMap.get(a).get(b).put(c, d);
        }
    }

    public D get(A a, B b, C c){
        if(innerMap.get(a) != null && innerMap.get(a).get(b) != null) {
            return innerMap.get(a).get(b).get(c);
        }
        return null;
    }
}
