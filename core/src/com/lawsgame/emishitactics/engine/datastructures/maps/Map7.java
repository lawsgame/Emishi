package com.lawsgame.emishitactics.engine.datastructures.maps;

import java.util.HashMap;

public class Map7<A, B, C, D, E, F, G, H> {

    private HashMap<A, HashMap<B,HashMap<C, HashMap<D,HashMap<E, HashMap<F, HashMap<G,H>>>>>>> innerMap;

    public Map7(){
        this.innerMap = new HashMap<A, HashMap<B,HashMap<C, HashMap<D, java.util.HashMap<E, HashMap<F, HashMap<G,H>>>>>>>();
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @param d : can value NULL
     */
    public void put(A a, B b, C c, D d, E e, F f, G g, H h){
        if(a != null && b != null && c != null && d != null && e != null && f != null && g != null){
            if(innerMap.get(a) == null)
                innerMap.put(a, new HashMap<B, HashMap<C, HashMap<D, HashMap<E, HashMap<F, HashMap<G,H>>>>>>());
            if(innerMap.get(a).get(b) == null)
                innerMap.get(a).put(b, new HashMap<C, HashMap<D, HashMap<E, HashMap<F, HashMap<G,H>>>>>());
            if(innerMap.get(a).get(b).get(c) == null)
                innerMap.get(a).get(b).put(c, new HashMap<D, HashMap<E, HashMap<F, HashMap<G,H>>>>());
            if(innerMap.get(a).get(b).get(c).get(d) == null)
                innerMap.get(a).get(b).get(c).put(d, new HashMap<E, HashMap<F, HashMap<G,H>>>());
            if(innerMap.get(a).get(b).get(c).get(d).get(e) == null)
                innerMap.get(a).get(b).get(c).get(d).put(e, new HashMap<F, HashMap<G,H>>());
            if(innerMap.get(a).get(b).get(c).get(d).get(e).get(f) == null)
                innerMap.get(a).get(b).get(c).get(d).get(e).put(f, new HashMap<G,H>());
            innerMap.get(a).get(b).get(c).get(d).get(e).get(f).put(g, h);
        }
    }

    public H get(A a, B b, C c, D d, E e, F f, G g){
        if(innerMap.get(a) != null
                && innerMap.get(a).get(b) != null
                && innerMap.get(a).get(b).get(c) != null
                && innerMap.get(a).get(b).get(c).get(d) != null
                && innerMap.get(a).get(b).get(c).get(d).get(f) != null) {
            return innerMap.get(a).get(b).get(c).get(d).get(e).get(f).get(g);
        }
        return null;
    }

    @Override
    public String toString(){
        StringBuilder res = new StringBuilder("\nMap 7 =");
        for(A a : innerMap.keySet()){
            res.append("\n  ");
            res.append(a.toString());
            for(B b : innerMap.get(a).keySet()){
                res.append((innerMap.get(a).keySet().size() == 1) ? " : " : "\n    ");
                res.append(b.toString());
                for(C c : innerMap.get(a).get(b).keySet()){
                    res.append((innerMap.get(a).get(b).keySet().size() == 1) ? " : " : "\n      ");
                    res.append(c.toString());
                    for(D d : innerMap.get(a).get(b).get(c).keySet()){
                        res.append((innerMap.get(a).get(b).get(c).keySet().size() == 1) ? " : " : "\n        ");
                        res.append(d.toString());
                        for(E e : innerMap.get(a).get(b).get(c).get(d).keySet()){
                            res.append((innerMap.get(a).get(b).get(c).get(d).keySet().size() == 1) ? " : " : "\n          ");
                            res.append(e.toString());
                            for(F f : innerMap.get(a).get(b).get(c).get(d).get(e).keySet()){
                                res.append((innerMap.get(a).get(b).get(c).get(d).get(e).keySet().size() == 1) ? " : " : "\n            ");
                                res.append(f.toString());
                                for(G g : innerMap.get(a).get(b).get(c).get(d).get(e).get(f).keySet()){
                                    res.append((innerMap.get(a).get(b).get(c).get(d).get(e).get(f).keySet().size() == 1) ? " : " : "\n              ");
                                    res.append(g.toString());
                                    res.append(" : ");
                                    res.append(innerMap.get(a).get(b).get(c).get(d).get(e).get(f).get(g).toString());
                                }
                            }
                        }
                    }
                }
            }
        }
        return res.toString();
    }
}
