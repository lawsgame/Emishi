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
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AttackCommand extends BattleCommand {
    private boolean launched;
    Array<Array<Unit.DamageNotif>> notifBundles;


    public AttackCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.ATTACK, scheduler, false, false);
    }

    @Override
    public void init() {
        super.init();
        launched = false;

    }

    @Override
    protected void execute() {
        launched = true;
        notifBundles = new Array<Array<Unit.DamageNotif>>();

        notifBundles.add(performAttack(rowActor, colActor, rowTarget, colTarget));
        if(!battlefield.getUnit(rowTarget, colTarget).isOutOfAction() && isTargetValid(rowTarget, colTarget, rowActor, colActor)){
            notifBundles.add(performAttack(rowTarget, colTarget, rowActor, colActor));
        }

        setOutcome();

        // remove OoA units
        Array<IUnit> OOAUnits = battlefield.getOOAUnits();
        battlefield.removeOOAUnits();
        Task removeOOAUnitTask = new Task();
        for(int i = 0; i < OOAUnits.size; i++){
            removeOOAUnitTask.addThread(new Thread(battlefieldRenderer, battlefieldRenderer, OOAUnits.get(i)));
        }
        scheduler.addTask(removeOOAUnitTask);

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


            //set up outcome

        }else{

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

    protected void setOutcome(){
        IUnit initiator =  battlefield.getUnit(rowActor, colActor);
        IUnit target = battlefield.getUnit(rowTarget, colTarget);

        IUnit winner;
        IUnit loser;
        Array<Unit.DamageNotif> notifs;
        if(initiator.isOutOfAction() == !target.isOutOfAction()){
            winner = (initiator.isOutOfAction()) ? target : initiator;
            loser = (initiator.isOutOfAction()) ? initiator : target;
            notifs = notifBundles.get(initiator.isOutOfAction() ? 1 : 0);


            //TODO:

        }
    }

    @Override
    public boolean isExecuting() {
        return launched && battlefieldRenderer.isExecuting();
    }

    @Override
    public boolean isExecutionCompleted() {
        return launched && !battlefieldRenderer.isExecuting();
    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean valid = false;
        if(battlefield.isTileOccupied(rowActor0, colActor0)){
            IUnit attacker = battlefield.getUnit(rowActor0, colActor0);
            if(battlefield.isTileOccupiedByFoe(rowTarget0, colTarget0, attacker.getAllegeance())) {
                int rangeMin = attacker.getCurrentWeaponRangeMin(rowActor0, colActor0, battlefield);
                int rangeMax = attacker.getCurrentWeaponRangeMax(rowActor0, colActor0, battlefield);
                int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
                if (rangeMin <= dist && dist <= rangeMax) {
                    valid = true;
                }
            }
        }
        return valid;
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        boolean targetAtRange = false;
        int[] actorPos = battlefield.getUnitPos(actor);
        int rangeMin = actor.getCurrentWeaponRangeMin(actorPos[0], actorPos[1], battlefield);
        int rangeMax = actor.getCurrentWeaponRangeMax(actorPos[0], actorPos[1], battlefield);
        int dist;
        for(int r = row - rangeMax; r <= row + rangeMax; r++ ){
            for(int c = col - rangeMax; c <= col + rangeMax; c++ ){
                dist = Utils.dist(row, col, r, c);
                if(rangeMin <= dist
                        && dist <= rangeMax
                        && battlefield.isTileOccupiedByFoe(r, c, actor.getAllegeance())){
                    targetAtRange = true;
                    continue;
                }
            }
            if(targetAtRange)
                continue;
        }
        return targetAtRange;
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

    public int getDropRate(boolean ofInitiator){
        return (ofInitiator) ?
                Formulas.getDropRate(rowActor, colActor, rowTarget, colTarget, battlefield) :
                Formulas.getDropRate(rowTarget, colTarget, rowActor, colActor, battlefield);
    }





}
