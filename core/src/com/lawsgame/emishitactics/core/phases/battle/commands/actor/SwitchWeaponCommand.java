package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class SwitchWeaponCommand extends SelfInflitedCommand {
    private final int weaponIndex;

    public SwitchWeaponCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, int weaponIndex) {
        super(bfr, ActionChoice.SWITCH_WEAPON, scheduler, playerInventory);
        this.weaponIndex = weaponIndex;
        setRegisterAction(false);
    }

    @Override
    protected void execute() {
        // update model
        getInitiator().switchWeapon(weaponIndex);
        // push render task
        scheduleRenderTask(new StandardTask(bfr.getUnitRenderer(getInitiator()), Data.AnimId.SWITCH_WEAPON));
    }

    @Override
    public void unexecute() {
        if(getInitiator().switchWeapon(weaponIndex)) {
            scheduleRenderTask(new StandardTask(bfr.getUnitRenderer(getInitiator()), Data.AnimId.SWITCH_WEAPON));
        }
        handleEvents(this);
    }



    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator)
                && weaponIndex < initiator.getWeapons().size
                && initiator.getWeapons().size > 1;
    }

    //------------ GETTERS -------------------------------------

    public int getWeaponIndex() {
        return weaponIndex;
    }

    public Weapon getNewWeapon(){
        Weapon nw = null;
        if(getInitiator() != null){
            nw = getInitiator().getWeapon(weaponIndex);
        }
        return nw;
    }

    @Override
    public String getName(I18NBundle bundle) {

        String str = "";
        if(bfr.getModel().isTileOccupied(rowActor, colActor)){
            Weapon weapon = bfr.getModel().getUnit(rowActor, colActor).getWeapon(weaponIndex);
            if(weapon != null){
                str = weapon.getName(bundle);
            }
        }
        return (str != "") ? str :super.getName(bundle);
    }
}
