package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.patterns.statemachine.StateMachine;

public class BattleInteractionSystem extends StateMachine<BattleInteractionState> {
    Battlefield battlefield;
    BattlefieldRenderer BFdRenderer;
    BattleCommandManager bcm;
    CameraManager gameCM;
    AssetManager asm;

    InputMultiplexer multiplexer;
    Stage UIStage;


    public BattleInteractionSystem(Battlefield battlefield, BattlefieldRenderer BFdRenderer, CameraManager gameCM, AssetManager asm) {
        this.battlefield = battlefield;
        this.BFdRenderer = BFdRenderer;
        this.gameCM = gameCM;
        this.asm =asm;
        this.bcm = new BattleCommandManager(battlefield);

        this.multiplexer = new InputMultiplexer();
        this.UIStage = new Stage();
    }

    @Override
    public void push(BattleInteractionState bis){
        multiplexer.clear();
        multiplexer.addProcessor(new GestureDetector(bis));
        multiplexer.addProcessor(UIStage);
        super.push(bis);
    }


    // --------------------- GETTES & SETTERS ----------------------------



    public Battlefield getBattlefield() {
        return battlefield;
    }

    public BattlefieldRenderer getBFRenderer() {
        return BFdRenderer;
    }

    public CameraManager getGCM() {
        return gameCM;
    }

    public AssetManager getASM() { return asm; }

    public BattleCommandManager getBCM() { return bcm; }

    public Stage getUIStage(){ return UIStage; }
}
