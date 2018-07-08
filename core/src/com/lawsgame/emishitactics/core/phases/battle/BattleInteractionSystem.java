package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lawsgame.emishitactics.core.models.Unit.Army;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.LongTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.LongUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.ShortTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.ShortUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ATilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.AUnitPanel;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.patterns.statemachine.StateMachine;

public class BattleInteractionSystem extends StateMachine<BattleInteractionState> {
    public Army playerArmy;
    public Battlefield battlefield;

    public BattlefieldRenderer bfr;
    public BattleCommandManager bcm;
    public CameraManager gcm;
    public AssetManager asm;
    public InputMultiplexer multiplexer;

    public Stage UIStage;
    public ATilePanel shortTilePanel;
    public ATilePanel longTilePanel;
    public AUnitPanel shortUnitPanel;
    public AUnitPanel longUnitPanel;


    public BattleInteractionSystem(Battlefield battlefield, BattlefieldRenderer bfr, CameraManager gcm, AssetManager asm, Stage stageUI, Army playerArmy) {
        this.battlefield = battlefield;
        this.bfr = bfr;
        this.gcm = gcm;
        this.asm = asm;
        this.bcm = new BattleCommandManager(battlefield);
        this.playerArmy = playerArmy;
        this.multiplexer = new InputMultiplexer();

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
    }




    // --------------------- GETTES & SETTERS ----------------------------

}
