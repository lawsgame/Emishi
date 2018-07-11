package com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.ExhautiveInfoIS;
import com.lawsgame.emishitactics.engine.inputs.InteractionState;

public abstract class BattleInteractionState extends InteractionState {
    protected BattleInteractionMachine bim;

    protected boolean battlefieldDisplayed;
    protected boolean infoDisplayable;
    protected boolean mapSlidable;
    protected boolean deploymentSubState = false;



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
    public void dispose() {
        bim.sltdTile.setVisible(false);
        end();

    }

    public abstract void end();
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
            bim.push(new ExhautiveInfoIS(bim, (int)gameY, (int)gameX));
        }
    }

    @Override
    public void pan(float gameDX, float gameDY) {
        if(mapSlidable && !bim.gcm.isCameraMoving()) {
            bim.gcm.translateCam(gameDX, gameDY);
        }
    }

    // -------------------- SHARED METHOD -----------------------------


    public void focusOn(int row, int col, boolean smoothly){
        bim.gcm.focusOn(col - 0.5f, row - 0.5f, smoothly);
    }

    public void highlight(int row, int col){
        bim.sltdTile.setVisible(true);
        bim.sltdTile.reset();
        bim.sltdTile.addTile(row, col);
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

    public void setDeploymentSubState(boolean deploymentSubState) {
        this.deploymentSubState = deploymentSubState;
    }
}
