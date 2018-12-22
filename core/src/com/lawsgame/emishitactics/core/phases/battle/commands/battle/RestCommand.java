package com.lawsgame.emishitactics.core.phases.battle.commands.battle;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class RestCommand extends BattleCommand {
    private int rowTarget;
    private int colTarget;
    private int physicalHealPower;
    private int moralHealPower;

    public RestCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, scheduler, playerInventory);
        this.rowTarget = -1;
        this.colTarget = -1;
        this.moralHealPower = 0;
        this.physicalHealPower = 0;
    }

    public void setHealPower(int moralHealPower, int physicalHealPower) {
        this.moralHealPower = moralHealPower;
        this.physicalHealPower = physicalHealPower;
    }

    public void setTarget(int rowTarget, int colTarget){
        this.rowTarget = rowTarget;
        this.colTarget = colTarget;
    }

    @Override
    protected void execute() {
        Unit healed = bfr.getModel().getUnit(rowTarget, colTarget);
        // update model
        healed.improveCondition(moralHealPower, physicalHealPower);
        // render
        scheduleRenderTask(new StandardTask(bfr.getUnitRenderer(healed), Data.AnimId.TREATED));
        // handle post events
        handleEvents(this, rowTarget, colTarget);
    }

    @Override
    protected void unexecute() {
        // do nothing
    }

    @Override
    public boolean isApplicable() {
        return bfr.getModel().isTileOccupied(rowTarget, colTarget) && bfr.getModel().getUnit(rowTarget, colTarget).isWounded(moralHealPower > 0, physicalHealPower > 0);
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}
