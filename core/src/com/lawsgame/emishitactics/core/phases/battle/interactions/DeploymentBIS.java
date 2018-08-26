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
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.AreaWidget;

public class DeploymentBIS extends BattleInteractionState {
    private int rowUnit;
    private int colUnit;
    private IUnit sltdUnit;
    private AreaWidget moveAreaWidget;
    private Array<AreaWidget> deploymentAreaWidgets;
    private StartButton startButton;

    private boolean initialized;

    public DeploymentBIS(final BattleInteractionMachine bim) {
        super(bim, true, true, true);
        this.initialized = false;

        AreaWidget areaWidget;
        this.deploymentAreaWidgets = new Array<AreaWidget>();
        for(int j = 0; j < bim.battlefield.getNumberOfDeploymentAreas(); j++) {
            areaWidget = new AreaWidget(bim.battlefield.getDeploymentArea(j));
            this.deploymentAreaWidgets.add(areaWidget);
        }

        bim.battlefield.randomlyDeploy(bim.player.getArmy());

        this.sltdUnit = bim.player.getArmy().getWarlord();
        int[] warlordPos = bim.battlefield.getUnitPos(sltdUnit);
        bim.focusOn(warlordPos[0], warlordPos[1], true, false, false, false, false);

        startButton = StartButton.create(bim.asm, bim.uiStage.getViewport());
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                bim.replace(new SelectActorBIS(bim, rowUnit, colUnit));
            }
        });

    }

    @Override
    public void init() {
        bim.uiStage.addActor(startButton);
        updateSltdUnit();
    }

    private void updateSltdUnit(){
        if(initialized) {
            this.moveAreaWidget = new AreaWidget(bim.battlefield, Data.AreaType.MOVE_AREA, bim.battlefield.getMoveArea(rowUnit, colUnit));
            this.sltdUnit = bim.battlefield.getUnit(rowUnit, colUnit);
            bim.focusOn(rowUnit, colUnit, true, true, true, true, false);
        }
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        if(bim.battlefield.isTileOccupied(row, col)){
            IUnit touchedUnit = bim.battlefield.getUnit(row, col);
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
                && bim.battlefield.isTileAvailable(row, col, sltdUnit.has(Data.Ability.PATHFINDER))){

            // if the selected unit belongs to the player's army adn the target tile is available
            if(isUnitRedeployingWithinTheSameArea(row, col, sltdUnit)) {

                bim.battlefield.moveUnit(rowUnit, colUnit, row, col, true);
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
        Array<IUnit> squadmembers = new Array<IUnit>();
        Array<int[]> tiles = bim.battlefield.getDeploymentArea(areaUnitIndex).getTiles();
        for(int i = 0; i < tiles.size; i++){
            r = tiles.get(i)[0];
            c = tiles.get(i)[1];
            if(bim.battlefield.isTileOccupiedBySameSquad(r, c, sltdUnit) && bim.battlefield.getUnit(r,c) != sltdUnit){
                squadmembers.add(bim.battlefield.removeUnit(r, c, true));
            }
        }

        // update sltd unit position
        bim.battlefield.moveUnit(rowUnit, colUnit, row, col, true);
        this.rowUnit = row;
        this.colUnit = col;

        // redeploy the other squad members in the new deployment area
        bim.battlefield.randomlyDeploy(squadmembers, areaTargetIndex);

        // update cam pos and UI
        updateSltdUnit();
    }


    private boolean isUnitRedeployingWithinTheSameArea(int rowTarget, int colTarget, IUnit unit){
        int[] unitPos = bim.battlefield.getUnitPos(unit);
        int areaTargetIndex = getDeploymentAreaIndex(rowTarget, colTarget);
        int areaUnitIndex = getDeploymentAreaIndex(unitPos[0], unitPos[1]);
        return  areaTargetIndex != -1 && areaUnitIndex != -1 && areaTargetIndex == areaUnitIndex;
    }

    private int getDeploymentAreaIndex(int rowTile, int colTile){
        for(int i = 0; i < bim.battlefield.getDeploymentAreas().size; i++){
            if(bim.battlefield.getDeploymentArea(i).contains(rowTile, colTile))
                return i;
        }
        return -1;
    }

    private boolean isDeployementAreaAvailable(int areaIndex){
        boolean res = false;
        if(0 == areaIndex){
            res = true;
        }else if(0 < areaIndex && areaIndex < bim.battlefield.getNumberOfDeploymentAreas()){
            res =  true;
            Array<int[]> targetArea = bim.battlefield.getDeploymentArea(areaIndex).getTiles();
            for(int i = 0; i < targetArea.size; i++){
                if(bim.battlefield.isTileOccupied(targetArea.get(i)[0], targetArea.get(i)[1])){
                    res = false;
                }
            }

        }
        return res;
    }

    @Override
    public void prerender(SpriteBatch batch) {

    }

    @Override
    public void renderBetween(SpriteBatch batch) {
        for(int j = 0; j < bim.battlefield.getNumberOfDeploymentAreas(); j++)
            deploymentAreaWidgets.get(j).render(batch);
        if(moveAreaWidget != null)
            moveAreaWidget.render(batch);
    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }

    @Override
    public void end() {
        super.end();
        bim.shortTilePanel.hide();
        bim.shortUnitPanel.hide();
        startButton.remove();
    }

    @Override
    public void update60(float dt) {

    }

    static class StartButton extends TextButton{
        public static final float WIDTH = 150;
        public static final float HEIGHT = 70;
        public static final float PAD_TOP = 7/8f;

        private StartButton(TextButtonStyle style, Viewport uiPort) {
            super("START BATTLE", style);
            setSize(WIDTH, HEIGHT);
            setX(uiPort.getWorldWidth()/2 - WIDTH/2);
            setY(uiPort.getWorldHeight()*PAD_TOP - HEIGHT/2);

        }

        static StartButton create(AssetManager asm, Viewport uiPort){
            TextureAtlas uiAtlas = asm.get(Assets.ATLAS_UI);
            Skin skin = new Skin(uiAtlas);

            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
            style.up = skin.getDrawable(Assets.UI_BUTTON_UP);
            style.down = skin.getDrawable(Assets.UI_BUTTON_DOWN);
            style.font = BattlePhase.testFont;

            return new StartButton(style, uiPort);
        }
    }


}
