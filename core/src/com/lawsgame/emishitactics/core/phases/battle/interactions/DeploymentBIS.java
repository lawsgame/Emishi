package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionSystem;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.TempoAreaRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;

public class DeploymentBIS extends BattleInteractionState{
    AreaRenderer deploymentAreaRenderer;

    public DeploymentBIS(BattleInteractionSystem bis, boolean fromSceneIS, AreaRenderer deploymentAreaRenderer) {
        super(bis, true,true,  true);
        if(fromSceneIS){
            bis.battlefield.randomlyDeployArmy(bis.playerArmy);
        }
        if(deploymentAreaRenderer == null){
            Array<int[]> deploymentArea = bis.battlefield.getDeploymentArea();
            this.deploymentAreaRenderer = new TempoAreaRenderer(bis.asm, bis.battlefield, Assets.HighlightedTile.DEPLOYMENT, deploymentArea);
        }else{
            this.deploymentAreaRenderer = deploymentAreaRenderer;
        }

        int[] warlordPos = bis.battlefield.getUnitPos(bis.playerArmy.getWarlord());
        bis.gcm.focusOn(warlordPos[0], warlordPos[1], true);

    }

    @Override
    public void prerender(SpriteBatch batch) {

    }

    @Override
    public void renderBetween(SpriteBatch batch) {
        deploymentAreaRenderer.render(batch);
    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }

    @Override
    public void handleTouchInput(float gameX, float gameY) {

    }

    @Override
    public void init() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float dt) {
        deploymentAreaRenderer.update(dt);
    }
}
