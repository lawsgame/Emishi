package com.lawsgame.emishitactics.engine.patterns.observer;

import com.badlogic.gdx.utils.Array;

public class ObservableEventHolder extends Observable{
    private Array<Event> events = new Array<Event>();

    public void attach(Event event){
        events.add(event);
    }
    private void detach(Event event) { events.removeValue(event, true);}

    /**
     * to be called before the model has been change
     * @param data : any data relevant to execute the relevant events.
     */
    public void change(Object data){
        for(int i = 0; i < events.size; i++){
            if(events.get(i).isTriggered(data)) {
                events.get(i).execute(data);
            }
            if(events.get(i).isFinished()){
                events.removeIndex(i);
                i--;
            }
        }
    }
}
