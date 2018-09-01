package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class ChooseOrientationCommand extends SelfInflitedCommand {
    protected Data.Orientation oldOrientation;
    protected Data.Orientation newOrientation;
    protected BattleUnitRenderer actorRenderer;

    public ChooseOrientationCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Data.Orientation newOrientation) {
        super(bfr, ActionChoice.CHOOSE_ORIENTATION, scheduler, true);
        this.newOrientation = newOrientation;
    }

    @Override
    protected void execute() {
        actorRenderer = battlefieldRenderer.getUnitRenderer(getActor());

        // register old model state
        oldOrientation = getActor().getOrientation();

        // update model
        getActor().setOrientation(newOrientation);

        // push render state
        scheduleRenderTask(new StandardTask(battlefieldRenderer.getUnitRenderer(getActor()), newOrientation));

    }

    @Override
    public void unexecute() {
        if(oldOrientation != null && battlefield.isTileOccupied(rowActor, colActor)) {
            IUnit actor = battlefield.getUnit(rowActor, colActor);
            if(actorRenderer.getModel() == actor) {
                actor.setOrientation(oldOrientation);
                scheduleRenderTask(new StandardTask(battlefieldRenderer.getUnitRenderer(actor), oldOrientation));
            }
        }
    }

    @Override
    public String getName(I18NBundle bundle) {
        return (newOrientation != null) ? bundle.get(newOrientation.name()) : super.getName(bundle);
    }
}
