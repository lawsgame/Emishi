package com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.InfoBIS;
import com.lawsgame.emishitactics.engine.inputs.InteractionState;

public abstract class BattleInteractionState extends InteractionState {
    protected final BattleInteractionMachine bim;

    private boolean battlefieldDisplayed;             // if true, the battlefield is displayed
    private boolean longPanelDisplayable;                  // if true, a long click on a tile while launch the InfoIS
    private boolean mapSlidable;                      // if true, sliding will allow the player the move around the map

    // on touch variables
    private int rowTouch;
    private int colTouch;
    private boolean inputNotHandled;

    public BattleInteractionState(BattleInteractionMachine bim,
                                  boolean BFDisplayable,
                                  boolean longPanelDisplayable,
                                  boolean mapSlidable) {
        super(bim.gcm.getCamera());
        this.bim = bim;

        this.battlefieldDisplayed = BFDisplayable;
        this.longPanelDisplayable = longPanelDisplayable;
        this.mapSlidable = mapSlidable;

    }


    @Override
    public void update(float dt) {
        update60(dt);
    }

    @Override
    public void dispose() { }

    public void end(){
        bim.removeTileHighlighting(false);
    }

    public abstract boolean handleTouchInput(int row, int col);
    public void update1(float dt) {}
    public void update3(float dt) {}
    public void update12(float dt) {}
    public void update60(float dt) {}
    public void renderAhead(SpriteBatch batch){}


    public boolean handleRawTouchInput(float x, float y){
        return false;
    }


    @Override
    public void onTouch(float gameX, float gameY) {
        if(!bim.gcm.isCameraMoving()) {
            if (!handleRawTouchInput(gameX, gameY)) {
                rowTouch = bim.bfr.getRow(gameX, gameY);
                colTouch = bim.bfr.getCol(gameX, gameY);
                if (!handleTouchInput(rowTouch, colTouch) && bim.battlefield.isTileExisted(rowTouch, colTouch)) {
                    bim.focusOn(rowTouch, colTouch, true, true, true, false, false);
                    if (bim.battlefield.isTileOccupiedByFoe(rowTouch, colTouch, Data.Affiliation.ALLY)) {
                        //TODO: add or remove selected foe action area to those of its alleageanca alreadt registered


                    }

                }
            }
        }
    }

    @Override
    public void onLongTouch(float gameX, float gameY) {
        if(longPanelDisplayable && !bim.gcm.isCameraMoving()){
            bim.push(new InfoBIS(bim, bim.bfr.getRow(gameX, gameY), bim.bfr.getCol(gameX, gameY)));
        }
    }

    @Override
    public void pan(float gameDX, float gameDY) {
        if(mapSlidable && !bim.gcm.isCameraMoving()) {
            bim.gcm.translateCam(gameDX, gameDY);
        }
    }


    // ------------------- SETTERS & GETTERS ---------------------------



    public final boolean isBattlefieldDisplayed() {
        return battlefieldDisplayed;
    }

    public final void setBattlefieldDisplayed(boolean battlefieldDisplayed) {
        this.battlefieldDisplayed = battlefieldDisplayed;
    }

    public final void setLongPanelDisplayable(boolean longPanelDisplayable) {
        this.longPanelDisplayable = longPanelDisplayable;
    }

    public final void setMapSlidable(boolean mapSlidable) {
        this.mapSlidable = mapSlidable;
    }



    public static class BISException extends Exception{

        public BISException(String s) {
            super(s);
        }
    }
}
