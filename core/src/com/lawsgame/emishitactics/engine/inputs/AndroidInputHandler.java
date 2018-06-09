package com.lawsgame.emishitactics.engine.inputs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.timers.CountDown;

/**
 *
 * A AndroidInputHandler is an simple implementation of an InputProcessor specific for android games
 *
 * Allow to cleanly capture three type of events
 * /onTouch = single touch
 * /onDoubleTouch = double touch
 * /pan = drag on
 *
 * HOW TO:
 * 1) create a AndroidInputHandler instance
 * 2) use Gdx.input.setInputPrecessor to enable it
 * 3) don't forget to call update()
 *
 */

public abstract class AndroidInputHandler implements InputProcessor, GameUpdatableEntity {

	private static final float INPUT_STANDARD_DELAY = 0.18f;

	private int history;
	private InputState state;
	private CountDown timer;
	private Vector3 pos = new Vector3();
	private Vector3 newPanPos = new Vector3();
	private Vector3 diff = new Vector3();
	protected OrthographicCamera gameCam;

	public enum InputState{
		NONE,
		SINGLE_TAP,
		DOUBLE_TAP,
		PAN
    }
	
	public AndroidInputHandler(float delay, OrthographicCamera gameCam){
		timer = new CountDown(delay);
		history = 0;
		state = InputState.NONE;
		this.gameCam = gameCam;
	}

	public AndroidInputHandler(OrthographicCamera gameCam){
		this(INPUT_STANDARD_DELAY, gameCam);
	}
	
	@Override
	public void update(float dt) {
		timer.update(dt);
		
		
		switch(state){
		case DOUBLE_TAP:
			this.state = InputState.NONE;
			this.onDoubleTap(pos.x, pos.y);
			break;
		case NONE:
			if(timer.isFinished()){
				timer.reset();
				switch(history){
				case 1: 
					this.pos.x = Gdx.input.getX();
					this.pos.y = Gdx.input.getY();
					state = InputState.PAN;
					break;
				case 2: state = InputState.SINGLE_TAP; break;
				default: state = InputState.DOUBLE_TAP; break;
				}
				history = 0;
			}
			break;
		case PAN:
            this.newPanPos.x = Gdx.input.getX();
            this.newPanPos.y = Gdx.input.getY();
            gameCam.unproject(newPanPos);
            gameCam.unproject(pos);
		    this.diff = newPanPos.sub(pos);

            this.pan(-diff.x, -diff.y);

            this.pos.x = Gdx.input.getX();
            this.pos.y = Gdx.input.getY();
			break;
		case SINGLE_TAP:
			this.onSingleTap(pos.x, pos.y);
			this.state = InputState.NONE;
			break;
		default:
			break;
			
		}
	}

	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		if(history == 0){
			this.pos.x = screenX;
			this.pos.y = screenY;
			gameCam.unproject(pos);
			timer.run();
		}
		history++;
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

	    if(history > 0) history++;
		if(state == InputState.PAN) state = InputState.NONE;

		return false;
	}
	
	public abstract void onDoubleTap(float gameX, float gameY);
	public abstract void onSingleTap(float gameX, float gameY);
	public abstract boolean pan(float gameDX, float gameDY);


	//--------------- UNUSED METHODS --------------------

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
