package com.lawsgame.emishitactics.engine.rendering;

import com.badlogic.gdx.utils.Disposable;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

public abstract class Renderer<M extends Observable> implements Observer, GameUpdatableEntity, Disposable{
    private M model;

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
    public abstract void setVisible(boolean visible);

    @Override
    public void dispose(){
        this.model.detach(this);
    }

    @Override
    public String toString() {
        return "renderer of "+model.toString();
    }

}
