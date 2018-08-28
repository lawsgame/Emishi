package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

import static com.lawsgame.emishitactics.core.models.Data.ActionChoice.END_TURN;

public class EndTurnCommand extends SelfInflitedCommand {



    public EndTurnCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, END_TURN, scheduler, true);
    }

    @Override
    public boolean canbePerformedBy(IUnit actor) {
        return true;
    }

    @Override
    protected void initiate() {

    }

    @Override
    protected void execute() {

        getActor().setMoved(true);
        getActor().setActed(true);

        final BattleUnitRenderer actorRenderer = battlefieldRenderer.getUnitRenderer(getActor());
        StandardTask doneTask = new StandardTask(actorRenderer, new Notification.Done(true));
        this.scheduleRenderTask(doneTask);

    }



}
