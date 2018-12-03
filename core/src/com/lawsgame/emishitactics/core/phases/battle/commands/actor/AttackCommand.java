package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic.HitCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AttackCommand extends HitCommand {

    protected HitCommand retalationBlow;

    public AttackCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, Data.ActionChoice.ATTACK, scheduler, playerInventory);
        this.setFree(false);

    }

    @Override
    protected void execute() {
        super.execute();

        if(retalationBlow.apply()){
            outcome.merge(retalationBlow.getOutcome());
            scheduleMultipleRenderTasks(retalationBlow.confiscateTasks());
        }

        scheduleRenderTask(resetOrientation());

    }

    @Override
    protected void provideActionPanelInfos() {
        super.provideActionPanelInfos();

        retalationBlow = new HitCommand(bfr, ActionChoice.ATTACK, scheduler, outcome.playerInventory);
        retalationBlow.setFree(true);
        retalationBlow.setDecoupled(true);
        retalationBlow.setRetaliation(true);

        retalationBlow.init();
        retalationBlow.setInitiator(rowTarget, colTarget);
        retalationBlow.setTarget(rowActor, colActor);
    }

    // -------------------- COMODITY BATTLE PANEL METHODS ------------------


    public HitCommand getInitialBlow(){
        return this;
    }

    public HitCommand getRetalationBlow(){
        return retalationBlow;
    }


}
