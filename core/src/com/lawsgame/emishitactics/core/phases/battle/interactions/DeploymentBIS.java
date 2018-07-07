package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionSystem;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class DeploymentBIS extends BattleInteractionState{

    public DeploymentBIS(BattleInteractionSystem BISys) {
        super(BISys, true,true,  true);
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
    public void onTouch(float gameX, float gameY) {

    }

    @Override
    public void init() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float dt) {

    }
}
