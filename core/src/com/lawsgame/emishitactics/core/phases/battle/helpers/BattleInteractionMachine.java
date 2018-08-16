package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.LongTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.LongUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.ShortTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.ShortUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.SimpleAreaWidget;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.AreaWidget;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.TilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.UnitPanel;
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
    public I18NBundle mainStringBundle;

    public Array<AreaWidget> highlightedTiles;

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
        this.mainStringBundle = asm.get(Assets.STRING_BUNDLE_MAIN);


        this.highlightedTiles = new Array<AreaWidget>();
        AreaWidget areaWidget;
        for(int i = 0; i < Data.MAX_UNITS_UNDER_WARLORD; i++){
            areaWidget = new SimpleAreaWidget(battlefield, (i == 0) ?
                    Data.AreaType.SELECTED_TILE :
                    Data.AreaType.SQUAD_MEMBER);
            areaWidget.setVisible(false);
            highlightedTiles.add(areaWidget);
        }


        // UI
        this.uiStage = stageUI;
        this.shortTilePanel = new ShortTilePanel(stageUI.getViewport());
        this.shortUnitPanel = new ShortUnitPanel(stageUI.getViewport());
        this.longUnitPanel = new LongUnitPanel(stageUI.getViewport());
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
     * convient method which
     *  - move the camera at the given position
     *  - highlight relevant tile
     *  - display relevant short panels
     *
     * @param row
     * @param col
     * @param moveCamSmoothly
     * @param squadMembersHighlighted
     * @param displayPanels
     */
    public void focusOn(int row, int col, boolean moveCamSmoothly, boolean squadMembersHighlighted, boolean displayPanels){
        if(battlefield.isTileExisted(row, col)) {
            moveCamera(row, col, moveCamSmoothly);
            highlight(row, col, squadMembersHighlighted);
            if(displayPanels) {
                shortTilePanel.hide();
                shortTilePanel.set(battlefield.getTile(row, col));
                shortTilePanel.show();
                shortUnitPanel.hide();
                if (battlefield.isTileOccupied(row, col)) {
                    shortUnitPanel.set(battlefield.getUnit(row, col));
                    shortUnitPanel.show();
                }
            }
        }

    }

    public void moveCamera(int row, int col, boolean smoothly){
        gcm.focusOn(col - 0.5f, row - 0.5f, smoothly);
    }

    public void highlight(int row, int col, boolean allSquad){
        hideHighlightedTiles();
        if(allSquad && battlefield.isTileOccupied(row, col)) {

            IUnit sltdUnit = battlefield.getUnit(row, col);
            Array<IUnit> squad = sltdUnit.getSquad(true);
            Data.AreaType type = (sltdUnit.isAllyWith(Data.Allegeance.ALLY)) ? Data.AreaType.SQUAD_MEMBER : Data.AreaType.FOE_SQUAD_MEMBER;
            int[] squadMemberPos;
            for (int i = 0; i < squad.size; i++) {
                if(squad.get(i) != sltdUnit){
                    squadMemberPos = battlefield.getUnitPos(squad.get(i));
                    if(squad.get(i).isStandardBearer()){
                        highlightedTiles.get(i).setTiles(
                                Utils.getEreaFromRange(battlefield, squadMemberPos[0], squadMemberPos[1], 0, sltdUnit.getArmy().getBannerRange(sltdUnit)),
                                type);
                    }else {
                        highlightedTiles.get(i).setTile(squadMemberPos[0], squadMemberPos[1], type);
                    }
                }else{
                    highlightedTiles.get(i).setTile(row, col, Data.AreaType.SELECTED_TILE);
                }
                highlightedTiles.get(i).setVisible(true);
            }
        }else {
            highlightedTiles.get(0).setTile(row, col, Data.AreaType.SELECTED_TILE);
            highlightedTiles.get(0).setVisible(true);
        }
    }

    public void hideHighlightedTiles(){
        for(int i = 0; i < highlightedTiles.size; i++){
            highlightedTiles.get(i).setVisible(false);
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

}
