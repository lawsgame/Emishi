package com.lawsgame.emishitactics.core.phases.battle.commands.event;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.Model;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class ReinforcementEvent extends BattleCommand {
    private Array<StiffenerData> stiffenerData;


    public ReinforcementEvent(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, scheduler, playerInventory);
        this.stiffenerData = new Array<StiffenerData>();
    }

    public static ReinforcementEvent addTrigger(final int turn, final BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, final MilitaryForce currentArmy){
        ReinforcementEvent event = new ReinforcementEvent(bfr, scheduler, playerInventory);
        Model.Trigger trigger = new Model.Trigger( true, event) {
            @Override
            public boolean isTriggerable(Object data) {
                return data instanceof Notification.BeginArmyTurn && bfr.getModel().getTurnSolver().getCurrentArmy() == currentArmy && turn <= bfr.getModel().getTurnSolver().getTurn();
            }

            @Override
            public String toString(){
                return "Reinforcmeent Trigger on turn "+turn+" at the beginning of the turn of " +currentArmy.getName();
            }
        };
        bfr.getModel().add(trigger);
        return event;
    }

    /**
     * allow to add one more unit to deploy.
     *
     * @param unit : any unit not yet deployed
     * @param entryRow : row of the tile where the unit begins to appear
     * @param entryCol : col of the tile where the unit begins to appear
     * @param deploymentRow : reachable tile row where the unit stand at the end of his move
     * @param deploymentCol : reachable tile col where the unit stand at the end of his move
     */
    public void addStiffener(Unit unit, int entryRow, int  entryCol, int deploymentRow, int deploymentCol){
        addStiffener(new StiffenerData(entryRow, entryCol, deploymentRow, deploymentCol, unit));
    }

    public void addStiffener(StiffenerData data){
        if(data.isValid(bfr.getModel())) {
            stiffenerData.add(data);
        }
    }

    @Override
    protected void execute() {
        Array<int[]> deploymentPositions = new Array<int[]>();
        for(int i = 0; i < stiffenerData.size; i++){
            deploymentPositions.add(new int[]{stiffenerData.get(i).deployRow, stiffenerData.get(i).deployCol});
        }
        float[] focusPoint = bfr.getCentriod(deploymentPositions);
        scheduleCameraTrip(focusPoint[0], focusPoint[1], Data.CAM_WAITING_TIME_BEFORE_PROCEEDING_TO_THE_NEXT_ACTION);

        Unit unit;
        StandardTask deployTask = new StandardTask();
        StandardTask.RendererSubTaskQueue thread;
        Notification.UnitAppears eventNotif = new Notification.UnitAppears();
        for(int i = 0; i < stiffenerData.size; i++) {
            //update model
            unit = stiffenerData.get(i).unit;
            bfr.getModel().deploy(deploymentPositions.get(i)[0], deploymentPositions.get(i)[1], unit, false);
            eventNotif.newlyDeployed.add(unit);
            //set renderers
            BattleUnitRenderer bur = bfr.addUnitRenderer(stiffenerData.get(i).entryRow, stiffenerData.get(i).entryCol, unit);
            bur.setVisible(false);
            // push render task
            thread = new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(unit));
            thread.addQuery(Notification.Visible.get(true));
            thread.addQuery(bfr.getModel(), new Notification.Walk(unit, stiffenerData.get(i).path, true));
            deployTask.addParallelSubTask(thread);
        }
        scheduleRenderTask(deployTask);
        handleEvents(eventNotif, -1,-1);
    }

    @Override
    protected void unexecute() { }

    @Override
    public boolean isApplicable() {
        Array<int[]> path;
        StiffenerData data;
        for(int i = 0; i < stiffenerData.size; i++){
            data = stiffenerData.get(i);
            data.path.clear();

            // check unit requirements
            if(!bfr.getModel().isUnitDeployable(data.unit)){
                return false;
            }

            // check tile requirements
            if(bfr.getModel().isTileOccupied(data.deployRow, data.deployCol)){
                return false;
            }

            // deployment tile should be different for each soldier
            for (int j = 0; j < stiffenerData.size; j++) {
                if (i != j && stiffenerData.get(j).deployRow == data.deployRow && stiffenerData.get(j).deployCol == data.deployCol) {
                    return false;
                }
            }

            // path must be valid
            path = bfr.getModel().getShortestPath(
                    data.entryRow,
                    data.entryCol,
                    data.deployRow,
                    data.deployCol,
                    data.unit.has(Data.Ability.PATHFINDER),
                    data.unit.getArmy().getAffiliation(), false);

            if(path.size == 0){
                return false;
            }else{
                data.path = path;
            }
        }
        return true;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }


    @Override
    public String toString() {
        String res = "Reinforcement Event";
        for(int i = 0; i < stiffenerData.size; i++){
            res = res + "   "+stiffenerData.get(i).toString();
        }
        return res;
    }



    public static class StiffenerData{
        public int entryRow;
        public int entryCol;
        public int deployRow;
        public int deployCol;
        public Array<int[]> path;
        public Unit unit;

        public StiffenerData(int entryRow, int entryCol, int deployRow, int deployCol, Unit unit) {
            this.entryRow = entryRow;
            this.entryCol = entryCol;
            this.deployRow = deployRow;
            this.deployCol = deployCol;
            this.unit = unit;
            this.path = new Array<int[]>();
        }

        @Override
        public String toString() {
            return "StiffenerData{" +
                    "entryRow=" + entryRow +
                    ", entryCol=" + entryCol +
                    ", deployRow=" + deployRow +
                    ", deployCol=" + deployCol +
                    ", unit=" + unit +
                    '}';
        }

        public boolean isValid(Battlefield bf) {
            return unit !=null && bf.isTileReachable(deployRow, deployCol, unit.has(Data.Ability.PATHFINDER));
        }
    }
}
