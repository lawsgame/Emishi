package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public abstract class Model extends Observable{
    private Array<Trigger> triggers;

    public Model(){
        this.triggers = new Array<Trigger>();
    }

    public void add(Trigger trigger){
        triggers.add(trigger);
    }
    public void remove(Trigger trigger) { triggers.removeValue(trigger, true);}

    public boolean isAnyEventTriggerable(Object data){

        for(int i =0; i < triggers.size; i++){
            if(triggers.get(i).isTriggered(data)){
                return true;
            }
        }
        return false;
    }

    public Array<Task> performEvents(Object data){
        Array<Task> tasks = new Array<Task>();

        for(int i = 0; i < triggers.size; i++){
            if(triggers.get(i).isTriggered(data)) {
                tasks.addAll(triggers.get(i).performEvent(data));
                if (triggers.get(i).isEmpty()) {
                    remove(triggers.get(i));
                    i--;
                }
            }
        }

        return tasks;
    }

}
