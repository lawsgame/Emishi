package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Thread;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Notification.ApplyDamage;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AttackCommand extends BattleCommand {
    protected boolean retaliationAllowed;
    protected MoveCommand moveCommand;
    protected SwitchPositionCommand switchPositionCommand;
    protected IUnit target;
    protected IUnit initiator;
    protected IUnit targetDefender;
    protected IUnit initiatorDefender;

    protected AttackCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, boolean retaliationAllowed) {
        super(bfr, ActionChoice.ATTACK, scheduler, false, true, false);
        this.retaliationAllowed = retaliationAllowed;
        this.moveCommand = new MoveCommand(bfr, scheduler);
        this.switchPositionCommand = new SwitchPositionCommand(bfr, scheduler);
        this.moveCommand.setFree(true);
        this.switchPositionCommand.setFree(true);
    }

    public AttackCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        this(bfr, scheduler, true);

    }

    @Override
    public void init() {
        super.init();
        target = battlefield.getUnit(rowTarget, colTarget);
        initiator = battlefield.getUnit(rowActor, colActor);
        targetDefender = setDefender(rowTarget, colTarget);
        initiatorDefender = setDefender(rowActor, colActor);
    }

    @Override
    protected void execute() {

        switchTargetGuardianPosition(rowTarget, colTarget, targetDefender);
        Array<ApplyDamage> notifBundleActor = performAttack(rowActor, colActor, rowTarget, colTarget, target, false);
        addOutcomeData(rowActor, colActor, notifBundleActor);

        if(retaliationAllowed
                && !battlefield.getUnit(rowTarget, colTarget).isOutOfAction()
                && isTargetValid(rowTarget, colTarget, rowActor, colActor)){

            switchTargetGuardianPosition(rowActor, colActor, initiatorDefender);
            Array<ApplyDamage> notifBundleTarget = performAttack(rowTarget, colTarget, rowActor, colActor, initiator, true);
            addOutcomeData(rowTarget, colTarget, notifBundleTarget);
        }

        removeOutOfActionUnits();
    }

    protected Array<ApplyDamage> performAttack(int rowAttacker, int colAttacker, int rowTarget, int colTarget, IUnit target0, boolean retaliate){
        Array<ApplyDamage> notifs = new Array<ApplyDamage>();
        IUnit attacker = battlefield.getUnit(rowAttacker, colAttacker);
        IUnit defender = battlefield.getUnit(rowTarget, colTarget);

        Task task = new Task();
        Thread initiatorThread = new Thread(battlefieldRenderer.getUnitRenderer(attacker));
        Thread targetThread = new Thread(battlefieldRenderer.getUnitRenderer(defender));
        Array<Thread> defendersThreads = new Array<Thread>();

        Data.Orientation attackerReorientation = Utils.getOrientationFromCoords(rowAttacker, colAttacker, rowTarget, colTarget);

        initiatorThread.addQuery(attacker.getOrientation());
        initiatorThread.addQuery(Data.AnimationId.ATTACK);

        if(!retaliate)
            attacker.setOrientation(attackerReorientation);

        boolean backstabbed = attacker.getOrientation() == defender.getOrientation();
        int hitrate = Formulas.getHitRate(rowAttacker, colAttacker, rowTarget, colTarget, battlefield);
        if(Utils.getMean(2,100) < hitrate){
            BattleUnitRenderer bur;

            // model-wise changes
            int dealtdamage = Formulas.getDealtDamage(rowAttacker, colAttacker, rowTarget, colTarget, battlefield);
            notifs = defender.applyDamage(dealtdamage, false);

            // view-wise scheduling
            if(!backstabbed){
                targetThread.addQuery(attacker.getOrientation().getOpposite());
            }
            for(int i = 0; i < notifs.size; i++){
                notifs.get(i).critical = false;
                notifs.get(i).backstab = backstabbed && i == 0;
                notifs.get(i).fleeingOrientation = attacker.getOrientation();
                if(i != 0){
                    bur = battlefieldRenderer.getUnitRenderer(notifs.get(i).wounded);

                    defendersThreads.add(new Thread(bur, bur, notifs.get(i)));
                }
            }
            targetThread.addQuery(notifs.get(0));
            if(!backstabbed && !defender.isOutOfAction()){
                targetThread.addQuery(defender.getOrientation());
            }

        }else{
            // view-wise scheduling
            targetThread.addQuery(attacker.getOrientation().getOpposite());
            targetThread.addQuery(Data.AnimationId.DODGE);
            targetThread.addQuery(defender.getOrientation());
        }

        task.addThread(initiatorThread);
        task.addThread(targetThread);
        task.addllThreads(defendersThreads);
        scheduler.addTask(task);

        // guardian
        switchBackTargetGuardianPositions(rowTarget, colTarget, target0);

        return notifs;
    }

    private void switchBackTargetGuardianPositions(int rowTarget0, int colTarget0, IUnit target0){
        IUnit defender = battlefield.getUnit(rowTarget0, colTarget0);
        if(target0 != null && defender != null && target0 != defender) {
            int[] targetPos = battlefield.getUnitPos(target0);
            if (defender.isOutOfAction()) {

                removeOutOfActionUnits();
                moveCommand.apply(targetPos[0], targetPos[1], rowTarget0, colTarget0);

                scheduler.addTask(new Task(battlefieldRenderer.getUnitRenderer(target0), target0.getOrientation()));
            } else {

                switchPositionCommand.apply(targetPos[0], targetPos[1], rowTarget0, colTarget0);
                Area.UnitArea area = battlefield.addGuardedArea(targetPos[0], targetPos[1]);

                Task task = new Task();
                task.addThread(new Thread(battlefieldRenderer, area));
                task.addThread(new Thread(battlefieldRenderer.getUnitRenderer(defender), defender.getOrientation()));
                task.addThread(new Thread(battlefieldRenderer.getUnitRenderer(target0), target0.getOrientation()));
                scheduler.addTask(task);
            }
        }
    }

    protected void switchTargetGuardianPosition(int rowTarget, int colTarget, IUnit defender){
        IUnit target = battlefield.getUnit(rowTarget, colTarget);
        if(battlefield.isTileOccupied(rowTarget, colTarget) && target  != defender) {
            int[] guardianPosition = battlefield.getUnitPos(defender);
            this.switchPositionCommand.apply(rowTarget, colTarget, guardianPosition[0], guardianPosition[1]);
        }
    }

    protected IUnit setDefender(int rowTarget, int colTarget){
        IUnit defender = battlefield.getUnit(rowTarget, colTarget);
        if(battlefield.isTileGuarded(rowTarget, colTarget, target.getAllegeance())){
            defender = battlefield.getAvailableGuardian(rowTarget, colTarget, target.getAllegeance()).random();
        }
        return defender;
    }

    protected void addOutcomeData(int rowReceiver, int colReceiver, Array<ApplyDamage> notifs){
        IUnit receiver =  battlefield.getUnit(rowReceiver, colReceiver);

        int experience;
        int lootRate;
        int dicesResult;
        Item droppedItem;
        if(!receiver.isOutOfAction()) {
            if (notifs.size == 1 && notifs.get(0).isRelevant()) {

                IUnit target = notifs.get(0).wounded;
                experience = Formulas.getGainedExperience(receiver.getLevel(), target.getLevel(), !target.isOutOfAction());
                outcome.receivers.add(receiver);
                outcome.experienceGained.add(experience);
                lootRate = Formulas.getLootRate(rowReceiver, colReceiver, battlefield);
                dicesResult = Utils.getMean(1, 100);
                if(dicesResult < lootRate){
                    droppedItem = target.getRandomlyDroppableItem();
                    outcome.droppedItems.add(droppedItem);
                }
            } else if (notifs.size > 1){

                //getInstance all wounded opponents
                Array<IUnit> squad = new Array<IUnit>();
                for(int i = 0; i < notifs.size; i++){
                    if(notifs.get(i).isRelevant())
                        squad.add(notifs.get(i).wounded);
                }

                // calculate the experience points obtained
                experience = Formulas.getGainedExperienceFoeEachSquadMember(receiver, squad);

                // fetch dropped items
                for(int i = 0; i < squad.size; i++) {
                    if(squad.get(i).isOutOfAction()) {
                        lootRate = Formulas.getLootRate(rowReceiver, colReceiver, battlefield);
                        if (Utils.getMean(1, 100) < lootRate) {
                            droppedItem = squad.get(i).getRandomlyDroppableItem();
                            outcome.droppedItems.add(droppedItem);
                        }
                    }
                }

                // add experience points
                squad = receiver.getSquad(true);
                for(int i = 0; i < squad.size; i++) {
                    outcome.experienceGained.add(experience);
                    outcome.receivers.add(squad.get(i));
                }

            }
        }


    }

    protected void removeOutOfActionUnits(){
        Array<IUnit> OOAUnits = battlefield.getOOAUnits();
        battlefield.removeOOAUnits();
        Task removeOOAUnitTask = new Task();
        for(int i = 0; i < OOAUnits.size; i++)
            removeOOAUnitTask.addThread(new Thread(battlefieldRenderer, battlefieldRenderer, OOAUnits.get(i)));
        scheduler.addTask(removeOOAUnitTask);
    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return isEnemyTargetValid(rowActor0, colActor0, rowTarget0, colTarget0, false);
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        return isEnemyAtActionRange(row, col, actor, false);
    }


    // -------------------- COMODITY BATTLE RESOLUTION METHODS ------------------

    //TODO:!!!!

    public int getHitRate(boolean retaliation){
        int[] defenderPos = (retaliation) ? battlefield.getUnitPos(targetDefender) : battlefield.getUnitPos(initiatorDefender);
        return (retaliation) ?
                Formulas.getHitRate(rowActor, colActor, defenderPos[0], defenderPos[1], battlefield) :
                Formulas.getHitRate(rowTarget, colTarget, defenderPos[0], defenderPos[1], battlefield);
    }

    public int getDealtDamage(boolean retaliation){
        int[] defenderPos = (retaliation) ? battlefield.getUnitPos(targetDefender) : battlefield.getUnitPos(initiatorDefender);
        return (retaliation) ?
                Formulas.getDealtDamage(rowActor, colActor, defenderPos[0], defenderPos[1], battlefield) :
                Formulas.getDealtDamage(rowTarget, colTarget, defenderPos[0], defenderPos[1], battlefield);
    }

    public int getLootRate(boolean retaliation){
        return (retaliation) ?
                Formulas.getLootRate(rowActor, colActor, battlefield) :
                Formulas.getLootRate(rowTarget, colTarget, battlefield);
    }

    public IUnit getTargetDefender(){
        return targetDefender;
    }


    public IUnit getInitiatorDefender() {
        return initiatorDefender;
    }
}
