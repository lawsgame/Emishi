package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.engine.rendering.Animation;

public class IsoUnitRenderer extends BattleUnitRenderer {
    private IsoBFR bfr;
    private float x;
    private float y;
    private boolean visible;
    private Animation currentAnimation;
    private Array<TextureRegion> currentSpriteSet;

    public IsoUnitRenderer(int row, int col, IUnit model, IsoBFR bfr) {
        super(model);
        this.bfr = bfr;
        setX(bfr.getCenterXFrom(row, col) - 0.5f);
        setY(bfr.getRenderYFrom(row, col) - IsoBFR.RATIO*0.5f);
        setVisible(true);
        //this.currentSpriteSet = bfr.getSpritePool()
    }

    @Override
    public void setX(float x) { this.x = x ; }

    @Override
    public void setY(float y) { this.y = y; }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setDone(boolean done) {

    }

    @Override
    public void setTargeted(boolean targeted) {

    }

    @Override
    public boolean isAnimationRolling() {
        return false;
    }

    @Override
    public void displayWalk(Array<int[]> path) {

    }

    @Override
    public void displayTakeHit(boolean moralOnly, int damageTaken, boolean critical, boolean backstab) {

    }

    @Override
    public void displayTreated(int healedHP) {

    }

    @Override
    public void displayPushed(Data.Orientation pushedTowards) {

    }

    @Override
    public void displayFlee(Data.Orientation fleeingDirection) {

    }

    @Override
    public void display(Data.AnimId id) {

    }

    @Override
    public void setOrientation(Data.Orientation or) {

    }

    @Override
    public void setWeaponType(Data.WeaponType type) {

    }

    @Override
    public void setHorseman(boolean horseman) {

    }

    @Override
    public void render(SpriteBatch batch) {

    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
