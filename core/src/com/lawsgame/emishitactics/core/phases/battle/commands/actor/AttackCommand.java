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

public class AttackCommand extends HitCommand {

    protected Array<HitCommand> retalationBlows;

    public AttackCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, Data.ActionChoice.ATTACK, scheduler, playerInventory);
        this.retalationBlows = new Array<HitCommand>();
        this.setFree(false);

    }

    @Override
    protected void execute() {
        super.execute();

        for(int i = 0; i < retalationBlows.size; i++){
            if(retalationBlows.get(i).apply()){
                outcome.merge(retalationBlows.get(i).getOutcome());
                scheduleMultipleRenderTasks(retalationBlows.get(i).confiscateTasks());
            }
        }

        scheduleRenderTask(resetOrientation());

    }

    @Override
    protected void provideActionPanelInfos() {
        super.provideActionPanelInfos();

        retalationBlows.clear();
        HitCommand blow;
        Array<int[]> targets = getTargetsFromImpactArea();
        for (int i = 0; i < targets.size; i++) {

            blow = new HitCommand(bfr, ActionChoice.ATTACK, scheduler, outcome.playerInventory);
            blow.setFree(true);
            blow.setDecoupled(true);
            blow.setRetaliation(true);

            blow.init();
            blow.setInitiator(targets.get(i)[0], targets.get(i)[1]);
            blow.setTarget(rowActor, colActor);
            if (blow.isApplicable())
                retalationBlows.add(blow);
        }


    }

    // -------------------- COMODITY BATTLE PANEL METHODS ------------------


    public HitCommand getInitialBlow(){
        return this;
    }

    public Array<HitCommand> getRetalationBlows(){
        return retalationBlows;
    }


}
