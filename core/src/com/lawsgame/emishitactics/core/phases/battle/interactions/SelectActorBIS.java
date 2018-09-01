package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Army;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class SelectActorBIS extends BattleInteractionState {
    int rowInit;
    int colInit;

    public SelectActorBIS(BattleInteractionMachine bim, int rowInit, int colInit, boolean newPlayerTurn) {
        super(bim, true, true, true);
        this.rowInit = rowInit;
        this.colInit = colInit;
        if(newPlayerTurn){
            bim.begin(Army.getPlayerArmy());
        }

    }

    public SelectActorBIS(BattleInteractionMachine bim, boolean newArmyTurn){
        this(bim, -1, -1, newArmyTurn);
    }

    @Override
    public void init() {
        System.out.println("SELECT ACTOR");


        int[] activeUnitPos = new int[2];
        if(bim.battlefield.isTileOccupiedByPlayerControlledUnit(rowInit, colInit)){
            activeUnitPos[0] = rowInit;
            activeUnitPos[1] = colInit;
        }else{
            activeUnitPos = bim.battlefield.getRandomlyStillActiveUnitsCoords(Army.getPlayerArmy().getId());
        }

        if(activeUnitPos != null){
            bim.focusOn(activeUnitPos[0], activeUnitPos[1], true, false, false, false, false);
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
        if(bim.battlefield.isTileOccupiedByPlayerControlledUnit(row, col)){
            IUnit selectedUnit = bim.battlefield.getUnit(row, col);
            if(!selectedUnit.isDone()) {
                bim.replace(new SelectActionBIS(bim, row, col));
                return true;
            }
        }
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