package com.lawsgame.emishitactics.engine.patterns.observer;

import com.badlogic.gdx.utils.Array;

public abstract class Observable {
	private Array<Observer> observers = new Array<Observer>();
	
	public void attach(Observer observer){
		observers.add(observer);
	}
	public void detach(Observer observer) { observers.removeValue(observer, true);}
	
	public void notifyAllObservers(Object dataBundle){
		for(int i = 0; i < observers.size; i++){
			observers.get(i).getNotification(this, dataBundle);
		}
	}

	public String displayObservers(){
		String str = "OBSERVERS";
		for(int i = 0; i < observers.size; i++){
			str += "\n  "+observers.get(i).toString();
		}
		return str;
	}
}
