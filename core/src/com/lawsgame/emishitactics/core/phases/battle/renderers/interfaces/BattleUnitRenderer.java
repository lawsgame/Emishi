package com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.renderers.UnitRenderer;

public abstract class BattleUnitRenderer extends UnitRenderer {

    public BattleUnitRenderer(Unit model) {
        super(model);
    }

    public abstract boolean isProceeding();
    public abstract void setTargeted(boolean targeted);

    public abstract void displayWalk(Array<int[]> path);
    public abstract void displayTakeHit(int damageTaken);
    public abstract void displayLevelup(int[] gainlvl);
    public abstract void displayTreated(int[] oldHtpsAndMoral);
    public abstract void displayPushed(Data.Orientation pushedTowards);

    public abstract void triggerAnimation(Data.AnimationId id);
}
