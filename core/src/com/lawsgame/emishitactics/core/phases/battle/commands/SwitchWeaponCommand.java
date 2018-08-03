package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.StandCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class SwitchWeaponCommand extends StandCommand{
    private int weaponIndex;
    private BattleUnitRenderer actorRenderer;

    public SwitchWeaponCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, int weaponIndex) {
        super(bfr, Data.ActionChoice.SWITCH_WEAPON, scheduler, true, false);
        this.weaponIndex = weaponIndex;
    }

    public SwitchWeaponCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        this(bfr, scheduler, -1);
    }

    @Override
    public void init() {
        super.init();
        weaponIndex = -1;
        actorRenderer = null;
    }

    @Override
    protected void execute() {
        IUnit actor = battlefield.getUnit(rowActor, colActor);
        actorRenderer = battlefieldRenderer.getUnitRenderer(actor);
        actor.switchWeapon(weaponIndex);
        scheduler.addTask(new AnimationScheduler.Task(actorRenderer, Data.AnimationId.SWITCH_WEAPON));
    }

    @Override
    public void undo() {
        if(battlefield.isTileOccupied(rowActor, colActor)){
            IUnit actor = battlefield.getUnit(rowActor, colActor);
            if(actorRenderer != null && actorRenderer.getModel() == actor) {
                actor.switchWeapon(weaponIndex);
                scheduler.addTask(new AnimationScheduler.Task(actorRenderer, Data.AnimationId.SWITCH_WEAPON));
            }
        }
    }

    public void setWeaponIndex(int index){
        this.weaponIndex = index;
    }
}
