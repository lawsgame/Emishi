package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.helpers.AssetProvider;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.helpers.ActionPanelPool;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattleCommandManager;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.Panel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.TilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.UnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.LongTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.LongUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.ShortTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.ShortUnitPanel;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.patterns.statemachine.StateMachine;

public class BattleInteractionMachine extends StateMachine<BattleInteractionState> implements Disposable{
    public final Player player;
    public final Battlefield battlefield;
    public final BattlefieldRenderer bfr;
    public final BattleCommandManager bcm;
    public final CameraManager gcm;
    public final AssetManager asm;
    public final ActionPanelPool app;
    public final TileHighlighter thl;
    public final AssetProvider assetProvider;
    public final InputMultiplexer multiplexer;
    public final AnimationScheduler scheduler;
    public final I18NBundle mainI18nBundle;

    public Stage uiStage;
    public final TilePanel shortTilePanel;
    public final TilePanel longTilePanel;
    public final UnitPanel shortUnitPanel;
    public final UnitPanel longUnitPanel;

    public SimpleCommand showSTP;
    public SimpleCommand showSUP;
    public SimpleCommand hideSTP;
    public SimpleCommand hideSUP;


    public BattleInteractionMachine(BattlefieldRenderer bfr, CameraManager gcm, AssetManager asm, Stage stageUI, Player player, AssetProvider assetProvider) {
        this.battlefield = bfr.getModel();
        this.bfr = bfr;
        this.gcm = gcm;
        this.asm = asm;
        this.player = player;
        this.assetProvider = assetProvider;
        this.app = new ActionPanelPool(stageUI.getViewport());
        this.scheduler = new AnimationScheduler();
        this.thl = new TileHighlighter(bfr);
        this.bcm = new BattleCommandManager(bfr, scheduler, player.getInventory(), thl);
        this.multiplexer = new InputMultiplexer();
        this.mainI18nBundle = asm.get(Assets.STRING_BUNDLE_MAIN);



        // UI
        this.uiStage = stageUI;
        this.shortTilePanel = new ShortTilePanel(stageUI.getViewport());
        this.shortUnitPanel = new ShortUnitPanel(stageUI.getViewport());
        this.longUnitPanel = new LongUnitPanel(stageUI.getViewport(), mainI18nBundle);
        this.longTilePanel = new LongTilePanel(stageUI.getViewport());

        stageUI.addActor(shortTilePanel);
        stageUI.addActor(shortUnitPanel);
        stageUI.addActor(longUnitPanel);
        stageUI.addActor(longTilePanel);

        showSTP = new ShowPanel(shortTilePanel);
        showSUP = new ShowPanel(shortUnitPanel);
        hideSTP = new HidePanel(shortTilePanel);
        hideSUP = new HidePanel(shortUnitPanel);


    }

    @Override
    public void push(BattleInteractionState bis){
        super.push(bis);
        updateInputHandler();
    }

    public void rollback(){
        super.rollback();
        updateInputHandler();
    }

    public void replace(BattleInteractionState bis){
        super.replace(bis);
        updateInputHandler();
    }

    private void updateInputHandler(){
        multiplexer.clear();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(new GestureDetector(getCurrentState()));
        Gdx.input.setInputProcessor(multiplexer);

    }

    // -------------------- SHARED METHOD -----------------------------

    /**
     * rowFocus : row of the tile on which the camera focus on
     * colFocus : col of the tile on which the camera focus on
     */
    private int rowFocus = -1, colFocus = -1;
    private Unit focusUnit = null;
    /**
     * convient method which
     *  - move the camera at the relevant location
     *  - highlight relevant tiles
     *  - display relevant short info panels
     *
     * @param rowTarget :           row of the tile the player last touched
     * @param colTarget :           col of the tile the player last touched
     * @param moveCamSmoothly :     (camera related) if true, the camera to the tile location smoothy, else the camera position is updated instantaneously
     * @param displayPanels :       (panel related) if true, the short info panels are displayed
     * @param erasePanelMemory :    (panel camera related) the method keep in memory which tile has been focused on the last time it was called to only updated short info panels which require to be updated, if true, this memory is erased.
     *                              Practicle use : when the selected unit remains the same while its stats changed, then set as TRUE allow the panel to update itself to reflect the new state of this unit.
     *                              Idem if the tile on which the selected unit stands has changed. Any other, this parameters should be FALsE.
     * @param rowSelectedTile :     (tile highlighting related) row on which the active unit stands on
     * @param colSelectedTile :     (tile highlighting related) col on which the active unit stands on
     * @param highligthSquad :      (tile highlighting related) if true and the tile is occupied by a unit, his whole squad is also highlighted
     */
    public void focusOn(int rowTarget, int colTarget, boolean moveCamSmoothly, boolean displayPanels, boolean erasePanelMemory, int rowSelectedTile, int colSelectedTile, boolean highligthSquad){

            moveCamera(rowTarget, colTarget, moveCamSmoothly);
            updateShortPanels(rowTarget, colTarget, displayPanels, erasePanelMemory);
            thl.highlight(rowTarget, colTarget, highligthSquad, rowSelectedTile, colSelectedTile);
    }

    /**
     * convient method which
     *  - move the camera at the relevant location
     *  - highlight relevant tiles
     *  - display relevant short info panels
     *
     * @param rowTarget :           row of the tile the player last touched
     * @param colTarget :           col of the tile the player last touched
     * @param moveCamSmoothly :     (camera related) if true, the camera to the tile location smoothy, else the camera position is updated instantaneously
     * @param displayPanels :       (panel related) if true, the short info panels are displayed
     * @param erasePanelMemory :         (panel camera related) the method keep in memory which tile has been focused on the last time it was called to only updated short info panels which require to be updated, if true, this memory is erased.
     *                              Practicle use : when the selected unit remains the same while its stats changed, then set as TRUE allow the panel to update itself to reflect the new state of this unit.
     *                              Idem if the tile on which the selected unit stands has changed. Any other, this parameters should be FALsE.
     * @param mode :                (tile highlighting related) defines what to do as for the selected tile highlighting
     * @param highligthSquad :      (tile highlighting related) if true and the tile is occupied by a unit, his whole squad is also highlighted
     */
    public void focusOn(int rowTarget, int colTarget, boolean moveCamSmoothly, boolean displayPanels, boolean erasePanelMemory, TileHighlighter.SltdUpdateMode mode, boolean highligthSquad){

        moveCamera(rowTarget, colTarget, moveCamSmoothly);
        updateShortPanels(rowTarget, colTarget, displayPanels, erasePanelMemory);
        thl.highlight(rowTarget, colTarget, highligthSquad, mode);

    }

    public void updateShortPanels(int rowTarget, int colTarget, boolean displayPanels, boolean erasePanelMemory){
        if(erasePanelMemory){
            rowFocus = -1;
            colFocus = -1;
            focusUnit = null;
        }

        if(bfr.getModel().isTileExisted(rowTarget, colTarget) && displayPanels){
            if(rowTarget != rowFocus || colTarget != colFocus || shortTilePanel.isHiding()) {
                rowFocus = rowTarget;
                colFocus = colTarget;
                shortTilePanel.hide();
                shortTilePanel.set(battlefield.getTile(rowTarget, colTarget).getType());
                shortTilePanel.show();
            }

            if (battlefield.isTileOccupied(rowTarget, colTarget)) {
                if (focusUnit == null || focusUnit != battlefield.getUnit(rowTarget, colTarget) || shortUnitPanel.isHiding()) {
                    focusUnit = battlefield.getUnit(rowTarget, colTarget);
                    shortUnitPanel.hide();
                    shortUnitPanel.set(battlefield.getUnit(rowTarget, colTarget));
                    shortUnitPanel.show();
                }
            } else {
                shortUnitPanel.hide();
            }
        }

    }

    public void moveCamera(int rowTarget, int colTarget, boolean smoothly){
        gcm.focusOn(bfr.getCenterX(rowTarget, colTarget), bfr.getCenterY(rowTarget, colTarget) , smoothly);
    }




    // --------------------- GETTES & SETTERS ----------------------------


    public String toString(){
        String str = "";
        for(int i=0; i < states.size(); i++){
            str += "\n"+states.get(i).toString();
        }
        return str;
    }

    @Override
    public void dispose() {
        assetProvider.dispose();
    }





    // ----------- UTILITY CLASS -----------------------------

    public static class ShowPanel extends SimpleCommand{
        private Panel panel;

        public ShowPanel(Panel panel) {
            this.panel = panel;
        }

        @Override
        public void apply() {
            panel.show();
        }
    }

    public static class HidePanel extends SimpleCommand{
        private Panel panel;

        public HidePanel(Panel panel) {
            this.panel = panel;
        }

        @Override
        public void apply() {
            panel.hide();
        }
    }
}
