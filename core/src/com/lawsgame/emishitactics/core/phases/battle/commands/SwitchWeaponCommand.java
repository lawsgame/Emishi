package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.StandCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
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
        actorRenderer = battlefieldRenderer.getUnitRenderer(battlefield.getUnit(rowActor, colActor));
    }

    @Override
    protected void execute() {
        // update model
        IUnit actor = battlefield.getUnit(rowActor, colActor);
        actor.switchWeapon(weaponIndex);

        // push render task
        scheduler.addTask(new StandardTask(actorRenderer, Data.AnimationId.SWITCH_WEAPON));
    }

    @Override
    public void undo() {
        if(battlefield.isTileOccupied(rowActor, colActor)){
            IUnit actor = battlefield.getUnit(rowActor, colActor);
            if(actorRenderer != null && actorRenderer.getModel() == actor) {
                actor.switchWeapon(weaponIndex);
                scheduler.addTask(new StandardTask(actorRenderer, Data.AnimationId.SWITCH_WEAPON));
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

    @Override
    public String getName(I18NBundle bundle) {
        String str = null;
        if(battlefield.isTileOccupied(rowActor, colActor)){
            Weapon weapon = battlefield.getUnit(rowActor, colActor).getWeapon(weaponIndex);
            if(weapon != null){
                str = weapon.getName(bundle);
            }
        }
        return (str != null) ? str :super.getName(bundle);
    }
}
