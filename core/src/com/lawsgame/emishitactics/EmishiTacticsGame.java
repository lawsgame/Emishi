package com.lawsgame.emishitactics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.PerformanceCounter;

import com.lawsgame.emishitactics.core.states.BattlePhase;
import com.lawsgame.emishitactics.engine.GPM;

public class EmishiTacticsGame extends ApplicationAdapter {
	public static final int SCREEN_PIXEL_WIDTH = 960;
	public static final int SCREEN_PIXEL_HEIGHT = 540;
	public static final String TITLE = "Emishi Wars";

	private SpriteBatch batch;
	private GPM gpm;

	public static PerformanceCounter PC = new PerformanceCounter("Performance monitor");
	public static FPSLogger FPS = new FPSLogger();

	@Override
	public void create () {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 0f);

		batch = new SpriteBatch();
		gpm = new GPM();
		gpm.push(new BattlePhase(gpm,0));

	}

	@Override
	public void resize (int width, int height){
		this.gpm.getCurrentState().getGameCM().getPort().update(width,height);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gpm.update(Gdx.graphics.getDeltaTime());
		gpm.render(batch);
		//FPS.log();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}