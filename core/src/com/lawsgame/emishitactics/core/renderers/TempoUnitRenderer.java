package com.lawsgame.emishitactics.core.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.renderers.interfaces.UnitRenderer;
import com.lawsgame.emishitactics.engine.timers.CountDown;

import static com.lawsgame.emishitactics.core.constants.Props.SPEED_WALK;

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

    public TempoUnitRenderer(int row, int col, Unit model) {
        super(model);
        this.x = col;
        this.y = row;
        this.triggerAnimation(Props.AnimationId.REST);
        this.updateRenderer();
    }

    //------------------ GAME ELEMENT METHODS -------------------------------------------

    @Override
    public void update(float dt) {
        countDown.update(dt);

        // go back to rest animation after performing animation
        if (countDown.isFinished()) {
            triggerAnimation(Props.AnimationId.REST);
            offabbTexture = null;
            updateRenderer();
            countDown.reset();
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
            remove unnecesary entries
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
    protected void triggerSwitchWeaponAnimation() {
        weapontTexture = TempoSprite2DPool.get().getWeaponSprite(model.getCurrentWeapon());
    }

    @Override
    protected void triggerTakeHitAnimation(int damageTaken) {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Props.AnimationId.TAKE_HIT, model.getArmy().isAlly());
        countDown.run();
    }

    @Override
    protected void triggerFleeAnimation() {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Props.AnimationId.WALK, model.getArmy().isAlly());
        orientationTexture = TempoSprite2DPool.get().getOrientationSprite(model.getCurrentOrientation().getOpposite());
        countDown.run();
    }

    @Override
    protected void triggerDieAnimation() {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Props.AnimationId.DIE, model.getArmy().isAlly());
    }

    @Override
    protected void triggerLevelUpAnimation(int[] gainlvl) {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Props.AnimationId.HEAL, model.getArmy().isAlly());
        countDown.run();
    }

    @Override
    protected void triggerHealedAnimation(int[] oldHtpsAndMoral) {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Props.AnimationId.HEAL, model.getArmy().isAlly());
    }

    @Override
    public void triggerAnimation(Props.AnimationId id) {
        switch (id){
            case WALK:
            case SWITCH_POSITION:
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
            case BACKSTABBED:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(id, model.getArmy().isAlly());
                countDown.run();
                break;
            case SWITCH_WEAPON:
                triggerSwitchWeaponAnimation();
                break;
            case PRAY:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Props.AnimationId.HEAL, model.getArmy().isAlly());
                countDown.run();
                break;
            case LEVEL_UP:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Props.AnimationId.HEAL, model.getArmy().isAlly());
                countDown.run();
                break;
            case HEALED:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Props.AnimationId.HEAL, model.getArmy().isAlly());
                countDown.run();
                break;
            case REST:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(model.getStance(), model.getArmy().isAlly(), model.isDone());
                break;
            case PARRIED_ATTACK:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Props.AnimationId.ATTACK, model.getArmy().isAlly());
                countDown.run();
                break;
            case TAKE_HIT:
                triggerTakeHitAnimation(99);
                break;
            case FLEE:
                triggerFleeAnimation();
                break;
            case DIE:
                triggerDieAnimation();
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
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Props.AnimationId.ATTACK, model.getArmy().isAlly());
                for(Props.OffensiveAbility ability : Props.OffensiveAbility.values()) {
                    if(ability.name().equals(id.name()))
                        offabbTexture = TempoSprite2DPool.get().getOffensiveAbbSprite(ability);
                }
                countDown.run();
                break;
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
    public void updateRenderer(){

        // update weapon
        weapontTexture = TempoSprite2DPool.get().getWeaponSprite(model.getCurrentWeapon());

        // update orientation
        orientationTexture = TempoSprite2DPool.get().getOrientationSprite(model.getCurrentOrientation());
        if(model.isUsingShield()){
            // update shield wielding rendering
            shieldTexture = TempoSprite2DPool.get().getShieldSprite();
        }
        if(model.isHorseman()){
            // update soldier mount rendering
            mountedTexture = TempoSprite2DPool.get().getMountedSprite();
        }
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
