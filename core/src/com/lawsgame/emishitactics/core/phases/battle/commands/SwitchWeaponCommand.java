package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.StandCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class SwitchWeaponCommand extends StandCommand{
    private int weaponIndex;
    private BattleUnitRenderer actorRenderer;

    public SwitchWeaponCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, int weaponIndex) {
        super(bfr, ActionChoice.SWITCH_WEAPON, scheduler, true, true, true);
        this.weaponIndex = weaponIndex;
    }

    public SwitchWeaponCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        this(bfr, scheduler, -1);
    }

    @Override
    public void init() {
        super.init();
        actorRenderer = null;
    }

    @Override
    protected void execute() {
        // update model
        IUnit actor = battlefield.getUnit(rowActor, colActor);
        actorRenderer = battlefieldRenderer.getUnitRenderer(actor);
        actor.switchWeapon(weaponIndex);

        // push render task
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

    @Override
    public boolean canbePerformedBy(IUnit actor) {
        return super.canbePerformedBy(actor) && weaponIndex < actor.getWeapons().size;
    }

    //------------ GETTERS -------------------------------------

    public int getWeaponIndex() {
        return weaponIndex;
    }
}
