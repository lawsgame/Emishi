package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.interfaces.SpriteProvider;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.AnimId;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.engine.math.functions.Function;
import com.lawsgame.emishitactics.engine.rendering.Animation;
import com.lawsgame.emishitactics.engine.timers.CountDown;

import static com.lawsgame.emishitactics.core.models.Data.AnimId.IDLE;

public class IsoUnitRenderer extends BattleUnitRenderer  {

    protected IsoBFR bfr;
    private Vector2 pos;
    protected AnimId state;
    protected Animation animation;
    protected Array<Sprite> unitSpriteSet;
    protected Sprite shadowSprite;

    // MODEL SNAPHSOT ATTRIBUTES

    protected Orientation orientation;
    protected boolean done;
    protected boolean crippled;
    protected boolean disabled;
    protected boolean promoted;
    protected boolean outOfAction = false;

    // RENDER ATTRIBUTES

    protected boolean blinking;
    private float blinkTime = 0;
    private Function blinkPeriod;
    private boolean visible;
    private boolean poolNextTask = false;
    private CountDown animationCountDown = new CountDown(Data.ANIMATION_DURATION);

    //walk (& switch position / pushed) attributes

    /**
     * the data struture is a Stack with on top the current step vgoal od the moving unit
     */
    private Array<float[]> remainingPath = new Array<float[]>();
    private boolean reveal = false;
    private float alpha = 1f;


    public IsoUnitRenderer(int row, int col, Unit model, IsoBFR bfr) {
        super(model);
        this.bfr = bfr;
        this.pos = new Vector2();
        this.animation = new Animation(1,1,false,false, false);
        this.orientation = model.getOrientation();
        this.done = model.isDone();
        this.promoted = model.isPromoted();
        this.blinkPeriod = new TargetPeriod();
        setCrippled(model.isCrippled());
        setDisabled(model.isDisabled());
        setVisible(true);
        TextureAtlas atlas = bfr.assetManager.get(Assets.ATLAS_BATTLE_ICONS);
        TextureRegion shadowRegion = atlas.findRegion(Assets.REGION_UNIT_SHADOW);
        this.shadowSprite = new Sprite(shadowRegion);
        this.shadowSprite.setSize(IsoBFR.SPRITE_STD_SIZE * 0.5f, IsoBFR.SPRITE_STD_SIZE * 0.25f);
        display(IDLE);
        setPos(row, col);
    }

    @Override
    public void render(SpriteBatch batch) {
        if(visible && bfr.isInFrame(unitSpriteSet.get(animation.getCurrentFrame()))){
            if(blinking){
                this.alpha = 0.7f + 0.3f* MathUtils.cos(blinkPeriod.getValue(blinkTime) * blinkTime);
            }
            updateAlpha();
            shadowSprite.draw(batch);
            unitSpriteSet.get(animation.getCurrentFrame()).draw(batch);
        }
    }

    private void updateAlpha(){
        unitSpriteSet.get(animation.getCurrentFrame()).setAlpha(alpha);
    }

    @Override
    public void update(float dt) {
        /*
         POOLING SCHEDULER TASK

         By setting poolNextTask to true, the renderer allow itself to wait one full frame
         for the scheduler to push the next set of tasks to be rendered
         before updating the current animation by calling BUR.display().

         Allowing in fine to blend animation without displaying the ugly blinking rest animation in-between
          */
        if(poolNextTask){
            poolNextTask = false;
            launchNextAnimation();
            if(state == IDLE){
                display(IDLE);
            }
        }

        handleDeplacement(dt);

        if(blinking) {
            blinkTime += dt;
        }

        if(animationCountDown.isFinished()){
            animationCountDown.reset();
            setBlinking(false);
            this.state = IDLE;
            /*
            launchNextAnimation is called twice :

            One time, right after the end of any animation to pool the next
            render task from the notification queue of the BUR super class

            A second time, through the use of poolNextTask if the BUR is still in
            idle mode to allow the scheduler to update the content of the notification queue
            of the renderer.
             */
            launchNextAnimation();
            if(state == IDLE) {
                this.poolNextTask = true;
            }
        }
        animation.update(dt);
        animationCountDown.update(dt);

    }

    public void setCenterX(float centerX, boolean updateRenderCall) {
        this.pos.x = centerX;
        updateSpritePos();
        if(updateRenderCall)
            bfr.updateBURRenderCall(this);
    }

    public void setCenterY(float centerY, boolean updateRenderCall) {
        this.pos.y = centerY;
        updateSpritePos();
        if(updateRenderCall)
            bfr.updateBURRenderCall(this);
    }

    @Override
    public void setPos(int row, int col) {
        setCenterX(bfr.getCenterX(row, col), false);
        setCenterY(bfr.getCenterY(row, col), false);
        bfr.updateBURRenderCall(this);
    }

    private void setBlinking(boolean blinking){
        this.blinking = blinking;
        if(!blinking) {
            for(int i = 0; i < unitSpriteSet.size; i++) {
                this.alpha = 1f;
            }
        }else{
            blinkTime = 0;
        }
    }

    @Override
    public float getCenterX() {
        return pos.x;
    }

    @Override
    public float getCenterY() {
        return pos.y;
    }

    @Override
    public int getCurrentRow() {
        return bfr.getRow(getCenterX(), getCenterY());
    }

    @Override
    public int getCurrentCol() {
        return bfr.getCol(getCenterX(), getCenterY());
    }

    @Override
    public void setDone(boolean done) {
        this.done = done;
        this.display(state);
    }

    @Override
    public void setTargeted(boolean targeted) {
        this.blinkPeriod = new TargetPeriod();
        setBlinking(targeted);
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public void setCrippled(boolean crippled) {
        this.crippled = crippled;
    }

    @Override
    public void setOrientation(Orientation or) {
        this.orientation = or;
        this.display(state);
    }

    @Override
    public WeaponType getWeaponType() {
        return getModel().getWeaponType();
    }

    @Override
    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
        display(state);
    }

    @Override
    public boolean isPromoted() {
        return promoted;
    }

    /**
     * usefull for the render task scheduling handled by the super class
     * @return true if the unit is not at rest.
     */
    @Override
    public boolean isIdling() {
        return state == IDLE;
    }

    /**
     *
     * @param path : path to a given tile, must not include the current tile occupied by this unit
     * @param reveal
     */
    @Override
    public void displayWalk(Array<int[]> path, boolean reveal) {

        int rowInitUnit = bfr.getRow(getCenterX(), getCenterY());
        int colInitUnit = bfr.getCol(getCenterX(), getCenterY());
        /*
        CHECK PATH VALIDITY
        for the validPath to be explotable, it is required
        - to not be EMPTY
        - to be a collection of TILE COORDS
        - which each of them are NEIGHBOR of its predecessor.
        - to start next to the last known position of the unit
         */
        boolean validPath = path.size > 0;
        int[] oldCoords = new int[]{rowInitUnit, colInitUnit};
        for(int i = 0; i < path.size; i++) {
            if(path.get(i).length < 2 || 1 != Utils.dist(oldCoords[0], oldCoords[1], path.get(i)[0], path.get(i)[1])) {
                validPath = false;
                break;
            }
            oldCoords = path.get(i);
        }

        if(validPath) {
            this.remainingPath.clear();

            int row;
            int col;
            for(int i = path.size - 1; i >= 0; i--){
                row = path.get(i)[0];
                col = path.get(i)[1];
                this.remainingPath.add(new float[]{bfr.getCenterX(row, col), bfr.getCenterY(row, col)});
            }
            this.remainingPath.add(new float[]{getCenterX(), getCenterY()});
            this.state = AnimId.WALK;

            if(reveal){
                this.alpha = 0f;
                this.reveal = true;
            }
        }
    }




    private Vector2 dl = new Vector2();
    private Vector2 checkingdl = new Vector2();
    private boolean pushed = false;
    private float[] vgoal = new float[2];
    private int[] previousGoal = new int[2]; //x, y
    private  int[] goal = new int[2]; // row, col

    private void updateMoveTempoStep(float dt){



        float[] vpreviousGoal = remainingPath.pop();
        if(remainingPath.size > 0) {

            if(reveal && MathUtils.isZero(1f - alpha, 0.1f)){
                this.alpha = 1f;
                this.reveal = false;
            }

            vgoal[0] = remainingPath.peek()[0];
            vgoal[1] = remainingPath.peek()[1];
            checkingdl.x = vgoal[0] - vpreviousGoal[0];
            checkingdl.y = vgoal[1] - vpreviousGoal[1];
            goal[0] = bfr.getRow(vgoal[0], vgoal[1]);
            goal[1] = bfr.getCol(vgoal[0], vgoal[1]);

            if (dl.len2() > 0 && checkingdl.isCollinear(dl, 0.01f)) {

                setCenterX(getCenterX() + dl.x*dt, false);
                setCenterY(getCenterY() + dl.y*dt, false);
            } else {

                previousGoal[0] = bfr.getRow(vpreviousGoal[0], vpreviousGoal[1]);
                previousGoal[1] = bfr.getCol(vpreviousGoal[0], vpreviousGoal[1]);

                checkingdl.nor();
                checkingdl.scl((pushed) ? Data.SPEED_PUSHED : Data.SPEED_WALK);
                dl.x = checkingdl.x;
                dl.y = checkingdl.y;
                setCenterX(vpreviousGoal[0] , false);
                setCenterY(vpreviousGoal[1] , false);

                if(!pushed) this.orientation = Utils.getOrientationFromCoords(previousGoal[0], previousGoal[1],goal[0], goal[1]);
                display(state);
            }
            bfr.updateBURRenderCall(goal[0], goal[1], this);



        }else{
            // walk animation finished !
            dl.x = 0;
            dl.y = 0;

            bfr.updateBURRenderCall( this);

            if(!pushed){

                //display(AnimId.IDLE);
                this.state = IDLE;
                launchNextAnimation();
                this.poolNextTask = true;
            }
            reveal = false;
            alpha = 1f;
            pushed = false;
        }
    }

    private void handleDeplacement(float dt){
        /*
        calculate the demi scalprod of (vpos.x - vgoal.x.(vpos.x - vgoal.x + dl.x)
        if negative, the step tempo vgoal must be updated
        */
        if(remainingPath.size > 0) {
            if ((getCenterX() - remainingPath.peek()[0]) * (getCenterX() + dl.x*dt - remainingPath.peek()[0]) <= 0) {
                updateMoveTempoStep(dt);
            } else {
                setCenterX(getCenterX() + dl.x * dt, false);
                setCenterY(getCenterY() + dl.y * dt, false);
                if(alpha < 1f){
                    this.alpha = 1 - Math.abs(getCenterX() - remainingPath.peek()[0]);
                }
            }
        }
    }

    @Override
    public void displayAttack(Notification.Attack query) {
        display(query.specialmove ? Data.AnimId.SPECIAL_MOVE : Data.AnimId.REGULAR_ATTACK);
    }


    @Override
    public void displayTakeHit(int lifeDamageTaken, int moralDamageTaken, boolean critical, boolean backstab) {
        this.blinkPeriod = new WoundedPeriod();
        setBlinking(true);
        display((backstab) ? AnimId.BACKSTAB : AnimId.WOUNDED);
    }

    @Override
    public void displayTreated(int healedHP) {
        display(AnimId.TREATED);
    }

    @Override
    public void displayPushed(Data.Orientation pushedTowards) {
        this.pushed = true;
        this.remainingPath.clear();
        float[] targetCenter = new float[2];
        switch (pushedTowards){
            case WEST:
                targetCenter[0]= bfr.getCenterX(bfr.getRow(pos.x, pos.y), bfr.getCol(pos.x, pos.y) - 1);
                targetCenter[1]= bfr.getCenterY(bfr.getRow(pos.x, pos.y), bfr.getCol(pos.x, pos.y) - 1);
                break;
            case NORTH:
                targetCenter[0]= bfr.getCenterX(bfr.getRow(pos.x, pos.y) + 1, bfr.getCol(pos.x, pos.y));
                targetCenter[1]= bfr.getCenterY(bfr.getRow(pos.x, pos.y) + 1, bfr.getCol(pos.x, pos.y));
                break;
            case SOUTH:
                targetCenter[0]= bfr.getCenterX(bfr.getRow(pos.x, pos.y) - 1, bfr.getCol(pos.x, pos.y));
                targetCenter[1]= bfr.getCenterY(bfr.getRow(pos.x, pos.y) - 1, bfr.getCol(pos.x, pos.y));
                break;
            case EAST:
                targetCenter[0]= bfr.getCenterX(bfr.getRow(pos.x, pos.y), bfr.getCol(pos.x, pos.y) + 1);
                targetCenter[1]= bfr.getCenterY(bfr.getRow(pos.x, pos.y), bfr.getCol(pos.x, pos.y) + 1);
                break;
        }
        this.remainingPath.add(new float[]{targetCenter[0], targetCenter[1]});
        this.remainingPath.add(new float[]{getCenterX(), getCenterY()});
        this.orientation = pushedTowards;
        this.state = AnimId.PUSHED;
    }

    @Override
    public void displayFlee(Orientation fleeingDirection) {
        this.orientation = fleeingDirection;
        display(AnimId.FLEE);
    }

    /**
     * NB : done = true only available
     *
     * @param animId
     */
    @Override
    public void display(AnimId animId) {
        Array<Sprite> updatedSet = bfr.spriteProvider.getUnitAnimationSS(this, animId, (done) ? SpriteProvider.Flavor.DONE : SpriteProvider.Flavor.NORMAL);
        if(updatedSet != null) {
            // set animation , sprites, ids and rendering coords fitting
            state = animId;
            unitSpriteSet = updatedSet;
            updateSpritePos();
            animation.set(updatedSet.size, animId.getSpeed(), animId.isLoop(), animId.isBacknforth(), animId.isRandomlyStarted());
            animation.stop();
            animation.play();
            animationCountDown.reset();
            if(state.isTimeLimited()) {
                animationCountDown.run();
            }
            outOfAction = state == AnimId.DIE || state == AnimId.FLEE;

        }
    }

    /**
     * to be called whenever the unit renderer change of position.
     * update sprites positions according to the value of {@link IsoUnitRenderer#pos}
     *
     */
    private void updateSpritePos(){
        float xSprite;
        for(int i = 0; i < unitSpriteSet.size; i++){
            xSprite = pos.x - IsoBFR.SPRITE_STD_SIZE*((unitSpriteSet.get(i).getWidth() == unitSpriteSet.get(i).getHeight()) ? 0.5f : 0.25f );
            unitSpriteSet.get(i).setX(xSprite);
            unitSpriteSet.get(i).setY(pos.y - IsoBFR.SPRITE_STD_SIZE*IsoBFR.RATIO*0.5f);
        }
        this.shadowSprite.setX(getCenterX() - shadowSprite.getWidth() / 2f);
        this.shadowSprite.setY(getCenterY() - shadowSprite.getHeight() / 2f);
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Vector2 getPos() {
        return pos;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    //------------------ HELPER CLASS--------------------------------

    public static class TargetPeriod implements Function{

        @Override
        public float getValue(float t) {
            return Data.BLINK_PERIOD_TARGET;
        }
    }

    public static class WoundedPeriod implements Function{


        /**
         *
         * @param t
         * @return Q(t) = (B + A*sin( 2*PI*t / animation_duration) * PI
         */
        @Override
        public float getValue(float t) {
            return (Data.BLINK_PERIOD_WOUNDED_BASE + Data.BLINK_PERIOD_WOUNDED_AMPLITUDE*MathUtils.sin( MathUtils.PI *(0.25f + 0.75f * t/ Data.ANIMATION_DURATION))) * MathUtils.PI;
        }
    }



}
