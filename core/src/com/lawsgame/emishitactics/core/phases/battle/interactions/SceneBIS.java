package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
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
    public void handleTouchInput(int row, int col) {
        bim.set(new DeploymentBIS(bim));
    }

    @Override
    public void init() {

    }

    @Override
    public void end() {

    }
}
