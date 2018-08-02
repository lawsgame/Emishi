package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Thread;
import com.lawsgame.emishitactics.core.models.Area;
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
    protected IUnit guarded;
    protected IUnit guardian;

    public AttackCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, boolean retaliationAllowed) {
        super(bfr, Data.ActionChoice.ATTACK, scheduler, false, false);
        this.retaliationAllowed = retaliationAllowed;
    }

    @Override
    public void init() {
        super.init();
        guarded = null;
        guardian = null;
    }

    @Override
    protected void execute() {
        IUnit attacker = battlefield.getUnit(rowActor, colActor);

        Array<ApplyDamage> notifBundleActor;
        Array<ApplyDamage> notifBundleTarget;
        notifBundleActor = performAttack(rowActor, colActor, rowTarget, colTarget);
        if(retaliationAllowed
                && !battlefield.getUnit(rowTarget, colTarget).isOutOfAction()
                && isTargetValid(rowTarget, colTarget, rowActor, colActor)){
            notifBundleTarget = performAttack(rowTarget, colTarget, rowActor, colActor);
            setOutcome(rowTarget, colTarget, notifBundleTarget);
        }
        setOutcome(rowActor, colActor, notifBundleActor);

        removeOutOfActionUnits();

        attacker.setActed(true);

    }

    protected Array<ApplyDamage> performAttack(int rowAttacker, int colAttacker, int rowDefender, int colDefender){
        Array<ApplyDamage> notifs = new Array<ApplyDamage>();
        IUnit attacker = battlefield.getUnit(rowAttacker, colAttacker);
        IUnit defender = setDefender(rowDefender, colDefender);
        //IUnit defender = battlefield.getUnit(rowDefender, colDefender);

        Task task = new Task();
        Thread initiatorThread = new Thread(battlefieldRenderer.getUnitRenderer(attacker));
        Thread targetThread = new Thread(battlefieldRenderer.getUnitRenderer(defender));
        Array<Thread> defendersThreads = new Array<Thread>();

        attacker.setOrientation(Utils.getOrientationFromCoords(rowAttacker, colAttacker, rowDefender, colDefender));

        initiatorThread.addQuery(attacker.getOrientation());
        initiatorThread.addQuery(Data.AnimationId.ATTACK);

        boolean backstabbed = attacker.getOrientation() == defender.getOrientation();
        int hitrate = Formulas.getHitRate(rowAttacker, colAttacker, rowDefender, colDefender, battlefield);
        if(Utils.getMean(2,100) < hitrate){
            BattleUnitRenderer bur;

            // model-wise changes
            int dealtdamage = Formulas.getDealtDamage(rowAttacker, colAttacker, rowDefender, colDefender, battlefield);
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
        resetTargetPosition();

        return notifs;
    }

    protected IUnit setDefender(int rowDefender, int colDefender){
        IUnit defender = battlefield.getUnit(rowDefender, colDefender);
        if(battlefield.isTileGuarded(rowDefender, colDefender, defender.getAllegeance())){
            this.guarded = defender;
            this.guardian = battlefield.getAvailableGuardianForTile(rowDefender, colDefender, defender.getAllegeance()).random();
            int[] guardianPosition = battlefield.getUnitPos(guardian);
            if(guardian != null && this.battlefield.switchUnitPositions(rowDefender, colDefender, guardianPosition[0], guardianPosition[1])){
                defender = guardian;
                this.scheduler.addTask(new Task(
                        battlefieldRenderer,
                        new SwitchPosition(guarded, guardian, SwitchPosition.Mode.GUARDIAN, battlefield)));
            }else{
                defender = battlefield.getUnit(rowDefender, colDefender);
                guarded = null;
                guardian = null;
            }
        }
        return defender;
    }

    protected void resetTargetPosition(){
        if(guarded != null && guardian != null){
            int[] guardianPos = battlefield.getUnitPos(guardian);
            int[] guardedPos = battlefield.getUnitPos(guarded);
            this.battlefield.switchUnitPositions(guardedPos[0], guardedPos[1], guardianPos[0], guardianPos[1]);

            this.scheduler.addTask(new Task(
                    battlefieldRenderer,
                    new SwitchPosition(guarded, guardian, SwitchPosition.Mode.GUARDIAN, battlefield)));

            if(!guardian.isOutOfAction()) {
                Area.UnitArea area = battlefield.addGuardedArea(guardedPos[0], guardedPos[1]);
                this.scheduler.addTask(new Task(battlefieldRenderer, area));
            }

            guarded = null;
            guardian = null;
        }
    }

    protected void setOutcome(int rowReceiver, int colReceiver, Array<ApplyDamage> notifs){
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
                    droppedItem = target.getDroppableItem();
                    outcome.droppedItems.add(droppedItem);
                }
            } else if (notifs.size > 1){

                //get all wounded opponents
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
                            droppedItem = squad.get(i).getDroppableItem();
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


}
