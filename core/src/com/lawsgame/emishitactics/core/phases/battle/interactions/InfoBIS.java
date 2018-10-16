package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class InfoBIS extends BattleInteractionState{
    int row;
    int col;

    public InfoBIS(BattleInteractionMachine bim, int row, int col) {
        super(bim, true, false, false, false, false);
        this.row = row;
        this.col = col;
    }

    @Override
    public void init() {

        super.init();
        bim.shortUnitPanel.hide();
        bim.shortTilePanel.hide();
        if(bim.battlefield.isTileOccupied(row, col)) {
            bim.longUnitPanel.set(bim.battlefield.getUnit(row, col));
            bim.longUnitPanel.show();
        }else{
            bim.longTilePanel.set(bim.battlefield.getTile(row, col).getType());
            bim.longTilePanel.show();
        }
    }

    @Override
    public void end() {
        super.end();
        bim.longTilePanel.hide();
        bim.longUnitPanel.hide();
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        bim.rollback();
        return true;
    }
}
