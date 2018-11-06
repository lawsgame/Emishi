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

    public boolean holdEvent(){
        return triggers.size > 0;
    }

    public String triggerToString(){
        String str = toString();
        str +=  "\n     registered TRIGGERS : ";
        for (int i = 0; i < triggers.size; i++) {
            str += "\n      trigger " + i + " : " + triggers.get(i);
            for (int j = 0; j < triggers.get(i).eventCommands.size; j++)
                str += "\n          event "+j+" : " + triggers.get(i).eventCommands.get(j).toString();
        }
        return str;
    }

}
