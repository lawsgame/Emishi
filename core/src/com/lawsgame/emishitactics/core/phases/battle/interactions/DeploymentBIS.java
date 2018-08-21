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
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.AreaWidget;

public class DeploymentBIS extends BattleInteractionState {
    int rowUnit;
    int colUnit;
    IUnit sltdUnit;
    AreaWidget moveAreaWidget;
    Array<AreaWidget> deploymentAreaWidgets;
    StartButton startButton;

    boolean initialized;

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
        bim.focusOn(warlordPos[0], warlordPos[1], true, false, false, false);

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
            bim.focusOn(rowUnit, colUnit, true, true, true, true);
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

            if(isUnitRedeployedWithinTheSameArea(row, col, sltdUnit)) {
                // if the selected unit belongs to the player's army and the buildingType at (rowInit, colInit) is available and within the deployment area, then redeploy the unit
                bim.battlefield.moveUnit(rowUnit, colUnit, row, col, true);
                this.rowUnit = row;
                this.colUnit = col;
                updateSltdUnit();
                return true;
            }
        }
        return false;
    }

    private boolean isUnitRedeployedWithinTheSameArea(int rowTargetTile, int colTarget, IUnit unit){
        /*
        int[] unitPos = bim.battlefield.getUnitPos(unit);
        return (Utils.arrayContains(bim.battlefield.getDeploymentArea(false), rowTargetTile, colTarget)
                && Utils.arrayContains(bim.battlefield.getDeploymentArea(false), unitPos))
                ||(Utils.arrayContains(bim.battlefield.getDeploymentArea(true), rowTargetTile, colTarget)
                && Utils.arrayContains(bim.battlefield.getDeploymentArea(true), unitPos));
        */
        return true;
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
