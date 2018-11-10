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

    public void setAllTriggersActive(boolean active){
        for(int i =0; i < triggers.size; i++)
            triggers.get(i).setActive(active);
    }

    public boolean isAnyEventTriggerable(Object data){

        for(int i =0; i < triggers.size; i++){
            if(triggers.get(i).isActive() && triggers.get(i).isTriggered(data)){
                return true;
            }
        }
        return false;
    }

    public Array<Task> performEvents(Object data){
        Array<Task> tasks = new Array<Task>();

        for(int i = 0; i < triggers.size; i++){
            if(triggers.get(i).isActive() && triggers.get(i).isTriggered(data)) {
                tasks.addAll(triggers.get(i).performEvent());
                if (triggers.get(i).isEmpty() || triggers.get(i).isUseOnce()) {
                    remove(triggers.get(i));
                    i--;
                }
            }
        }

        return tasks;
    }

    public boolean holdEventTrigger(){
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
        private boolean sleepAfterOnce;
        private boolean useOnce;
        private boolean active;
        private String tag;

        public Trigger(boolean useOnce, boolean sleepAfterOnce){
            this.useOnce = useOnce;
            this.sleepAfterOnce = sleepAfterOnce;
            this.eventCommands = new Array<BattleCommand>();
            this.tag = "";
            this.active = true;
        }

        public abstract boolean isTriggered(Object data);

        Array<Task> performEvent(){
            Array<Task> tasks = new Array<Task>();

            for(int i = 0; i < eventCommands.size; i++){
                eventCommands.get(i).setDecoupled(true);
                if(eventCommands.get(i).apply()){
                    tasks.addAll(eventCommands.get(i).confiscateTasks());
                }
            }

            if(sleepAfterOnce){
                active = false;
            }

            return tasks;
        }

        void setActive(boolean active){
            this.active = active;
        }

        public void addEvent(BattleCommand event){
            this.eventCommands.add(event);
        }

        boolean isEmpty(){
            return eventCommands.size == 0;
        }

        boolean isUseOnce(){
            return useOnce;
        }

        boolean isActive(){
            return active;
        }

        public void setTag(String tag){
            this.tag = tag;
        }

        public String toString(){
            return (tag.equals("")) ? super.toString() : tag;
        }

    }
}
