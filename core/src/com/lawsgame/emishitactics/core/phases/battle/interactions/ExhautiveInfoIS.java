package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class ExhautiveInfoIS extends BattleInteractionState{

    public ExhautiveInfoIS(BattleInteractionMachine bim, int row, int col) {
        super(bim, true, false, false);
        if(bim.battlefield.isTileOccupied(row, col)) {
            bim.longUnitPanel.set(bim.battlefield, row, col);
            bim.longUnitPanel.show();
        }else{
            bim.longTilePanel.set(bim.battlefield.getTile(row, col));
            bim.longTilePanel.show();
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void end() {
        bim.longTilePanel.hide();
        bim.longUnitPanel.hide();
    }

    @Override
    public void handleTouchInput(int row, int col) {
        bim.rollback();
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
