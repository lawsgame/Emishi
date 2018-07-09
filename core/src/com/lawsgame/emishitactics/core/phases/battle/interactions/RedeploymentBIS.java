package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.TempoArea;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.Area;

public class RedeploymentBIS extends BattleInteractionState {
    int rowUnit;
    int colUnit;
    Unit sltdUnit;
    Area moveArea;

    public RedeploymentBIS(BattleInteractionMachine bim, int rowUnit, int colUnit) {
        super(bim);
        this.rowUnit = rowUnit;
        this.colUnit = colUnit;
        init();
    }

    @Override
    public void init() {
        this.moveArea = new TempoArea(bim.asm, bim.battlefield, Assets.HighlightedTile.MOVE_RANGE, bim.battlefield.getMoveArea(rowUnit, colUnit));
        this.sltdUnit = bim.battlefield.getUnit(rowUnit, colUnit);
        focusOn(rowUnit, colUnit, true);
        highlight(rowUnit, colUnit);
        bim.shortTilePanel.set(bim.battlefield.getTile(rowUnit, colUnit));
        bim.shortTilePanel.show();
        bim.shortUnitPanel.set(bim.battlefield, rowUnit, colUnit);
        bim.shortUnitPanel.show();
        bim.deploymentArea.setVisible(true);
    }

    @Override
    public void handleTouchInput(int row, int col) {
        System.out.println("INPUT");

        if(bim.battlefield.isTileOccupied(row, col)){
            Unit touchedUnit = bim.battlefield.getUnit(row, col);
            if(touchedUnit != sltdUnit){
                //touchedUnit become the selected unit
                this.rowUnit = row;
                this.colUnit = col;
                init();
            }
        }else if(sltdUnit.isPlayerControlled()
                && bim.deploymentArea.contains(row, col)
                && bim.battlefield.isTileAvailable(row, col, sltdUnit.has(Data.PassiveAbility.PATHFINDER))){
            // if the selected unit belongs to the player's army and the tile at (row, col) is available and within the deployment area, then redeploy the unit
            bim.battlefield.moveUnit(rowUnit, colUnit, row, col, false);
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
