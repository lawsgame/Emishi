package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.TempoArea;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.Area;

public class DeploymentBIS extends BattleInteractionState {
    int rowUnit;
    int colUnit;
    Unit sltdUnit;
    Area moveArea;
    Area deploymentArea;

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
        this.deploymentArea = new TempoArea(bim.asm, bim.battlefield, Assets.HighlightedTile.DEPLOYMENT, bim.battlefield.getDeploymentArea());

    }

    @Override
    public void init() {
        if(initialized) {
            this.moveArea = new TempoArea(bim.asm, bim.battlefield, Assets.HighlightedTile.MOVE_RANGE, bim.battlefield.getMoveArea(rowUnit, colUnit));
            this.sltdUnit = bim.battlefield.getUnit(rowUnit, colUnit);
            focusOn(rowUnit, colUnit, true);
            highlight(rowUnit, colUnit);
            bim.shortTilePanel.set(bim.battlefield.getTile(rowUnit, colUnit));
            bim.shortTilePanel.show();
            bim.shortUnitPanel.set(bim.battlefield, rowUnit, colUnit);
            bim.shortUnitPanel.show();
        }
    }

    @Override
    public void handleTouchInput(int row, int col) {
        if(bim.battlefield.isTileOccupied(row, col)){
            Unit touchedUnit = bim.battlefield.getUnit(row, col);
            if (touchedUnit != sltdUnit || !initialized) {
                //touchedUnit become the selected unit
                initialized = true;
                this.rowUnit = row;
                this.colUnit = col;
                init();
            }
        }else if(sltdUnit.isPlayerControlled()
                && deploymentArea.contains(row, col)
                && bim.battlefield.isTileAvailable(row, col, sltdUnit.has(Data.Ability.PATHFINDER))){
            // if the selected unit belongs to the player's army and the tile at (row, col) is available and within the deployment area, then redeploy the unit
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
        deploymentArea.render(batch);
        if(moveArea != null)
            moveArea.render(batch);
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
