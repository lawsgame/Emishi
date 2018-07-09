package com.lawsgame.emishitactics.engine.patterns.statemachine;

import java.util.Stack;

public abstract class StateMachine <S extends State> {
    protected Stack<S> states;

    public StateMachine (){
        states = new Stack<S>();
    }

    public void push (S s){
        states.push(s);
        s.init();
    }

    public void pop(){
        if(states.size() > 0) {
            State s = states.pop();
            s.dispose();
        }
    }

    public void set(S s){
        states.pop();
        push(s);
    }

    public S getCurrentState(){
        return states.peek();
    }


}
