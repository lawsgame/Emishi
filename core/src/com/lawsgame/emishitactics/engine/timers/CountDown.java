package com.lawsgame.emishitactics.engine.timers;


import com.lawsgame.emishitactics.engine.GameUpdatableEntity;

/**
 *
 * a CountDown is a GameUpdatableEntity defines by its #delay.
 * /run = begins the count down
 * /finished = is true if the count down is finished
 * /reset = reset the count down to be reused with not necessarily the same delay
 */

public class CountDown implements GameUpdatableEntity {
	final float delay;
	private float time;
	private boolean finished;
	private boolean running;
	
	public CountDown(float delay){
		this.delay = delay;
		this.time = delay;
		this.finished = false;
		this.running = false;
	}
	
	@Override
	public void update(float dt) {
		if(running){
			if(time > 0){
				time -= dt;
			}else{
				finished = true;
				running = false;
			}
		}
	}
	
	public void run(){
		running = true;
	}
	
	public void reset(){
		time = delay;
		finished = false;
		running = false;
	}

	public void reset(float newdelay){
		time = newdelay;
		finished = false;
		running = false;
	}

	public boolean isRunning() {
		return running;
	}
	
	public boolean isFinished() {
		return finished;
	}

    public float getValue() {
        return time;
    }
}
