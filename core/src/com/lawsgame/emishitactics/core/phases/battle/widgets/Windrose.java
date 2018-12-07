package com.lawsgame.emishitactics.core.phases.battle.widgets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.GameElement;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.rendering.Animation;

import java.util.HashMap;

/**
 *
 * need initialize(), render() and update() to be used
 *
 */
public class Windrose extends Observable implements GameElement {
    private static final float ANIM_SPEED = 0.22f;
    private static final float ARROW_SIZE_FACTOR = 0.8f;
    private static final HashMap<Orientation, float[]> spriteRelPos = new HashMap<Orientation, float[]>();

    private Vector2 targetPos;
    private boolean enable;
    private boolean visible;
    private Orientation previousSelectedOrientation;
    private BattlefieldRenderer bfr;
    private Animation arrowActiveAnimation;
    private Animation arrowInactiveAnimation;
    private HashMap<Orientation, Array<Sprite>> arrowActiveSprites;
    private HashMap<Orientation, Array<Sprite>> arrowInactiveSprites;

    public Windrose(BattlefieldRenderer bfr, AssetManager asm){
        if(spriteRelPos.size() == 0){
            float sss= IsoBFR.SPRITE_STD_SIZE;
            float asf = ARROW_SIZE_FACTOR;
            spriteRelPos.put(Orientation.NORTH, new float[] {-0.5f * sss * asf  ,0.5f * sss});
            spriteRelPos.put(Orientation.WEST, new float[]  {-0.5f * sss * asf  ,0.5f * (1f - 0.5f * asf) * sss});
            spriteRelPos.put(Orientation.SOUTH, new float[] {0f                 ,0.5f * (1f - 0.5f * asf) * sss});
            spriteRelPos.put(Orientation.EAST, new float[]  {0f                 ,0.5f * sss});
        }
        this.bfr = bfr;
        this.enable = false;
        this.visible = false;
        this.targetPos = new Vector2();
        this.previousSelectedOrientation = null;
        // set arrow sprites
        TextureAtlas atlas = asm.get(Assets.ATLAS_BATTLE_ICONS);
        this.arrowActiveSprites = new HashMap<Orientation, Array<Sprite>>();
        this.arrowInactiveSprites = new HashMap<Orientation, Array<Sprite>>();
        for(Orientation or: Orientation.values()) {
            setArrowSprite(atlas, true, or);
            setArrowSprite(atlas, false, or);
        }
        // set arrow animations
        arrowActiveAnimation = new Animation(arrowActiveSprites.get(Orientation.NORTH).size, ANIM_SPEED, true, true, false);
        arrowInactiveAnimation = new Animation(arrowInactiveSprites.get(Orientation.NORTH).size, ANIM_SPEED, true,  true, false);
    }

    private void setArrowSprite(TextureAtlas atlas, boolean active, Orientation or){
        // create the array of arrow sprite for a given tuple {active, or}
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(Assets.getWindroseArrowTexture(active, or == Orientation.NORTH || or == Orientation.EAST));

        System.out.println("    > Windrose.setArrowSprite() "+active+" "+or.name().toLowerCase()+" "+regions.size);

        Array<Sprite> arrowSprites = new Array<Sprite>();
        Sprite arrowSprite;
        for(int i = 0; i < regions.size; i++){
            arrowSprite = new Sprite(regions.get(i));
            arrowSprite.setSize(IsoBFR.SPRITE_STD_SIZE * 0.5f * ARROW_SIZE_FACTOR, IsoBFR.SPRITE_STD_SIZE * 0.25f * ARROW_SIZE_FACTOR);
            if(or == Orientation.EAST || or == Orientation.SOUTH){
                arrowSprite.setFlip(true, false);
            }
            arrowSprites.add(arrowSprite);
        }
        // add the array of sprites with the relevant orientation as key
        if(active) {
            arrowActiveSprites.put(or, arrowSprites);
        }else{
            arrowInactiveSprites.put(or, arrowSprites);
        }
    }


    public void initialize(BattleUnitRenderer iur){
        targetPos.x = iur.getCenterX();
        targetPos.y = iur.getCenterY();
        enable = true;
        visible = true;
        arrowInactiveAnimation.stop();
        arrowActiveAnimation.stop();
        previousSelectedOrientation = iur.getOrientation();
        Orientation or;
        float xSprite;
        float ySprite;
        for(int i = 0; i < Orientation.values().length; i++) {
            or = Orientation.values()[i];
            for(int j = 0; j < arrowActiveSprites.get(or).size; j++) {
                xSprite = iur.getCenterX() + spriteRelPos.get(or)[0];
                ySprite = iur.getCenterY() + spriteRelPos.get(or)[1];
                arrowActiveSprites.get(or).get(j).setPosition(xSprite, ySprite);
            }
            for(int j = 0; j < arrowInactiveSprites.get(or).size; j++) {
                xSprite = iur.getCenterX() + spriteRelPos.get(or)[0];
                ySprite = iur.getCenterY() + spriteRelPos.get(or)[1];
                arrowInactiveSprites.get(or).get(j).setPosition(xSprite, ySprite);
            }
        }
        arrowActiveAnimation.play();
        arrowInactiveAnimation.play();
    }

    public boolean handleInput(float xInput, float yInput){
        if(enable){
            Orientation orGotten = bfr.getOrientationFromPos(targetPos.x, targetPos.y + IsoBFR.SPRITE_STD_SIZE * 0.5f, xInput, yInput);
            System.out.println(orGotten);
            setArrowOrientation(orGotten);
            return true;
        }
        return false;
    }

    public void setArrowOrientation(Orientation orGotten){
        if(orGotten == previousSelectedOrientation){
            notifyAllObservers(orGotten);
        }else{
            previousSelectedOrientation = orGotten;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if(visible){
            for(int i = 0; i < Orientation.values().length; i++){
                if(Orientation.values()[i] == previousSelectedOrientation){
                    if(arrowActiveSprites.get(Orientation.values()[i]).size > 0) {
                        arrowActiveSprites.get(Orientation.values()[i]).get(arrowActiveAnimation.getCurrentFrame()).draw(batch);
                    }
                }else{
                    if(arrowActiveSprites.get(Orientation.values()[i]).size > 0) {
                        arrowInactiveSprites.get(Orientation.values()[i]).get(arrowInactiveAnimation.getCurrentFrame()).draw(batch);
                    }
                }
            }
        }
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void update(float dt) {
        if(visible) {
            arrowActiveAnimation.update(dt);
            arrowInactiveAnimation.update(dt);
        }
    }
}
