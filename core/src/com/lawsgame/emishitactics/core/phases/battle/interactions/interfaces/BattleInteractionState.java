package com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.InfoBIS;
import com.lawsgame.emishitactics.engine.inputs.InteractionState;

public abstract class BattleInteractionState extends InteractionState {
    protected final BattleInteractionMachine bim;

    private boolean battlefieldDisplayed;             // if true, the battlefield is displayed
    private boolean infoDisplayable;                  // if true, a long click on a tile while launch the InfoIS
    private boolean mapSlidable;                      // if true, sliding will allow the player the move around the map



    public BattleInteractionState(BattleInteractionMachine bim, boolean BFDisplayable, boolean infoDisplayable, boolean mapSlidable ) {
        super(bim.gcm.getCamera());
        this.bim = bim;

        this.battlefieldDisplayed = BFDisplayable;
        this.infoDisplayable = infoDisplayable;
        this.mapSlidable = mapSlidable;

    }

    public BattleInteractionState(BattleInteractionMachine bim){
        this(bim, true, true, true);
    }


    @Override
    public void update(float dt) {
        update60(dt);
    }

    @Override
    public void dispose() { }

    public void end(){
        bim.hideHighlightedTiles();
    }

    public abstract void handleTouchInput(int row, int col);
    public void update1(float dt) {}
    public void update3(float dt) {}
    public void update12(float dt) {}
    public abstract void update60(float dt);
    public abstract void prerender(SpriteBatch batch);
    public abstract void renderBetween(SpriteBatch batch);
    public abstract void renderAhead(SpriteBatch batch);

    @Override
    public void onTouch(float gameX, float gameY) {
        if(!bim.gcm.isCameraMoving()){
            handleTouchInput((int)gameY, (int)gameX);
        }
    }

    @Override
    public void onLongTouch(float gameX, float gameY) {
        if(infoDisplayable && !bim.gcm.isCameraMoving()){
            bim.push(new InfoBIS(bim, (int)gameY, (int)gameX));
        }
    }

    @Override
    public void pan(float gameDX, float gameDY) {
        if(mapSlidable && !bim.gcm.isCameraMoving()) {
            bim.gcm.translateCam(gameDX, gameDY);
        }
    }


    // ------------------- SETTERS & GETTERS ---------------------------



    public boolean isBattlefieldDisplayed() {
        return battlefieldDisplayed;
    }

    public void setBattlefieldDisplayed(boolean battlefieldDisplayed) {
        this.battlefieldDisplayed = battlefieldDisplayed;
    }

    public void setInfoDisplayable(boolean infoDisplayable) {
        this.infoDisplayable = infoDisplayable;
    }

    public void setMapSlidable(boolean mapSlidable) {
        this.mapSlidable = mapSlidable;
    }



    public static class BISException extends Exception{

        public BISException(String s) {
            super(s);
        }
    }
}
