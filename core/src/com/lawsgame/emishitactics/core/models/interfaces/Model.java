package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
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



    // ---------------------- TRIGGER ----------------------------------------------

    public static abstract class Trigger {
        protected final Array<BattleCommand> eventCommands;
        private boolean once;
        private String tag;

        public Trigger(boolean once){
            this.once = once;
            this.eventCommands = new Array<BattleCommand>();
            this.tag = "";
        }

        public abstract boolean isTriggered(Object data);

        public Array<Task> performEvent(Object data){
            Array<Task> tasks = new Array<Task>();
            if(isTriggered(data)){
                for(int i = 0; i < eventCommands.size; i++){
                    eventCommands.get(i).setDecoupled(true);
                    if(eventCommands.get(i).apply()){
                        tasks.addAll(eventCommands.get(i).confiscateTasks());
                    }
                }

                if(once) {
                    eventCommands.clear();
                }

            }
            return tasks;
        }

        public void addEvent(BattleCommand event){
            this.eventCommands.add(event);
        }

        public boolean isEmpty(){
            return eventCommands.size == 0;
        }

        public void setTag(String tag){
            this.tag = tag;
        }

        public String toString(){
            return (tag.equals("")) ? super.toString() : tag;
        }

    }
}
