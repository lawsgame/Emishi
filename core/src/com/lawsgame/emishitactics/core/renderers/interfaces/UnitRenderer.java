package com.lawsgame.emishitactics.core.renderers.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.engine.GameElement;


/**
 * there is two ways to update the rendering of a unit
 *  - An automatized way through the use of the Observer pattern when the model is updated
 *  - A manual way by calling triggerAnimation()
 */
public abstract class UnitRenderer extends Renderer<Unit> implements GameElement {

    public UnitRenderer(Unit model) {
        super(model);
        this.model.getBanner().attach(this);
        this.model.getArmy().attach(this);
    }

    public abstract boolean isProceeding();

    public abstract void setTargeted(boolean targeted);


    @Override
    public void getNotification(Object data) {
        if(data instanceof Props.Weapon){
            triggerSwitchWeaponAnimation();
        }else if( data instanceof Integer){
            int damageTaken = (Integer)data;
            triggerTakeHitAnimation(damageTaken);
        }else if (data instanceof  int[]){
            int[] array = (int[])data;
            if(array.length == 2) {
                triggerHealedAnimation(array);
            }else if(array.length == 10) {
                triggerLevelUpAnimation(array);
            }
        }else if (data instanceof Boolean){
            boolean died = (Boolean)data;
            if(died){
                triggerDieAnimation();
            }else{
                triggerFleeAnimation();
            }
        }else{
            updateRenderer();
        }
    }

    public abstract void triggerMoveAnimation(Array<int[]> path);
    protected abstract void triggerSwitchWeaponAnimation();
    protected abstract void triggerTakeHitAnimation(int damageTaken);
    protected abstract void triggerFleeAnimation();
    protected abstract void triggerDieAnimation();
    protected abstract void triggerLevelUpAnimation(int[] gainlvl);
    protected abstract void triggerHealedAnimation(int[] oldHtpsAndMoral);
    protected abstract void updateRenderer();

    public abstract void triggerAnimation(Props.AnimationId id);
}
