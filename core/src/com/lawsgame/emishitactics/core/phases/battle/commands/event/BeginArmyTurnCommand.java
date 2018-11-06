package com.lawsgame.emishitactics.core.phases.battle.commands.event;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Notification.BeginArmyTurn;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.EventCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class BeginArmyTurnCommand extends BattleCommand {
    protected IArmy army;

    public BeginArmyTurnCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, IArmy army) {
        super(bfr, scheduler);
        this.army = army;
    }


    @Override
    public boolean isApplicable() {
        return true;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    protected void execute() {
        if (army != null) {
            army.replenishMoral(true);
            army.updateActionPoints();
            if (army.isPlayerControlled())
                bfr.getModel().incrementTurn();

            Array<Task> tasks;
            BeginArmyTurn beginArmyTurn = new BeginArmyTurn(army);
            if(bfr.getModel().isAnyEventTriggerable(beginArmyTurn)){
                tasks = bfr.getModel().performEvents(beginArmyTurn);
                scheduleMultipleRenderTasks(tasks);
            }
        }
    }

    @Override
    protected void unexecute() {

    }

}
