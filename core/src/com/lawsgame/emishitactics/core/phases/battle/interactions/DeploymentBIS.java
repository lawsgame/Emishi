package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.SimpleAreaWidget;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.AreaWidget;

public class DeploymentBIS extends BattleInteractionState {
    int rowUnit;
    int colUnit;
    IUnit sltdUnit;
    AreaWidget moveAreaWidget;
    AreaWidget deploymentAreaWidget;

    boolean initialized;

    public DeploymentBIS(BattleInteractionMachine bim) {
        super(bim);
        bim.battlefield.randomlyDeployArmy(bim.playerArmy);
        this.sltdUnit = bim.playerArmy.getWarlord();
        int[] warlordPos = bim.battlefield.getUnitPos(sltdUnit);
        this.rowUnit = warlordPos[0];
        this.colUnit = warlordPos[1];
        highlight(rowUnit, colUnit);
        focusOn(rowUnit, colUnit, true);
        this.deploymentAreaWidget = new SimpleAreaWidget(bim.battlefield, Data.AreaType.DEPLOYMENT, bim.battlefield.getDeploymentArea());
        initialized = false;

    }

    @Override
    public void init() {
        if(initialized) {
            this.moveAreaWidget = new SimpleAreaWidget(bim.battlefield, Data.AreaType.MOVE_RANGE, bim.battlefield.getMoveArea(rowUnit, colUnit));
            this.sltdUnit = bim.battlefield.getUnit(rowUnit, colUnit);
            focusOn(rowUnit, colUnit, true);
            highlight(rowUnit, colUnit);
            bim.shortTilePanel.hide();
            bim.shortTilePanel.set(bim.battlefield.getTile(rowUnit, colUnit));
            bim.shortTilePanel.show();
            bim.shortUnitPanel.hide();
            bim.shortUnitPanel.set(bim.battlefield, rowUnit, colUnit);
            bim.shortUnitPanel.show();
        }
    }

    @Override
    public void handleTouchInput(int row, int col) {
        if(bim.battlefield.isTileOccupied(row, col)){
            IUnit touchedUnit = bim.battlefield.getUnit(row, col);
            if (touchedUnit != sltdUnit || !initialized) {
                //touchedUnit become the selected unit
                initialized = true;
                this.rowUnit = row;
                this.colUnit = col;
                init();
            }
        }else if(initialized
                && sltdUnit.getArmy().isPlayerControlled()
                && deploymentAreaWidget.contains(row, col)
                && bim.battlefield.isTileAvailable(row, col, sltdUnit.has(Data.Ability.PATHFINDER))){
            // if the selected unit belongs to the player's army and the tileType at (row, col) is available and within the deployment area, then redeploy the unit
            bim.battlefield.moveUnit(rowUnit, colUnit, row, col);
            bim.battlefield.notifyAllObservers(new int[]{row, col});
            this.rowUnit = row;
            this.colUnit = col;
            init();
        }else{
            bim.shortUnitPanel.hide();
            bim.shortTilePanel.hide();
            bim.shortTilePanel.set(bim.battlefield.getTile(row, col));
            bim.shortTilePanel.show();
        }
    }

    @Override
    public void prerender(SpriteBatch batch) {

    }

    @Override
    public void renderBetween(SpriteBatch batch) {
        deploymentAreaWidget.render(batch);
        if(moveAreaWidget != null)
            moveAreaWidget.render(batch);
    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }

    @Override
    public void end() {
        bim.shortTilePanel.hide();
        bim.shortUnitPanel.hide();
    }

    @Override
    public void update60(float dt) {

    }


}
