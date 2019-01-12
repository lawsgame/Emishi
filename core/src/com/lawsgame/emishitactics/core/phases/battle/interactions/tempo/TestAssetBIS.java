package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;
import com.lawsgame.emishitactics.engine.rendering.Animation;

public class TestAssetBIS extends BattleInteractionState {
    private static final float ANIM_DELTA_SPEED = 0.3f;

    Array<Sprite> sprites;
    Animation animation;

    public TestAssetBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true, true, false);

        // ----***$$$ Animation $$$***-----

        Array<TextureRegion> atr = ((IsoBFR)bim.bfr).assetProvider.sparkleTR.get(Data.SparkleType.LOOT);
        Sprite sprite;
        this.sprites = new Array<Sprite>();
        for(int i =0; i < atr.size; i++){
            sprite = new Sprite(atr.get(i));
            sprite.setPosition(1, 1);
            sprite.setSize(1, 1);
            sprites.add(sprite);
        }
        this.animation = new Animation(sprites.size, Data.ANIMATION_NORMAL_SPEED, true, false, false);
        this.animation.play();
    }

    @Override
    public void renderAhead(SpriteBatch batch) {
        sprites.get(animation.getCurrentFrame()).draw(batch);
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }

    @Override
    public void update60(float dt) {
        animation.update(dt);
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
