package com.lawsgame.emishitactics.engine.patterns.statemachine;

import com.badlogic.gdx.utils.Disposable;

public interface State extends Disposable{
    void init();    // to be called whenever the state is activated
    void end();     // to be called whenever the state is disactivated
    //void dispose() => to be called whener the state is removed from the stack

}
