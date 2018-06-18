package com.lawsgame.emishitactics.core.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.managers.TempoSprite2DPool;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.renderers.interfaces.UnitRenderer;
import com.lawsgame.emishitactics.engine.timers.CountDown;

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

    public TempoUnitRenderer(int row, int col, Unit model) {
        super(model);
        this.x = col;
        this.y = row;
        this.triggerAnimation(Props.AnimationId.REST);
        this.updateRenderer();
        System.out.println("HEY");
    }

    //------------------ GAME ELEMENT METHODS -------------------------------------------

    @Override
    public void update(float dt) {
        countDown.update(dt);

        if( countDown.isFinished()){
            triggerAnimation(Props.AnimationId.REST);
            offabbTexture = null;
            updateRenderer();
            countDown.reset();
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
        //TODO:
        this.x = path.peek()[0];
        this.y = path.peek()[1];
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
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Props.AnimationId.TAKE_HIT, model.getArmy().isAlly());
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



}
