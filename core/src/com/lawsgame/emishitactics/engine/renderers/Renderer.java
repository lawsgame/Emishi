package com.lawsgame.emishitactics.engine.renderers;

import com.badlogic.gdx.utils.Disposable;
import com.lawsgame.emishitactics.engine.GameElement;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

public abstract class Renderer<M extends Observable> implements Observer, GameUpdatableEntity, Disposable{
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

    public abstract boolean isExecuting();

    @Override
    public void dispose(){
        this.model.detach(this);
    }
}
