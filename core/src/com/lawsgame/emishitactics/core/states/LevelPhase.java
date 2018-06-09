package com.lawsgame.emishitactics.core.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.managers.BattlefieldLoader;
import com.lawsgame.emishitactics.core.managers.Sprite2DPool;
import com.lawsgame.emishitactics.core.managers.interfaces.ISpritePool;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.renderers.Battlefield2DRenderer;
import com.lawsgame.emishitactics.core.renderers.interfaces.ABattlefieldRenderer;
import com.lawsgame.emishitactics.engine.GPM;
import com.lawsgame.emishitactics.engine.GamePhase;

public class LevelPhase extends GamePhase {
    static final float GAME_PORT_WIDTH = 19f;


    Battlefield battlefield;
    ABattlefieldRenderer bfRenderer;


    public void loadRequiredAssets(int bfId) {
        asm.load(Assets.ATLAS_MAPS, TextureAtlas.class);
        asm.load(Assets.ATLAS_TILES, TextureAtlas.class);
        asm.finishLoading();

    }

    public LevelPhase(GPM gsm,int battlefieldId ){
        super(gsm, GAME_PORT_WIDTH);
        loadRequiredAssets(battlefieldId);

        this.battlefield = BattlefieldLoader.load(this, battlefieldId);
        this.setLevelDimension(battlefield.getWidth(), battlefield.getHeight());

        TextureAtlas tileAtlas = asm.get(Assets.ATLAS_TILES);
        ISpritePool spritePool = new Sprite2DPool(tileAtlas);
        bfRenderer = new Battlefield2DRenderer(battlefield, spritePool);

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

        if(Gdx.input.isKeyPressed(Input.Keys.UP)) this.translateGameCam(0, 0.1f);
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) this.translateGameCam(0, -0.1f);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) this.translateGameCam(-0.1f, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) this.translateGameCam(0.1f, 0);


    }

    @Override
    public void preRender(SpriteBatch batch) {

    }

    @Override
    public void renderWorld(SpriteBatch batch) {
        bfRenderer.render(batch);
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
