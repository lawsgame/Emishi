package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.helpers.implementations.SpriteProviderImp;
import com.lawsgame.emishitactics.core.helpers.interfaces.SpriteProvider;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;
import com.lawsgame.emishitactics.engine.rendering.Animation;

public class TestAssetBIS extends BattleInteractionState {
    private static final float ANIM_DELTA_SPEED = 0.3f;

    SpriteProvider spriteProvider;
    Array<Sprite> sprites;
    Sprite sprite;
    Animation animation;

    public TestAssetBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true, true, false);

        // ----***$$$ Animation $$$***-----

        this.spriteProvider = new SpriteProviderImp(IsoBFR.SPRITE_STD_SIZE);
        this.spriteProvider.load(bim.asm, bim.bfr.getModel());
        this.sprites = new Array<Sprite>();

        this.sprites = spriteProvider.getUnitAnimationSS(
                Data.UnitTemplate.SOLAIRE,
                Data.WeaponType.SWORD,
                false,
                false,
                true,
                Data.Orientation.WEST,
                Data.AnimId.IDLE,
                true,
                SpriteProvider.Flavor.NORMAL
        );
        for(int i =0; i < sprites.size; i++){
            sprites.get(i).setPosition(1, 1);
            sprites.get(i).setSize(1, 1);
        }
        this.animation = new Animation(sprites.size, Data.ANIMATION_NORMAL_SPEED, true, false, false);
        this.animation.play();

        /*
        Array<TextureRegion> atr = ((IsoBFR)bim.bfr).assetProvider.sparkleTR.get(Data.SparkleType.LOOT);
        Sprite sprite;
        this.sprites = new Array<Sprite>();
        for(int i =0; i < atr.size; i++){
            sprite = new Sprite(atr.get(i));
            sprite.setPosition(1, 1);
            sprite.setSize(1, 1);
            sprites.add(sprite);
        }
        */

        /*
        this.sprites = spriteProvider.getSparkleSS(Data.SparkleType.LOOT);
        for(int i = 0; i < sprites.size; i++){
            sprites.get(i).setPosition(1, 1);
        }
        this.animation = new Animation(sprites.size, Data.ANIMATION_NORMAL_SPEED, true, false, false);
        this.animation.play();
        */

        //this.sprite = spriteProvider.getPortrait(bim.bfr.getUnitRenderer(bim.player.getArmy().getWarlord()));
        //this.sprite = spriteProvider.getPortrait(false, Data.UnitTemplate.SOLAR_KNIGHT);
        //this.sprite.setSize(2,2);
        //this.sprite.setPosition(2,2);

        /*
        String path = Assets.UNIT_SPRITES_DIR+"/solaire/sword/promoted.pack";
        bim.asm.load(path, TextureAtlas.class);
        bim.asm.finishLoading();
        String id;
        Sprite sprite;
        this.sprites = new Array<Sprite>();
        if(bim.asm.isLoaded(path)){
            TextureAtlas atlas = bim.asm.get(path);
            id = Assets.getRegionUnit(Data.AnimUnitSSId.REST, true, true, SpriteProvider.Flavor.WHITEBG);
            Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(id);
            for(int i = 0; i < regions.size; i++){
                sprite = new Sprite(regions.get(i));
                sprite.setSize(2,2);
                sprite.setPosition(2,2);
                sprites.add(sprite);
            }
        }

        */






    }

    @Override
    public void renderAhead(SpriteBatch batch) {
        if(sprites != null
                && sprites.size > 0
                && animation != null)     sprites.get(animation.getCurrentFrame()).draw(batch);
        if(sprite != null)                          sprite.draw(batch);

    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }

    @Override
    public void update60(float dt) {
        if(animation != null)    animation.update(dt);
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            animation.setSpeed(animation.getSpeed() * (1f - ANIM_DELTA_SPEED));
            System.out.println("animation speed : "+animation.getSpeed()+"s");
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            animation.setSpeed(animation.getSpeed() * (1f + ANIM_DELTA_SPEED));
            System.out.println("animation speed : "+animation.getSpeed()+"s");
        }
    }

}
