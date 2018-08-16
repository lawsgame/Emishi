package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class SceneBIS extends BattleInteractionState {


    public SceneBIS(BattleInteractionMachine BISys) {
        super(BISys, true, false, false);
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

    @Override
    public boolean handleTouchInput(int row, int col) {

        bim.replace(new DeploymentBIS(bim));
        return true;
    }

    @Override
    public void init() {

    }

}
