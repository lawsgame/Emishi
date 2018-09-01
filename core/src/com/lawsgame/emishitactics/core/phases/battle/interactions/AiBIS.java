package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class AiBIS extends BattleInteractionState {

    public AiBIS(BattleInteractionMachine bim) {
        super(bim, false, false, false);
    }

    @Override
    public void init() {
        System.out.println("AI");

        bim.replace( new SelectActorBIS(bim, true));
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
