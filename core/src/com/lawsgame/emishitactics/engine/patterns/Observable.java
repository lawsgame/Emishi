package com.lawsgame.emishitactics.engine.patterns;

import com.badlogic.gdx.utils.Array;

public abstract class Observable {
	private Array<Observer> observers = new Array<Observer>();
	
	public void attach(Observer observer){
		observers.add(observer);
	}
	
	public void notifyAllObservers(Object dataBundle){
		for(int i = 0; i < observers.size; i++){
			observers.get(i).getNotification(dataBundle);
		}
	}

}
