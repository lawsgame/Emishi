package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.TacticsGame;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.engine.utils.Lawgger;

public class BattleOverBIS extends BattleInteractionState {
    private static Lawgger log = Lawgger.createInstance(BattleOverBIS.class);

    public BattleOverBIS(BattleInteractionMachine bim) {
        super(bim, true, false, false, true, false);
    }

    @Override
    public void init() {

        log.info("BATTLE OVER!");
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
