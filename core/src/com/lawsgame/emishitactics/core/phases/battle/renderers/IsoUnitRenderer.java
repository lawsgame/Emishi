package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.AnimId;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.engine.rendering.Animation;

public class IsoUnitRenderer extends BattleUnitRenderer  {

    protected IsoBFR bfr;
    private float centerX; // center X
    private float centerY; // center Y
    protected AnimId state;
    protected Animation animation;
    protected Array<Sprite> spriteSet;

    // model delayed attributes
    protected WeaponType weaponType;
    protected Orientation orientation;
    protected boolean horseman;
    protected boolean shieldbearer;
    protected boolean done;
    protected boolean targeted;
    protected boolean promoted;

    // render attributes
    private boolean visible;
    private float blinkTime = 0;


    public IsoUnitRenderer(int row, int col, IUnit model, IsoBFR bfr) {
        super(model);

        this.bfr = bfr;
        this.animation = new Animation(1,1,false,false);

        setVisible(true);

        this.weaponType = model.getWeaponType();
        this.orientation = model.getOrientation();
        this.horseman = model.isHorseman();
        this.shieldbearer = model.isShielbearer();
        this.done = model.isDone();
        this.promoted = model.isPromoted();

        display(AnimId.REST);
        setPos(row, col);
    }

    @Override
    public void render(SpriteBatch batch) {
        if(visible){
            if(targeted){
                spriteSet.get(animation.getCurrentFrame()).setAlpha(0.7f + 0.3f* MathUtils.cos(Data.TARGET_BLINK_PERIOD * blinkTime));
            }
            spriteSet.get(animation.getCurrentFrame()).draw(batch);
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        animation.update(dt);
        if(targeted) blinkTime += dt;
    }

    public void setCenterX(float centerX, boolean updateRenderCall) {
        this.centerX = centerX;
        updateSpritePos();
        if(updateRenderCall) bfr.updateBURRenderCall(this);
    }

    public void setCenterY(float centerY, boolean updateRenderCall) {
        this.centerY = centerY;
        updateSpritePos();
        if(updateRenderCall) bfr.updateBURRenderCall(this);
    }

    @Override
    public void setPos(int row, int col) {
        setCenterX(bfr.getCenterX(row, col), false);
        setCenterY(bfr.getCenterY(row, col), false);
        bfr.updateBURRenderCall(this);
    }

    @Override
    public float getCenterX() {
        return centerX;
    }

    @Override
    public float getCenterY() {
        return centerY;
    }

    float i = 0f;
    @Override
    public void setDone(boolean done) {
        this.done = done;
        this.updateAnimation(state, orientation);
    }

    @Override
    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
        if(!targeted) {
            for(int i = 0; i < spriteSet.size; i++)
                spriteSet.get(i).setAlpha(1);
        }else{
            blinkTime = 0;
        }
    }

    @Override
    public void setOrientation(Orientation or) {
        this.orientation = or;
        this.updateAnimation(state, or);
    }

    @Override
    public void setWeaponType(WeaponType type) {
        this.weaponType = type;
        this.updateAnimation(state, orientation);
    }

    @Override
    public void setHorseman(boolean horseman) {
        this.horseman = horseman;
        this.updateAnimation(state, orientation);
    }

    /**
     * usefull for the render task scheduling handled by the super class
     * @return true if the unit is not at rest.
     */
    @Override
    public boolean isResting() {
        return state == AnimId.REST;
    }

    @Override
    public void displayWalk(Array<int[]> path, boolean switchpos) {

    }

    @Override
    public void displayTakeHit(boolean moralOnly, int damageTaken, boolean critical, boolean backstab) {
        updateAnimation(AnimId.TAKE_HIT, orientation);
    }

    @Override
    public void displayTreated(int healedHP) {
        updateAnimation(AnimId.HEAL, orientation);
    }

    @Override
    public void displayPushed(Data.Orientation pushedTowards) {
        updateAnimation(AnimId.PUSHED, orientation);
    }

    @Override
    public void displayFlee(Orientation fleeingDirection) {
        updateAnimation(AnimId.FLEE, fleeingDirection);
    }

    @Override
    public void display(AnimId id) {
        updateAnimation(id, orientation);
    }

    private void updateAnimation(AnimId animId, Orientation or){

        // fetch the required sprite set
       Array<Sprite> updatedSet = (getModel().isCharacter()) ?
                bfr.spriteProvider.charaSpriteTree.getSpriteSet(promoted, getModel().getTemplate(), weaponType, or, getModel().isWarChief(), done, animId.getSpriteSetId()) :
                bfr.spriteProvider.genSpriteTree.getSpriteSet(getModel().getArmy().isPlayerControlled(), shieldbearer, horseman, getModel().getTemplate(), weaponType, or, getModel().isWarChief(), done, animId.getSpriteSetId()) ;

        if(updatedSet != null) {

            // set animation , sprites, ids and rendering coords fitting
            this.state = animId;
            this.spriteSet = updatedSet;
            updateSpritePos();
            this.animation.set(updatedSet.size, animId.getSpeed(), animId.isLoop(), animId.isBacknforth());
            this.animation.stop();
            this.animation.play();
            if(!animId.isLoop()) this.animation.attach(this);
        }
    }

    private void updateSpritePos(){
        float xSprite;
        for(int i = 0; i < spriteSet.size; i++){
            xSprite = centerX - IsoBFR.SPRITE_STD_SIZE*((spriteSet.get(i).getWidth() == spriteSet.get(i).getHeight()) ? 0.5f : 0.25f );
            spriteSet.get(i).setX(xSprite);
            spriteSet.get(i).setY(centerY - IsoBFR.SPRITE_STD_SIZE*IsoBFR.RATIO*0.5f);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void getNotification(Object data) {
        super.getNotification(data);
        if(data instanceof Animation && data == animation){
            this.animation.stop();
            this.animation.detach(this);
            this.state = AnimId.REST;
            this.setOrientation(orientation);
        }
    }

}
