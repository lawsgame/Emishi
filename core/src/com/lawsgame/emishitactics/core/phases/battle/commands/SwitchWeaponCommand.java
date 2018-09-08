package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class SwitchWeaponCommand extends SelfInflitedCommand {
    private final int weaponIndex;

    public SwitchWeaponCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, int weaponIndex) {
        super(bfr, ActionChoice.SWITCH_WEAPON, scheduler, true);
        this.weaponIndex = weaponIndex;
    }

    @Override
    protected void execute() {
        // update model
        getActor().switchWeapon(weaponIndex);

        // push render task
        scheduleRenderTask(new StandardTask(battlefieldRenderer.getUnitRenderer(getActor()), Data.AnimId.SWITCH_WEAPON));
    }

    @Override
    public void unexecute() {
        if(getActor().switchWeapon(weaponIndex)) {
            scheduleRenderTask(new StandardTask(battlefieldRenderer.getUnitRenderer(getActor()), Data.AnimId.SWITCH_WEAPON));
        }


    }

    @Override
    public boolean canbePerformedBy(IUnit actor) {
        return super.canbePerformedBy(actor) && weaponIndex < actor.getWeapons().size;
    }

    //------------ GETTERS -------------------------------------

    public int getWeaponIndex() {
        return weaponIndex;
    }

    @Override
    public String getName(I18NBundle bundle) {

        String str = "";
        if(battlefield.isTileOccupied(rowActor, colActor)){
            Weapon weapon = battlefield.getUnit(rowActor, colActor).getWeapon(weaponIndex);
            if(weapon != null){
                str = weapon.getName(bundle);
            }
        }
        return (str != "") ? str :super.getName(bundle);
    }
}
