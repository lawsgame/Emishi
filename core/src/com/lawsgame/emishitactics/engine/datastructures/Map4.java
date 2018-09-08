package com.lawsgame.emishitactics.engine.datastructures;

import java.util.HashMap;

public class Map4<A, B, C, D, E> {
    private HashMap<A, HashMap<B,HashMap<C, HashMap<D,E>>>> innerMap;

    public Map4(){
        this.innerMap = new HashMap<A, HashMap<B,HashMap<C, HashMap<D, E>>>>();
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @param d : can value NULL
     */
    public void put(A a, B b, C c, D d, E e){
        if(a != null && b != null && c != null){
            if(innerMap.get(a) == null)
                innerMap.put(a, new HashMap<B, HashMap<C, HashMap<D, E>>>());
            if(innerMap.get(a).get(b) == null)
                innerMap.get(a).put(b, new HashMap<C, HashMap<D, E>>());
            if(innerMap.get(a).get(b).get(c) == null)
                innerMap.get(a).get(b).put(c, new HashMap<D, E>());
            innerMap.get(a).get(b).get(c).put(d, e);
        }
    }

    public E get(A a, B b, C c, D d){
        if(innerMap.get(a) != null
                && innerMap.get(a).get(b) != null
                && innerMap.get(a).get(b).get(c) != null) {
            return innerMap.get(a).get(b).get(c).get(d);
        }
        return null;
    }
}
