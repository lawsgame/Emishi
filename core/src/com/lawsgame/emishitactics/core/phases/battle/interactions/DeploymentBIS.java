package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.TempoArea;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.Area;

public class DeploymentBIS extends BattleInteractionState{

    public DeploymentBIS(BattleInteractionMachine bis) {
        super(bis, true,true,  true);
        bis.battlefield.randomlyDeployArmy(bis.playerArmy);
        init();
    }

    @Override
    public void init() {
        int[] warlordPos = bim.battlefield.getUnitPos(bim.playerArmy.getWarlord());
        focusOn(warlordPos[0], warlordPos[1], true);
        bim.deploymentArea.setVisible(true);
    }

    @Override
    public void prerender(SpriteBatch batch) { }

    @Override
    public void renderBetween(SpriteBatch batch) {
    }

    @Override
    public void renderAhead(SpriteBatch batch) { }

    @Override
    public void handleTouchInput(int row, int col) {
        if(bim.battlefield.isTileOccupied(row, col)){
            bim.set(new RedeploymentBIS(bim, row, col));
        }else{
            bim.shortTilePanel.hide();
            bim.shortTilePanel.set(bim.battlefield.getTile(row, col));
            bim.shortTilePanel.show();
        }
    }

    @Override
    public void end() { }

    @Override
    public void update60(float dt) { }
}
