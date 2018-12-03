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
        bim.pp.shortUnitPanel.hide();
        bim.pp.shortTilePanel.hide();
        if(bim.bfr.getModel().isTileOccupied(row, col)) {
            bim.pp.longUnitPanel.update(bim.bfr.getModel().getUnit(row, col));
            bim.pp.longUnitPanel.show();
        }else{
            bim.pp.longTilePanel.update(bim.bfr.getModel().getTile(row, col).getType());
            bim.pp.longTilePanel.show();
        }
    }

    @Override
    public void end() {
        super.end();
        bim.pp.longTilePanel.hide();
        bim.pp.longUnitPanel.hide();
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        bim.rollback();
        return true;
    }
}
