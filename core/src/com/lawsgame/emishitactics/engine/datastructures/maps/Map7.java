package com.lawsgame.emishitactics.engine.datastructures.maps;

import java.util.HashMap;

public class Map7<A, B, C, D, E, F, G, H> {
    private A stdA = null;
    private B stdB = null;
    private C stdC = null;
    private D stdD = null;
    private E stdE = null;
    private F stdF = null;
    private G stdG = null;
    private HashMap<A, HashMap<B,HashMap<C, HashMap<D,HashMap<E, HashMap<F, HashMap<G,H>>>>>>> innerMap;

    public Map7(){
        this.innerMap = new HashMap<A, HashMap<B,HashMap<C, HashMap<D, java.util.HashMap<E, HashMap<F, HashMap<G,H>>>>>>>();
    }

    public Map7(A a, B b, C c, D d, E e, F f,  G g){
        this();
        this.stdA = a;
        this.stdB = b;
        this.stdC = c;
        this.stdD = d;
        this.stdE = e;
        this.stdF = f;
        this.stdG = g;
        boolean successfullyAdded = put(a, b, c, d, e, f, g, null);
        if(!successfullyAdded){
            try {
                throw new Exception("at least one of the standard arguments is NULL");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @param d : can value NULL
     */
    public boolean put(A a, B b, C c, D d, E e, F f, G g, H h){
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
            return true;
        }else{
            return false;
        }
    }

    public H get(A a, B b, C c, D d, E e, F f, G g){
        if(innerMap.get(a) == null){
            return (a != stdA) ? get(stdA, b, c, d, e, f, g) : null;
        }else{
            if(innerMap.get(a).get(b) == null){
                return (b != stdB) ? get(a, stdB, c, d, e, f, g) : null;
            }else{
                if(innerMap.get(a).get(b).get(c) == null){
                    return (c != stdC) ? get(a, b, stdC, d, e, f, g) : null;
                }else{
                    if(innerMap.get(a).get(b).get(c).get(d) == null){
                        return (d != stdD) ? get(a, b, c, stdD, e, f, g) : null;
                    }else{
                        if(innerMap.get(a).get(b).get(c).get(d).get(e) == null){
                            return (e != stdE) ? get(a, b, c, d, stdE, f, g) : null;
                        }else{
                            if(innerMap.get(a).get(b).get(c).get(d).get(e).get(f) == null){
                                return (f != stdF) ? get(a, b, c, d, e, stdF, g) : null;
                            }else{
                                if(innerMap.get(a).get(b).get(c).get(d).get(e).get(f).get(g) == null){
                                    return (g != stdG) ? get(stdA, b, c, d, e, f, stdG) : null;
                                }else {
                                    return innerMap.get(a).get(b).get(c).get(d).get(e).get(f).get(g);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public H getStandard(){
        return get(stdA, stdB, stdC,stdD,stdE,stdF,stdG);
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

    public A getStdA() {
        return stdA;
    }

    public B getStdB() {
        return stdB;
    }

    public C getStdC() {
        return stdC;
    }

    public D getStdD() {
        return stdD;
    }

    public E getStdE() {
        return stdE;
    }

    public F getStdF() {
        return stdF;
    }

    public G getStdG() {
        return stdG;
    }
}
