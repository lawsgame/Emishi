package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.TacticsGame;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class BattleOverBIS extends BattleInteractionState {

    public BattleOverBIS(BattleInteractionMachine bim) {
        super(bim, true, false, false, true, false);
    }

    @Override
    public void init() {

        TacticsGame.debug(this.getClass(), "BATTLE OVER!");
        super.init();
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }


    @Override
    public void renderAhead(SpriteBatch batch) {

    }


}
