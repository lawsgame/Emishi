package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Thread;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AttackCommand extends BattleCommand {

    public AttackCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.ATTACK, scheduler, false, false);
    }

    @Override
    protected void execute() {
        IUnit attacker = battlefield.getUnit(rowActor, colActor);

        Array<Unit.DamageNotif> notifBundleActor;
        Array<Unit.DamageNotif> notifBundleTarget;
        notifBundleActor = performAttack(rowActor, colActor, rowTarget, colTarget);
        if(!battlefield.getUnit(rowTarget, colTarget).isOutOfAction() && isTargetValid(rowTarget, colTarget, rowActor, colActor)){
            notifBundleTarget = performAttack(rowTarget, colTarget, rowActor, colActor);
            setOutcome(rowTarget, colTarget, notifBundleTarget);
        }
        setOutcome(rowActor, colActor, notifBundleActor);
        removeOutOfActionUnits();

        attacker.setActed(true);

    }


    protected Array<Unit.DamageNotif> performAttack(int rowAttacker, int colAttacker, int rowDefender, int colDefender){
        IUnit attacker = battlefield.getUnit(rowAttacker, colAttacker);
        IUnit defender = battlefield.getUnit(rowDefender, colDefender);
        Array<Unit.DamageNotif> notifs = new Array<Unit.DamageNotif>();

        // FIRST ATTACK

        Task task = new Task();
        Thread initiatorThread = new Thread(battlefieldRenderer.getUnitRenderer(attacker));
        Thread targetThread = new Thread(battlefieldRenderer.getUnitRenderer(defender));
        Array<Thread> defendersThreads = new Array<Thread>();

        attacker.setOrientation(Utils.getOrientationFromCoords(rowAttacker, colAttacker, rowDefender, colDefender));

        initiatorThread.addQuery(attacker.getOrientation());
        initiatorThread.addQuery(Data.AnimationId.ATTACK);

        boolean backstabbed = attacker.getOrientation() == defender.getOrientation();
        int hitrate = Formulas.getHitRate(rowAttacker, colAttacker, rowDefender, colDefender, battlefield);

        BattleUnitRenderer bur;
        if(Utils.getMean(2,100) < hitrate){

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

        return notifs;
    }


    protected void setOutcome(int rowReceiver, int colReceiver, Array<Unit.DamageNotif> notifs){
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
        return isEnemyTargetValid(rowActor0, colActor0, rowTarget0, colTarget0);
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        return isEnemyAtActionRange(row, col, actor);
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
