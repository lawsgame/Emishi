package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class ChooseOrientationCommand extends SelfInflitedCommand {
    protected Data.Orientation oldOrientation;
    protected Data.Orientation newOrientation;
    protected BattleUnitRenderer actorRenderer;

    public ChooseOrientationCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, Data.Orientation newOrientation) {
        super(bfr, ActionChoice.CHOOSE_ORIENTATION, scheduler, playerInventory, true);
        this.newOrientation = newOrientation;
    }

    @Override
    protected void execute() {
        actorRenderer = bfr.getUnitRenderer(getInitiator());

        // register old model state
        oldOrientation = getInitiator().getOrientation();

        // update model
        getInitiator().setOrientation(newOrientation);

        // push render state
        scheduleRenderTask(new StandardTask(bfr.getUnitRenderer(getInitiator()), newOrientation));
    }

    @Override
    public void unexecute() {
        if(oldOrientation != null && battlefield.isTileOccupied(rowActor, colActor)) {
            IUnit actor = battlefield.getUnit(rowActor, colActor);
            if(actorRenderer.getModel() == actor) {
                actor.setOrientation(oldOrientation);
                scheduleRenderTask(new StandardTask(bfr.getUnitRenderer(actor), oldOrientation));
            }
        }
    }

    @Override
    public String getName(I18NBundle bundle) {
        return (newOrientation != null) ? bundle.get(newOrientation.name()) : super.getName(bundle);
    }
}
