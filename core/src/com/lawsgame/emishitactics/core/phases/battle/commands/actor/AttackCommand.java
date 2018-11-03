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
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AttackCommand extends ActorCommand {

    protected HitCommand initialBlow;
    protected Array<HitCommand> retalationBlows;

    public AttackCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, Data.ActionChoice.ATTACK, scheduler, playerInventory, false);

        this.initialBlow = new HitCommand(bfr, ActionChoice.ATTACK, scheduler, playerInventory);
        this.initialBlow.setDecoupled(true);
        this.initialBlow.setFree(true);
        this.retalationBlows = new Array<HitCommand>();

    }

    @Override
    protected void execute() {


        if(initialBlow.apply())
            scheduleMultipleRenderTasks(initialBlow.confiscateTasks());

        for(int i = 0; i < retalationBlows.size; i++){
            if(retalationBlows.get(i).apply()){
                scheduleMultipleRenderTasks(retalationBlows.get(i).confiscateTasks());
            }
        }

        scheduleRenderTask(initialBlow.resetOrientation());

        System.out.println(scheduler);


    }



    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        initialBlow.setInitiator(rowActor0, colActor0);
        if(initialBlow.isInitiatorValid()) {
            initialBlow.setTarget(rowTarget0, colTarget0);

            retalationBlows.clear();
            HitCommand blow;
            Array<int[]> targets = getTargetsFromImpactArea();
            for(int i = 0; i < targets.size; i++){
                blow = new HitCommand(bfr, ActionChoice.ATTACK, scheduler, outcome.playerInventory);
                blow.setFree(true);
                blow.setDecoupled(true);
                blow.init();
                blow.setInitiator(targets.get(i)[0], targets.get(i)[1]);
                blow.setTarget(rowActor0, colActor0);
                if(blow.isApplicable())
                    retalationBlows.add(blow);
            }
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


    public HitCommand getInitialBlow(){
        return initialBlow;
    }

    public Array<HitCommand> getRetalationBlows(){
        return retalationBlows;
    }


}
