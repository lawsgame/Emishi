package com.lawsgame.emishitactics.core.states.interactions;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.states.commands.BattleCommandManager;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.GameRenderableEntity;
import com.lawsgame.emishitactics.engine.patterns.statemachine.StateMachine;

public class BattleInteractionSystem extends StateMachine<BattleInteractionState> {
    Battlefield battlefield;
    BattlefieldRenderer battlefieldRenderer;
    BattleCommandManager bcm;
    CameraManager gameCM;
    AssetManager asm;


    public BattleInteractionSystem(Battlefield battlefield, BattlefieldRenderer battlefieldRenderer, CameraManager gameCM, AssetManager asm) {
        this.battlefield = battlefield;
        this.battlefieldRenderer = battlefieldRenderer;
        this.gameCM = gameCM;
        this.asm =asm;
        bcm = new BattleCommandManager(battlefield);

    }


    // --------------------- GETTES & SETTERS ----------------------------



    public Battlefield getBattlefield() {
        return battlefield;
    }

    public BattlefieldRenderer getBattlefieldRenderer() {
        return battlefieldRenderer;
    }

    public CameraManager getGameCM() {
        return gameCM;
    }
}
