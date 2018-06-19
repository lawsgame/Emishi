package com.lawsgame.emishitactics.core.states.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.GameRenderableEntity;
import com.lawsgame.emishitactics.engine.patterns.statemachine.StateMachine;

public class BattleInteractionSystem extends StateMachine<BattleInteractionState> implements GameRenderableEntity{
    Battlefield battlefield;
    BattlefieldRenderer battlefieldRenderer;
    CameraManager gameCM;

    public BattleInteractionSystem(Battlefield battlefield, BattlefieldRenderer battlefieldRenderer, CameraManager gameCM) {
        this.battlefield = battlefield;
        this.battlefieldRenderer = battlefieldRenderer;
        this.gameCM = gameCM;

    }

    public void update1(float dt) {
        getCurrentState().update1(dt);
    }

    public void update3(float dt) {
        getCurrentState().update3(dt);
    }

    public void update12(float dt) {
        getCurrentState().update12(dt);
    }

    public void update(float dt){
        getCurrentState().update(dt);
        battlefieldRenderer.update(dt);
    }


    @Override
    public void render(SpriteBatch batch) {
        if(getCurrentState().battlefieldDisplayed) battlefieldRenderer.renderTiles(batch);
        getCurrentState().renderBetween(batch);
        if(getCurrentState().battlefieldDisplayed) battlefieldRenderer.renderUnits(batch);
        getCurrentState().renderAhead(batch);
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
