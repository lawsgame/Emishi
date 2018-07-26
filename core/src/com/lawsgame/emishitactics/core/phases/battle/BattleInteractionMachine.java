package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.LongTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.LongUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.ShortTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.ShortUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.SimpleAreaWidget;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.AreaWidget;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.TilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.UnitPanel;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.patterns.statemachine.StateMachine;

public class BattleInteractionMachine extends StateMachine<BattleInteractionState> {
    public IArmy playerArmy;
    public Battlefield battlefield;

    public BattlefieldRenderer bfr;
    public BattleCommandManager bcm;
    public CameraManager gcm;
    public AssetManager asm;
    public InputMultiplexer multiplexer;
    public AnimationScheduler scheduler;


    public AreaWidget sltdTile;
    public Stage UIStage;
    public TilePanel shortTilePanel;
    public TilePanel longTilePanel;
    public UnitPanel shortUnitPanel;
    public UnitPanel longUnitPanel;


    public BattleInteractionMachine(Battlefield battlefield, BattlefieldRenderer bfr, CameraManager gcm, AssetManager asm, Stage stageUI, IArmy playerArmy) {
        this.battlefield = battlefield;
        this.bfr = bfr;
        this.gcm = gcm;
        this.asm = asm;
        this.scheduler = new AnimationScheduler();
        this.bcm = new BattleCommandManager(bfr, scheduler);
        this.playerArmy = playerArmy;
        this.multiplexer = new InputMultiplexer();



        this.sltdTile = new SimpleAreaWidget(battlefield, Data.AreaType.SELECTED_UNIT);
        this.sltdTile.setVisible(false);

        // UI
        this.UIStage = stageUI;

        this.shortTilePanel = new ShortTilePanel(stageUI.getViewport());
        this.shortUnitPanel = new ShortUnitPanel(stageUI.getViewport());
        this.longUnitPanel = new LongUnitPanel(stageUI.getViewport());
        this.longTilePanel = new LongTilePanel(stageUI.getViewport());

        stageUI.addActor(shortTilePanel);
        stageUI.addActor(shortUnitPanel);
        stageUI.addActor(longUnitPanel);
        stageUI.addActor(longTilePanel);


    }

    @Override
    public void push(BattleInteractionState bis){
        multiplexer.clear();
        multiplexer.addProcessor(new GestureDetector(bis));
        multiplexer.addProcessor(UIStage);
        Gdx.input.setInputProcessor(multiplexer);
        super.push(bis);

        //TODO: remove test
        System.out.println(this);
    }

    public void rollback(){
        if(states.size() > 1){
            pop();
            multiplexer.clear();
            multiplexer.addProcessor(new GestureDetector(getCurrentState()));
            multiplexer.addProcessor(UIStage);
            Gdx.input.setInputProcessor(multiplexer);
            getCurrentState().init();

            //TODO: remove test
            System.out.println(this);
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
