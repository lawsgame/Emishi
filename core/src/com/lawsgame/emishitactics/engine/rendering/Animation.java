package com.lawsgame.emishitactics.engine.rendering;


import com.badlogic.gdx.math.MathUtils;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;


public class Animation extends Observable implements GameUpdatableEntity {
	private int frameCurrentIndex;
	private int length;
	private float speed;
	private boolean backnforth;
	private boolean loop;
	private boolean randomStart;

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
	public Animation(int length, float speed, boolean loop, boolean backnforth, boolean randomStart){
		set(length, speed, loop, backnforth, randomStart);
	}

	public Animation(){}
	
	@Override
	public void update(float dt){
		if(playing && length > 1){
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
	    time = 0;
		playing = false;
		finish = false;
		direction = Direction.FORTH;
		frameCurrentIndex = (randomStart) ? MathUtils.random(length - 1) : 0;
	}
	
	public int getCurrentFrame(){
		return frameCurrentIndex;
	}

	public boolean isFinish() {
		return finish;
	}

	public void set(int length, float speed, boolean loop, boolean backnforth, boolean randomStart){
		setLength(length);
		setSpeed(speed);
		this.loop = loop;
		this.backnforth = backnforth;
		this.randomStart = randomStart;
		stop();
	}

	public void setSpeed(float speed){
		if(speed > 0) {
			this.speed = speed;
		}
	}

	public void setLength(int length){
		if(length < 1){
			try {
				throw new Exception("Animation.class do not accept a lenght lower than 1, however the length parameter is at : "+length);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			this.length = length;
		}
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

	public boolean isPlaying() { return playing; }


	@Override
	public String toString() {
		return "Animation{" +
				"frameCurrentIndex=" + frameCurrentIndex +
				", length=" + length +
				", speed=" + speed +
				", backnforth=" + backnforth +
				", loop=" + loop +
				", randomStart=" + randomStart +
				", direction=" + direction +
				", finish=" + finish +
				", playing=" + playing +
				'}';
	}
}
