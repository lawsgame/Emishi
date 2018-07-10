package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.TempoSprite2DPool;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.engine.timers.CountDown;

import static com.lawsgame.emishitactics.core.constants.Data.SPEED_PUSHED;
import static com.lawsgame.emishitactics.core.constants.Data.SPEED_WALK;

public class TempoUnitRenderer extends BattleUnitRenderer {
    protected float x;
    protected float y;
    protected Battlefield battlefield;

    private TextureRegion unitTexture;
    private TextureRegion weapontTexture;
    private TextureRegion shieldTexture;
    private TextureRegion mountedTexture;
    private TextureRegion orientationTexture;
    private TextureRegion offabbTexture = null;

    private boolean targeted = false;
    private boolean executing = false;
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
    private boolean hitTaken = false;
    private boolean handleOSUnit = false;


    public TempoUnitRenderer(int row, int col, Unit model, Battlefield battlefield) {
        super(model);
        this.x = col;
        this.y = row;
        this.battlefield = battlefield;
        display(Data.AnimationId.REST);
        getNotification(null);
    }

    //------------------ GAME ELEMENT METHODS -------------------------------------------

    @Override
    public void update(float dt) {
        countDown.update(dt);
        handleCountDownBasedCompletedAnimation();
        handleWalkAnimation(dt);
    }

    private void handleCountDownBasedCompletedAnimation(){
        // go back to rest animation after performing animation
        if (countDown.isFinished()) {
            offabbTexture = null;
            countDown.reset();

            //if the model died or fled, do not restore rest animation
            if(hitTaken && model.isOutOfCombat()){
                hitTaken = false;
                handleOSUnit = true;
                if (model.isDead()) {
                    display(Data.AnimationId.DIE);
                } else  {
                    display(Data.AnimationId.FLEE);
                }
            }else if(handleOSUnit) {
                handleOSUnit = false;
                battlefield.removeUnit((int)y,(int)x);
            }else{
                display(Data.AnimationId.REST);
            }
        }
    }

    private void handleWalkAnimation(float dt){
        // handle walk animation
        if (remainingPath.size > 0) {
            dl = dt * ((pushed) ? SPEED_PUSHED: SPEED_WALK);
            if (x == remainingPath.get(0)[1]) {
                if ((y < remainingPath.get(0)[0] && y + dl >= remainingPath.get(0)[0])
                        || (y > remainingPath.get(0)[0] && y + dl <= remainingPath.get(0)[0])) {
                    y = remainingPath.get(0)[0];
                    updatePoint = true;
                } else if (y < remainingPath.get(0)[0]) {
                    y += dl;
                } else if (y > remainingPath.get(0)[0]) {
                    y -= dl;
                }
            }
            if (y == remainingPath.get(0)[0]) {
                if ((x < remainingPath.get(0)[1] && x + dl >= remainingPath.get(0)[1])
                        || (x > remainingPath.get(0)[1] && x + dl <= remainingPath.get(0)[1])) {
                    x = remainingPath.get(0)[1];
                    updatePoint = true;
                } else if (x < remainingPath.get(0)[1]) {
                    x += dl;
                } else if (x > remainingPath.get(0)[1])
                    x -= dl;
            }

            if (updatePoint) {
                updatePoint = false;
                remainingPath.removeIndex(0);

                if (remainingPath.size == 0) {
                    pushed = false;
                    executing = false;
                    display(Data.AnimationId.REST);
                } else {
                    model.setOrientation(Utils.getOrientationFromCoords(y, x, remainingPath.get(0)[0], remainingPath.get(0)[1]));

                }
            }
        }
    }


    @Override
    public void render(SpriteBatch batch) {
        batch.draw(unitTexture, x, y, 1, 1);
        batch.draw(weapontTexture, x, y, 0.25f, 0.25f);
        batch.draw(orientationTexture, x + 0.75f, y + 0.75f, 0.25f, 0.25f);
        if(shieldTexture != null) batch.draw(shieldTexture, x + 0.75f, y, 0.25f, 0.25f);
        if(mountedTexture != null) batch.draw(mountedTexture, x + 0.75f, y + 0.25f, 0.25f, 0.25f);
        if(offabbTexture != null) batch.draw(offabbTexture, x, y + 1,1, 0.25f);
    }



    // ---------------- TRIGGER ANIMATION METHODS ---------------------------------------





    @Override
    public void displayWalk(Array<int[]> path) {
        boolean validPath = true;
        int[] oldCoords = new int[]{(int)y, (int)x};

        /*
        CHECK PATH VALIDITY
        for the path to be explotable, it is required to collect tile coords of tiles which each of them are neighbours of its predecessor and follower in the list.
         */
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
            if(path.size > 0) {
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

                Data.Orientation or = Utils.getOrientationFromCoords(y,x,path.get(0)[0], path.get(0)[1]);
                model.setOrientation(or);
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.WALK, model.getArmy().isAlly());
                executing = true;

            }
        }



    }

    @Override
    public void displayTakeHit(int damageTaken) {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.TAKE_HIT, model.getArmy().isAlly());
        countDown.run();
        hitTaken = true;
    }


    @Override
    public void displayLevelup(int[] gainlvl) {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.LEVELUP, model.getArmy().isAlly());
        countDown.run();
    }

    @Override
    public void displayTreated(int[] oldHtpsAndMoral) {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.TREATED, model.getArmy().isAlly());
        countDown.run();
    }

    @Override
    public void displayPushed(Data.Orientation pushedTowards){
        model.setOrientation(pushedTowards);
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.PUSHED, model.getArmy().isAlly());
        pushed = true;
        switch (pushedTowards){
            case WEST: remainingPath.add(new int[]{(int)y, (int)x - 1}); break;
            case NORTH: remainingPath.add(new int[]{(int)y + 1, (int)x}); break;
            case SOUTH: remainingPath.add(new int[]{(int)y - 1, (int)x}); break;
            case EAST: remainingPath.add(new int[]{(int)y, (int)x + 1}); break;
        }

    }

    @Override
    public void display(Data.AnimationId id) {
        switch (id){
            case WALK:
            case SWITCH_POSITION:
            case LEVELUP:
            case TREATED:
            case TAKE_HIT:
            case PUSHED:
                try{
                    throw new IllegalArgumentException("the following animation order "+id.name()+" required calling a specific method as it demands extra parameters to be conducted successfully");
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
                break;
            case ATTACK:
            case PUSH:
            case HEAL:
            case STEAL:
            case BUILD:
            case GUARD:
            case PRAY:
            case DIE:
            case DODGE:
            case BACKSTABBED:
            case REST:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(id, model.getArmy().isAlly());
                countDown.run();
                break;
            case SWITCH_WEAPON:
                weapontTexture = TempoSprite2DPool.get().getWeaponSprite(model.getCurrentWeapon());
                break;
            case FLEE:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.WALK, model.getArmy().isAlly());
                orientationTexture = TempoSprite2DPool.get().getOrientationSprite(model.getOrientation().getOpposite());
                countDown.run();
                break;
            case GUARDED:
                //TODO:
                break;
            case FOCUSED_BLOW:
            case CRIPPLING_BLOW:
            case SWIRLING_BLOW:
            case SWIFT_BLOW:
            case HEAVY_BLOW:
            case CRUNCHING_BLOW:
            case WAR_CRY:
            case POISONOUS_ATTACK:
            case HARASS:
            case LINIENT_BLOW:
            case FURY:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.ATTACK, model.getArmy().isAlly());
                for(Data.OffensiveAbility ability : Data.OffensiveAbility.values()) {
                    if(ability.name().equals(id.name()))
                        offabbTexture = TempoSprite2DPool.get().getOffensiveAbbSprite(ability);
                }
                countDown.run();
                break;
                default:
        }
    }


    // ---------------- OTHERS METHODS ---------------------------------------------------



    @Override
    public boolean isExecuting() {
        return countDown.isRunning();
    }

    @Override
    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
    }


    @Override
    public void getNotification(Object data){
        if(data == null) {
            // update weapon
            weapontTexture = TempoSprite2DPool.get().getWeaponSprite(model.getCurrentWeapon());
            // update orientation
            orientationTexture = TempoSprite2DPool.get().getOrientationSprite(model.getOrientation());
            if (model.isUsingShield()) {
                // update shield wielding rendering
                shieldTexture = TempoSprite2DPool.get().getShieldSprite();
            }
            if (model.isHorseman()) {
                // update soldier mount rendering
                mountedTexture = TempoSprite2DPool.get().getMountedSprite();
            }

        }else if (data instanceof Integer){
            int damageTaken = (Integer)data;
            displayTakeHit(damageTaken);

        }else if(data instanceof int[]){
            int[] array = (int[])data;
            if(array.length == 2){
                int[] oldHpts = (int[])data;
                displayTreated(oldHpts);
            }else if(array.length == 10){
                int[] gainLvl = (int[])data;
                displayLevelup(gainLvl);
            }

        }else if(data instanceof Data.AnimationId){
            display((Data.AnimationId)data);

        }else if(data instanceof Data.Orientation){
            displayPushed((Data.Orientation)data);

        }
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }
}
