package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

import static com.lawsgame.emishitactics.core.models.Data.ActionChoice.END_TURN;

public class EndUnitTurnCommand extends SelfInflitedCommand {



    public EndUnitTurnCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, END_TURN, scheduler, playerInventory, true);
    }

    @Override
    public boolean canbePerformedBy(IUnit actor) {
        return true;
    }

    @Override
    protected void execute() {

        getInitiator().setMoved(true);
        getInitiator().setActed(true);

        StandardTask doneTask = new StandardTask(bfr.getUnitRenderer(getInitiator()), Notification.Done.get(true));
        this.scheduleRenderTask(doneTask);

    }



}
