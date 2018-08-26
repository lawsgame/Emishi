package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;

import static com.lawsgame.emishitactics.core.models.ActionChoice.END_TURN;

public class EndTurnCommand extends SelfInflitedCommand {



    public EndTurnCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, END_TURN, scheduler, false, true, true);
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
