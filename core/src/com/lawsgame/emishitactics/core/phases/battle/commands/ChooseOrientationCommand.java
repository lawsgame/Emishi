package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.StandCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class ChooseOrientationCommand extends StandCommand {
    protected Data.Orientation oldOrientation;
    protected Data.Orientation newOrientation;
    protected BattleUnitRenderer actorRenderer;

    public ChooseOrientationCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        this(bfr, scheduler, Data.Orientation.NORTH);
    }

    public ChooseOrientationCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Data.Orientation newOrientation) {
        super(bfr, ActionChoice.CHOOSE_ORIENTATION, scheduler, true, true, true);
        this.newOrientation = newOrientation;
    }

    @Override
    public void init() {
        super.init();
        actorRenderer = null;
    }

    @Override
    protected void execute() {
        IUnit actor = battlefield.getUnit(rowActor, colActor);
        actorRenderer = battlefieldRenderer.getUnitRenderer(actor);

        // register old model state
        oldOrientation = actor.getOrientation();

        // update model
        actor.setOrientation(newOrientation);

        // push render state
        scheduler.addTask(new Task(battlefieldRenderer.getUnitRenderer(actor), newOrientation));

    }



    @Override
    public void undo() {
        if(battlefield.isTileOccupied(rowActor, colActor)) {
            IUnit actor = battlefield.getUnit(rowActor, colActor);
            if(actorRenderer.getModel() == actor) {
                actor.setOrientation(oldOrientation);
                scheduler.addTask(new Task(battlefieldRenderer.getUnitRenderer(actor), oldOrientation));
            }
        }
    }

    public void setOrientation(Data.Orientation orientation){
        newOrientation = orientation;
    }
}
