package com.lawsgame.emishitactics.engine.patterns;

import com.lawsgame.emishitactics.engine.patterns.statemachine.State;
import com.lawsgame.emishitactics.engine.patterns.statemachine.StateMachine;

import org.junit.Before;
import org.junit.Test;

public class StateMachineTest {
    private StateMachine<TestState> bim ;
    private TestState bis1;
    private TestState bis2;
    private TestState bis3;

    public StateMachineTest(){}

    @Before
    public void beforeTest(){
        bim = new StateMachine<TestState>();
        bis1 = new TestState( "1");
        bis2 = new TestState("2");
        bis3 = new TestState( "3");

        bim.rollback();
        bim.replace(bis1);
        bim.push(bis2);
        bim.push(bis1);
        bim.replace(bis3);
        bim.rollback();

    }

     @Test
     public void test(){

     }

    static class TestState implements State{
        private String tag;

        public TestState(String tag) {
            this.tag = tag;
        }

        @Override
        public void init() {

            //System.out.println(tag+" => INIT");
        }

        @Override
        public void dispose() {

            //System.out.println(tag+" => DISPOSE");
        }

        @Override
        public void end() {

            //System.out.println(tag+" => END");
        }
    }

}
