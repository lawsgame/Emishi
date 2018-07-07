package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public class AnimationTask {

    protected Array<Observable> models;
    protected Array<Object> dataBundles;



    public AnimationTask(){
        models = new Array<Observable>();
        dataBundles = new Array<Object>();
    }

    public AnimationTask(Observable[] models, Object[] dataBundles) {
        this();
        set(models, dataBundles);
    }

    public AnimationTask(Observable model, Object dataBundle){
        this();
        set(model, dataBundle);
    }


    public void set(Observable model, Object dataBundle){
        this.models.clear();
        this.dataBundles.clear();
        addTarget(model, dataBundle);
    }

    public void set(Observable[] models, Object[] dataBundles) {
        this.models.clear();
        this.dataBundles.clear();
        if(models.length == dataBundles.length) {
            this.models.addAll(models);
            this.dataBundles.addAll(dataBundles);
        }
    }

    public void addTarget(Observable model, Object dataBundle){
        this.models.add(model);
        this.dataBundles.add(dataBundle);
    }

    public void dispatch(){
        for(int i = 0; i < models.size; i++){
            models.get(i).notifyAllObservers(dataBundles.get(i));
        }
    }
}
