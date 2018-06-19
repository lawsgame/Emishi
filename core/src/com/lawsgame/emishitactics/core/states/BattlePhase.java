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
import com.lawsgame.emishitactics.core.states.interactions.BattleInteractionState;
import com.lawsgame.emishitactics.core.states.interactions.BattleInteractionSystem;
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
        this.bis = new BattleInteractionSystem(battlefield, battlefieldRenderer, getGameCM());

        BattleInteractionState firstBIS = new BattleInteractionState(bis) {
            @Override
            public void update(float dt) {

            }

            @Override
            public void update1(float dt) {

            }

            @Override
            public void update3(float dt) {

            }

            @Override
            public void update12(float dt) {

            }

            @Override
            public void renderBetween(SpriteBatch batch) {

            }

            @Override
            public void renderAhead(SpriteBatch batch) {

            }

            @Override
            public void initiate() {

            }

            @Override
            public void onTouch(float gameX, float gameY) {

            }

            @Override
            public void dispose() {

            }
        };

        bis.push(firstBIS);
    }

    @Override
    public void init() { }

    @Override
    public void update1(float dt) {
        bis.update1(dt);
    }

    @Override
    public void update3(float dt) {
        bis.update3(dt);
    }

    @Override
    public void update12(float dt) {
        bis.update12(dt);

        //TEST
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) this.getGameCM().translateGameCam(0, 0.1f);
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) this.getGameCM().translateGameCam(0, -0.1f);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) this.getGameCM().translateGameCam(-0.1f, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) this.getGameCM().translateGameCam(0.1f, 0);


        if(Gdx.input.isKeyJustPressed(Input.Keys.A)) bis.getBattlefieldRenderer().getUnitRenderer(bis.getBattlefield().getUnit(9,9)).triggerAnimation(Props.AnimationId.PUSH);
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z))  bis.getBattlefieldRenderer().getUnitRenderer(bis.getBattlefield().getUnit(9,9)).triggerAnimation(Props.AnimationId.ATTACK);
        if(Gdx.input.isKeyJustPressed(Input.Keys.E))  bis.getBattlefieldRenderer().getUnitRenderer(bis.getBattlefield().getUnit(9,9)).triggerAnimation(Props.AnimationId.DODGE);
        if(Gdx.input.isKeyJustPressed(Input.Keys.Q))  bis.getBattlefieldRenderer().getUnitRenderer(bis.getBattlefield().getUnit(9,9)).triggerAnimation(Props.AnimationId.STEAL);
        if(Gdx.input.isKeyJustPressed(Input.Keys.S))  bis.getBattlefieldRenderer().getUnitRenderer(bis.getBattlefield().getUnit(9,9)).triggerAnimation(Props.AnimationId.FOCUSED_BLOW);
        if(Gdx.input.isKeyJustPressed(Input.Keys.D))  bis.getBattlefieldRenderer().getUnitRenderer(bis.getBattlefield().getUnit(9,9)).triggerAnimation(Props.AnimationId.HEAVY_BLOW);
        if(Gdx.input.isKeyJustPressed(Input.Keys.W))  bis.getBattlefieldRenderer().getUnitRenderer(bis.getBattlefield().getUnit(9,9)).triggerAnimation(Props.AnimationId.LEVEL_UP);
        if(Gdx.input.isKeyJustPressed(Input.Keys.X))  bis.getBattlefieldRenderer().getUnitRenderer(bis.getBattlefield().getUnit(9,9)).triggerAnimation(Props.AnimationId.FLEE);
        if(Gdx.input.isKeyJustPressed(Input.Keys.C))  bis.getBattlefieldRenderer().getUnitRenderer(bis.getBattlefield().getUnit(9,9)).triggerAnimation(Props.AnimationId.TAKE_HIT);

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
            bis.getBattlefieldRenderer().getUnitRenderer(bis.getBattlefield().getUnit(9,8)).triggerMoveAnimation(path);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.K))  bis.getBattlefieldRenderer().triggerBuildAnimation(9, 9, Props.TileType.BRIDGE, bis.getBattlefield().getUnit(9, 8));

        if(Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            bis.getCurrentState().setInfoDisplayable(!bis.getCurrentState().isInfoDisplayable());
            System.out.println(bis.getCurrentState().isInfoDisplayable());
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.I)) bis.getCurrentState().setMapSlidable(!bis.getCurrentState().isMapSlidable());
        if(Gdx.input.isKeyJustPressed(Input.Keys.O)) bis.getCurrentState().setBattlefieldDisplayed(!bis.getCurrentState().isBattlefieldDisplayed());

    }


    @Override
    public void update60(float dt) {
        bis.update(dt);
    }

    @Override
    public void preRender(SpriteBatch batch) {

    }

    @Override
    public void renderWorld(SpriteBatch batch) {
        bis.render(batch);
    }

    @Override
    public void renderUI(SpriteBatch batch) {

    }

    @Override
    public void dispose() {

    }

    // --------------------- GETTERS & SETTERS ------------------------



}
