package com.lawsgame.emishitactics.core.renderers.interfaces;

import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

public abstract class Renderer<M extends Observable> implements Observer, GameUpdatableEntity{
      protected M model;

    public Renderer(M model){
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
