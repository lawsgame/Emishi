package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic.HitCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AttackCommand extends ActorCommand {

    protected HitCommand initialBlow;
    protected HitCommand relationBlow;

    public AttackCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, Data.ActionChoice.ATTACK, scheduler, playerInventory, false);

        this.initialBlow = new HitCommand(bfr, ActionChoice.ATTACK, scheduler, playerInventory);
        this.initialBlow.setDecoupled(true);
        this.initialBlow.setFree(true);
        this.relationBlow = new HitCommand(bfr, ActionChoice.ATTACK, scheduler, playerInventory);
        this.relationBlow.setRetaliation(true);
        this.relationBlow.setDecoupled(true);
        this.relationBlow.setFree(true);

    }

    @Override
    protected void execute() {

    }



    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        initialBlow.setInitiator(rowActor0, colActor0);
        if(initialBlow.isInitiatorValid()) {
            relationBlow.setInitiator(rowTarget0, colTarget0);
            relationBlow.setTarget(rowActor0, colActor0);
            initialBlow.setTarget(rowTarget0, colTarget0);
            return initialBlow.isTargetValid(rowActor0, colActor0, rowTarget0, colTarget0);
        }
        return false;
    }

    @Override
    public Array<int[]> getTargetsAtRange(int row, int col, IUnit actor) {
        return initialBlow.getTargetsAtRange(row, col, actor);
    }

    @Override
    public Array<int[]> getTargetsFromImpactArea(int rowActor0, int colActor0, int rowTarget0, int colTarget0, IUnit actor) {
        return initialBlow.getTargetsFromImpactArea(rowActor0, colActor0, rowTarget0,colTarget0, actor);
    }

    // -------------------- COMODITY BATTLE PANEL METHODS ------------------



    public int getHitRate(boolean retaliation){
        return 0;
    }

    public int getDealtDamage(boolean retaliation){
        return 0;
    }

    public int getLootRate(boolean retaliation){
        int lootRate = 0;
        if(!retaliation){
            lootRate = Formulas.getLootRate(getInitiator());
        }else if(relationBlow.isApplicable()){
            lootRate = Formulas.getLootRate(getTarget());
        }
        return lootRate;
    }

    public IUnit getTargetDefender(){
        return initialBlow.getTarget();
    }


    public IUnit getInitiatorDefender() {
        return relationBlow.getInitiator();
    }
}
