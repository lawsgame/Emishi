package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class SwitchWeaponCommand extends SelfInflitedCommand {
    private int weaponIndex;
    private BattleUnitRenderer actorRenderer;

    public SwitchWeaponCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, int weaponIndex) {
        super(bfr, ActionChoice.SWITCH_WEAPON, scheduler, true);
        this.weaponIndex = weaponIndex;
    }

    @Override
    public void initiate() {
        actorRenderer = battlefieldRenderer.getUnitRenderer(battlefield.getUnit(rowActor, colActor));
    }

    @Override
    protected void execute() {
        // update model
        IUnit actor = battlefield.getUnit(rowActor, colActor);
        actor.switchWeapon(weaponIndex);

        // push render task
        scheduleRenderTask(new StandardTask(actorRenderer, Data.AnimationId.SWITCH_WEAPON));
    }

    @Override
    public void undo() {
        if(battlefield.isTileOccupied(rowActor, colActor)){
            IUnit actor = battlefield.getUnit(rowActor, colActor);
            if(actorRenderer != null && actorRenderer.getModel() == actor) {
                if(actor.switchWeapon(weaponIndex)) {
                    scheduleRenderTask(new StandardTask(actorRenderer, Data.AnimationId.SWITCH_WEAPON));
                }
            }
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
