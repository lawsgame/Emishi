package com.lawsgame.emishitactics.engine.patterns.statemachine;

import java.util.Stack;

public class StateMachine <S extends State> {
    protected Stack<S> states;

    public StateMachine (){
        states = new Stack<S>();
    }

    public void push (S s){
        if(states.size() > 0)
            states.peek().end();
        states.push(s);
        s.init();
    }

    public void rollback(){
        if(states.size() > 1){

            states.peek().end();
            states.pop().dispose();
            states.peek().init();

        }
    }

    public void replace(S s){
        if(states.size() > 0) {

            states.peek().end();
            states.pop().dispose();
            states.push(s);
            states.peek().init();

        }
    }

    public boolean isEmpty(){
        return states.size() == 0;
    }

    public S getCurrentState(){
        return states.peek();
    }


}
