package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class ChangeTactic extends SelfInflitedCommand {
    private Data.BBMode mode;
    private Data.BBMode previousMode;

    public ChangeTactic(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, Data.BBMode mode) {
        super(bfr, Data.ActionChoice.CHANGE_TACTIC  , scheduler, playerInventory, true);
        this.mode = mode;
    }

    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator) && initiator.isWarChief() && !initiator.getArmy().isSquadOversized(initiator);
    }

    @Override
    protected void execute() {
        // register previous mode
        previousMode = getInitiator().getBanner().getMode();
        getInitiator().getBanner().setMode(mode);
        scheduleRenderTask(new StandardTask(bfr.getUnitRenderer(getInitiator()), Data.AnimId.CHANGE_STRATEGY));

    }

    @Override
    protected void unexecute() {
        super.unexecute();
        if(previousMode != getInitiator().getBanner().getMode()){
            getInitiator().getBanner().setMode(previousMode);
            scheduleRenderTask(new StandardTask(bfr.getUnitRenderer(getInitiator()), Data.AnimId.CHANGE_STRATEGY));
        }
    }

    public Data.BBMode getMode() {
        return mode;
    }
}
