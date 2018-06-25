package com.lawsgame.emishitactics.core.states.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.engine.inputs.InteractionState;

public abstract class BattleInteractionState extends InteractionState {
    protected BattleInteractionSystem bis;

    protected boolean battlefieldDisplayed = true;
    protected boolean infoDisplayable = true;
    protected boolean mapSlidable = true;

    public BattleInteractionState(BattleInteractionSystem bis) {
        super(bis.gameCM.getCamera());
        this.bis = bis;
    }

    public abstract void update1(float dt);
    public abstract void update3(float dt);
    public abstract void update12(float dt);
    public abstract void renderBetween(SpriteBatch batch);
    public abstract void renderAhead(SpriteBatch batch);
    public abstract void renderUI();

    @Override
    public void onLongTouch(float gameX, float gameY) {
        if(infoDisplayable){
            //TODO:
            System.out.println("display info of ("+(int)gameY+","+(int)gameX+")");
        }
    }

    @Override
    public void pan(float gameDX, float gameDY) {
        if(mapSlidable) {
            bis.gameCM.translateGameCam(gameDX, gameDY);
        }
    }



    //--------------------SHARED METHODS ------------------------------



    // ------------------- SETTERS & GETTERS ---------------------------



    public void setBattlefieldDisplayed(boolean battlefieldDisplayed) {
        this.battlefieldDisplayed = battlefieldDisplayed;
    }

    public void setInfoDisplayable(boolean infoDisplayable) {
        this.infoDisplayable = infoDisplayable;
    }

    public void setMapSlidable(boolean mapSlidable) {
        this.mapSlidable = mapSlidable;
    }

    public boolean isBattlefieldDisplayed() {
        return battlefieldDisplayed;
    }

    public boolean isInfoDisplayable() {
        return infoDisplayable;
    }

    public boolean isMapSlidable() {
        return mapSlidable;
    }
}
