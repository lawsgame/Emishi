package com.lawsgame.emishitactics.engine.rendering;


import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;


public class Animation extends Observable implements GameUpdatableEntity {
	private int frameCurrentIndex = 0;
	private int length;
	private float speed;
	private boolean backnforth;
	private boolean loop;

	private Direction direction = Direction.FORTH;
	private boolean finish = false;
	private boolean playing = false;
	private float time = 0;

	private enum Direction{
		FORTH,
		BACK
	}
	
	/**
	 * 
	 * @param length: number of frames
	 * @param speed: dealy before displaying the next frame, in seconds
	 * @param loop: whether or not the animation loops
	 */
	public Animation(int length, float speed, boolean loop, boolean backnforth){
		this.length = length;
		this.speed = speed;
		this.loop = loop;
		this.backnforth = backnforth;
	}
	
	@Override
	public void update(float dt){
		if(playing){
			time +=dt;
			if(time  > speed){
				time = 0;

				if(direction == Direction.FORTH) {

					frameCurrentIndex++;

					if(frameCurrentIndex == length - 1 && backnforth) {
						direction = Direction.BACK;
					}

					if (frameCurrentIndex >= length) {
						if (loop) frameCurrentIndex = 0;
						else {
							frameCurrentIndex = length - 1;
							finish = true;
							notifyAllObservers(this);
						}
					}
				}else{

					frameCurrentIndex--;

					if (frameCurrentIndex == 0) {
						if (loop) {
							direction = Direction.FORTH;
						} else {
							finish = true;
							notifyAllObservers(this);
						}
					}
				}
			}
		}
	}
	
	public void play(){
		playing = true;
	}
	
	public void pause(){
		playing = false;
	}
	
	public void stop(){
		playing = false;
		finish = false;
		frameCurrentIndex = 0;
	}
	
	public int getCurrentFrame(){
		return frameCurrentIndex;
	}

	public boolean isFinish() {
		return finish;
	}

	public void set(int length, float speed, boolean loop, boolean backnforth){
	    stop();
        this.length = length;
        this.speed = speed;
        this.loop = loop;
        this.backnforth = backnforth;
    }

    public int getLength() {
        return length;
    }

    public float getSpeed() {
        return speed;
    }

    public boolean isBacknforth() {
        return backnforth;
    }

    public boolean isLoop() {
        return loop;
    }
}
