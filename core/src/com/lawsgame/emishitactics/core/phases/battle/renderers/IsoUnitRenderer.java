package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.AnimId;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.engine.math.geometry.Vector;
import com.lawsgame.emishitactics.engine.rendering.Animation;
import com.lawsgame.emishitactics.engine.timers.CountDown;

public class IsoUnitRenderer extends BattleUnitRenderer  {

    private static final float VECTPROD_ERROR_MARGIN = 0.01f;

    protected IsoBFR bfr;
    private float cx; // center X
    private float cy; // center Y
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
    protected boolean outofaction = false;


    // RENDER ATTRIBUTES

    private boolean visible;
    private float blinkTime = 0;
    private CountDown animationCountDown = new CountDown(Data.ANIMATION_DURATION);

    //walk (& switch position / pushed) attributes

    /**
     * the data struture is a Stack with on top the current step vgoal od the moving unit
     */
    private Array<float[]> remainingPath = new Array<float[]>();



    public IsoUnitRenderer(int row, int col, IUnit model, IsoBFR bfr) {
        super(model);

        this.bfr = bfr;
        this.animation = new Animation(1,1,false,false, false);

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
        if(visible && bfr.isSpriteWithinFrame(spriteSet.get(animation.getCurrentFrame()))){
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
        animationCountDown.update(dt);

        handleDeplacement(dt);

        if(targeted) blinkTime += dt;

        if(animationCountDown.isFinished()){
            animationCountDown.reset();
            if(!outofaction) {
                this.display(AnimId.REST);
            }else{
                this.state = AnimId.REST;
            }

        }

    }

    public void setCenterX(float centerX, boolean updateRenderCall) {
        this.cx = centerX;
        updateSpritePos();
        if(updateRenderCall)
            bfr.updateBURRenderCall(this);
    }

    public void setCenterY(float centerY, boolean updateRenderCall) {
        this.cy = centerY;
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

    @Override
    public float getCenterX() {
        return cx;
    }

    @Override
    public float getCenterY() {
        return cy;
    }

    @Override
    public void setDone(boolean done) {
        this.done = done;
        this.display(state);
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
        this.display(state);
    }

    @Override
    public void setWeaponType(WeaponType type) {
        this.weaponType = type;
        this.display(state);
    }

    @Override
    public void setHorseman(boolean horseman) {
        this.horseman = horseman;
        this.display(state);
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
        for(int[] coords: path) {
            if(coords.length < 2 || 1 != Utils.dist(oldCoords[0], oldCoords[1], coords[0], coords[1])) {
                validPath = false;
                break;
            }
            oldCoords = coords;
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

        }
    }

    private Vector dl = new Vector();
    Vector checkingdl = new Vector();
    private boolean pushed = false;
    private float[] vgoal = new float[2];
    float[] vpreviousGoal; //x, y
    int[] previousGoal = new int[2]; //x, y
    int[] goal = new int[2]; // row, col

    private void updateMoveTempoStep(float dt){

        vpreviousGoal = remainingPath.pop();
        if(remainingPath.size > 0) {

            vgoal[0] = remainingPath.peek()[0];
            vgoal[1] = remainingPath.peek()[1];
            checkingdl.x = vgoal[0] - vpreviousGoal[0];
            checkingdl.y = vgoal[1] - vpreviousGoal[1];
            goal[0] = bfr.getRow(vgoal[0], vgoal[1]);
            goal[1] = bfr.getCol(vgoal[0], vgoal[1]);

            if (dl.length() > 0 && MathUtils.isZero(Vector.vectprod(checkingdl, dl), VECTPROD_ERROR_MARGIN) && Vector.scalprod(checkingdl, dl) > 0) {

                setCenterX(getCenterX() + dl.x*dt, false);
                setCenterY(getCenterY() + dl.y*dt, false);
            } else {

                previousGoal[0] = bfr.getRow(vpreviousGoal[0], vpreviousGoal[1]);
                previousGoal[1] = bfr.getCol(vpreviousGoal[0], vpreviousGoal[1]);

                checkingdl.normalize();
                checkingdl.multiply((pushed) ? Data.SPEED_PUSHED : Data.SPEED_WALK);
                dl.x = checkingdl.x;
                dl.y = checkingdl.y;
                setCenterX(vpreviousGoal[0] , false);
                setCenterY(vpreviousGoal[1] , false);

                if(!pushed) this.orientation = Utils.getOrientationFromCoords(previousGoal[0], previousGoal[1],goal[0], goal[1]);
                display(state);
            }
            bfr.updateBURRenderCall(goal[0], goal[1], this);

        }else{
            dl.x = 0;
            dl.y = 0;

            if(!pushed) display(AnimId.REST);
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
            }
        }
    }

    @Override
    public void displayTakeHit(boolean moralOnly, int damageTaken, boolean critical, boolean backstab) {
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
                targetCenter[0]= bfr.getCenterX(bfr.getRow(cx, cy), bfr.getCol(cx, cy) - 1);
                targetCenter[1]= bfr.getCenterY(bfr.getRow(cx, cy), bfr.getCol(cx, cy) - 1);
                break;
            case NORTH:
                targetCenter[0]= bfr.getCenterX(bfr.getRow(cx, cy) + 1, bfr.getCol(cx, cy));
                targetCenter[1]= bfr.getCenterY(bfr.getRow(cx, cy) + 1, bfr.getCol(cx, cy));
                break;
            case SOUTH:
                targetCenter[0]= bfr.getCenterX(bfr.getRow(cx, cy) - 1, bfr.getCol(cx, cy));
                targetCenter[1]= bfr.getCenterY(bfr.getRow(cx, cy) - 1, bfr.getCol(cx, cy));
                break;
            case EAST:
                targetCenter[0]= bfr.getCenterX(bfr.getRow(cx, cy), bfr.getCol(cx, cy) + 1);
                targetCenter[1]= bfr.getCenterY(bfr.getRow(cx, cy), bfr.getCol(cx, cy) + 1);
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
        Array<Sprite> updatedSet = (getModel().isCharacter()) ?
                bfr.assetProvider.charaSpriteTree.getSpriteSet(
                        promoted,
                        getModel().getTemplate(),
                        weaponType,
                        orientation,
                        done,
                        animId.getSpriteSetId(getModel().isWarChief())) :
                bfr.assetProvider.genSpriteTree.getSpriteSet(
                        getModel().getArmy().isPlayerControlled(),
                        shieldbearer,
                        horseman,
                        getModel().getTemplate(),
                        weaponType,
                        orientation,
                        done,
                        animId.getSpriteSetId(getModel().isWarChief())) ;

        if(updatedSet != null) {

            // build animation , sprites, ids and rendering coords fitting
            this.state = animId;
            this.spriteSet = updatedSet;
            updateSpritePos();
            this.animation.set(updatedSet.size, animId.getSpeed(), animId.isLoop(), animId.isBacknforth(), animId.isRandomlyStarted());
            this.animation.stop();
            this.animation.play();
            if(state.isTimeLimited()) {
                animationCountDown.run();
            }
            if(state == AnimId.DIE || state == AnimId.FLEE) {
                outofaction = true;
            }
        }
    }

    private void updateSpritePos(){
        float xSprite;
        for(int i = 0; i < spriteSet.size; i++){
            xSprite = cx - IsoBFR.SPRITE_STD_SIZE*((spriteSet.get(i).getWidth() == spriteSet.get(i).getHeight()) ? 0.5f : 0.25f );
            spriteSet.get(i).setX(xSprite);
            spriteSet.get(i).setY(cy - IsoBFR.SPRITE_STD_SIZE*IsoBFR.RATIO*0.5f);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}
