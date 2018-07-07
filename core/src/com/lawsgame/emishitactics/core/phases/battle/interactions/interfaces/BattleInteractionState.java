package com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionSystem;
import com.lawsgame.emishitactics.engine.inputs.InteractionState;

public abstract class BattleInteractionState extends InteractionState {
    protected BattleInteractionSystem BISys;

    protected boolean battlefieldDisplayed;
    protected boolean infoDisplayable;
    protected boolean mapSlidable;

    public BattleInteractionState(BattleInteractionSystem BISys, boolean BFDisplayed, boolean infoDisplayed, boolean mapSlidable ) {
        super(BISys.getGCM().getCamera());
        this.BISys = BISys;

        this.battlefieldDisplayed = true;
        this.infoDisplayable = true;
        this.mapSlidable = true;
    }

    public void update1(float dt) {}
    public void update3(float dt) {}
    public void update12(float dt) {}
    public abstract void prerender(SpriteBatch batch);
    public abstract void renderBetween(SpriteBatch batch);
    public abstract void renderAhead(SpriteBatch batch);

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
            BISys.getGCM().translateGameCam(gameDX, gameDY);
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
