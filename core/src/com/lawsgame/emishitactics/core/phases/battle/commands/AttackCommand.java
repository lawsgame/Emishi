package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification.ApplyDamage;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.RendererThread;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AttackCommand extends BattleCommand {
    protected boolean retaliationAllowed;
    protected MoveCommand moveCommand;
    protected SwitchPositionCommand switchPositionCommand;
    protected IUnit targetDefender;
    protected IUnit initiatorDefender;

    protected AttackCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, boolean retaliationAllowed) {
        super(bfr, Data.ActionChoice.ATTACK, scheduler, playerInventory, false);
        this.retaliationAllowed = retaliationAllowed;
        this.moveCommand = new MoveCommand(bfr, scheduler, playerInventory);
        this.switchPositionCommand = new SwitchPositionCommand(bfr, scheduler, playerInventory);
        this.moveCommand.setFree(true);
        this.switchPositionCommand.setFree(true);
    }

    public AttackCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        this(bfr, scheduler, playerInventory, true);

    }

    @Override
    protected void init(){
        super.init();
        targetDefender = setDefender(rowTarget, colTarget);
        initiatorDefender = setDefender(rowActor, colActor);
    }

    @Override
    protected void execute() {

        switchTargetGuardianPosition(rowTarget, colTarget, targetDefender);
        Array<ApplyDamage> notifBundleActor = performAttack(rowActor, colActor, rowTarget, colTarget, getTarget(), false);
        addOutcomeData(rowActor, colActor, notifBundleActor);

        if(retaliationAllowed
                && !battlefield.getUnit(rowTarget, colTarget).isOutOfAction()
                && isTargetValid(rowTarget, colTarget, rowActor, colActor)){

            switchTargetGuardianPosition(rowActor, colActor, initiatorDefender);
            Array<ApplyDamage> notifBundleTarget = performAttack(rowTarget, colTarget, rowActor, colActor, getInitiator(), true);
            addOutcomeData(rowTarget, colTarget, notifBundleTarget);
        }

        removeOutOfActionUnits();
    }

    protected Array<ApplyDamage> performAttack(int rowAttacker, int colAttacker, int rowTarget, int colTarget, IUnit target0, boolean retaliate){
        Array<ApplyDamage> notifs = new Array<ApplyDamage>();
        IUnit attacker = battlefield.getUnit(rowAttacker, colAttacker);
        IUnit defender = battlefield.getUnit(rowTarget, colTarget);

        StandardTask task = new StandardTask();
        RendererThread attackerRendererThread = new RendererThread(battlefieldRenderer.getUnitRenderer(attacker));
        RendererThread targetRendererThread = new RendererThread(battlefieldRenderer.getUnitRenderer(defender));
        Array<RendererThread> defendersThreads = new Array<RendererThread>();

        Data.Orientation attackerReorientation = Utils.getOrientationFromCoords(rowAttacker, colAttacker, rowTarget, colTarget);
        if(!retaliate) attacker.setOrientation(attackerReorientation);

        attackerRendererThread.addQuery(attackerReorientation);
        attackerRendererThread.addQuery(Data.AnimId.ATTACK);

        boolean backstabbed = attacker.getOrientation() == defender.getOrientation() && !retaliate;
        int hitrate = Formulas.getHitRate(rowAttacker, colAttacker, rowTarget, colTarget, battlefield);

        if(Utils.getMean(2,100) < hitrate){
            BattleUnitRenderer bur;

            // model-wise change
            int dealtdamage = Formulas.getDealtDamage(rowAttacker, colAttacker, rowTarget, colTarget, battlefield);
            notifs = defender.applyDamage(dealtdamage, false);

            // view-wise scheduling
            if(!backstabbed){
                targetRendererThread.addQuery(attacker.getOrientation().getOpposite());
            }
            for(int i = 0; i < notifs.size; i++){
                notifs.get(i).critical = false;
                notifs.get(i).backstab = backstabbed && i == 0;
                notifs.get(i).fleeingOrientation = attacker.getOrientation();
                if(i != 0){
                    bur = battlefieldRenderer.getUnitRenderer(notifs.get(i).wounded);

                    defendersThreads.add(new RendererThread(bur, notifs.get(i)));
                }
            }
            targetRendererThread.addQuery(notifs.get(0));
            if(!backstabbed && !defender.isOutOfAction()){
                targetRendererThread.addQuery(defender.getOrientation());
            }

        }else{
            // view-wise scheduling

            /*
             *  No need to do it if retaliation, the initiator who is now the defender is already oriented in the right direction
             *  Futhermore, the target/attacker orientation model-wise remains as it was before the attack and consequently, can hold any
             *  value possible and then does permit to build correctly the initiator/defender orientation anyway.
              */
            if(!retaliate)
                targetRendererThread.addQuery(attacker.getOrientation().getOpposite());
            targetRendererThread.addQuery(Data.AnimId.DODGE);
            targetRendererThread.addQuery(defender.getOrientation());
        }

        // reset the attacker orientation if he is retaliated
        if(retaliate)
            attackerRendererThread.addQuery(attacker.getOrientation());

        task.addThread(attackerRendererThread);
        task.addThread(targetRendererThread);
        task.addllThreads(defendersThreads);
        scheduleRenderTask(task);

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

                scheduleRenderTask(new StandardTask(battlefieldRenderer.getUnitRenderer(target0), target0.getOrientation()));
            } else {

                switchPositionCommand.apply(targetPos[0], targetPos[1], rowTarget0, colTarget0);
                Area.UnitArea area = battlefield.addGuardedArea(targetPos[0], targetPos[1]);

                StandardTask task = new StandardTask();
                task.addThread(new RendererThread(battlefieldRenderer, area));
                task.addThread(new RendererThread(battlefieldRenderer.getUnitRenderer(defender), defender.getOrientation()));
                task.addThread(new RendererThread(battlefieldRenderer.getUnitRenderer(target0), target0.getOrientation()));
                scheduleRenderTask(task);
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
        if(battlefield.isTileGuarded(rowTarget, colTarget, defender.getArmy().getAffiliation())){
            defender = battlefield.getAvailableGuardians(rowTarget, colTarget, defender.getArmy().getAffiliation()).random();
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
                outcome.expHolders.add(new ExperiencePointsHolder(receiver, experience));
                lootRate = Formulas.getLootRate(rowReceiver, colReceiver, battlefield);
                dicesResult = Utils.getMean(1, 100);
                if(dicesResult < lootRate){
                    droppedItem = target.getRandomlyDroppableItem();
                    outcome.droppedItemHolders.add(new DroppedItemHolder(droppedItem, receiver.isMobilized() && receiver.getArmy().isPlayerControlled()));
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
                            droppedItem = squad.get(i).getRandomlyDroppableItem();
                            outcome.droppedItemHolders.add(new DroppedItemHolder(droppedItem, receiver.isMobilized() && receiver.getArmy().isPlayerControlled()));
                        }
                    }
                }

                // add experience points
                squad = receiver.getSquad(true);
                for(int i = 0; i < squad.size; i++) {
                    outcome.expHolders.add(new ExperiencePointsHolder(squad.get(i), experience));
                }

            }
        }


    }

    protected void removeOutOfActionUnits(){
        Array<IUnit> OOAUnits = battlefield.getOOAUnits();
        battlefield.removeOOAUnits(false);
        StandardTask removeOOAUnitTask = new StandardTask();
        for(int i = 0; i < OOAUnits.size; i++)
            removeOOAUnitTask.addThread(new RendererThread(battlefieldRenderer, OOAUnits.get(i)));
        scheduleRenderTask(removeOOAUnitTask);
    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return isEnemyTargetValid(rowActor0, colActor0, rowTarget0, colTarget0, false);
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        return isEnemyAtActionRange(row, col, actor, false);
    }


    // -------------------- COMODITY BATTLE PANEL METHODS ------------------



    public int getHitRate(boolean retaliation){
        int[] defenderPos = (retaliation) ? battlefield.getUnitPos(initiatorDefender) : battlefield.getUnitPos(targetDefender);
        int hitRate = 0;
        if(!retaliation){
            hitRate = Formulas.getHitRate(rowActor, colActor, defenderPos[0], defenderPos[1], battlefield);
        }else if(retaliation && isTargetValid(rowTarget, colTarget, rowActor, colActor)){
            hitRate = Formulas.getHitRate(rowTarget, colTarget, defenderPos[0], defenderPos[1], battlefield);
        }
        return hitRate;
    }

    public int getDealtDamage(boolean retaliation){
        int[] defenderPos = (retaliation) ? battlefield.getUnitPos(initiatorDefender) : battlefield.getUnitPos(targetDefender);
        int dealtDamage = 0;
        if(!retaliation){
            dealtDamage = Formulas.getDealtDamage(rowActor, colActor, defenderPos[0], defenderPos[1], battlefield);
        }else if(retaliation && isTargetValid(rowTarget, colTarget, rowActor, colActor)){
            dealtDamage = Formulas.getDealtDamage(rowTarget, colTarget, defenderPos[0], defenderPos[1], battlefield);
        }

        return dealtDamage;
    }

    public int getLootRate(boolean retaliation){
        int lootRate = 0;
        if(!retaliation){
            lootRate = Formulas.getLootRate(rowActor, colActor, battlefield);
        }else if(retaliation && isTargetValid(rowTarget, colTarget, rowActor, colActor)){
            lootRate = Formulas.getLootRate(rowTarget, colTarget, battlefield);
        }
        return lootRate;
    }

    public IUnit getTargetDefender(){
        return targetDefender;
    }


    public IUnit getInitiatorDefender() {
        return initiatorDefender;
    }
}
