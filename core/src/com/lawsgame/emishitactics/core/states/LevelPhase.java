package com.lawsgame.emishitactics.core.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.managers.BattlefieldLoader;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.renderers.Battlefield2DRenderer;
import com.lawsgame.emishitactics.core.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.GPM;
import com.lawsgame.emishitactics.engine.GamePhase;


public class LevelPhase extends GamePhase {
    static final float GAME_PORT_WIDTH = 19f;


    Battlefield battlefield;
    BattlefieldRenderer bfRenderer;


    public void loadRequiredAssets() {
        asm.load(Assets.ATLAS_MAPS, TextureAtlas.class);
        asm.load(Assets.ATLAS_TILES, TextureAtlas.class);
        asm.load(Assets.ATLAS_UI, TextureAtlas.class);
        asm.load(Assets.ATLAS_UNITS, TextureAtlas.class);
        asm.finishLoading();

    }

    public LevelPhase(GPM gsm,int battlefieldId ){
        super(gsm, GAME_PORT_WIDTH);
        loadRequiredAssets();

        this.battlefield = BattlefieldLoader.load(this, battlefieldId);
        this.getGameCM().setCameraBoundaries(battlefield.getWidth(), battlefield.getHeight());
        this.bfRenderer = new Battlefield2DRenderer(battlefield, asm);

        System.out.println(battlefield.isTileOccupied(9, 9));

    }

    @Override
    public void update1(float dt) {

    }

    @Override
    public void update3(float dt) {

    }

    @Override
    public void update12(float dt) {
        bfRenderer.update(dt);
    }


    @Override
    public void update60(float dt) {
        //TEST
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) this.getGameCM().translateGameCam(0, 0.1f);
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) this.getGameCM().translateGameCam(0, -0.1f);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) this.getGameCM().translateGameCam(-0.1f, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) this.getGameCM().translateGameCam(0.1f, 0);


    }

    @Override
    public void preRender(SpriteBatch batch) {

    }

    @Override
    public void renderWorld(SpriteBatch batch) {
        bfRenderer.renderTiles(batch);
        bfRenderer.renderUnits(batch);
    }

    @Override
    public void renderUI(SpriteBatch batch) {

    }

    @Override
    public void dispose() {

    }

    // --------------------- GETTERS & SETTERS ------------------------


    public Battlefield getBattlefield() {
        return battlefield;
    }
}
