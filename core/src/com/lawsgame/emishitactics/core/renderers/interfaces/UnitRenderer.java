package com.lawsgame.emishitactics.core.renderers.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.engine.GameElement;


/**
 * there is two ways to update the rendering of a unit
 *  - An automatized way through the use of the Observer pattern when the battlefield is updated
 *  - A manual way by calling triggerAnimation()
 */
public abstract class UnitRenderer extends Renderer<Unit> implements GameElement {

    public UnitRenderer(Unit model) {
        super(model);
        this.model.getBanner().attach(this);
        this.model.getArmy().attach(this);
    }

    public abstract void resumeStance();

    public abstract boolean isProceeding();
    public abstract void setTargeted(boolean targeted);

    public abstract void triggerMoveAnimation(Array<int[]> path);
    public abstract void triggerTakeHitAnimation(int damageTaken);
    public abstract void triggerLevelUpAnimation(int[] gainlvl);
    public abstract void triggerHealedAnimation(int[] oldHtpsAndMoral);

    public abstract void triggerAnimation(Data.AnimationId id);
}
