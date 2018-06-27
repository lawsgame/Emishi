package com.lawsgame.emishitactics.core.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.TempoSprite2DPool;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.renderers.interfaces.UnitRenderer;
import com.lawsgame.emishitactics.engine.timers.CountDown;

import static com.lawsgame.emishitactics.core.constants.Data.SPEED_WALK;

public class TempoUnitRenderer extends UnitRenderer {
    protected float x;
    protected float y;

    private TextureRegion unitTexture;
    private TextureRegion weapontTexture;
    private TextureRegion shieldTexture;
    private TextureRegion mountedTexture;
    private TextureRegion orientationTexture;
    private TextureRegion offabbTexture = null;

    private boolean targeted = false;
    private boolean proceeding = false;
    private CountDown countDown = new CountDown(2f);

    //walk attributes
    private float dl;
    private boolean updatePoint = false;
    private Array<int[]> remainingPath = new Array<int[]>();

    // hit attributes
    boolean hitTaken = false;

    public TempoUnitRenderer(int row, int col, Unit model) {
        super(model);
        this.x = col;
        this.y = row;
        triggerAnimation(Data.AnimationId.REST);
        getNotification(null);
    }

    //------------------ GAME ELEMENT METHODS -------------------------------------------

    @Override
    public void update(float dt) {
        countDown.update(dt);

        // go back to rest animation after performing animation
        if (countDown.isFinished()) {
            countDown.reset();

            // trigger the die/flee anination after the take_hit one if required
            if(hitTaken && model.isOutOfCombat()){
                hitTaken = false;
                if(model.isDead()){
                    triggerAnimation(Data.AnimationId.DIE);
                }else{
                    triggerAnimation(Data.AnimationId.FLEE);
                }
            } else {
                proceeding = false;

                //if the model died or fled, do not restore rest animation
                if(!model.isOutOfCombat()) {
                    triggerAnimation(Data.AnimationId.REST);
                    offabbTexture = null;
                }
            }
        }

        // handle walk animation
        if (remainingPath.size > 0) {
            dl = dt * SPEED_WALK;
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
                    proceeding = false;
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
    public void triggerMoveAnimation(Array<int[]> path) {
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
            if(path.size > 1) {
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
            }


            proceeding = true;
        }



    }

    @Override
    public void triggerTakeHitAnimation(int damageTaken) {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.TAKE_HIT, model.getArmy().isAlly());
        hitTaken = true;
        proceeding = true;
        countDown.run();
    }


    @Override
    public void triggerLevelUpAnimation(int[] gainlvl) {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.HEAL, model.getArmy().isAlly());
        proceeding = true;
        countDown.run();
    }

    @Override
    public void triggerHealedAnimation(int[] oldHtpsAndMoral) {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.HEAL, model.getArmy().isAlly());
    }

    @Override
    public void triggerAnimation(Data.AnimationId id) {
        switch (id){
            case WALK:
            case SWITCH_POSITION:
            case LEVEL_UP:
            case HEALED:
            case TAKE_HIT:
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
            case DODGE:
            case BLOCK:
            case PARRY:
            case PRAY:
            case DIE:
            case BACKSTABBED:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(id, model.getArmy().isAlly());
                proceeding = true;
                countDown.run();
                break;
            case SWITCH_WEAPON:
                weapontTexture = TempoSprite2DPool.get().getWeaponSprite(model.getCurrentWeapon());
                break;
            case REST:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(model.getStance(), model.getArmy().isAlly(), model.isDone());
                break;
            case PARRIED_ATTACK:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.ATTACK, model.getArmy().isAlly());
                proceeding = true;
                countDown.run();
                break;
            case FLEE:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.WALK, model.getArmy().isAlly());
                orientationTexture = TempoSprite2DPool.get().getOrientationSprite(model.getCurrentOrientation().getOpposite());
                proceeding = true;
                countDown.run();
                break;
            case FOCUSED_BLOW:
            case CRIPPLING_BLOW:
            case SWIRLING_BLOW:
            case SWIFT_BLOW:
            case HEAVY_BLOW:
            case CRUNCHING_BLOW:
            case WAR_CRY:
            case POISONOUS_ATTACK:
            case GUARD_BREAK:
            case LINIENT_BLOW:
            case FURY:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.ATTACK, model.getArmy().isAlly());
                for(Data.OffensiveAbility ability : Data.OffensiveAbility.values()) {
                    if(ability.name().equals(id.name()))
                        offabbTexture = TempoSprite2DPool.get().getOffensiveAbbSprite(ability);
                }
                proceeding = true;
                countDown.run();
                break;
                default:
        }
    }


    // ---------------- OTHERS METHODS ---------------------------------------------------



    @Override
    public boolean isProceeding() {
        return proceeding;
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
            orientationTexture = TempoSprite2DPool.get().getOrientationSprite(model.getCurrentOrientation());
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
            triggerTakeHitAnimation(damageTaken);
        }else if(data instanceof int[]){
            int[] gainLvl = (int[])data;
        }
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
