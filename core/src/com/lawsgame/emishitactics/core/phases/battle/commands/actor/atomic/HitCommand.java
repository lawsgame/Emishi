package com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification.ApplyDamage;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.GuardCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.SwitchPositionCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.WalkCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.RendererThread;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class HitCommand extends ActorCommand{
    protected int damageDealt;
    protected int hitrate;
    private SwitchPositionCommand switchcommand;
    private ChooseOrientationCommand orientationCommand;
    private WalkCommand walkcommand;
    private GuardCommand guardCommand;

    private int rowInitDefender = -1;
    private int colInitDefender = -1;
    private BattleUnitRenderer defenderRenderer = null;

    protected boolean retaliation;
    protected boolean resetOrientation;
    protected boolean specialmove;
    protected boolean cripplingTarget;
    protected boolean disablingTarget;
    protected boolean healingFromDamage;
    protected boolean moralDamage;
    protected boolean repeatableOnKill;
    protected boolean stealing;



    public HitCommand(BattlefieldRenderer bfr, Data.ActionChoice choice, AnimationScheduler scheduler, Inventory playerInventory, int damageDealt, int hitrate) {
        super(bfr, choice, scheduler, playerInventory, true);
        this.damageDealt = damageDealt;
        this.hitrate = hitrate;
        this.retaliation = false;
        this.resetOrientation = false;
        this.specialmove = false;
        this.cripplingTarget = false;
        this.disablingTarget = false;
        this.healingFromDamage = false;
        this.moralDamage = false;
        this.repeatableOnKill = false;
        this.stealing = false;

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

    public HitCommand(BattlefieldRenderer bfr, Data.ActionChoice choice, AnimationScheduler scheduler, Inventory playerInventory) {
        this(bfr, choice, scheduler, playerInventory, 1, 50);
    }

    @Override
    public void init() {
        super.init();
        rowInitDefender = -1;
        colInitDefender = -1;
        defenderRenderer = null;
    }

    //--------------- EXECUTE -----------------------------------------------

    @Override
    protected void execute() {

        // set orientation
        Orientation initiatorOr = getInitiator().getOrientation();
        Orientation defenderInitOr = defenderRenderer.getModel().getOrientation();
        Orientation targetInitOr = getTarget().getOrientation();

        // switch guardian / target
        if(switchcommand.apply(rowTarget, colTarget, rowInitDefender, colInitDefender)) {
            scheduleMultipleRenderTasks(switchcommand.confiscateTasks());
        }

        // PERFORM ATTACK
        performAttack();

        // HANDLE EVENTS
        //handleEvents();

        // remove OOA units
        removeOutOfActionUnits();

        // switch back guardian / target
        if(switchcommand.apply(rowTarget, colTarget, rowInitDefender, colInitDefender)){
            scheduleMultipleRenderTasks(switchcommand.confiscateTasks());
        }else if(walkcommand.apply(rowInitDefender, colInitDefender, rowTarget, colTarget)){
            scheduleMultipleRenderTasks(walkcommand.confiscateTasks());
        }
        if(isDefenderGuardian() && guardCommand.apply(rowInitDefender, colInitDefender)){
            scheduleMultipleRenderTasks(guardCommand.confiscateTasks());
        }


        // reset orientation
        if(resetOrientation) {
            if (retaliation) {
                orientationCommand.setOrientation(initiatorOr);
                if (orientationCommand.apply(rowActor, colActor))
                    scheduleMultipleRenderTasks(orientationCommand.confiscateTasks());
            }

            orientationCommand.setOrientation(targetInitOr);
            if (orientationCommand.apply(rowTarget, colTarget)) {
                scheduleMultipleRenderTasks(orientationCommand.confiscateTasks());
            }
            orientationCommand.setOrientation(defenderInitOr);
            if (orientationCommand.apply(rowInitDefender, colInitDefender)) {
                scheduleMultipleRenderTasks(orientationCommand.confiscateTasks());
            }
        }
    }

    protected void handleEvents(){

        Array<Task> tasks;
        if(getInitiator().isAnyEventTriggerable(null)){
            eventTriggered = true;
            tasks = getInitiator().performEvents(null);
            scheduleMultipleRenderTasks(tasks);
        }
        if(getTarget().isAnyEventTriggerable(null)){
            eventTriggered = true;
            tasks = getTarget().performEvents(null);
            scheduleMultipleRenderTasks(tasks);
        }
        if(isDefenderGuardian() && defenderRenderer.getModel().isAnyEventTriggerable(null)){
            eventTriggered = true;
            tasks = defenderRenderer.getModel().performEvents(null);
            scheduleMultipleRenderTasks(tasks);
        }
    }

    protected void performAttack(){

        orientationCommand.setOrientation(Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget));
        if(orientationCommand.apply(rowActor, colActor))
            scheduleMultipleRenderTasks(orientationCommand.confiscateTasks());

        boolean backstabbed = getInitiator().getOrientation() == defenderRenderer.getModel().getOrientation();

        if(!backstabbed) {
            orientationCommand.setOrientation(getInitiator().getOrientation().getOpposite());
            if (orientationCommand.apply(rowTarget, colTarget))
                scheduleMultipleRenderTasks(orientationCommand.confiscateTasks());
        }

        StandardTask task = new StandardTask();
        RendererThread attackerThread = new RendererThread(bfr.getUnitRenderer(getInitiator()));
        Array<StandardTask.RendererThread> targetsThreads = new Array<StandardTask.RendererThread>();
        targetsThreads.add(new RendererThread(bfr.getUnitRenderer(defenderRenderer.getModel())));
        attackerThread.addQuery(Data.AnimId.ATTACK);

        int dicesroll = Utils.getMean(2,100);
        if(dicesroll < hitrate){

            Array<ApplyDamage> notifs = defenderRenderer.getModel().applyDamage(damageDealt, false);

            if(!backstabbed){
                defenderRenderer.getModel().setOrientation(getInitiator().getOrientation().getOpposite());
                targetsThreads.get(0).addQuery(getInitiator().getOrientation().getOpposite());
            }
            for(int i = 0; i < notifs.size; i++){
                notifs.get(i).critical = false;
                notifs.get(i).backstab = backstabbed && i == 0;
                notifs.get(i).fleeingOrientation = getInitiator().getOrientation();
                if(i != 0){
                    targetsThreads.add(new StandardTask.RendererThread(bfr.getUnitRenderer(notifs.get(i).wounded), notifs.get(i)));
                }else{
                    targetsThreads.get(0).addQuery(notifs.get(0));
                }
            }

            updateOutcome(getInitiator(), notifs);;

        }else{
            defenderRenderer.getModel().setOrientation(getInitiator().getOrientation().getOpposite());
            targetsThreads.get(0).addQuery(getInitiator().getOrientation().getOpposite());
            targetsThreads.get(0).addQuery(Data.AnimId.DODGE);
        }

        task.addThread(attackerThread);
        task.addllThreads(targetsThreads);
        scheduleRenderTask(task);

    }

    protected final boolean isDefenderGuardian(){
        return rowInitDefender != rowTarget || colInitDefender != colTarget;
    }


    protected void updateOutcome(IUnit receiver, Array<ApplyDamage> notifs){
        int experience = 0;
        int lootRate = Formulas.getLootRate(receiver);
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
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        if(isEnemyTargetValid(rowActor0, colActor0, rowTarget0, colTarget0, false)) {
            setDefender(rowTarget0, colTarget0, rowTarget0, colTarget0);
            return true;
        }
        return false;
    }

    /**
     *
     * @param rowTarget0 : where the target would be when attacked
     * @param colTarget0 : where the target would be when attacked
     * @param currentTargetRow : current position of the target
     * @param currentTargetCol : current position of the target
     */
    public void setDefender(int currentTargetRow, int currentTargetCol, int rowTarget0, int colTarget0){
        IUnit defender = bfr.getModel().getUnit(currentTargetRow, currentTargetCol);
        if(bfr.getModel().isTileGuarded(rowTarget0, colTarget0, defender.getArmy().getAffiliation())){

            defender = bfr.getModel().getStrongestGuardian(rowTarget0, colTarget0, defender.getArmy().getAffiliation());
            int[] defenderPos = bfr.getModel().getUnitPos(defender);
            this.rowInitDefender = defenderPos[0];
            this.colInitDefender = defenderPos[1];
            this.defenderRenderer = bfr.getUnitRenderer(defender);
        }else{

            this.rowInitDefender = rowTarget0;
            this.colInitDefender = colTarget0;
            this.defenderRenderer = bfr.getUnitRenderer(bfr.getModel().getUnit(currentTargetRow, currentTargetCol));
        }
    }

    @Override
    public Array<int[]> getTargetsAtRange(int row, int col, IUnit actor) {
        return getFoesAtRange(row, col, actor, false);
    }

    @Override
    public Array<int[]> getTargetsFromImpactArea(int rowActor0, int colActor0, int rowTarget0, int colTarget0, IUnit actor) {
        return getTargetedFoes(rowActor0, colActor0, rowTarget0, colTarget0, actor, false);
    }


    //-------------- GETTERS & SETTERS ---------------------------------


    public BattleUnitRenderer getDefenderRenderer() {
        return defenderRenderer;
    }

    public int getRowInitDefender() {
        return rowInitDefender;
    }

    public int getColInitDefender() {
        return colInitDefender;
    }

    public void setDamageDealt(int damageDealt) {
        this.damageDealt = damageDealt;
    }

    public void setHitrate(int hitrate) {
        this.hitrate = hitrate;
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

    public void setMoralDamage(boolean moralDamage) {
        this.moralDamage = moralDamage;
    }

    public void setRepeatableOnKill(boolean repeatableOnKill) {
        this.repeatableOnKill = repeatableOnKill;
    }

    public void setStealing(boolean stealing) {
        this.stealing = stealing;
    }

    public void setResetOrientation(boolean resetOrientation) {
        this.resetOrientation = resetOrientation;
    }
}
