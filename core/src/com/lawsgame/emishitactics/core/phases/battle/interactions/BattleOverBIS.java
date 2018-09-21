package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class BattleOverBIS extends BattleInteractionState {

    public BattleOverBIS(BattleInteractionMachine bim) {
        super(bim, true, false, false);
    }

    @Override
    public void init() {
        System.out.println("BATTLE OVER!");
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }


    @Override
    public void renderAhead(SpriteBatch batch) {
        batch.draw(TempoSpritePool.get().getBlackBGSprite(), 0, 0, bim.gcm.getPortWidth(), bim.gcm.getPortHeight());
    }


}
