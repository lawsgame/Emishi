package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class SelectActorBIS extends BattleInteractionState {
    int rowInit;
    int colInit;

    public SelectActorBIS(BattleInteractionMachine bim, int rowInit, int colInit) {
        super(bim, true, true, true);
        this.rowInit = rowInit;
        this.colInit = colInit;

    }

    public SelectActorBIS(BattleInteractionMachine bim){
        this(bim, -1, -1);
    }

    @Override
    public void init() {
        int[] activeUnitPos = new int[2];
        if(bim.battlefield.isTileOccupiedByPlayerControlledUnit(rowInit, colInit)){
            activeUnitPos[0] = rowInit;
            activeUnitPos[1] = colInit;
        }else{
            activeUnitPos = bim.battlefield.getRandomlyStillActiveUnitsCoords(bim.player.getArmy().getId());
        }

        if(activeUnitPos != null){
            bim.focusOn(activeUnitPos[0], activeUnitPos[1], true, false, false);
        }else{
            try {
                throw new BISException("push SelectActorBIS while there no active unit left in the player army");
            } catch (BISException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public boolean handleTouchInput(int row, int col) {

        return false;
    }

    @Override
    public void update60(float dt) {

    }

    @Override
    public void prerender(SpriteBatch batch) {

    }

    @Override
    public void renderBetween(SpriteBatch batch) {

    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }


}
