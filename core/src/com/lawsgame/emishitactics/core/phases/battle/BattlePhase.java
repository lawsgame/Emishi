package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.lawsgame.emishitactics.TacticsGame;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.phases.battle.interactions.SceneBIS;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.interactions.tempo.TestBIS;
import com.lawsgame.emishitactics.core.phases.battle.interactions.tempo.TestCommandBIS;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;
import com.lawsgame.emishitactics.engine.GPM;
import com.lawsgame.emishitactics.engine.GamePhase;
import com.lawsgame.emishitactics.engine.utils.Lawgger;


public class BattlePhase extends GamePhase {
    private static Lawgger log = Lawgger.createInstance(BattlePhase.class);
    public static BitmapFont testFont;

    private BattleInteractionMachine bim;

    public void loadRequiredAssets() {
        asm.load(Assets.ATLAS_MAPS, TextureAtlas.class);
        asm.load(Assets.ATLAS_BATTLE_ICONS, TextureAtlas.class);
        asm.load(Assets.SKIN_UI, Skin.class);
        asm.load(Assets.STRING_BUNDLE_MAIN, I18NBundle.class); //, new I18NBundleLoader.I18NBundleParameter(new Locale("fr", "FR")));
        asm.finishLoading();

    }

    public void setFontParams(boolean clean){
        //TEST
        Skin skin = asm.get(Assets.SKIN_UI);
        testFont = skin.getFont("default-font");

        if(clean) {

            float requiredSize = 18f;
            // float scale = 1f;
            float lineSpacingFactor = 0.95f;
            float charSpacingFactor = 1.05f;
            // prevent LIBGDX of replacing ghyph to fit the pîxel canvas, leading to a slight misalignment of the rendered glyphs
            testFont.setUseIntegerPositions(false);
            // run linear filtering on the underlying texture to smoothe the glyph on screen
            testFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            //add character spacing to avoid overlapping causing by the addition of outline of glyph
            for (int i = 0; i < testFont.getData().glyphs.length; i++) {
                if (testFont.getData().glyphs[i] != null) {
                    for (int j = 0; j < testFont.getData().glyphs[i].length; j++) {
                        if (testFont.getData().glyphs[i][j] != null) {
                            testFont.getData().glyphs[i][j].xadvance *= charSpacingFactor;
                        }
                    }
                }
            }
            testFont.getData().setLineHeight(testFont.getData().lineHeight * lineSpacingFactor);
            testFont.getData().setScale(requiredSize / testFont.getData().lineHeight);
        }

    }

    public BattlePhase(GPM gpm, Player player, int chapterId ){
        super(gpm, getPixelPerfectGamePortWidth());
        log.info("Game Port Width : " +getPixelPerfectGamePortWidth());

        loadRequiredAssets();
        setFontParams(true);

        this.bim = new BattleInteractionMachine(gameCM, asm, stageUI, player, chapterId);
        //BattleInteractionState initBIS = new TestAssetBIS(bim);
        //BattleInteractionState initBIS = new TestCommandBIS(bim);
        //BattleInteractionState initBIS = new TestBIS(bim);
        BattleInteractionState initBIS = new SceneBIS(bim);
        bim.push(initBIS);

    }

    private static float getPixelPerfectGamePortWidth(){
        return Gdx.app.getGraphics().getWidth() * IsoBFR.SPRITE_STD_SIZE/ 128f;
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

    @Override
    public void dispose() {
        super.dispose();
        bim.dispose();

    }


// --------------------- GETTERS & SETTERS ------------------------



}
