package com.lawsgame.emishitactics.core.states.commands;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.command.Command;

public abstract class BattleCommand implements Command, GameUpdatableEntity{
    protected Battlefield battlefield;
    protected Props.ActionChoice actionChoice;
    protected Unit actor;

    public BattleCommand(Battlefield bf, Props.ActionChoice choice){
        this.battlefield = bf;
        this.actionChoice = choice;
        this.actor = null;
    }

    public abstract boolean isUndoable();                              // can be deleted afterwards
    public abstract boolean isFree();                                  // does not set Unit.acted as true
    public abstract boolean isEndTurnCommand();                        // is only callable when the unit turn is ending
    public abstract void display(SpriteBatch batch);                   // render the infos regarding the action to perform
    public abstract Array<int[]> getAvailableTargets();
    public abstract void setTarget(int rowTarget, int colTarget);

    protected boolean canBeExecuted(Unit givenActor){
        //TODO:



        return false;
    }


    //------------------ GETTERS & SETTERS ---------------------------

    public Props.ActionChoice getActionChoice() {
        return actionChoice;
    }

    public void setBattlefield(Battlefield battlefield){
        setBattlefield(battlefield);
    }

    public Battlefield getBattlefield() {
        return battlefield;
    }

    public Unit getActor() {
        return actor;
    }

    public void setActor(Unit actor) {
        this.actor = actor;
    }

    private class BCWrongSetCallingException extends Exception {
        BCWrongSetCallingException(String msg){
            super(msg);
        }
    }
}
