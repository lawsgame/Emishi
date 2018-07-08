package com.lawsgame.emishitactics.engine.renderers;


import com.lawsgame.emishitactics.engine.GameUpdatableEntity;

public class Animation implements GameUpdatableEntity {
	private int frameCurrentIndex = 0;
	private int length;
	private float speed;
	private boolean loop;
	
	private boolean finish = false;
	private boolean playing = false;
	private float time = 0;
	
	/**
	 * 
	 * @param length: number of frames
	 * @param speed: dealy before displaying the next frame, in seconds
	 * @param loop: whether or not the animation loops
	 */
	public Animation(int length, float speed, boolean loop){
		this.length = length;
		this.speed = speed;
		this.loop = loop;
	}
	
	@Override
	public void update(float dt){
		if(playing){
			time +=dt;
			if(time  > speed){
				time = 0;
				frameCurrentIndex++;
				if(frameCurrentIndex >=  length){
					if(loop) frameCurrentIndex = 0;
					else{
						frameCurrentIndex = length-1;
						finish = true;
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
}
