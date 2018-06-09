package com.lawsgame.emishitactics.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.lawsgame.emishitactics.engine.patterns.Observable;


/**
 *
 */
public abstract class GamePhase extends Observable implements Disposable, GameElement {
	protected GPM gpm;                      // allow to switch to another phase
	protected AssetManager asm;             // allow to have access to all the required assets: xml, sounds, textures.
	
	private OrthographicCamera gameCam;     // level camera
	private Viewport gamePort;              // defines the dimension of the frame through the player see the level
	private Rectangle scissors;
	private Rectangle clipBounds;
	private float levelWidth;               // defines the rectangle within the camera is allowed to move.
    private float levelHeight;              // defines the rectangle within the camera is allowed to move.

	private OrthographicCamera uiCam;       // UI camera
	private Viewport uiPort;

	private float acc1 = 0;
	private float acc3 = 0;
	private float acc12 = 0;

	private static float aspectRatio = 0;

	/**
	 * world GAME coordinate system (gdx.SpriteBatch)
	 * y
	 * ^
	 * |
	 * |
	 * |
	 * (y0,x0)------> x
	 *
	 * world UI coordinate system (gdx.SpriteBatch)
	 * 1*aspect ratio
	 * ^
	 * |
	 * |
	 * |
	 * (0,0)------> 1
	 */
	public GamePhase (GPM gpm, AssetManager asm, float gamePortWidth, float uiPortWidth, float worldWidth, float worldHeight){
		this.gpm = gpm;
		this.asm = asm;
        float gamePortHeight = gamePortWidth*getAspRatio();
        this.clipBounds = new Rectangle(0,0,gamePortWidth, gamePortHeight);
        this.setLevelDimension(worldWidth, worldHeight);


		this.gameCam = new OrthographicCamera();
		this.gamePort = new FitViewport(gamePortWidth, gamePortHeight, gameCam);
		this.gameCam.setToOrtho(false, gamePortWidth, gamePortHeight);
		this.gameCam.update();

        float uiPortHeight = uiPortWidth*getAspRatio();
		this.uiCam = new OrthographicCamera();
		this.uiPort = new FitViewport(uiPortWidth, uiPortHeight);
		this.uiCam.setToOrtho(false, uiPortWidth, uiPortHeight);
		this.uiCam.update();

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
		batch.setProjectionMatrix(gameCam.combined);
		batch.begin();
		scissors = new Rectangle();
		ScissorStack.calculateScissors(gameCam, batch.getTransformMatrix(), clipBounds, scissors);
		ScissorStack.pushScissors(scissors);

		renderWorld(batch);
		
		batch.flush();
		ScissorStack.popScissors();
		batch.end();


        /**
         * others stuff to renderUnderlyingMapPart without using the SpriteBatch instance
         */
		batch.begin();
		batch.setProjectionMatrix(uiCam.combined);

		renderUI(batch);

		batch.end();

	}


	public abstract void preRender(SpriteBatch batch);
	public abstract void renderWorld(SpriteBatch batch);
	public abstract void renderUI(SpriteBatch batch);
	
	/**
	 * 1) check whether or not the new camera position is inside the world boundaries
	 * 2) modify the camera position consequently
	 * 3) update the camera to take the changes into account
	 * 4) update the clip bounds and the components instances to follow the camera accordingly
	 */
	public boolean translateGameCam(float dx, float dy){
		float oldCamPosX = gameCam.position.x;
		float oldCamPosY = gameCam.position.y;
		
		if(gameCam.position.x - clipBounds.width/2 + dx < 0){
			dx = 0;
			gameCam.position.x = clipBounds.width/2;
		}
		if(gameCam.position.y - clipBounds.height/2 + dy < 0){
			dy = 0;
			gameCam.position.y = clipBounds.height/2;
		}
		if(gameCam.position.x + clipBounds.width/2 + dx > levelWidth){
			dx = 0;
			gameCam.position.x =  levelWidth- clipBounds.width/2;
		}
		if(gameCam.position.y + clipBounds.height/2 + dy > levelHeight){
			dy = 0;
			gameCam.position.y =  levelHeight - clipBounds.height/2;
		}
	
		gameCam.translate(dx, dy);
		gameCam.update();
		clipBounds.x += gameCam.position.x - oldCamPosX;
		clipBounds.y += gameCam.position.y - oldCamPosY;

		notifyAllObservers(null);
		return !((dx == 0f) && (dy == 0f));
		
	}

	public void setGameCamPos(float x, float y){
		if(x < clipBounds.width/2f) x = clipBounds.width/2;
		if(x > levelWidth - clipBounds.width/2f) x = levelWidth - clipBounds.width/2;
		if(y < clipBounds.height/2f) y = clipBounds.height/2f;
		if(y > levelHeight - clipBounds.height/2f) y = levelHeight - clipBounds.height/2f;

		gameCam.position.x = x;
		gameCam.position.y = y;
		clipBounds.x = x - clipBounds.width/2f;
		clipBounds.y = y - clipBounds.height/2f;
		gameCam.update();
	}

	//-------- SETTERS & GETTERS ---------


	public AssetManager getAsm() {
		return asm;
	}

	public OrthographicCamera getGameCam() {
		return gameCam;
	}

	public float getLevelWidth() {
		return levelWidth;
	}

    public float getLevelHeight() {
        return levelHeight;
    }

	public void setLevelDimension(float w, float h) {
		if( w < clipBounds.width) w =  clipBounds.width;
		if( h < clipBounds.height) h = clipBounds.height;
		this.levelWidth = w;
		this.levelHeight = h;
	}

	public float getGamePortWidth() {return gamePort.getWorldWidth();}

	public float getGamePortHeigth() {
		return gamePort.getWorldHeight();
	}

	public float getUIPortWidth(){ return this.uiPort.getWorldWidth(); }

	public float getUIPortHeigth(){ return this.uiPort.getWorldHeight(); }

	public Viewport getGamePort(){
		return gamePort;
	}

	public static float getAspRatio(){
		if(aspectRatio == 0){
			aspectRatio = Gdx.app.getGraphics().getHeight()/(float)Gdx.app.getGraphics().getWidth();
		}
		return aspectRatio;
	}
}
