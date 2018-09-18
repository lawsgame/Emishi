package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.helpers.SpriteProvider;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattlefieldLoader;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.interactions.tempo.TestBIS;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.GPM;
import com.lawsgame.emishitactics.engine.GamePhase;


public class BattlePhase extends GamePhase {

    public static BitmapFont testFont = new BitmapFont();
    //private static

    private BattleInteractionMachine bim;

    public void loadRequiredAssets() {
        //TEMPO
        asm.load(Assets.ATLAS_TEMPO_UI, TextureAtlas.class);
        asm.load(Assets.ATLAS_TEMPO_UNITS, TextureAtlas.class);
        asm.load(Assets.ATLAS_TEMPO_TILES, TextureAtlas.class);

        asm.load(Assets.ATLAS_MAPS, TextureAtlas.class);
        asm.load(Assets.ATLAS_TILES, TextureAtlas.class);
        asm.load(Assets.STRING_BUNDLE_MAIN, I18NBundle.class); //, new I18NBundleLoader.I18NBundleParameter(new Locale("fr", "FR")));
        asm.finishLoading();

    }

    public BattlePhase(GPM gsm, Player player, int chapterId ){
        super(gsm, Data.GAME_PORT_WIDTH);

        // load assets
        loadRequiredAssets();

        // load the battlefield
        Battlefield battlefield = BattlefieldLoader.load(this, chapterId);
        battlefield.randomlyDeploy(player.getArmy());

        // set up sprite pool and battlefield renderers
        TempoSpritePool.getInstance().set(asm);
        //BattlefieldRenderer battlefieldRenderer = new TempoBattlefield2DRenderer(battlefield, TempoSpritePool.getInstance());
        SpriteProvider spriteProvider = new SpriteProvider();
        spriteProvider.set(battlefield, asm);
        BattlefieldRenderer battlefieldRenderer = new IsoBFR(battlefield, spriteProvider);
        battlefieldRenderer.setGameCamParameters(this.getGameCM());

        //lauch initial BIS
        this.bim = new BattleInteractionMachine(battlefield, battlefieldRenderer, gameCM, asm, stageUI, player, spriteProvider);
        BattleInteractionState initBIS = new TestBIS(bim);
        //BattleInteractionState initBIS = new SceneBIS(bim);
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
