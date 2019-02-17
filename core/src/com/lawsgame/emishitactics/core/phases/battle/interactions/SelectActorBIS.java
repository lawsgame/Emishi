package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.lawsgame.emishitactics.TacticsGame;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.battle.BeginArmyTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

public class SelectActorBIS extends BattleInteractionState implements Observer {
    int rowInit;
    int colInit;
    boolean readyToUse;
    BeginArmyTurnCommand beginCommand;

    public SelectActorBIS(BattleInteractionMachine bim, int rowInit, int colInit, boolean newPlayerTurn) {
        super(bim, true, true, true, false, true);
        this.rowInit = rowInit;
        this.colInit = colInit;
        this.readyToUse = !newPlayerTurn;
        if(newPlayerTurn){
            beginCommand = new BeginArmyTurnCommand(bim.bfr, bim.scheduler, bim.player.getInventory(), bim.player.getArmy());
            beginCommand.attach(this);
            beginCommand.apply();
        }

    }

    public SelectActorBIS(BattleInteractionMachine bim, boolean newArmyTurn){
        this(bim, -1, -1, newArmyTurn);
    }

    @Override
    public void init() {
        if(readyToUse) {
            TacticsGame.debug(this.getClass(), "SELECT ACTOR");
            super.init();
            int[] activeUnitPos = new int[2];
            if (bim.bfr.getModel().isTileOccupiedByPlayerControlledUnit(rowInit, colInit)) {
                activeUnitPos[0] = rowInit;
                activeUnitPos[1] = colInit;
            } else {
                activeUnitPos = bim.bfr.getModel().getUnitsPos(bim.player.getArmy(), true, true).random();
            }

            if (activeUnitPos != null) {
                bim.focusOn(activeUnitPos[0], activeUnitPos[1], true, false, false, TileHighlighter.SltdUpdateMode.ERASE_SLTD_TILE_MEMORY, false);
            } else {
                try {
                    throw new BISException("push SelectActorBIS while there no active unit left in the player army");
                } catch (BISException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        if(readyToUse) {
            if (bim.bfr.getModel().isTileOccupiedByPlayerControlledUnit(row, col)) {
                Unit selectedUnit = bim.bfr.getModel().getUnit(row, col);
                if (!selectedUnit.isDone()) {
                    bim.replace(new SelectActionBIS(bim, selectedUnit));
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void getNotification(Observable sender, Object data) {
        if(sender == beginCommand) {
            beginCommand.detach(this);
            readyToUse = true;
            init();
        }
    }
}
