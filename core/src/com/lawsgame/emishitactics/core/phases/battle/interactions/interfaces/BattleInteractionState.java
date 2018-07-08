package com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionSystem;
import com.lawsgame.emishitactics.engine.inputs.InteractionState;

public abstract class BattleInteractionState extends InteractionState {
    protected BattleInteractionSystem bis;

    protected boolean battlefieldDisplayed;
    protected boolean infoDisplayable;
    protected boolean mapSlidable;
    protected boolean animationsProceeding;

    public BattleInteractionState(BattleInteractionSystem bis, boolean BFDisplayed, boolean infoDisplayed, boolean mapSlidable ) {
        super(bis.gcm.getCamera());
        this.bis = bis;

        this.battlefieldDisplayed = true;
        this.infoDisplayable = true;
        this.mapSlidable = true;
    }

    public abstract void handleTouchInput(float gameX, float gameY);
    public void update1(float dt) {}
    public void update3(float dt) {}
    public void update12(float dt) {}
    public abstract void prerender(SpriteBatch batch);
    public abstract void renderBetween(SpriteBatch batch);
    public abstract void renderAhead(SpriteBatch batch);

    @Override
    public void onTouch(float gameX, float gameY) {
        if(!bis.gcm.isCameraMoving()){
            handleTouchInput(gameX, gameY);
        }
    }

    @Override
    public void onLongTouch(float gameX, float gameY) {
        if(infoDisplayable && !bis.gcm.isCameraMoving()){
            //TODO:
            System.out.println("display info of ("+(int)gameY+","+(int)gameX+")");
        }
    }

    @Override
    public void pan(float gameDX, float gameDY) {
        if(mapSlidable && !bis.gcm.isCameraMoving()) {
            bis.gcm.translateCam(gameDX, gameDY);
        }
    }


    // ------------------- SETTERS & GETTERS ---------------------------



    public boolean isBattlefieldDisplayed() {
        return battlefieldDisplayed;
    }


}
