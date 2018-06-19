package com.lawsgame.emishitactics.core.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.managers.BattlefieldLoader;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.renderers.TempoBattlefield2DRenderer;
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
        this.bfRenderer = new TempoBattlefield2DRenderer(battlefield, asm);


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


        if(Gdx.input.isKeyJustPressed(Input.Keys.A)) bfRenderer.getUnitRenderer(battlefield.getUnit(9,9)).triggerAnimation(Props.AnimationId.PUSH);
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)) bfRenderer.getUnitRenderer(battlefield.getUnit(9,9)).triggerAnimation(Props.AnimationId.ATTACK);
        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) bfRenderer.getUnitRenderer(battlefield.getUnit(9,9)).triggerAnimation(Props.AnimationId.DODGE);
        if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) bfRenderer.getUnitRenderer(battlefield.getUnit(9,9)).triggerAnimation(Props.AnimationId.STEAL);
        if(Gdx.input.isKeyJustPressed(Input.Keys.S)) bfRenderer.getUnitRenderer(battlefield.getUnit(9,9)).triggerAnimation(Props.AnimationId.FOCUSED_BLOW);
        if(Gdx.input.isKeyJustPressed(Input.Keys.D)) bfRenderer.getUnitRenderer(battlefield.getUnit(9,9)).triggerAnimation(Props.AnimationId.HEAVY_BLOW);
        if(Gdx.input.isKeyJustPressed(Input.Keys.W)) bfRenderer.getUnitRenderer(battlefield.getUnit(9,9)).triggerAnimation(Props.AnimationId.LEVEL_UP);
        if(Gdx.input.isKeyJustPressed(Input.Keys.X)) bfRenderer.getUnitRenderer(battlefield.getUnit(9,9)).triggerAnimation(Props.AnimationId.FLEE);
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) bfRenderer.getUnitRenderer(battlefield.getUnit(9,9)).triggerAnimation(Props.AnimationId.TAKE_HIT);

        if(Gdx.input.isKeyJustPressed(Input.Keys.J)){
            System.out.println("MOVE");
            Array<int[]> path = new Array<int[]>();
            path.add(new int[]{8,8});
            path.add(new int[]{8,7});
            path.add(new int[]{8,6});
            path.add(new int[]{7,6});
            path.add(new int[]{6,6});
            path.add(new int[]{5,6});
            path.add(new int[]{4,6});
            bfRenderer.getUnitRenderer(battlefield.getUnit(9,8)).triggerMoveAnimation(path);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.K)) bfRenderer.triggerBuildAnimation(9, 9, Props.TileType.BRIDGE, battlefield.getUnit(9, 8));


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
