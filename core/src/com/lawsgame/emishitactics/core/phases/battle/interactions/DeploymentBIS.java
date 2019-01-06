package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class DeploymentBIS extends BattleInteractionState {
    private int rowUnit;
    private int colUnit;
    private Unit sltdUnit;
    private Area moveArea;
    private StartButton startButton;

    private boolean initialized;

    public DeploymentBIS(final BattleInteractionMachine bim) {
        super(bim, true, true, true, false, true);
        this.initialized = false;

        this.sltdUnit = bim.player.getArmy().getWarlord();
        int[] warlordPos = bim.bfr.getModel().getUnitPos(sltdUnit);
        bim.focusOn(warlordPos[0], warlordPos[1], true, false, false, TileHighlighter.SltdUpdateMode.ERASE_SLTD_TILE_MEMORY, false);

        startButton = new StartButton(bim.asm.get(Assets.SKIN_UI, Skin.class), bim.uiStage.getViewport());
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                bim.replace(new SelectActorBIS(bim, rowUnit, colUnit, true));
            }
        });

    }

    @Override
    public void init() {
        System.out.println("DEPLOYEMENT BIS");
        super.init();
        bim.uiStage.addActor(startButton);
        bim.bfr.displayDeploymentAreas(true);
        updateSltdUnit();
    }

    private void updateSltdUnit(){
        if(initialized) {
            //update model area
            bim.bfr.removeAreaRenderer(moveArea);
            this.moveArea = new Area(bim.bfr.getModel(), Data.AreaType.MOVE_AREA, bim.bfr.getModel().getMoveArea(rowUnit, colUnit));
            bim.bfr.addAreaRenderer(moveArea);

            this.sltdUnit = bim.bfr.getModel().getUnit(rowUnit, colUnit);
            bim.focusOn(rowUnit, colUnit, true, true, false, TileHighlighter.SltdUpdateMode.MATCH_TOUCHED_TILE, true);
        }
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        if(bim.bfr.getModel().isTileOccupiedByAlly(row, col, bim.player.getArmy().getAffiliation())){
            Unit touchedUnit = bim.bfr.getModel().getUnit(row, col);
            if (touchedUnit != sltdUnit || !initialized) {

                //touchedUnit become the selected unit
                initialized = true;
                this.rowUnit = row;
                this.colUnit = col;
                updateSltdUnit();
                return true;
            }
        }else if(initialized
                && sltdUnit.getArmy().isPlayerControlled()
                && bim.bfr.getModel().isTileAvailable(row, col, sltdUnit.has(Data.Ability.PATHFINDER))){

            // if the selected unit belongs to the player's army adn the target tile is available
            if(isUnitRedeployingWithinTheSameArea(row, col, sltdUnit)) {

                bim.bfr.getModel().moveUnit(rowUnit, colUnit, row, col, true);
                this.rowUnit = row;
                this.colUnit = col;
                updateSltdUnit();
                return true;
            }else if(isDeployementAreaAvailable(getDeploymentAreaIndex(row, col))
                    && sltdUnit.getSquadIndex() != 0){

                redeploySquad(row, col);
                return true;
            }
        }
        return false;
    }


    private void redeploySquad(int row, int col) {

        //get the area relevant indexes
        int areaTargetIndex = getDeploymentAreaIndex(row, col);
        int areaUnitIndex = getDeploymentAreaIndex(rowUnit, colUnit);

        //fetch all squad member
        int r;
        int c;
        Array<Unit> squadmembers = new Array<Unit>();
        Array<int[]> tiles = bim.bfr.getModel().getDeploymentArea(areaUnitIndex).getTiles();
        for(int i = 0; i < tiles.size; i++){
            r = tiles.get(i)[0];
            c = tiles.get(i)[1];
            if(bim.bfr.getModel().isTileOccupiedByAnotherSquadMember(r, c, sltdUnit)){
                squadmembers.add(bim.bfr.getModel().removeUnit(r, c));
            }
        }

        // update sltd unit position
        bim.bfr.getModel().moveUnit(rowUnit, colUnit, row, col, true);
        this.rowUnit = row;
        this.colUnit = col;

        // redeploy the other squad members in the new deployment area
        bim.bfr.getModel().randomlyDeploy(squadmembers, areaTargetIndex, true);

        // update cam pos and UI
        updateSltdUnit();
    }


    private boolean isUnitRedeployingWithinTheSameArea(int rowTarget, int colTarget, Unit unit){
        int[] unitPos = bim.bfr.getModel().getUnitPos(unit);
        int areaTargetIndex = getDeploymentAreaIndex(rowTarget, colTarget);
        int areaUnitIndex = getDeploymentAreaIndex(unitPos[0], unitPos[1]);
        return  areaTargetIndex != -1 && areaUnitIndex != -1 && areaTargetIndex == areaUnitIndex;
    }

    private int getDeploymentAreaIndex(int rowTile, int colTile){
        for(int i = 0; i < bim.bfr.getModel().getDeploymentAreas().size; i++){
            if(bim.bfr.getModel().getDeploymentArea(i).contains(rowTile, colTile))
                return i;
        }
        return -1;
    }

    private boolean isDeployementAreaAvailable(int areaIndex){
        boolean res = false;
        if(0 == areaIndex){
            res = true;
        }else if(0 < areaIndex && areaIndex < bim.bfr.getModel().getNumberOfDeploymentAreas()){
            res =  true;
            Array<int[]> targetArea = bim.bfr.getModel().getDeploymentArea(areaIndex).getTiles();
            for(int i = 0; i < targetArea.size; i++){
                if(bim.bfr.getModel().isTileOccupied(targetArea.get(i)[0], targetArea.get(i)[1])){
                    res = false;
                }
            }

        }
        return res;
    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }

    @Override
    public void end() {
        super.end();
        bim.bfr.removeAreaRenderer(moveArea);
        bim.bfr.displayDeploymentAreas(false);
        bim.pp.shortTilePanel.hide();
        bim.pp.shortUnitPanel.hide();
        startButton.remove();
    }

    @Override
    public void update60(float dt) {

    }

    static class StartButton extends TextButton{
        public static final float WIDTH = 150;
        public static final float HEIGHT = 70;
        public static final float PAD_TOP = 7/8f;

        private StartButton(Skin skin, Viewport uiPort) {
            super("START BATTLE", skin, "default");
            setSize(WIDTH, HEIGHT);
            setX(uiPort.getWorldWidth()/2 - WIDTH/2);
            setY(uiPort.getWorldHeight()*PAD_TOP - HEIGHT/2);

        }
    }


}
