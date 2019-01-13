package com.lawsgame.emishitactics.engine.datastructures.maps;

import java.util.HashMap;

public class Map6<A, B, C, D, E, F, G> {

    private HashMap<A, HashMap<B,HashMap<C, HashMap<D,HashMap<E, HashMap<F, G>>>>>> innerMap;

    public Map6(){
        this.innerMap = new HashMap<A, HashMap<B,HashMap<C, HashMap<D, java.util.HashMap<E, HashMap<F, G>>>>>>();
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @param d : can value NULL
     */
    public void put(A a, B b, C c, D d, E e, F f, G g){
        if(a != null && b != null && c != null && d != null && e != null && f != null){
            if(innerMap.get(a) == null)
                innerMap.put(a, new HashMap<B, HashMap<C, HashMap<D, HashMap<E, HashMap<F, G>>>>>());
            if(innerMap.get(a).get(b) == null)
                innerMap.get(a).put(b, new HashMap<C, HashMap<D, HashMap<E, HashMap<F, G>>>>());
            if(innerMap.get(a).get(b).get(c) == null)
                innerMap.get(a).get(b).put(c, new HashMap<D, HashMap<E, HashMap<F, G>>>());
            if(innerMap.get(a).get(b).get(c).get(d) == null)
                innerMap.get(a).get(b).get(c).put(d, new HashMap<E, HashMap<F, G>>());
            if(innerMap.get(a).get(b).get(c).get(d).get(e) == null)
                innerMap.get(a).get(b).get(c).get(d).put(e, new HashMap<F, G>());
            innerMap.get(a).get(b).get(c).get(d).get(e).put(f, g);
        }
    }

    public G get(A a, B b, C c, D d, E e, F f){
        if(innerMap.get(a) != null
                && innerMap.get(a).get(b) != null
                && innerMap.get(a).get(b).get(c) != null
                && innerMap.get(a).get(b).get(c).get(d) != null) {
            return innerMap.get(a).get(b).get(c).get(d).get(e).get(f);
        }
        return null;
    }
}
