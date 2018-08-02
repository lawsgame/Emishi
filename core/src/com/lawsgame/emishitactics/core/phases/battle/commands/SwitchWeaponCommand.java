package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.StandCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class SwitchWeaponCommand extends StandCommand{
    private int weaponIndex;

    public SwitchWeaponCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.SWITCH_WEAPON, scheduler, true, false);
    }

    @Override
    public void init() {
        super.init();
        weaponIndex = -1;
    }

    @Override
    protected void execute() {
        IUnit actor = battlefield.getUnit(rowActor, colActor);
        BattleUnitRenderer bur = battlefieldRenderer.getUnitRenderer(actor);
        actor.switchWeapon(weaponIndex);
        scheduler.addTask(new AnimationScheduler.Task(bur, Data.AnimationId.SWITCH_WEAPON));
    }

    public void setWeaponIndex(int index){
        this.weaponIndex = index;
    }
}
