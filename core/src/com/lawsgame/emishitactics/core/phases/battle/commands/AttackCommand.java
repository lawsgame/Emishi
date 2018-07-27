package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Thread;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AttackCommand extends BattleCommand {
    private boolean launched;
    private boolean executionCompleted;


    public AttackCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.ATTACK, scheduler);
    }

    @Override
    public void init() {
        launched = false;
    }

    @Override
    protected void execute() {
        launched = true;

        performAttack(rowActor, colActor, rowTarget, colTarget);
        if(!battlefield.getUnit(rowTarget, colTarget).isOutOfAction()
                && isTargetValid(rowTarget, colTarget, rowActor, colActor)){
            performAttack(rowTarget, colTarget, rowActor, colActor);
        }

        Array<IUnit> OOAUnits = battlefield.getOOAUnits();
        battlefield.removeOOAUnits();
        Task removeOOAUnitTask = new Task();
        for(int i = 0; i < OOAUnits.size; i++){
            removeOOAUnitTask.addThread(new Thread(battlefieldRenderer, battlefieldRenderer, OOAUnits.get(i)));
        }
        scheduler.addTask(removeOOAUnitTask);

    }


    protected void performAttack(int rowAttacker, int colAttacker, int rowTarget, int colTarget){
        IUnit initiator = battlefield.getUnit(rowAttacker, colAttacker);
        IUnit target = battlefield.getUnit(rowTarget, colTarget);

        // FIRST ATTACK

        Task task = new Task();
        Thread initiatorThread = new Thread(battlefieldRenderer.getUnitRenderer(initiator));
        Thread targetThread = new Thread(battlefieldRenderer.getUnitRenderer(target));
        Array<Thread> defendersThreads = new Array<Thread>();

        initiator.setOrientation(Utils.getOrientationFromCoords(rowAttacker, colAttacker, rowTarget, colTarget));

        initiatorThread.addQuery(initiator.getOrientation());
        initiatorThread.addQuery(Data.AnimationId.ATTACK);

        boolean backstabbed = initiator.getOrientation() == target.getOrientation();
        int hitrate = getAttackAccuracy(rowAttacker, colAttacker, rowTarget, colTarget, backstabbed) - getAvoidance(rowAttacker, colAttacker, rowTarget, colTarget);

        BattleUnitRenderer bur;
        if(Utils.getMean(2,100) < hitrate){

            if(!backstabbed){
                targetThread.addQuery(initiator.getOrientation().getOpposite());
            }

            int dealtdamage = getAttackMight(rowAttacker, colAttacker, rowTarget, colTarget) - getDefense(rowAttacker, colAttacker, rowTarget, colTarget);
            Array<Unit.DamageNotif> notifs = target.applyDamage(dealtdamage, false);
            for(int i = 0; i < notifs.size; i++){
                notifs.get(i).critical = false;
                notifs.get(i).backstab = backstabbed && i == 0;
                notifs.get(i).fleeingOrientation = initiator.getOrientation();
                if(i != 0){
                    bur = battlefieldRenderer.getUnitRenderer(notifs.get(i).wounded);

                    defendersThreads.add(new Thread(bur, bur, notifs.get(i)));
                }
            }

            targetThread.addQuery(notifs.get(0));
            if(!backstabbed && !target.isOutOfAction()){
                targetThread.addQuery(target.getOrientation());
            }

        }else{

            targetThread.addQuery(initiator.getOrientation().getOpposite());
            targetThread.addQuery(Data.AnimationId.DODGE);
            targetThread.addQuery(target.getOrientation());
        }

        // restore the model and view orientation matching

        task.addThread(initiatorThread);
        task.addThread(targetThread);
        task.addllThreads(defendersThreads);
        scheduler.addTask(task);
    }


    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public boolean isEndTurnCommandOnly() {
        return false;
    }

    @Override
    public boolean isExecuting() {
        return launched &&  !executionCompleted;
    }

    @Override
    public boolean isExecutionCompleted() {
        return executionCompleted;
    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean validate = false;
        if(battlefield.isTileOccupied(rowActor0, colActor0)){
            IUnit attacker = battlefield.getUnit(rowActor0, colActor0);
            if(battlefield.isTileOccupiedByFoe(rowTarget0, colTarget0, attacker.getAllegeance())) {
                int rangeMin = attacker.getCurrentWeaponRangeMin(rowActor0, colActor0, battlefield);
                int rangeMax = attacker.getCurrentWeaponRangeMax(rowActor0, colActor0, battlefield);
                int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
                if (rangeMin <= dist && dist <= rangeMax) {
                    validate = true;
                }
            }
        }
        return validate;
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        boolean targetAtRange = false;
        int[] actorPos = battlefield.getUnitPos(actor);
        int rangeMin = actor.getCurrentWeaponRangeMin(actorPos[0], actorPos[1], battlefield);
        int rangeMax = actor.getCurrentWeaponRangeMax(actorPos[0], actorPos[1], battlefield);
        int dist;
        for(int r = row - rangeMin; r <= row + rangeMax; r++ ){
            for(int c = col - rangeMin; c <= col + rangeMax; c++ ){
                dist = Utils.dist(row, col, r, c);
                if(rangeMin <= dist && dist <= rangeMax && battlefield.isTileOccupiedByFoe(r, c, actor.getAllegeance())){
                    targetAtRange = true;
                    continue;
                }
            }
            if(targetAtRange) continue;
        }
        return targetAtRange;
    }

    @Override
    public void undo() { }

    @Override
    public void redo() { }

    // -------------------- COMODITY BATTLE RESOLUTION METHODS ------------------


    public int getAttackMight(int rowAttacker0, int colAttacker0, int rowTarget0, int colTarget0){
        int attackMight = 0;
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0) && battlefield.isTileOccupied(rowTarget0, colTarget0)){
            IUnit attacker = battlefield.getUnit(rowAttacker0, colAttacker0);
            attackMight += attacker.getCurrentWeapon().getDamage();
            attackMight += attacker.getAppStrength();
            attackMight += battlefield.getTile(rowAttacker0, colAttacker0).getAttackMightBonus();
        }
        return attackMight;
    }

    public int getAttackAccuracy(int rowAttacker0, int colAttacker0, int rowTarget0, int colTarget0, boolean backstab){
        int attackAccuracy = 0;
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0) && battlefield.isTileOccupied(rowTarget0, colTarget0)){
            IUnit attacker = battlefield.getUnit(rowAttacker0, colAttacker0);
            attackAccuracy += attacker.getCurrentWeapon().getAccuracy() ;
            attackAccuracy += attacker.getAppDexterity() * Data.DEX_FACTOR_ATT_ACC;
            attackAccuracy += battlefield.getTile(rowAttacker0, colAttacker0).getAttackAccBonus();
            attackAccuracy += Data.HIT_RATE_BACK_ACC_BONUS;
            if(attackAccuracy > 100) attackAccuracy = 100;
        }
        return attackAccuracy;
    }

    public int getDefense(int rowAttacker0, int colAttacker0, int rowTarget0, int colTarget0){
        int defense = 0;
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0) && battlefield.isTileOccupied(rowTarget0, colTarget0)){
            IUnit target = battlefield.getUnit(rowTarget0, colTarget0);
            IUnit attacker = battlefield.getUnit(rowAttacker0, colAttacker0);
            defense += target.getAppDefense(attacker.getCurrentWeapon().getDamageType());
            defense += battlefield.getTile(rowTarget0, colTarget0).getDefenseBonus();
        }
        return defense;
    }

    public int getAvoidance(int rowAttacker0, int colAttacker0, int rowTarget0, int colTarget0){
        int avoidance = 0;
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0) && battlefield.isTileOccupied(rowTarget0, colTarget0)){
            IUnit target = battlefield.getUnit(rowTarget0, colTarget0);
            avoidance += target.getAppAvoidance();
            avoidance += battlefield.getTile(rowTarget0, colTarget0).getAvoidBonus();
        }
        return avoidance;
    }

}
