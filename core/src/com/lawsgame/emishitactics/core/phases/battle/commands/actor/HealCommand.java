package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class HealCommand extends SelfInflitedCommand {
    private final boolean BOOST_HP = true;
    private final boolean BOOST_MORAL = true;

    private int healPower;
    private Unit[] patients;
    private int[] recoveredHP;
    private int[] recoveredMoral;

    public HealCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, ActionChoice.HEAL, scheduler, playerInventory, false);
    }

    @Override
    public void init() {
        super.init();
        healPower = 0;
        recoveredHP = new int[0];
        recoveredMoral = new int[0];
        patients = new Unit[0];

    }

    @Override
    protected void provideActionPanelInfos() {
        super.provideActionPanelInfos();

        healPower = Formulas.getHealPower(rowActor, colActor, rowTarget, colTarget, getInitiator(), getTarget(), bfr.getModel());
        Array<int[]> targets = getTargetsFromImpactArea();
        patients = new Unit[targets.size];
        recoveredHP = new int[targets.size];
        recoveredMoral = new int[targets.size];
        for(int i = 0; i < targets.size; i++){
            patients[i] = bfr.getModel().getUnit(targets.get(i)[0], targets.get(i)[1]);
            recoveredHP[i] = (BOOST_HP) ? patients[i].getRecoveredHitPoints(healPower) : 0;
            recoveredMoral[i] = (BOOST_MORAL) ? patients[i].getRecoveredMoralPoints(healPower) : 0;
        }

    }

    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator) && initiator.has(Data.Ability.HEAL);
    }

    @Override
    protected void execute() {



        StandardTask task = new StandardTask();
        task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(getInitiator()), Data.AnimId.HEAL));

        for(int i = 0; i < patients.length; i++) {
            if(patients[i].improveCondition((BOOST_MORAL) ? healPower : 0 , (BOOST_HP) ? healPower : 0)) {
                task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(patients[i]), new Notification.Treated(healPower)));
            }
        }

        // push render task
        scheduleRenderTask(task);
        // set outcome
        outcome.add(getInitiator(), choice.getExperience());
        // handle events
        handleEvents(this);
    }

    @Override
    public Array<int[]> getTargetsAtRange(int row, int col, Unit actor) {
        return getAlliesAtRange(row, col, actor, true, false );
    }




    //-------------------- GETTERS & SETTERS ----------------------

    public Unit[] getPatients(){
        return patients;
    }

    public int[] getRecoveredHitPoints(){
        return recoveredHP;
    }

    public int[] getRecoveredMoralPoints(){
        return recoveredMoral;
    }



}
