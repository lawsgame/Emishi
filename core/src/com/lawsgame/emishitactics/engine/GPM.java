package com.lawsgame.emishitactics.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * GamePhaseManager or GPM is a machine state which handles the phase switching
 */

public class GPM implements GameElement {
	private Stack<GamePhase> states;
	
	public GPM (){
		states = new Stack<GamePhase>();
	}
	
	public void push (GamePhase s){
		states.push(s);
	}
	
	public void pop(){
		GamePhase s = states.pop();
		s.dispose();
		
	}
	
	public void set(GamePhase s){
		pop();
		push(s);
	}
	
	public void update (float dt){
		states.peek().update(dt);
	}
	
	public void render(SpriteBatch batch ){
		states.peek().render(batch);
	}

	public GamePhase getCurrentGamePhase(){
		return states.peek();
	}
}
