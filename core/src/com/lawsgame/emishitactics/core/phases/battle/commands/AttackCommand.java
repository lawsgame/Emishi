package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Thread;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Notification.ApplyDamage;
import com.lawsgame.emishitactics.core.models.Notification.SwitchPosition;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AttackCommand extends BattleCommand {
    protected boolean retaliationAllowed;
    protected MoveCommand moveCommand;
    protected SwitchPositionCommand switchPositionCommand;
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
        targetDefender = null;
        initiatorDefender = null;
    }

    @Override
    protected void execute() {


        Array<ApplyDamage> notifBundleActor = performAttack(rowActor, colActor, rowTarget, colTarget);
        addDataOutcome(rowActor, colActor, notifBundleActor);

        if(retaliationAllowed
                && !battlefield.getUnit(rowTarget, colTarget).isOutOfAction()
                && isTargetValid(rowTarget, colTarget, rowActor, colActor)){

            Array<ApplyDamage> notifBundleTarget = performAttack(rowTarget, colTarget, rowActor, colActor);
            addDataOutcome(rowTarget, colTarget, notifBundleTarget);
        }


        removeOutOfActionUnits();
    }

    protected Array<ApplyDamage> performAttack(int rowAttacker, int colAttacker, int rowTarget, int colTarget){
        Array<ApplyDamage> notifs = new Array<ApplyDamage>();
        IUnit attacker = battlefield.getUnit(rowAttacker, colAttacker);
        IUnit target = battlefield.getUnit(rowTarget, colTarget);
        IUnit defender = setDefender(rowTarget, colTarget);

        Task task = new Task();
        Thread initiatorThread = new Thread(battlefieldRenderer.getUnitRenderer(attacker));
        Thread targetThread = new Thread(battlefieldRenderer.getUnitRenderer(defender));
        Array<Thread> defendersThreads = new Array<Thread>();

        attacker.setOrientation(Utils.getOrientationFromCoords(rowAttacker, colAttacker, rowTarget, colTarget));

        initiatorThread.addQuery(attacker.getOrientation());
        initiatorThread.addQuery(Data.AnimationId.ATTACK);

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
        resetTargetPosition(rowTarget, colTarget, target);

        return notifs;
    }

    protected IUnit setDefender(int rowTarget, int colTarget){
        IUnit target = battlefield.getUnit(rowTarget, colTarget);

        IUnit defender;
        if(battlefield.isTileGuarded(rowTarget, colTarget, target.getAllegeance())){
            defender = battlefield.getAvailableGuardian(rowTarget, colTarget, target.getAllegeance()).random();
            int[] guardianPosition = battlefield.getUnitPos(defender);
            this.switchPositionCommand.apply(rowTarget, colTarget, guardianPosition[0], guardianPosition[1]);

        }else{
            defender = target;
        }

        return defender;
    }

    protected void resetTargetPosition(int rowDefender, int colDefender, IUnit target){
        IUnit defender = battlefield.getUnit(rowDefender, colDefender);
        if(target != null && defender != null && target != defender) {
            int[] targetPos = battlefield.getUnitPos(target);
            if (defender.isOutOfAction()) {
                removeOutOfActionUnits();
                moveCommand.apply(targetPos[0], targetPos[1], rowDefender, colDefender);
            } else {
                switchPositionCommand.apply(targetPos[0], targetPos[1], rowDefender, colDefender);
                battlefield.addGuardedArea(targetPos[0], targetPos[1]);
            }
        }
    }

    protected void addDataOutcome(int rowReceiver, int colReceiver, Array<ApplyDamage> notifs){
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

    public int getHitRate(boolean ofInitiator){
        return (ofInitiator) ?
                Formulas.getHitRate(rowActor, colActor, rowTarget, colTarget, battlefield) :
                Formulas.getHitRate(rowTarget, colTarget, rowActor, colActor, battlefield);
    }

    public int getDealtDamage(boolean ofInitiator){
        return (ofInitiator) ?
                Formulas.getDealtDamage(rowActor, colActor, rowTarget, colTarget, battlefield) :
                Formulas.getDealtDamage(rowTarget, colTarget, rowActor, colActor, battlefield);
    }

    public int getLootRate(boolean ofInitiator){
        return (ofInitiator) ?
                Formulas.getLootRate(rowActor, colActor, battlefield) :
                Formulas.getLootRate(rowTarget, colTarget, battlefield);
    }

    public IUnit getTargetDefender(){
        return battlefield.getUnit(rowTarget, colTarget); //targetDefender;
    }


    public IUnit getInitiatorDefender() {
        return battlefield.getUnit(rowActor, colActor); //initiatorDefender;
    }
}
