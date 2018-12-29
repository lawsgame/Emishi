package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class ChooseOrientationCommand extends SelfInflitedCommand {
    protected Orientation oldOrientation;
    protected Orientation newOrientation;

    public ChooseOrientationCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, Orientation newOrientation) {
        super(bfr, ActionChoice.CHOOSE_ORIENTATION, scheduler, playerInventory);
        setFree(true);
        setOrientation(newOrientation);
    }

    public void setOrientation(Orientation newOrientation){
        this.newOrientation = newOrientation;
    }

    @Override
    protected void execute() {

        // register old model state
        oldOrientation = getInitiator().getOrientation();

        // update model
        getInitiator().setOrientation(newOrientation);

        // push render state
        scheduleRenderTask(new StandardTask(bfr.getUnitRenderer(getInitiator()), newOrientation));
    }

    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator) && initiator.getOrientation() != newOrientation;
    }

    @Override
    public void unexecute() {
        if(oldOrientation != null && bfr.getModel().isTileOccupied(rowActor, colActor)) {
            Unit unit = bfr.getModel().getUnit(rowActor, colActor);
            if(getInitiator() == unit) {
                unit.setOrientation(oldOrientation);
                scheduleRenderTask(new StandardTask(bfr.getUnitRenderer(unit), oldOrientation));
            }
        }
    }

    @Override
    public String getName(I18NBundle bundle) {
        return (newOrientation != null) ? bundle.get(newOrientation.name()) : super.getName(bundle);
    }
}
