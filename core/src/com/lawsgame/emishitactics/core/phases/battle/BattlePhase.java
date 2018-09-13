package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.helpers.SpritePool;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Army;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattlefieldLoader;
import com.lawsgame.emishitactics.core.phases.battle.interactions.SceneBIS;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.interactions.tempo.TestBIS;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.tempo.TempoBattlefield2DRenderer;
import com.lawsgame.emishitactics.engine.GPM;
import com.lawsgame.emishitactics.engine.GamePhase;


public class BattlePhase extends GamePhase {

    public static BitmapFont testFont = new BitmapFont();
    //private static

    private BattleInteractionMachine bim;

    public void loadRequiredAssets() {
        asm.load(Assets.ATLAS_MAPS, TextureAtlas.class);
        asm.load(Assets.ATLAS_TEMPO_TILES, TextureAtlas.class);
        asm.load(Assets.ATLAS_TILES, TextureAtlas.class);
        asm.load(Assets.ATLAS_TEMPO_UI, TextureAtlas.class);
        asm.load(Assets.ATLAS_TEMPO_UNITS, TextureAtlas.class);
        asm.load(Assets.STRING_BUNDLE_MAIN, I18NBundle.class); //, new I18NBundleLoader.I18NBundleParameter(new Locale("fr", "FR")));
        //asm.load(Assets.SKIN_UI, Skin.class);
        asm.finishLoading();



    }

    public BattlePhase(GPM gsm, Player player, int chapterId ){
        super(gsm, Data.GAME_PORT_WIDTH);

        // load assets
        loadRequiredAssets();

        // load and setup the battlefield and its receiver
        Battlefield battlefield = BattlefieldLoader.load(this, chapterId);


        TempoSpritePool.getInstance().set(asm);
        BattlefieldRenderer battlefieldRenderer = new TempoBattlefield2DRenderer(battlefield, TempoSpritePool.getInstance());
        SpritePool spritePool = new SpritePool();
        spritePool.set(battlefield, asm);
        //BattlefieldRenderer battlefieldRenderer = new IsoBFR(battlefield, spritePool);
        battlefieldRenderer.setGameCamParameters(this.getGameCM());

        // TEST player army
        Unit warlord = new Unit("Aterui", Data.Job.SERGEANT, 18, Data.WeaponType.SWORD, false, false, false, false);
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        Unit soldier1 = new Unit("Taro", Data.Job.SERGEANT, 5, Data.WeaponType.SWORD, false, false, false, false);
        soldier1.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        Unit soldier2 = new Unit("Maro", Data.Job.SERGEANT, 5, Data.WeaponType.SWORD, false, false, false, false);
        soldier2.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        Unit warchief1 = new Unit("Azamaru", Data.Job.SERGEANT, 5, Data.WeaponType.SWORD, false, false, false, false);
        warchief1.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));

        IArmy playerArmy = Army.getPlayerArmy();
        playerArmy.setLeadershipConditionEnabled(false);
        playerArmy.add(warlord);
        playerArmy.add(warchief1);
        playerArmy.add(soldier1);
        playerArmy.add(soldier2);
        playerArmy.appointWarLord(warlord);
        playerArmy.appointWarChief(warchief1);
        playerArmy.appointSoldier(soldier1, 0);
        playerArmy.appointSoldier(soldier2, 1);


        this.bim = new BattleInteractionMachine(battlefield, battlefieldRenderer, gameCM, asm, stageUI, player);
        //BattleInteractionState initBIS = new TestBIS(bim);
        BattleInteractionState initBIS = new SceneBIS(bim);
        bim.push(initBIS);


    }

    @Override
    public void init() { }

    @Override
    public void end() { }

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
        bim.bfr.prerender();
    }

    @Override
    public void renderWorld(SpriteBatch batch) {
        if(bim.getCurrentState().isBattlefieldDisplayed()) {
            bim.bfr.render(batch);
            bim.getCurrentState().renderAhead(batch);
        }
    }




    // --------------------- GETTERS & SETTERS ------------------------



}
