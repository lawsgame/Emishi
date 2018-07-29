package com.lawsgame.emishitactics.core.phases;

import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.helpers.BattlefieldLoader;
import com.lawsgame.emishitactics.core.models.Army;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.TestBIS;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.TempoBattlefield2DRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.GPM;
import com.lawsgame.emishitactics.engine.GamePhase;

import java.util.Locale;


public class BattlePhase extends GamePhase {

    public static BitmapFont testFont = new BitmapFont();

    private BattleInteractionMachine bim;

    public void loadRequiredAssets() {
        asm.load(Assets.ATLAS_MAPS, TextureAtlas.class);
        asm.load(Assets.ATLAS_TILES, TextureAtlas.class);
        asm.load(Assets.ATLAS_UI, TextureAtlas.class);
        asm.load(Assets.ATLAS_UNITS, TextureAtlas.class);
        asm.load(Assets.STRING_BUNDLE_MAIN, I18NBundle.class, new I18NBundleLoader.I18NBundleParameter(new Locale("fr", "FR")));
        asm.finishLoading();

    }

    public BattlePhase(GPM gsm, int battlefieldId ){
        super(gsm, Data.GAME_PORT_WIDTH);
        loadRequiredAssets();

        // load and setup the battlefield and its receiver
        Battlefield battlefield = BattlefieldLoader.load(this, battlefieldId);
        this.getGameCM().setCameraBoundaries(battlefield.getWidth(), battlefield.getHeight());
        BattlefieldRenderer battlefieldRenderer = new TempoBattlefield2DRenderer(battlefield, asm);

        // set the player army
        Unit warlord = new Unit("Aterui", Data.Job.SOLAR_KNIGHT, 5, Data.WeaponType.SWORD, false, false, false, false);
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));

        IArmy playerArmy = new Army(Data.Allegeance.ALLY, true);
        playerArmy.setLeadershipConditionEnabled(true);
        playerArmy.add(warlord);
        playerArmy.appointWarLord(warlord);

        // set the initial BattleInteractionState
        this.bim = new BattleInteractionMachine(battlefield, battlefieldRenderer, gameCM, asm, stageUI, playerArmy);
        BattleInteractionState initBIS = new TestBIS(bim);
        //BattleInteractionState initBIS = new SceneBIS(bim);
        bim.push(initBIS);


    }

    @Override
    public void init() { }

    @Override
    public void update1(float dt) {
        bim.getCurrentState().update1(dt);
    }

    @Override
    public void update3(float dt) {
        bim.getCurrentState().update3(dt);
    }

    @Override
    public void update20(float dt) {
        bim.getCurrentState().update12(dt);
    }


    @Override
    public void update60(float dt) {
        bim.getCurrentState().update(dt);
        bim.bfr.update(dt);
        bim.scheduler.update(dt);
    }

    @Override
    public void preRender(SpriteBatch batch) {

    }

    @Override
    public void renderWorld(SpriteBatch batch) {
        if(bim.getCurrentState().isBattlefieldDisplayed()) bim.bfr.renderTiles(batch);
        bim.getCurrentState().renderBetween(batch);
        bim.sltdTile.render(batch);
        if(bim.getCurrentState().isBattlefieldDisplayed()) bim.bfr.renderUnits(batch);
        bim.getCurrentState().renderAhead(batch);
    }


    @Override
    public void dispose() {
        asm.dispose();
    }

    // --------------------- GETTERS & SETTERS ------------------------



}
