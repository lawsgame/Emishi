package com.lawsgame.emishitactics.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Disposable;
import com.lawsgame.emishitactics.engine.patterns.statemachine.State;


/**
 *
 */
public abstract class GamePhase implements State, GameElement {
	protected GPM gpm;                      // allow to switch to another phase
	protected AssetManager asm;             // allow to have access to all the required assets: xml, sounds, textures.


	private Rectangle scissors;

	private CameraManager gameCM;
	private CameraManager uiCM;

	private float acc1 = 0;
	private float acc3 = 0;
	private float acc12 = 0;

	private static float aspectRatio = 0;

	/**
	 * world GAME coordinate system (gdx.SpriteBatch)
	 * yCenter
	 * ^
	 * |
	 * |
	 * |
	 * (y0,x0)------> xCenter
	 *
	 * world UI coordinate system (gdx.SpriteBatch)
	 * 1*aspect ratio
	 * ^
	 * |
	 * |
	 * |
	 * (0,0)------> 1
	 *
	 *
	 *	the GamePhase.class is the root to all update/render loops
	 *	futhermore, it aggregates two différent cameras:
	 *		- the game one : focus on the level
	 *		- the UI one : focus on the UI
	 *	Finally, it also aggregates an AssetManager
	 */
	public GamePhase (GPM gpm, AssetManager asm, float gamePortWidth, float uiPortWidth, float gameWorldWidth, float gameWorldHeight){
		this.gpm = gpm;
		this.asm = asm;
		this.gameCM = new CameraManager(gameWorldWidth, gameWorldHeight, gamePortWidth);
		this.uiCM = new CameraManager(uiPortWidth);
	}

	public GamePhase (GPM gsm, AssetManager asm, float gamePortWidth, float worldWidth, float worldHeight){
		this(gsm, asm, gamePortWidth, 1, worldWidth, worldHeight);
	}

	public GamePhase (GPM gsm, float gamePortWidth, float worldWidth, float worldHeight){
		this(gsm, new AssetManager(), gamePortWidth, 1, worldWidth, worldHeight);
	}

	public GamePhase (GPM gsm, AssetManager asm, float gamePortWidth){
		this(gsm, asm, gamePortWidth, 1, gamePortWidth, gamePortWidth*getAspRatio());
	}

	public GamePhase (GPM gsm, float gamePortWidth){
		this(gsm, new AssetManager(), gamePortWidth, 1, gamePortWidth, gamePortWidth*getAspRatio());
	}

	public void update(float dt){
		acc1 +=dt;
		acc3 +=dt;
		acc12 +=dt;
		
		update60(dt);
		
		if(acc12  > 0.08){
			update12(acc12);
			acc12 = 0;
		}
		
		if(acc3  > 0.32){
			update3(acc3);
			acc3 = 0;
		}
		
		if(acc1  > 0.99){
			update1(acc1);
			acc1 = 0;
		}
	}


	
	public abstract void update1(float dt);
	public abstract void update3(float dt);
	public abstract void update12(float dt);
	public abstract void update60(float dt);

	@Override
	public void render(SpriteBatch batch){
        /**
         * stuff to renderUnderlyingMapPart without the batch instance, typically renderUnderlyingMapPart background color
         */
		preRender(batch);


        /**
         * renderUnderlyingMapPart textures and shaders
         */
		batch.setProjectionMatrix(gameCM.getCamera().combined);
		batch.begin();
		scissors = new Rectangle();
		ScissorStack.calculateScissors(gameCM.getCamera(), batch.getTransformMatrix(), gameCM.getClipBounds(), scissors);
		ScissorStack.pushScissors(scissors);

		renderWorld(batch);
		
		batch.flush();
		ScissorStack.popScissors();
		batch.end();


        /**
         * others stuff to renderUnderlyingMapPart without using the SpriteBatch instance
         */
		batch.begin();
		batch.setProjectionMatrix(uiCM.getCamera().combined);

		renderUI(batch);

		batch.end();

	}


	public abstract void preRender(SpriteBatch batch);
	public abstract void renderWorld(SpriteBatch batch);
	public abstract void renderUI(SpriteBatch batch);
	

	//-------- SETTERS & GETTERS ---------


	public AssetManager getAsm() {
		return asm;
	}


	public static float getAspRatio(){
		if(aspectRatio == 0){
			aspectRatio = Gdx.app.getGraphics().getHeight()/(float)Gdx.app.getGraphics().getWidth();
		}
		return aspectRatio;
	}

    public CameraManager getGameCM() {
        return gameCM;
    }
}
