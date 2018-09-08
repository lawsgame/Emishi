package com.lawsgame.emishitactics.engine.datastructures;

import java.util.HashMap;

public class Map5<A, B, C, D, E, F> {
    private HashMap<A, HashMap<B,HashMap<C, HashMap<D,HashMap<E, F>>>>> innerMap;

    public Map5(){
        this.innerMap = new HashMap<A, HashMap<B,HashMap<C, HashMap<D, HashMap<E, F>>>>>();
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @param d : can value NULL
     */
    public void put(A a, B b, C c, D d, E e, F f){
        if(a != null && b != null && c != null){
            if(innerMap.get(a) == null)
                innerMap.put(a, new HashMap<B, HashMap<C, HashMap<D, HashMap<E, F>>>>());
            if(innerMap.get(a).get(b) == null)
                innerMap.get(a).put(b, new HashMap<C, HashMap<D, HashMap<E, F>>>());
            if(innerMap.get(a).get(b).get(c) == null)
                innerMap.get(a).get(b).put(c, new HashMap<D, HashMap<E, F>>());
            if(innerMap.get(a).get(b).get(c).get(d) == null)
                innerMap.get(a).get(b).get(c).put(d, new HashMap<E, F>());
            innerMap.get(a).get(b).get(c).get(d).put(e, f);
        }
    }

    public F get(A a, B b, C c, D d, E e){
        if(innerMap.get(a) != null
                && innerMap.get(a).get(b) != null
                && innerMap.get(a).get(b).get(c) != null
                && innerMap.get(a).get(b).get(c).get(d) != null) {
            return innerMap.get(a).get(b).get(c).get(d).get(e);
        }
        return null;
    }
}
