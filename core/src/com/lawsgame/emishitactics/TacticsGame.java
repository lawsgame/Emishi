package com.lawsgame.emishitactics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.PerformanceCounter;

import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.engine.GPM;

public class TacticsGame extends ApplicationAdapter {
	public static final int SCREEN_PIXEL_WIDTH = 960;
	public static final int SCREEN_PIXEL_HEIGHT = 540;
	public static final String TITLE = "Lawsgame Tactical Game";

	private SpriteBatch batch;
	private GPM gpm;

	public static PerformanceCounter PC = new PerformanceCounter("Performance monitor");
	public static FPSLogger FPS = new FPSLogger();

	@Override
	public void create () {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 0f);


		this.batch = new SpriteBatch();
		this.gpm = new GPM();
		this.gpm.push(new BattlePhase(gpm, Player.create(), 0));

	}

	@Override
	public void resize (int width, int height){
		this.gpm.getCurrentState().getGameCM().getPort().update(width,height);
	}

	@Override
	public void render () {
		//Anti aliasing enabled
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gpm.update(Gdx.graphics.getDeltaTime());
		gpm.render(batch);
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}