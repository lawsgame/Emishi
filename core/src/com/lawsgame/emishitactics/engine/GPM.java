package com.lawsgame.emishitactics.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.engine.patterns.statemachine.StateMachine;

import java.util.Stack;

/**
 * GamePhaseManager or GPM is a machine state which handles the phase switching
 */

public class GPM extends StateMachine<GamePhase> implements GameElement {

	public void update (float dt){
		states.peek().update(dt);
	}
	
	public void render(SpriteBatch batch ){
		states.peek().render(batch);
	}


}
