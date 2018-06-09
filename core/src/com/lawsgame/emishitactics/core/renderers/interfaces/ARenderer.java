package com.lawsgame.emishitactics.core.renderers.interfaces;

import com.lawsgame.emishitactics.engine.GameElement;
import com.lawsgame.emishitactics.engine.patterns.Observable;
import com.lawsgame.emishitactics.engine.patterns.Observer;

public abstract class ARenderer<M extends Observable> implements Observer, GameElement{
    protected M model;

    public ARenderer(M model){
        setModel(model);
    }

    public M getModel(){
        return model;
    }

    public void setModel(M model){
        this.model = model;
        this.model.attach(this);
    }
}
