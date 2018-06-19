package com.lawsgame.emishitactics.engine.inputs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.lawsgame.emishitactics.engine.patterns.statemachine.State;

public abstract class InteractionState extends AndroidInputHandler implements State{

    public InteractionState(OrthographicCamera camera) {
        super(camera);
    }

    @Override
    public void init() {
        Gdx.input.setInputProcessor(this);
        initiate();
    }

    public abstract void initiate();
}

