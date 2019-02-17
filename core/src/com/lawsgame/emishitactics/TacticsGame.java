package com.lawsgame.emishitactics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.engine.GPM;
import com.lawsgame.emishitactics.engine.utils.ConsoleColor;

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
		// set debug level
        setLogLevel(LogLevel.DEBUG);
		// instanciate the canvas to draw
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 0f);
		this.batch = new SpriteBatch();
		// launch the game
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



	// -----***$$$ LOG SYSTEM $$$***-----------------


    private static LogLevel logLevel = LogLevel.ERROR;
    private static StringBuilder logBuilder = new StringBuilder();

    public enum LogLevel{
        DEBUG(0),
        INFO(1),
        ERROR(2);

        int priority;

        LogLevel(int priority) {
            this.priority = priority;
        }
    }

	public static void setLogLevel(LogLevel wantedlogLevel){
	    logLevel = wantedlogLevel;
    }

    public static void debug(String msg){
        if(logLevel.priority >= LogLevel.DEBUG.priority){
            System.out.println(ConsoleColor.BLUE.code()+msg+ConsoleColor.RESET.code());
        }
    }

    public static void debug(Class<?> caller, String msg){
        if(logLevel.priority <= LogLevel.DEBUG.priority){
            logBuilder.setLength(0);
            logBuilder.append(ConsoleColor.BLUE_BOLD.code());
            logBuilder.append(caller.getSimpleName());
            logBuilder.append(ConsoleColor.RESET.code());
            logBuilder.append(" : ");
            logBuilder.append(msg);
            System.out.println(logBuilder.toString());
        }
    }

    public static void info(String msg){
	    if(logLevel.priority <= LogLevel.INFO.priority){
            System.out.println(msg);
        }
    }

    public static void info(Class<?> caller, String msg){
        if(logLevel.priority <= LogLevel.INFO.priority){
            logBuilder.setLength(0);
            logBuilder.append(ConsoleColor.GREEN_BOLD.code());
            logBuilder.append(caller.getSimpleName());
            logBuilder.append(ConsoleColor.RESET.code());
            logBuilder.append(" : ");
            logBuilder.append(msg);
            System.out.println(logBuilder.toString());
        }
    }

    public static void warn(String msg){
        if(logLevel.priority <= LogLevel.ERROR.priority){
            System.out.println(ConsoleColor.RED.code()+msg+ConsoleColor.RESET.code());
        }
    }

    public static void warn(Class<?> caller, String msg){
        if(logLevel.priority <= LogLevel.ERROR.priority){
            logBuilder.setLength(0);
            logBuilder.append(ConsoleColor.RED_BOLD.code());
            logBuilder.append(caller.getSimpleName());
            logBuilder.append(ConsoleColor.RESET.code());
            logBuilder.append(" : ");
            logBuilder.append(msg);
            System.out.println(logBuilder.toString());
        }
    }
}