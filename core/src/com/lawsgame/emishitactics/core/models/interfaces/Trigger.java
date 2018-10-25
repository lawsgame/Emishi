package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.phases.battle.commands.EventCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;

public abstract class Trigger {
    private Array<EventCommand> eventCommands;
    private boolean once;

    public Trigger(boolean once){
        this.once = once;
        this.eventCommands = new Array<EventCommand>();
    }

    public abstract boolean isTriggered(Object data);

    public Array<Task> performEvent(Object data){
        Array<Task> tasks = new Array<Task>();
        if(isTriggered(data)){
            for(int i = 0; i < eventCommands.size; i++){
                if(eventCommands.get(i).isApplicable()){
                    eventCommands.get(i).setDecoupled(true);
                    eventCommands.get(i).apply();
                    tasks.addAll(eventCommands.get(i).confiscateTasks());
                }
            }
            if(once)
                eventCommands.clear();
        }
        return tasks;
    }

    public void addEvent(EventCommand event){
        this.eventCommands.add(event);
    }

    public boolean isEmpty(){
        return eventCommands.size == 0;
    }
}
