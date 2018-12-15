package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.helpers.AssetProvider;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.TileRenderer;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.rendering.Animation;
import com.lawsgame.emishitactics.engine.rendering.Renderer;

public abstract class IsoTileRenderer extends TileRenderer {



    public final int row;
    public final int col;
    public IsoBFR bfr;
    private Animation sparkleAnimation;
    private Array<Sprite> sparkleSS;
    private Sprite sparkleShadow;

    protected IsoTileRenderer(int row, int col, Tile model, IsoBFR bfr) {

        super(model);
        this.row = row;
        this.col = col;
        this.bfr = bfr;
        this.sparkleSS = new Array<Sprite>();
        this.sparkleAnimation = null;
        TextureAtlas battleIconsAtlas = bfr.assetManager.get(Assets.ATLAS_BATTLE_ICONS);
        this.sparkleShadow = new Sprite(battleIconsAtlas.findRegion(Assets.REGION_UNIT_SHADOW));
        this.sparkleShadow.setSize(getSparkleSpriteSize(), getSparkleSpriteSize() / 2f);
        this.sparkleShadow.setPosition(bfr.getCenterX(row, col) - sparkleShadow.getWidth()/2f, bfr.getCenterY(row, col) - sparkleShadow.getHeight()/2f);
    }

    public static IsoTileRenderer create(int row, int col, Tile model, IsoBFR bfr){
        return new SimpleITR(row, col, model, bfr);
    }

    public abstract void renderLowerPart(SpriteBatch batch);

    public void renderUpperPart(SpriteBatch batch){
        if(isRevealed()) {
            sparkleShadow.draw(batch);
            sparkleSS.get(sparkleAnimation.getCurrentFrame()).draw(batch);
        }
    }

    /**
     * Optimization method
     *
     * @param bfr
     * @return true, if the sprites are within the camera frame.
     */
    public abstract boolean isInFrame(IsoBFR bfr);

    @Override
    public boolean isExecuting() { return false; }

    @Override
    public void update(float dt) {
        if(sparkleAnimation != null) {
            sparkleAnimation.update(dt);
        }
    }

    /**
     * USELESS
     *
     * @param visible
     */
    @Override
    @Deprecated
    public void setVisible(boolean visible) { }

    @Override
    public boolean isRevealed(){
        return sparkleAnimation != null && sparkleAnimation.isPlaying();
    }

    @Override
    public void setRevealed(boolean revealed, Data.SparkleType type){
        if(revealed){
            if(type != null) {
                this.sparkleSS.clear();
                Sprite sprite;
                float cx = bfr.getCenterX(row, col);
                float cy = bfr.getCenterY(row, col);
                Array<? extends TextureRegion> sparkleTRs = bfr.assetProvider.sparkleTR.get(type);
                for (int i = 0; i < sparkleTRs.size; i++) {
                    sprite = new Sprite(sparkleTRs.get(i));

                    sprite.setY(cy);
                    sprite.setX(cx - getSparkleSpriteSize() /2f);
                    sprite.setSize(getSparkleSpriteSize(), getSparkleSpriteSize());


                    this.sparkleSS.add(sprite);
                }
                if (this.sparkleSS.size > 0) {
                    this.sparkleAnimation = new Animation(this.sparkleSS.size, Data.ANIMATIONL_SPEED_SPARKLE, true, true, true);
                    this.sparkleAnimation.play();
                }
            }
        }else{
            sparkleAnimation = null;
        }

        System.out.println("    animation : "+sparkleAnimation);
    }

    private float getSparkleSpriteSize(){
        return IsoBFR.SPRITE_STD_SIZE / 4f;
    }

    @Override
    public void getNotification(Observable sender, Object data) { }






    // -------------------- DAUGTHER CLASSES ------------------

    public static class SimpleITR extends IsoTileRenderer{
        private Sprite upperPart;
        private Sprite lowerPart;

        protected SimpleITR(int row, int col, Tile model, IsoBFR bfr) {
            super(row, col, model, bfr);
            Array<Sprite> spriteset = bfr.assetProvider.getTileSpriteSet(model.getType().getUpperPart());
            if(spriteset.size > 0){
                this.upperPart = spriteset.get(0);
                this.upperPart.setPosition(bfr.getRenderXFrom(row, col), bfr.getRenderYFrom(row, col, true));
            }
            spriteset = bfr.assetProvider.getTileSpriteSet(model.getType().getLowerPart());
            if(spriteset.size > 0){
                this.lowerPart = spriteset.get(0);
                this.lowerPart.setPosition(bfr.getRenderXFrom(row, col), bfr.getRenderYFrom(row, col, false));
            }

        }

        @Override
        public void renderLowerPart(SpriteBatch batch) {
            if(lowerPart != null)
                lowerPart.draw(batch);
        }

        @Override
        public void renderUpperPart(SpriteBatch batch) {
            if(upperPart != null)
                upperPart.draw(batch);
            super.renderUpperPart(batch);
        }

        @Override
        public boolean isInFrame(IsoBFR bfr) {
            return bfr.isInFrame(lowerPart) || bfr.isInFrame(upperPart);
        }

    }




    public static class AnimatedITR extends IsoTileRenderer{
        private Array<Sprite> upperPart;
        private Array<Sprite> lowerPart;
        private Animation upperAnimation;
        private Animation lowerAnimation;

        protected AnimatedITR(int row, int col, Tile model, IsoBFR bfr) {
            super(row, col, model, bfr);
            this.upperPart = bfr.assetProvider.getTileSpriteSet(model.getType().getUpperPart());
            this.lowerPart = bfr.assetProvider.getTileSpriteSet(model.getType().getLowerPart());
            this.upperAnimation = new Animation(upperPart.size, Data.ANIMATION_NORMAL_SPEED, true, true, true);
            this.lowerAnimation = new Animation(lowerPart.size, Data.ANIMATION_NORMAL_SPEED, true, true, true);
        }

        @Override
        public void renderLowerPart(SpriteBatch batch) {
            lowerPart.get(lowerAnimation.getCurrentFrame()).draw(batch);
        }

        @Override
        public void renderUpperPart(SpriteBatch batch) {
            upperPart.get(upperAnimation.getCurrentFrame()).draw(batch);
            super.renderUpperPart(batch);
        }

        @Override
        public void update(float dt) {
            super.update(dt);
            upperAnimation.update(dt);
            lowerAnimation.update(dt);
        }

        @Override
        public boolean isInFrame(IsoBFR bfr) {
            return bfr.isInFrame(lowerPart.get(lowerAnimation.getCurrentFrame()))
                    || bfr.isInFrame(upperPart.get(upperAnimation.getCurrentFrame()));
        }
    }
}
