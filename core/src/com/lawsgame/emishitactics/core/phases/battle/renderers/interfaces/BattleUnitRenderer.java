package com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.engine.rendering.Renderer;
import com.lawsgame.emishitactics.engine.GameElement;

public abstract class BattleUnitRenderer extends Renderer<Unit> implements GameElement {

    public BattleUnitRenderer(Unit model) {
        super(model);
    }

    public abstract void setX(float x);
    public abstract void setY(float y);
    public abstract void setTargeted(boolean targeted);
    public abstract void setVisible(boolean visible);

    public abstract void displayWalk(Array<int[]> path);
    public abstract void displayTakeHit(boolean moralOnly, int damageTaken, boolean critical);
    public abstract void displayLevelup(int[] gainlvl);
    public abstract void displayTreated(int[] oldHtpsAndMoral);
    public abstract void displayPushed(Data.Orientation pushedTowards);
    public abstract void display(Data.AnimationId id);
}
