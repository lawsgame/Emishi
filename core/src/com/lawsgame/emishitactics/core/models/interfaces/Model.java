package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
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
            if(triggers.get(i).isActive() && triggers.get(i).isTriggerable(data)){
                return true;
            }
        }
        return false;
    }

    public Array<Task> performEvents(Object data, final ActorCommand.Outcome callerOutcome){
        Array<Task> tasks = new Array<Task>();
        for(int i = 0; i < triggers.size; i++){
            if(triggers.get(i).isTriggered(data)) {
                tasks.addAll(triggers.get(i).performEvent(callerOutcome));
                if (triggers.get(i).isUseOnce()) {
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

    public boolean searchForEventType(Class<? extends BattleCommand> eventType){
        for(int i = 0; i < triggers.size; i++){
            if(eventType.isInstance(triggers.get(i).eventCommand)){
                return true;
            }
        }
        return false;
    }



    //------------------- GETTERS & SETTERS ---------------------

    public String triggerToString(){
        String str = toString();
        str +=  "\n     registered TRIGGERS : ";
        for (int i = 0; i < triggers.size; i++) {
            str += "\n          trigger " + i + " : " + triggers.get(i);
            str += "\n              event : " + triggers.get(i).getEventCommand().toString();
        }
        return str;
    }




    // ---------------------- TRIGGER ----------------------------------------------

    public static abstract class Trigger {
        private BattleCommand eventCommand;
        private boolean useOnce;
        private boolean active;
        private String tag;

        public Trigger(boolean useOnce, BattleCommand eventCommand){
            this.useOnce = useOnce;
            this.tag = "";
            this.active = true;
            this.eventCommand = eventCommand;
            this.eventCommand.setDecoupled(true);
        }




        //-------------------- PROCESS -------------------------------


        protected abstract boolean isTriggerable(Object data);

        protected  boolean isTriggered(Object data){
            return isActive() && isTriggerable(data)  && eventCommand.isApplicable();
        }

        /**
         *
         * @param callerOutcome : outcome of the source of the event triggering ( NotNull is it is an ActorCommand)
         * @return the render tasks generate to those events.
         */
        Array<Task> performEvent(ActorCommand.Outcome callerOutcome){
            Array<Task> tasks = new Array<Task>();


            if(eventCommand.apply()){
                callerOutcome.merge( eventCommand.getOutcome());
                tasks.addAll(eventCommand.confiscateTasks());
            }

            return tasks;
        }




        // --------------------- SETTERS & GETTERS -----------------------------------

        public void setEvent(BattleCommand event){
            this.eventCommand = event;
            this.eventCommand.setDecoupled(true);
        }

        public BattleCommand getEventCommand(){
            return eventCommand;
        }

        boolean isUseOnce(){
            return useOnce;
        }

        boolean isActive(){
            return active;
        }

        void setActive(boolean active){
            this.active = active;
        }

        public void setTag(String tag){
            this.tag = tag;
        }

        public String toString(){
            return (tag.equals("")) ? super.toString() : tag;
        }

    }
}
