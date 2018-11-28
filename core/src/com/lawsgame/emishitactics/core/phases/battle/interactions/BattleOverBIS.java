package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class BattleOverBIS extends BattleInteractionState {

    public BattleOverBIS(BattleInteractionMachine bim) {
        super(bim, true, false, false, true, false);
    }

    @Override
    public void init() {

        System.out.println("BATTLE OVER!");
        super.init();
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }


    @Override
    public void renderAhead(SpriteBatch batch) {
        batch.draw(TempoSpritePool.get().getBlackBGSprite(), bim.bfr.getGCM().getClipBounds().getX(), bim.bfr.getGCM().getClipBounds().getY(), bim.bfr.getGCM().getPortWidth(), bim.bfr.getGCM().getPortHeight());
    }


}
