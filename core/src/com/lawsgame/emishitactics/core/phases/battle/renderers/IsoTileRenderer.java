package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.helpers.AssetProvider;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.rendering.Animation;
import com.lawsgame.emishitactics.engine.rendering.Renderer;

public abstract class IsoTileRenderer extends Renderer<Tile> {
    public final int row;
    public final int col;

    protected IsoTileRenderer(int row, int col, Tile model) {

        super(model);
        this.row = row;
        this.col = col;
    }

    public static IsoTileRenderer create(int row, int col, Tile model, AssetProvider provider, IsoBFR bfr){
        return new SimpleITR(row, col, model, provider, bfr);
    }

    public abstract void renderLowerPart(SpriteBatch batch);
    public abstract void renderUpperPart(SpriteBatch batch);

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
    public void update(float dt) { }

    /**
     * USELESS
     *
     * @param visible
     */
    @Override
    @Deprecated
    public void setVisible(boolean visible) { }

    @Override
    public void getNotification(Observable sender, Object data) { }


    // -------------------- DAUGTHER CLASSES ------------------

    public static class SimpleITR extends IsoTileRenderer{
        private Sprite upperPart;
        private Sprite lowerPart;

        protected SimpleITR(int row, int col, Tile model, AssetProvider provider, IsoBFR bfr) {
            super(row, col, model);
            Array<Sprite> spriteset = provider.getTileSpriteSet(model.getType().getUpperPart());
            if(spriteset.size > 0){
                this.upperPart = spriteset.get(0);
                this.upperPart.setPosition(bfr.getRenderXFrom(row, col), bfr.getRenderYFrom(row, col, true));
            }
            spriteset = provider.getTileSpriteSet(model.getType().getLowerPart());
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

        protected AnimatedITR(int row, int col, Tile model, AssetProvider provider, IsoBFR bfr) {
            super(row, col, model);
            this.upperPart = provider.getTileSpriteSet(model.getType().getUpperPart());
            this.lowerPart = provider.getTileSpriteSet(model.getType().getLowerPart());
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
