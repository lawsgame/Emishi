package com.lawsgame.emishitactics.core.phases;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.helpers.BattlefieldLoader;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Unit.Army;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionSystem;
import com.lawsgame.emishitactics.core.phases.battle.renderers.TempoBattlefield2DRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.interactions.TestBIS;
import com.lawsgame.emishitactics.engine.GPM;
import com.lawsgame.emishitactics.engine.GamePhase;



public class BattlePhase extends GamePhase {

    public static BitmapFont testFont = new BitmapFont();

    private BattleInteractionSystem bis;

    public void loadRequiredAssets() {
        asm.load(Assets.ATLAS_MAPS, TextureAtlas.class);
        asm.load(Assets.ATLAS_TILES, TextureAtlas.class);
        asm.load(Assets.ATLAS_UI, TextureAtlas.class);
        asm.load(Assets.ATLAS_UNITS, TextureAtlas.class);
        asm.finishLoading();

    }

    public BattlePhase(GPM gsm, int battlefieldId ){
        super(gsm, Data.GAME_PORT_WIDTH);
        loadRequiredAssets();

        // load and setup the battlefield and its renderer
        Battlefield battlefield = BattlefieldLoader.load(this, battlefieldId);
        this.getGameCM().setCameraBoundaries(battlefield.getWidth(), battlefield.getHeight());
        BattlefieldRenderer battlefieldRenderer = new TempoBattlefield2DRenderer(battlefield, asm);

        // set the player army

        Unit warlord = new Unit(true,
                Data.UnitTemplate.EMISHI_TRIBESMAN,19,
                Data.Ethnicity.JAPANESE,
                Data.Orientation.SOUTH,
                Data.Behaviour.CONTROLLED_BY_PLAYER, Data.Weapon.YUMI, Data.Weapon.WARABITE, true);
        warlord.setPasAb1(Data.PassiveAbility.PRAYER);
        warlord.setPasAb2(Data.PassiveAbility.NONE);
        warlord.setOffensiveAbility(Data.OffensiveAbility.CRUNCHING_BLOW);
        warlord.equip(Data.Item.NONE, true);
        warlord.equip(Data.Item.NONE, false);
        warlord.setItemStealable(false);
        warlord.setName("Aterui");
        warlord.setLeadership(15);

        Army playerArmy = new Army(Data.ArmyType.PLAYER);
        playerArmy.appointWarLord(warlord);

        // set the initial BattleInteractionState
        this.bis = new BattleInteractionSystem(battlefield, battlefieldRenderer, gameCM, asm, stageUI, playerArmy);
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
    public void update20(float dt) {
        bis.getCurrentState().update12(dt);
    }


    @Override
    public void update60(float dt) {
        bis.getCurrentState().update(dt);
        bis.bfr.update(dt);
    }

    @Override
    public void preRender(SpriteBatch batch) {

    }

    @Override
    public void renderWorld(SpriteBatch batch) {
        if(bis.getCurrentState().isBattlefieldDisplayed()) bis.bfr.renderTiles(batch);
        bis.getCurrentState().renderBetween(batch);
        if(bis.getCurrentState().isBattlefieldDisplayed()) bis.bfr.renderUnits(batch);
        bis.getCurrentState().renderAhead(batch);
    }


    @Override
    public void dispose() {
        asm.dispose();
    }

    // --------------------- GETTERS & SETTERS ------------------------



}
