package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.patterns.statemachine.StateMachine;

public class BattleInteractionSystem extends StateMachine<BattleInteractionState> {
    Battlefield battlefield;
    BattlefieldRenderer battlefieldRenderer;
    BattleCommandManager bcm;
    CameraManager gameCM;
    AssetManager asm;

    Stage stageUI;


    public BattleInteractionSystem(Battlefield battlefield, BattlefieldRenderer battlefieldRenderer, CameraManager gameCM, AssetManager asm) {
        this.battlefield = battlefield;
        this.battlefieldRenderer = battlefieldRenderer;
        this.gameCM = gameCM;
        this.asm =asm;
        bcm = new BattleCommandManager(battlefield);
        stageUI = new Stage();
    }

    @Override
    public void push(BattleInteractionState bis){

    }


    // --------------------- GETTES & SETTERS ----------------------------



    public Battlefield getBattlefield() {
        return battlefield;
    }

    public BattlefieldRenderer getBFRenderer() {
        return battlefieldRenderer;
    }

    public CameraManager getGameCM() {
        return gameCM;
    }

    public AssetManager getASM() { return asm; }

    public BattleCommandManager getBCM() { return bcm; }

    public Stage getStageUI(){ return stageUI; }
}
