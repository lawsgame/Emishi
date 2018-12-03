package com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.interactions.InfoBIS;
import com.lawsgame.emishitactics.engine.inputs.InteractionState;

public abstract class BattleInteractionState extends InteractionState {
    protected final BattleInteractionMachine bim;

    private boolean battlefieldDisplayed;             // if true, the battlefield is displayed
    private boolean longPanelDisplayable;             // if true, a long click on a tile while launch the InfoIS
    private boolean mapSlidable;                      // if true, sliding will allow the player the move around the map
    private boolean FAAUpdatable;                     // FFA : foe action area
    private boolean clearFAA;                     // FFA : foe action area

    // on touch variables
    private int rowTouch;
    private int colTouch;


    public BattleInteractionState(BattleInteractionMachine bim,
                                  boolean BFDisplayable,
                                  boolean longPanelDisplayable,
                                  boolean mapSlidable,
                                  boolean clearFAA,
                                  boolean FAAUpdatable) {
        super(bim.bfr.getGCM().getCamera());
        this.bim = bim;

        this.battlefieldDisplayed = BFDisplayable;
        this.longPanelDisplayable = longPanelDisplayable;
        this.mapSlidable = mapSlidable;
        this.clearFAA = clearFAA;
        this.FAAUpdatable = FAAUpdatable;
    }

    @Override
    public void init() {
        if(clearFAA)
            bim.thl.ffa.clear();
    }

    @Override
    public void update(float dt) {
        update60(dt);
    }

    @Override
    public void dispose() { }

    public void end(){
        bim.thl.removeTileHighlighting(false, false);
    }

    public abstract boolean handleTouchInput(int row, int col);
    public void update1(float dt) {}
    public void update3(float dt) {}
    public void update12(float dt) {}
    public void update60(float dt) {}
    public void renderAhead(SpriteBatch batch){}


    /**
     *
     * player input are taken into account only if the camera does not move.
     *
     * First, it check wether the input is handled by BIS.handleTouchInput()
     * If not (aka. return false), the input is handled generically by using:
     *      1) BIM.moveCameraTo()
     *      2) (optional) updating FFA if the given tile is occupied by a foe.
     *
     * @param gameX
     * @param gameY
     */
    @Override
    public void onTouch(float gameX, float gameY) {
        if(!bim.bfr.getGCM().isCameraMoving()) {

            rowTouch = bim.bfr.getRow(gameX, gameY);
            colTouch = bim.bfr.getCol(gameX, gameY);
            if (!handleTouchInput(rowTouch, colTouch) && bim.bfr.getModel().isTileExisted(rowTouch, colTouch)) {

                bim.focusOn(rowTouch, colTouch, true, true, false, TileHighlighter.SltdUpdateMode.UNCHANGED, true);
                if (FAAUpdatable && bim.bfr.getModel().isTileOccupiedByFoe(rowTouch, colTouch, bim.player.getArmy().getAffiliation())) {

                    bim.thl.ffa.update(rowTouch, colTouch);
                }
            }
        }
    }

    @Override
    public void onLongTouch(float gameX, float gameY) {
        if(longPanelDisplayable && !bim.bfr.getGCM().isCameraMoving()){
            bim.push(new InfoBIS(bim, bim.bfr.getRow(gameX, gameY), bim.bfr.getCol(gameX, gameY)));
        }
    }

    @Override
    public void pan(float gameDX, float gameDY) {
        if(mapSlidable && !bim.bfr.getGCM().isCameraMoving()) {
            bim.bfr.getGCM().translateCam(gameDX, gameDY);
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
