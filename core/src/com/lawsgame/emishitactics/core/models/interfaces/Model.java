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

    public boolean isAnyEventTriggerable(){
        for(int i =0; i < triggers.size; i++){
            if(triggers.get(i).isTriggered()){
                return true;
            }
        }
        return false;
    }

    public Array<Task> performEvents(){
        Array<Task> tasks = new Array<Task>();
        for(int i = 0; i < triggers.size; i++){
            if(triggers.get(i).isTriggered()) {
                tasks.addAll(triggers.get(i).performEvent());
                if (triggers.get(i).isEmpty()) {
                    triggers.removeIndex(i);
                    i--;
                }
            }
        }
        return tasks;
    }

}
