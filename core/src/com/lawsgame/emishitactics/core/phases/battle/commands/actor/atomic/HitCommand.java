package com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Notification.TakeDamage;
import com.lawsgame.emishitactics.core.models.Notification.Attack;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.GuardCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.SwitchPositionCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.WalkCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.RendererSubTaskQueue;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class HitCommand extends ActorCommand{
    private SwitchPositionCommand switchcommand;
    private ChooseOrientationCommand orientationCommand;
    private WalkCommand walkcommand;
    private GuardCommand guardCommand;

    private Array<DefenderData> defendersData;
    private Orientation initiatorInitOr;

    protected boolean retaliation;
    protected boolean resetOrientation;
    protected boolean specialmove;
    protected boolean ignorePhysicalDamage;
    protected boolean ignoreMoralDamage;
    protected boolean cripplingTarget;
    protected boolean disablingTarget;
    protected boolean repeatableOnKill;
    protected boolean healingFromDamage;

    public HitCommand(BattlefieldRenderer bfr, Data.ActionChoice choice, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, choice, scheduler, playerInventory, true);
        this.retaliation = false;
        this.resetOrientation = false;
        this.specialmove = false;
        this.cripplingTarget = false;
        this.disablingTarget = false;
        this.healingFromDamage = false;
        this.ignorePhysicalDamage = false;
        this.ignoreMoralDamage = false;
        this.repeatableOnKill = false;
        this.defendersData = new Array<DefenderData>();

        this.switchcommand = new SwitchPositionCommand(bfr, scheduler, playerInventory);
        this.switchcommand.setDecoupled(true);
        this.switchcommand.setFree(true);

        this.walkcommand = new WalkCommand(bfr, scheduler, playerInventory);
        this.walkcommand.setDecoupled(true);
        this.walkcommand.setFree(true);

        this.orientationCommand = new ChooseOrientationCommand(bfr, scheduler, playerInventory, null);
        this.orientationCommand.setDecoupled(true);
        this.orientationCommand.setFree(true);

        this.guardCommand = new GuardCommand(bfr, scheduler, playerInventory);
        this.guardCommand.setDecoupled(true);
        this.guardCommand.setFree(true);
    }


    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator) && initiator.getCurrentWeapon().isUsable();
    }

    @Override
    public void init() {
        super.init();
        defendersData.clear();
    }

    //--------------- EXECUTE -----------------------------------------------

    @Override
    protected void execute() {
        Notification.OOAReport report;

        initiatorInitOr = getInitiator().getOrientation();

        // SWITCH GUARDIAN - TARGET
        for(int i = 0; i < defendersData.size; i++) {
            if (switchcommand.apply(
                    defendersData.get(i).rowInitTarget,
                    defendersData.get(i).colInitTarget,
                    defendersData.get(i).rowInitDefender,
                    defendersData.get(i).colInitDefender)) {
                scheduleMultipleRenderTasks(switchcommand.confiscateTasks());
            }
        }

        // PERFORM ATTACK
        performAttack();

        // REMOVE OOA UNITS
        report = removeOutOfActionUnits();

        // HANDLE EVENTS
        handleEvents(report);

        // SWITCH BACK GUARDIAN - TARGET
        for(int i = 0; i < defendersData.size; i++) {
            if (switchcommand.apply(
                    defendersData.get(i).rowInitTarget,
                    defendersData.get(i).colInitTarget,
                    defendersData.get(i).rowInitDefender,
                    defendersData.get(i).colInitDefender)) {
                scheduleMultipleRenderTasks(switchcommand.confiscateTasks());
            }else if (walkcommand.apply(
                    defendersData.get(i).rowInitDefender,
                    defendersData.get(i).colInitDefender,
                    defendersData.get(i).rowInitTarget,
                    defendersData.get(i).colInitTarget)){
                scheduleMultipleRenderTasks(walkcommand.confiscateTasks());
            }
            if(isDefenderGuardian(i) && guardCommand.apply(defendersData.get(i).rowInitDefender, defendersData.get(i).colInitTarget)){
                scheduleMultipleRenderTasks(guardCommand.confiscateTasks());
            }
        }

        // RESET ORIENTATION
        if(resetOrientation) {
            scheduleRenderTask(resetOrientation());
        }
    }

    public Task resetOrientation(){
        StandardTask resetOrTask = new StandardTask();
        if (retaliation) {
            getInitiator().setOrientation(initiatorInitOr);
            resetOrTask.addParallelSubTask(new RendererSubTaskQueue(bfr.getUnitRenderer(getInitiator()), initiatorInitOr));
        }
        DefenderData data;
        for(int i = 0; i < defendersData.size; i++){
            data = defendersData.get(i);
            data.targetRenderer.getModel().setOrientation(data.targetInitOrientation);
            resetOrTask.addParallelSubTask(new RendererSubTaskQueue(data.targetRenderer, data.targetInitOrientation));
            if(isDefenderGuardian(i)) {
                data.defenderRenderer.getModel().setOrientation(data.defenderInitOrientation);
                resetOrTask.addParallelSubTask(new RendererSubTaskQueue(data.defenderRenderer, data.defenderInitOrientation));
            }
        }
        return resetOrTask;
    }

    protected void performAttack(){
        StandardTask task = new StandardTask();
        RendererSubTaskQueue initiatorThread = new RendererSubTaskQueue(bfr.getUnitRenderer(getInitiator()));
        Array<RendererSubTaskQueue> defenderThreads = new Array<RendererSubTaskQueue>();
        for(int i = 0; i < defendersData.size; i++)
            defenderThreads.add(new RendererSubTaskQueue(defendersData.get(i).defenderRenderer));

        Array<TakeDamage> notifs = new Array<TakeDamage>();
        Attack attackNotif = new Attack(specialmove);

        Orientation reOrientation = Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget);
        getInitiator().setOrientation(reOrientation);
        initiatorThread.addQuery(reOrientation);
        initiatorThread.addQuery(attackNotif);

        getInitiator().getCurrentWeapon().decrementDurability();

        DefenderData data;
        for(int i = 0; i < defendersData.size; i++) {

            data = defendersData.get(i);
            int dicesroll = Utils.getMean(2, 100);
            if (dicesroll < defendersData.get(i).hitrate) {

                int[] dealDamageRange = Formulas.getDealtDamageRange(rowActor, colActor, data.rowInitTarget, data.colInitTarget, getInitiator(),data.defenderRenderer.getModel(), bfr.getModel());
                int appliedDamage = Formulas.getRandomDamageInput(dealDamageRange);
                TakeDamage notif = data.defenderRenderer.getModel().takeDamage(appliedDamage, ignorePhysicalDamage, ignoreMoralDamage, data.moralModifier);
                notif.critical = false;
                notif.crippled = cripplingTarget;
                notif.disabled = disablingTarget;
                notif.backstab = getInitiator().getOrientation() == data.defenderRenderer.getModel().getOrientation();
                notif.fleeingOrientation = getInitiator().getOrientation();
                data.defenderRenderer.getModel().setCrippled(cripplingTarget);
                data.defenderRenderer.getModel().setDisabled(disablingTarget);
                registerAction = !repeatableOnKill || !data.defenderRenderer.getModel().isOutOfAction();


                if (!notif.backstab) {
                    reOrientation = getInitiator().getOrientation().getOpposite();
                    data.defenderRenderer.getModel().setOrientation(reOrientation);
                    defenderThreads.get(i).addQuery(reOrientation);
                }

                defenderThreads.get(i).addQuery(notif);

                notifs.add(notif);

            } else {

                reOrientation = getInitiator().getOrientation().getOpposite();
                data.defenderRenderer.getModel().setOrientation(reOrientation);
                defenderThreads.get(i).addQuery(reOrientation);
                defenderThreads.get(i).addQuery(Data.AnimId.DODGE);
            }

            updateOutcome(getInitiator(), notifs);
        }

        if(healingFromDamage){
            int healValue = 0;
            for(int i = 0; i < notifs.size; i++){
                healValue += notifs.get(i).lifeDamageTaken;
            }
            boolean treated = getInitiator().improveCondition(healValue, healValue);
            if(treated)
                attackNotif.lifeDrained = healValue;
        }

        task.addParallelSubTask(initiatorThread);
        for(int i = 0; i < defenderThreads.size; i++)
            task.addParallelSubTask(defenderThreads.get(i));
        scheduleRenderTask(task);

    }

    protected final boolean isDefenderGuardian(int index){
        return defendersData.get(index).isTargetGuarded();
    }


    protected void updateOutcome(Unit receiver, Array<TakeDamage> notifs){
        int experience = 0;
        int lootRate = getLootRate();
        int dicesResult;
        Item droppedItem;
        if(receiver != null) {
            for(int i = 0; i < notifs.size; i++){
                experience += Formulas.getGainedExperience(receiver.getLevel(), notifs.get(i).wounded.getLevel(), !notifs.get(i).wounded.isOutOfAction());
                if(notifs.get(i).wounded.isOutOfAction()) {
                    dicesResult = Utils.getMean(1, 100);
                    if (dicesResult < lootRate) {
                        droppedItem = notifs.get(i).wounded.getRandomlyDroppableItem();
                        outcome.add(droppedItem, receiver.isMobilized() && receiver.getArmy().isPlayerControlled());
                    }
                }
            }
        }
        outcome.add(receiver, experience);
    }


    //------------------------- CHECK METHODS ---------------------------------

    @Override
    public boolean isTargetValid(Unit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return isEnemyTargetValid(initiator, rowActor0, colActor0, rowTarget0, colTarget0, false);
    }

    @Override
    protected void provideActionPanelInfos() {
        setDefenders(rowActor, colActor, rowTarget, colTarget);
    }

    private void setDefenders(int rowActor, int colActor, int rowTarget, int colTarget){
        defendersData.clear();

        Unit target;
        Array<int[]> targetsPos = getTargetsFromImpactArea(rowActor, colActor, rowTarget, colTarget, getInitiator());
        Unit defender;
        int[] defenderPos;
        Unit guardian;
        Array<Unit> unavailableGuardians = new Array<Unit>();

        //remove units already targeted by the attack to become guardians of other targeted units
        for (int i = 0; i < targetsPos.size; i++) {
            if (bfr.getModel().isTileOccupied(targetsPos.get(i)[0], targetsPos.get(i)[1])) {
                unavailableGuardians.add(bfr.getModel().getUnit(targetsPos.get(i)[0], targetsPos.get(i)[1]));
            }
        }

        // set the defenders data
        for (int i = 0; i < targetsPos.size; i++) {
            if (bfr.getModel().isTileOccupied(targetsPos.get(i)[0], targetsPos.get(i)[1])) {

                target = bfr.getModel().getUnit(targetsPos.get(i)[0], targetsPos.get(i)[1]);
                defender = target;
                defenderPos = targetsPos.get(i);
                guardian = bfr.getModel().getStrongestAvailableGuardian(rowTarget, colTarget, defender.getArmy().getAffiliation(), unavailableGuardians);
                if (guardian != null) {
                    defender = guardian;
                    defenderPos = bfr.getModel().getUnitPos(guardian);
                    unavailableGuardians.add(guardian);
                }

                defendersData.add(new DefenderData(
                        getInitiator(),
                        bfr.getUnitRenderer(target),
                        bfr.getUnitRenderer(defender),
                        target.getOrientation(),
                        defender.getOrientation(),
                        rowActor,
                        colActor,
                        targetsPos.get(i)[0],
                        targetsPos.get(i)[1],
                        defenderPos[0],
                        defenderPos[1],
                        bfr.getModel(),
                        choice));
            }
        }


    }

    @Override
    public Array<int[]> getTargetsAtRange(int row, int col, Unit actor) {
        return getFoesAtRange(row, col, actor, false);
    }

    @Override
    public Array<int[]> getTargetsFromImpactArea(int rowActor0, int colActor0, int rowTarget0, int colTarget0, Unit actor) {
        return getTargetedFoes(rowActor0, colActor0, rowTarget0, colTarget0, actor, false);
    }


    //-------------- GETTERS & SETTERS ---------------------------------

    public Array<DefenderData> getDefenderData() {
        return defendersData;
    }

    protected int getLootRate(){
        return Formulas.getLootRate(getInitiator(), rowActor, colActor, bfr.getModel());
    }

    public void setRetaliation(boolean retaliation) {
        this.retaliation = retaliation;
    }

    public void setSpecialmove(boolean specialmove) {
        this.specialmove = specialmove;
    }

    public void setCripplingTarget(boolean cripplingTarget) {
        this.cripplingTarget = cripplingTarget;
    }

    public void setDisablingTarget(boolean disablingTarget) {
        this.disablingTarget = disablingTarget;
    }

    public void setHealingFromDamage(boolean healingFromDamage) {
        this.healingFromDamage = healingFromDamage;
    }

    public void setIgnorePhysicalDamage(boolean ignorePhysicalDamage) {
        this.ignorePhysicalDamage = ignorePhysicalDamage;
    }

    public void setIgnoreMoralDamage(boolean ignoreMoralDamage) {
        this.ignoreMoralDamage = ignoreMoralDamage;
    }

    public void setRepeatableOnKill(boolean repeatableOnKill) {
        this.repeatableOnKill = repeatableOnKill;
    }

    public void setResetOrientation(boolean resetOrientation) {
        this.resetOrientation = resetOrientation;
    }


    //------------------------- DEFENDER DATA --------------------------------

    public static class DefenderData {
        public final Unit attacker;
        public final BattleUnitRenderer targetRenderer;
        public final BattleUnitRenderer defenderRenderer;
        public final Orientation targetInitOrientation;
        public final Orientation defenderInitOrientation;
        public final int rowInitTarget;
        public final int colInitTarget;
        public final int rowInitDefender;
        public final int colInitDefender;
        public final int hitrate;
        public final int[] damageDealt;
        public final float moralModifier;
        public final int lootRate;
        public final int APCost;


        public DefenderData(
                Unit attacker,
                BattleUnitRenderer targetRenderer,
                BattleUnitRenderer defenderRenderer,
                Orientation targetInitOrientation,
                Orientation defenderInitOrientation,
                int rowAttacker,
                int colAttacker,
                int rowInitTarget,
                int colInitTarget,
                int rowInitDefender,
                int colInitDefender,
                Battlefield bf,
                Data.ActionChoice choice) {

            this.attacker = attacker;
            this.targetRenderer = targetRenderer;
            this.defenderRenderer = defenderRenderer;
            this.targetInitOrientation = targetInitOrientation;
            this.defenderInitOrientation = defenderInitOrientation;
            this.rowInitTarget = rowInitTarget;
            this.colInitTarget = colInitTarget;
            this.rowInitDefender = rowInitDefender;
            this.colInitDefender = colInitDefender;
            this.hitrate = Formulas.getHitRate(rowAttacker, colAttacker, rowInitTarget, colInitTarget, attacker, defenderRenderer.getModel(), bf);
            this.damageDealt = Formulas.getDealtDamageRange(rowAttacker, colAttacker, rowInitTarget, colInitDefender, attacker, defenderRenderer.getModel(), bf);
            this.moralModifier = Formulas.getMoralModifier(rowAttacker, colAttacker, rowInitTarget, colInitTarget, attacker, defenderRenderer.getModel(), bf);
            this.lootRate = Formulas.getLootRate(attacker, rowAttacker, colAttacker, bf);
            this.APCost = choice.getCost(rowAttacker, colAttacker, attacker, bf);
        }

        public boolean isTargetGuarded(){
            return rowInitTarget != rowInitDefender ||  colInitTarget != colInitDefender ;
        }

        public String toString(){
            String str = "\n    HITCOMMAND :\n   attacker : "+attacker.getName();
            str +=  "\n   target : "+targetRenderer.getModel().getName();
            str +=  "\n   defender : "+defenderRenderer.getModel().getName();
            str +="\n\n   Damage dealt : "+damageDealt;
            str +=  "\n   Hit rate : "+hitrate;
            str +=  "\n   Loot rate : "+lootRate;
            return str;
        }
    }

}
