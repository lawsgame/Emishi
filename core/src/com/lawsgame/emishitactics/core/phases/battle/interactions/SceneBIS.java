package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionSystem;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class SceneBIS extends BattleInteractionState {


    public SceneBIS(BattleInteractionSystem BISys) {
        super(BISys, true, false, false);
        System.out.println("SceneBIS");
    }

    @Override
    public void update(float dt) {

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
    public void handleTouchInput(float gameX, float gameY) {
        bis.set(new DeploymentBIS(bis, true, null));
    }

    @Override
    public void init() {

    }

    @Override
    public void dispose() {

    }
}
