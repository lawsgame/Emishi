package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.helpers.implementations.SpriteProviderImp;
import com.lawsgame.emishitactics.core.helpers.interfaces.SpriteProvider;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattleCommandManager;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattlefieldLoader;
import com.lawsgame.emishitactics.core.phases.battle.helpers.loaders.BattlefieldLoaderImp;
import com.lawsgame.emishitactics.core.phases.battle.helpers.PanelPool;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.WidgetFactory;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.patterns.statemachine.StateMachine;
import com.lawsgame.emishitactics.engine.utils.Lawgger;

public class BattleInteractionMachine extends StateMachine<BattleInteractionState> implements Disposable{
    private static Lawgger log = Lawgger.createInstance(BattleInteractionMachine.class);

    public final Player player;
    public final BattlefieldRenderer bfr;
    public final BattleCommandManager bcm;
    public final AssetManager asm;
    public final SpriteProvider spriteProvider;
    public final TileHighlighter thl;
    public final WidgetFactory wf;
    public final PanelPool pp;
    public final InputMultiplexer multiplexer;
    public final AnimationScheduler scheduler;
    public final I18NBundle localization;
    public final Stage uiStage;
    public final Skin uiSkin;


    public BattleInteractionMachine(CameraManager gcm, AssetManager asm, Stage stageUI, Player player, int chapterId) {
        this.asm = asm;
        this.player = player;
        this.uiStage = stageUI;
        this.multiplexer = new InputMultiplexer();
        this.scheduler = new AnimationScheduler();
        this.localization = asm.get(Assets.STRING_BUNDLE_MAIN);
        this.uiSkin = asm.get(Assets.SKIN_UI, Skin.class);
        BattlefieldLoader battleLoader = new BattlefieldLoaderImp();
        Battlefield battlefield = battleLoader.load(asm, chapterId);
        battlefield.randomlyDeploy(player.getArmy(), true);
        battlefield.getBattleTurnManager().init(player);
        this.spriteProvider = new SpriteProviderImp(IsoBFR.SPRITE_STD_SIZE);
        this.bfr = new IsoBFR(battlefield, gcm, asm, spriteProvider);
        this.wf = new WidgetFactory(stageUI, uiSkin, bfr);
        this.pp = new PanelPool(wf, localization);
        battleLoader.addEvents(asm, bfr, scheduler, player.getInventory(), pp.shortUnitPanel);
        this.spriteProvider.load(asm, battlefield);
        this.bfr.init();
        this.thl = new TileHighlighter(bfr);
        this.bcm = new BattleCommandManager(bfr, scheduler, player.getInventory(), thl);


        // TEST

        /*
        ReinforcementEvent event = ReinforcementEvent.addTrigger(1, bfr, scheduler, player.getInventory(), player.getArmy());
        Unit soldier = new Unit("toro", Data.UnitTemplate.SOLAR_KNIGHT, Data.WeaponType.SWORD);
        MilitaryForce enemyForce = bfr.getModel().getBattleTurnManager().getArmyByName("enemy army 1");
        if(enemyForce != null){
            enemyForce.add(soldier);
            enemyForce.appointSoldier(soldier, 0);
        }
        event.addStiffener(soldier, 10,0, 10,2);
        */
        log.info(battlefield.triggerToString());


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

            bfr.moveCameraTo(rowTarget, colTarget, moveCamSmoothly);
            if(displayPanels)
                pp.updateShortPanels(bfr, rowTarget, colTarget, erasePanelMemory);
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

        bfr.moveCameraTo(rowTarget, colTarget, moveCamSmoothly);
        if(displayPanels)
            pp.updateShortPanels(bfr, rowTarget, colTarget, erasePanelMemory);
        thl.highlight(rowTarget, colTarget, highligthSquad, mode);

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
        bfr.dispose();
    }

}
