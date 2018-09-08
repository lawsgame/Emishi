package com.lawsgame.emishitactics.core.phases.battle.renderers.tempo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.SpriteSetId;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.engine.timers.CountDown;

import static com.lawsgame.emishitactics.core.models.Data.SPEED_PUSHED;
import static com.lawsgame.emishitactics.core.models.Data.SPEED_WALK;

public class TempoUnitRenderer extends BattleUnitRenderer {

    private static float BLINK_PERIOD = 1.5f * MathUtils.PI;

    protected boolean visible;

    private Sprite unitSprite;
    private TextureRegion weapontTexture;
    private TextureRegion mountedTexture;
    private TextureRegion orientationTexture;
    private TextureRegion offabbTexture = null;

    private boolean done;
    private boolean targeted;
    private float blinkTime = 0;
    private boolean executing;

    private CountDown countDown = new CountDown(2f){
      @Override
      public void run(){
          super.run();
          executing = true;
      }

      @Override
      public void reset(){
          super.reset();
          executing = false;
      }
    };

    //walk (& switch position / pushed) attributes
    private float dl;
    private boolean updatePoint = false;
    private boolean pushed = false;
    private Array<int[]> remainingPath = new Array<int[]>(); // array of (r, c) <=> (y, x)




    public TempoUnitRenderer(int row, int col, IUnit model) {
        super(model);
        this.unitSprite = new Sprite();
        unitSprite.setX(col);
        unitSprite.setY(row);
        unitSprite.setSize(1, 1);
        this.executing = false;
        this.done = false;
        this.visible = true;
        weapontTexture = TempoSpritePool.getInstance().getWeaponSprite(model.getCurrentWeapon().getTemplate().getWeaponType());
        orientationTexture = TempoSpritePool.getInstance().getOrientationSprite(model.getOrientation());
        display(Data.AnimId.REST);
    }


    //------------------ GAME ELEMENT METHODS -------------------------------------------

    @Override
    public void update(float dt) {

        countDown.update(dt);
        if (countDown.isFinished()) {
            offabbTexture = null;
            countDown.reset();
            if(!isExecuting())
                display(Data.AnimId.REST);

        }

        if(targeted) blinkTime += dt;

        handleWalkAnimation(dt);

        super.update(dt);
    }



    private void handleWalkAnimation(float dt){
        // handle walk animation
        if (remainingPath.size > 0) {

            dl = dt * ((pushed) ? SPEED_PUSHED: SPEED_WALK);
            if (unitSprite.getX() == remainingPath.get(0)[1]) {
                if ((unitSprite.getY() < remainingPath.get(0)[0] && unitSprite.getY() + dl >= remainingPath.get(0)[0])
                        || (unitSprite.getY() > remainingPath.get(0)[0] && unitSprite.getY() + dl <= remainingPath.get(0)[0])) {
                    setY(remainingPath.get(0)[0]);
                    updatePoint = true;
                } else if (unitSprite.getY() < remainingPath.get(0)[0]) {
                    setY(unitSprite.getY() + dl);
                } else if (unitSprite.getY() > remainingPath.get(0)[0]) {
                    setY(unitSprite.getY() - dl);
                }
            }
            if (unitSprite.getY() == remainingPath.get(0)[0]) {
                if ((unitSprite.getX() < remainingPath.get(0)[1] && unitSprite.getX() + dl >= remainingPath.get(0)[1])
                        || (unitSprite.getX() > remainingPath.get(0)[1] && unitSprite.getX() + dl <= remainingPath.get(0)[1])) {
                    setX(remainingPath.get(0)[1]);
                    updatePoint = true;
                } else if (unitSprite.getX() < remainingPath.get(0)[1]) {
                    setX(  unitSprite.getX() + dl);
                } else if (unitSprite.getX() > remainingPath.get(0)[1])
                    setX(unitSprite.getX() - dl);
            }



            if (updatePoint) {
                updatePoint = false;
                remainingPath.removeIndex(0);

                if (remainingPath.size == 0) {
                    pushed = false;
                    executing = false;
                    display(Data.AnimId.REST);
                } else {
                    Data.Orientation orientation = Utils.getOrientationFromCoords(unitSprite.getY(), unitSprite.getX(), remainingPath.get(0)[0], remainingPath.get(0)[1]);
                    orientationTexture = TempoSpritePool.getInstance().getOrientationSprite(orientation);
                }
            }
        }
    }


    @Override
    public void render(SpriteBatch batch) {
        if(visible) {
            if(targeted){
                unitSprite.setAlpha(0.7f + 0.3f*MathUtils.cos(BLINK_PERIOD * blinkTime));
            }
            unitSprite.draw(batch);
            batch.draw(weapontTexture, getX(), getY(), 0.25f, 0.25f);
            batch.draw(orientationTexture, getX() + 0.75f, getY() + 0.75f, 0.25f, 0.25f);
            if (mountedTexture != null) batch.draw(mountedTexture, getX() + 0.75f, getY() + 0f, 0.25f, 0.25f);
            if (offabbTexture != null) batch.draw(offabbTexture, getX(), getY() + 1, 1, 0.25f);
        }
    }



    // ---------------- TRIGGER ANIMATION METHODS ---------------------------------------





    @Override
    public void displayWalk(Array<int[]> path) {
        /*
        CHECK PATH VALIDITY
        for the validPath to be explotable, it is required
        - to not be EMPTY
        - to be a collection of TILE COORDS
        - which each of them are NEIGHBOR of its predecessor.
        - to start next to the last known position of the unit
         */
        boolean validPath = path.size > 0;
        int[] oldCoords = new int[]{(int) unitSprite.getY(), (int) unitSprite.getX()};
        for(int[] coords: path) {
            if(coords.length < 2 || 1 != Utils.dist(oldCoords[0], oldCoords[1], coords[0], coords[1])) {
                validPath = false;
                continue;
            }
            oldCoords = coords;
        }

        if(validPath) {
            this.remainingPath.addAll(path);


                /*
            CLEAN PATH
            remove unnecesary entries for the walk position algorithm use in  UnitRenderer.update(dt)
             */
            int[] pPreviousEntry;
            int[] previousEntry;
            int[] entry;
            for (int i = 2; i < remainingPath.size; i++) {
                entry = remainingPath.get(i);
                previousEntry = remainingPath.get(i - 1);
                pPreviousEntry = remainingPath.get(i - 2);

                if ( (entry[0] == previousEntry[0] && previousEntry [0] == pPreviousEntry[0]) || (entry[1] == previousEntry[1] && previousEntry [1] == pPreviousEntry[1])){
                    remainingPath.removeIndex(i-1);
                    i--;
                }

            }

            Data.Orientation or = Utils.getOrientationFromCoords(unitSprite.getY(), unitSprite.getX(),path.get(0)[0], path.get(0)[1]);
            orientationTexture = TempoSpritePool.getInstance().getOrientationSprite(or);
            unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.WALK_FLEE_SWITCHPOSITION, getModel().getArmy().getAllegeance()));
            executing = true;



        }



    }

    @Override
    public void displayTakeHit(boolean moralOnly, int damageTaken, boolean critical, boolean backstab) {
        if(backstab){
            unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.BACKSTABED_PUSHED, getModel().getArmy().getAllegeance()));
        }else{
            unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.TAKE_HIT, getModel().getArmy().getAllegeance()));
        }
        countDown.run();
    }


    @Override
    public void displayTreated(int healedHP) {

        unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED, getModel().getArmy().getAllegeance()));
        countDown.run();
    }

    @Override
    public void displayPushed(Data.Orientation pushedTowards){
        orientationTexture = TempoSpritePool.getInstance().getOrientationSprite(pushedTowards);
        unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.BACKSTABED_PUSHED, getModel().getArmy().getAllegeance()));
        pushed = true;
        int x = (int) getX();
        int y = (int) getY();
        switch (pushedTowards){
            case WEST: remainingPath.add(new int[]{y, x - 1}); break;
            case NORTH: remainingPath.add(new int[]{y + 1, x});break;
            case SOUTH: remainingPath.add(new int[]{y - 1, x}); break;
            case EAST: remainingPath.add(new int[]{y, x + 1}); break;
        }

    }

    @Override
    public void displayFlee(Data.Orientation fleeingDirection) {
        unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.WALK_FLEE_SWITCHPOSITION, getModel().getArmy().getAllegeance()));
        orientationTexture = TempoSpritePool.getInstance().getOrientationSprite(fleeingDirection);
        countDown.run();
    }

    @Override
    public void display(Data.AnimId id) {
        switch (id){
            case ATTACK:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.REGULAR_ATTACK, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case PUSH:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.PUSH, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case HEAL:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.HEAL, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case STEAL:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.STEAL, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case BUILD:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.BUILD, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case GUARD:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case GUARDED:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case DIE:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.DIE, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case DODGE:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.DODGE, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case WOUNDED:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.TAKE_HIT, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case SPECIAL_MOVE:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.SPECIAL_MOVE, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case LEVELUP:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case SWITCH_WEAPON:
                unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(SpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED, getModel().getArmy().getAllegeance()));
                countDown.run();
                break;
            case REST:
                if(done){
                    unitSprite.setRegion(TempoSpritePool.getInstance().getDoneUnitSprite());
                }else {
                    unitSprite.setRegion(TempoSpritePool.getInstance().getUnitSprite(Data.SpriteSetId.REST, getModel().getArmy().getAllegeance()));
                }
                break;
                default:
        }
    }

    @Override
    public void setOrientation(Data.Orientation or) {
        orientationTexture = TempoSpritePool.getInstance().getOrientationSprite(or);
    }

    @Override
    public void setWeaponType(Data.WeaponType type) {
        weapontTexture = TempoSpritePool.getInstance().getWeaponSprite(type);
    }

    @Override
    public void setHorseman(boolean horseman) {
        if (horseman){
            mountedTexture = TempoSpritePool.getInstance().getMountedSprite();
        }else{
            mountedTexture = null;
        }
    }


    // ---------------- OTHERS METHODS ---------------------------------------------------





    @Override
    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
        if(!targeted) {
            unitSprite.setAlpha(1);
        }else{
            blinkTime = 0;
        }
    }

    @Override
    public boolean isAnimationRolling() {
        return executing;
    }

    @Override
    public void setDone(boolean done) {
        this.done = done;
        display(Data.AnimId.REST);
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }




    @Override
    public void setX(float x) {
        this.unitSprite.setX(x);
    }

    @Override
    public void setY(float y) {
        this.unitSprite.setY(y);
    }

    public float getX(){
        return unitSprite.getX();
    }

    public float getY(){
        return unitSprite.getY();
    }



}
