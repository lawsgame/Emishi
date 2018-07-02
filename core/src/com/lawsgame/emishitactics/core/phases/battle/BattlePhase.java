package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.helpers.BattlefieldLoader;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.renderers.TempoBattlefield2DRenderer;
import com.lawsgame.emishitactics.core.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.interactions.TestBIS;
import com.lawsgame.emishitactics.engine.GPM;
import com.lawsgame.emishitactics.engine.GamePhase;



public class BattlePhase extends GamePhase {
    private static final float GAME_PORT_WIDTH = 19f;

    private BattleInteractionSystem bis;

    public void loadRequiredAssets() {
        asm.load(Assets.ATLAS_MAPS, TextureAtlas.class);
        asm.load(Assets.ATLAS_TILES, TextureAtlas.class);
        asm.load(Assets.ATLAS_UI, TextureAtlas.class);
        asm.load(Assets.ATLAS_UNITS, TextureAtlas.class);
        asm.finishLoading();

    }

    public BattlePhase(GPM gsm, int battlefieldId ){
        super(gsm, GAME_PORT_WIDTH);
        loadRequiredAssets();

        Battlefield battlefield = BattlefieldLoader.load(this, battlefieldId);
        this.getGameCM().setCameraBoundaries(battlefield.getWidth(), battlefield.getHeight());
        BattlefieldRenderer battlefieldRenderer = new TempoBattlefield2DRenderer(battlefield, asm);
        this.bis = new BattleInteractionSystem(battlefield, battlefieldRenderer, getGameCM(), asm);

        BattleInteractionState initBIS = new TestBIS(bis);
        bis.push(initBIS);


    }

    @Override
    public void init() { }

    @Override
    public void update1(float dt) {
        bis.getCurrentState().update1(dt);
    }

    @Override
    public void update3(float dt) {
        bis.getCurrentState().update3(dt);
    }

    @Override
    public void update12(float dt) {
        bis.getCurrentState().update12(dt);
    }


    @Override
    public void update60(float dt) {
        bis.getCurrentState().update(dt);
        bis.getBFRenderer().update(dt);


    }

    @Override
    public void preRender(SpriteBatch batch) {

    }

    @Override
    public void renderWorld(SpriteBatch batch) {
        if(bis.getCurrentState().isBattlefieldDisplayed()) bis.getBFRenderer().renderTiles(batch);
        bis.getCurrentState().renderBetween(batch);
        if(bis.getCurrentState().isBattlefieldDisplayed()) bis.getBFRenderer().renderUnits(batch);
        bis.getCurrentState().renderAhead(batch);
    }

    @Override
    public void renderUI(SpriteBatch batch) {
        bis.getCurrentState().renderUI();
    }

    @Override
    public void dispose() {
        asm.dispose();
    }

    // --------------------- GETTERS & SETTERS ------------------------



}
