package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.helpers.ActionPanelPool;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattleCommandManager;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.AreaWidget;
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

public class BattleInteractionMachine extends StateMachine<BattleInteractionState> {
    public Player player;
    public Battlefield battlefield;

    public BattlefieldRenderer bfr;
    public BattleCommandManager bcm;
    public CameraManager gcm;
    public AssetManager asm;
    public ActionPanelPool app;
    public InputMultiplexer multiplexer;
    public AnimationScheduler scheduler;
    public I18NBundle mainI18nBundle;

    public AreaWidget selectedTile;
    public AreaWidget touchedTile;
    public Array<AreaWidget> touchedRelatedTiles;

    public Stage uiStage;
    public final TilePanel shortTilePanel;
    public final TilePanel longTilePanel;
    public final UnitPanel shortUnitPanel;
    public final UnitPanel longUnitPanel;

    public SimpleCommand showSTP;
    public SimpleCommand showSUP;
    public SimpleCommand hideSTP;
    public SimpleCommand hideSUP;


    public BattleInteractionMachine(Battlefield battlefield, BattlefieldRenderer bfr, CameraManager gcm, AssetManager asm, Stage stageUI, Player player) {
        this.battlefield = battlefield;
        this.bfr = bfr;
        this.gcm = gcm;
        this.asm = asm;
        this.app = new ActionPanelPool(stageUI.getViewport());
        this.scheduler = new AnimationScheduler();
        this.bcm = new BattleCommandManager(bfr, scheduler);
        this.player = player;
        this.multiplexer = new InputMultiplexer();
        this.mainI18nBundle = asm.get(Assets.STRING_BUNDLE_MAIN);


        this.selectedTile = new AreaWidget(battlefield, Data.AreaType.SELECTED_UNIT);
        this.touchedTile = new AreaWidget(battlefield, Data.AreaType.TOUCHED_TILE);
        this.touchedRelatedTiles = new Array<AreaWidget>();
        AreaWidget areaWidget;
        for(int i = 1; i < Data.MAX_UNITS_UNDER_WARLORD; i++){
            areaWidget = new AreaWidget(battlefield, Data.AreaType.SQUAD_MEMBER );
            areaWidget.setVisible(false);
            touchedRelatedTiles.add(areaWidget);
        }


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

        showSTP = new SimpleCommand() {
            @Override
            public void apply() {
                shortTilePanel.show();
            }
        };
        showSUP = new SimpleCommand() {
            @Override
            public void apply() {
                shortUnitPanel.show();
            }
        };
        hideSTP = new SimpleCommand() {
            @Override
            public void apply() {
                shortTilePanel.hide();
            }
        };
        hideSUP = new SimpleCommand() {
            @Override
            public void apply() {
                shortUnitPanel.hide();
            }
        };

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

    private int rowFocus = -1, colFocus = -1;
    private IUnit focusUnit = null;
    /**
     * convient method which
     *  - move the camera at the relevant location
     *  - highlight relevant tiles
     *  - display relevant short info panels
     *
     * @param rowTarget
     * @param colTarget
     * @param moveCamSmoothly :     if true, the camera to the tile location smoothy, else the camera position is updated instantaneously
     * @param highlightAllsquad :   if true and the tile is occupied by a unit, his whole squad is also highlighted
     * @param displayPanels :       if true, the short info panels are displayed
     * @param targetIsSelected :    govern each area type / color is used to highlight the focused tile
     * @param resetMemory :         the method keep in memory which tile has been focused on the last time it was called to only updated short info panels which require to be updated, if true, this memory is erased.
     */
    public void focusOn(int rowTarget, int colTarget, boolean moveCamSmoothly, boolean highlightAllsquad, boolean displayPanels, boolean targetIsSelected, boolean resetMemory){
        if(resetMemory){
            rowFocus = -1;
            colFocus = -1;
            focusUnit = null;
        }

        if(battlefield.isTileExisted(rowTarget, colTarget)) {
            moveCamera(rowTarget, colTarget, moveCamSmoothly);
            highlight(rowTarget, colTarget, highlightAllsquad, targetIsSelected);
            if(displayPanels) {

                if(rowTarget != rowFocus || colTarget != colFocus || shortTilePanel.isHiding()) {
                    rowFocus = rowTarget;
                    colFocus = colTarget;
                    shortTilePanel.hide();
                    shortTilePanel.set(battlefield.getTile(rowTarget, colTarget));
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
    }

    public void moveCamera(int row, int col, boolean smoothly){
        gcm.focusOn(col, row , smoothly);
    }

    public void highlight(int row, int col, boolean allSquad, boolean selected){
        removeTileHighlighting(true);
        if(allSquad && battlefield.isTileOccupied(row, col)) {

            IUnit sltdUnit = battlefield.getUnit(row, col);
            Array<IUnit> squad = sltdUnit.getSquad(true);
            Data.AreaType type = Data.AreaType.SQUAD_MEMBER ;
            int[] squadMemberPos;
            for (int i = 0; i < squad.size; i++) {
                if(squad.get(i) != sltdUnit){
                    squadMemberPos = battlefield.getUnitPos(squad.get(i));
                    if(squad.get(i).isStandardBearer()){
                        touchedRelatedTiles.get(i).setTiles(
                                Utils.getEreaFromRange(battlefield, squadMemberPos[0], squadMemberPos[1], 0, sltdUnit.getArmy().getBannerRange(sltdUnit)),
                                type,
                                true);
                    }else {
                        touchedRelatedTiles.get(i).setTile(squadMemberPos[0], squadMemberPos[1], type, true);
                    }
                    touchedRelatedTiles.get(i).setVisible(true);
                }
            }
        }

        if(selected){
            selectedTile.setTile(row, col, true);
            selectedTile.setVisible(true);
        }else {
            touchedTile.setTile(row, col, true);
            touchedTile.setVisible(true);
        }

    }

    public void removeTileHighlighting(boolean exceptSelectedTile){
        touchedTile.setVisible(false);
        if(!exceptSelectedTile) selectedTile.setVisible(false);
        for(int i = 0; i < touchedRelatedTiles.size; i++){
            touchedRelatedTiles.get(i).setVisible(false);
        }
    }



    // --------------------- GETTES & SETTERS ----------------------------


    public String toString(){
        String str = "";
        for(int i=0; i < states.size(); i++){
            str += "\n"+states.get(i).toString();
        }
        return str;
    }

    public static class FocusOn extends SimpleCommand{
        private BattleInteractionMachine bim;
        private int rowFocus;
        private int colFocus;


        public FocusOn (BattleInteractionMachine bim, int rowFocus, int colFocus) {
            this.bim = bim;
            this.rowFocus = rowFocus;
            this.colFocus = colFocus;
        }

        @Override
        public void apply() {
            bim.focusOn(rowFocus, colFocus, true, false, false, true, false);

        }
    }

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
