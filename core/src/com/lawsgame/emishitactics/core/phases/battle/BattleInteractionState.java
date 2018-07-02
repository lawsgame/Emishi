package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.engine.inputs.InteractionState;

public abstract class BattleInteractionState extends InteractionState {
    protected BattleInteractionSystem BISys;

    protected boolean battlefieldDisplayed = true;
    protected boolean infoDisplayable = true;
    protected boolean mapSlidable = true;

    public BattleInteractionState(BattleInteractionSystem BISys) {
        super(BISys.gameCM.getCamera());
        this.BISys = BISys;
    }

    public abstract void update1(float dt);
    public abstract void update3(float dt);
    public abstract void update12(float dt);
    public abstract void prerender(SpriteBatch batch);
    public abstract void renderBetween(SpriteBatch batch);
    public abstract void renderAhead(SpriteBatch batch);
    public abstract void renderUI(SpriteBatch batch);

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
            BISys.gameCM.translateGameCam(gameDX, gameDY);
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
