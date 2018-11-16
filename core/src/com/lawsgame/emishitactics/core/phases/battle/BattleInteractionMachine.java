package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.helpers.AssetProvider;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.helpers.ActionPanelPool;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattleCommandManager;
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
    public final AssetProvider assetProvider;
    public final InputMultiplexer multiplexer;
    public final AnimationScheduler scheduler;
    public final I18NBundle mainI18nBundle;

    private Area selectedTile;
    private Area touchedTile;
    private Area warchiefBannerCoveredArea;

    public FoeActionArea ffa;

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
        this.assetProvider = assetProvider;
        this.app = new ActionPanelPool(stageUI.getViewport());
        this.scheduler = new AnimationScheduler();
        this.bcm = new BattleCommandManager(bfr, scheduler, player.getInventory());
        this.player = player;
        this.multiplexer = new InputMultiplexer();
        this.mainI18nBundle = asm.get(Assets.STRING_BUNDLE_MAIN);

        this.selectedTile = new Area(battlefield, Data.AreaType.SELECTED_UNIT);
        this.touchedTile = new Area(battlefield, Data.AreaType.TOUCHED_TILE);
        this.warchiefBannerCoveredArea = new Area(battlefield, Data.AreaType.BANNER_AREA);
        this.bfr.addAreaRenderer(selectedTile);
        this.bfr.addAreaRenderer(touchedTile);
        this.bfr.addAreaRenderer(warchiefBannerCoveredArea);
        this.bfr.getAreaRenderer(touchedTile).setVisible(false);
        this.bfr.getAreaRenderer(selectedTile).setVisible(false);
        this.bfr.getAreaRenderer(warchiefBannerCoveredArea).setVisible(false);

        this.ffa = new FoeActionArea(bfr);

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


        //TEST
        /*
        Trigger trigger;
        for(int r = 0; r < battlefield.getNbRows(); r++){
            for(int c = 0; c < battlefield.getNbColumns(); c++){
                trigger = new SimpleTrigger(true);
                battlefield.getTile(r, c).add(trigger);
                trigger.addEvent(new MessageEvent(bfr, scheduler, "     event triggerd at "+r+" "+c));
            }
        }
        */


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
    private Unit focusUnit = null;
    /**
     * convient method which
     *  - move the camera at the relevant location
     *  - highlight relevant tiles
     *  - display relevant short info panels
     *
     * @param rowTarget
     * @param colTarget
     * @param moveCamSmoothly :     if true, the camera to the tile location smoothy, else the camera position is updated instantaneously
     * @param highligthSquad :      if true and the tile is occupied by a unit, his whole squad is also highlighted
     * @param displayPanels :       if true, the short info panels are displayed
     * @param targetIsSelected :    govern each area type / color is used to highlight the focused tile
     * @param resetMemory :         the method keep in memory which tile has been focused on the last time it was called to only updated short info panels which require to be updated, if true, this memory is erased.
     */
    public void focusOn(int rowTarget, int colTarget, boolean moveCamSmoothly, boolean highligthSquad, boolean displayPanels, boolean targetIsSelected, boolean resetMemory){
        if(resetMemory){
            rowFocus = -1;
            colFocus = -1;
            focusUnit = null;
        }

        if(battlefield.isTileExisted(rowTarget, colTarget)) {
            moveCamera(rowTarget, colTarget, moveCamSmoothly);
            highlight(rowTarget, colTarget, highligthSquad, targetIsSelected);
            if(displayPanels) {

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
    }

    protected void moveCamera(int rowTile, int colTile, boolean smoothly){
        gcm.focusOn(bfr.getCenterX(rowTile, colTile), bfr.getCenterY(rowTile, colTile) , smoothly);
    }

    protected void highlight(int row, int col, boolean bannerShown, boolean selected){
        removeTileHighlighting(true);

        // touch feed back
        touchedTile.setTiles(row, col, true);
        bfr.getAreaRenderer(touchedTile).setVisible(true);

        // set selected tile
        if(selected){
            selectedTile.setTiles(row, col, true);
            bfr.getAreaRenderer(selectedTile).setVisible(true);
        }

        // show warchief positon and his banner covered area
        if(bannerShown && battlefield.isTileOccupied(row, col)) {
            Unit warchief = battlefield.getUnit(row, col).getWarchief();
            if (warchief != battlefield.getUnit(row, col)) {
                if(bfr.getModel().isUnitDeployed(warchief) && !warchief.isOutOfAction()) {
                    warchiefBannerCoveredArea.setTiles(bfr.getUnitRenderer(warchief).getCurrentRow(), bfr.getUnitRenderer(warchief).getCurrentCol(), 0, warchief.getArmy().getBannerRange(warchief), true);
                    bfr.getAreaRenderer(warchiefBannerCoveredArea).setVisible(true);
                }
            }
        }



    }

    public void removeTileHighlighting(boolean exceptSelectedTile){
        bfr.getAreaRenderer(touchedTile).setVisible(false);
        bfr.getAreaRenderer(warchiefBannerCoveredArea).setVisible(false);
        if(!exceptSelectedTile) bfr.getAreaRenderer(selectedTile).setVisible(false);

    }


    // --------------------- GETTES & SETTERS ----------------------------


    public boolean isStateActive(BattleInteractionState bis){
        return states.peek() == bis;
    }

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


    public static class FoeActionArea{
        private Area actionArea;
        private Array<Unit> foes;

        public FoeActionArea(BattlefieldRenderer bfr){
            this.actionArea = new Area(bfr.getModel(), Data.AreaType.FOE_ACTION_AREA);
            this.foes = new Array<Unit>();
            bfr.addAreaRenderer(this.actionArea);
        }

        public void update(int rowFoe, int colFoe){
            Battlefield bf = actionArea.getBattlefield();
            if(bf.isTileOccupied(rowFoe, colFoe)) {
                Unit foe  = bf.getUnit(rowFoe, colFoe);
                if (foes.contains(foe, true)) {

                    this.foes.removeValue(foe, true);
                    this.actionArea.clear(false);
                    int[] foePos;
                    Array<int[]> tiles = new Array<int[]>();
                    for(int i = 0; i < foes.size; i++) {
                        foePos = bf.getUnitPos(foes.get(i));
                        tiles.addAll(bf.getActionArea(foePos[0], foePos[1]));
                    }
                    actionArea.add(tiles, true);
                } else {

                    foes.add(foe);
                    actionArea.add(bf.getActionArea(rowFoe, colFoe), true);
                }
            }
        }

        public void clear(){
            actionArea.clear(true);
            foes.clear();
        }
    }

}
