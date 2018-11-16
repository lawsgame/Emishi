package com.lawsgame.emishitactics.core.phases.battle.commands.event;

import com.badlogic.gdx.utils.Array;
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
    private Array<Unit> reinforcements;
    private Array<int[]> entryPoints;
    private Array<int[]> deploymentPositions;
    private Array<Array<int[]>> paths;


    public ReinforcementEvent(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, scheduler, playerInventory);
        this.reinforcements = new Array<Unit>();
        this.entryPoints = new Array<int[]>();
        this.deploymentPositions = new Array<int[]>();
        this.paths = new Array<Array<int[]>>();
    }

    public static ReinforcementEvent addTrigger(final int turn, final BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, final MilitaryForce currentArmy){

        Model.Trigger trigger = new Model.Trigger( true, true) {

            @Override
            public boolean isTriggered(Object data) {
                return data instanceof Notification.BeginArmyTurn && bfr.getModel().getCurrentArmy() == currentArmy && turn <= bfr.getModel().getTurn();
            }
        };

        ReinforcementEvent event = new ReinforcementEvent(bfr, scheduler, playerInventory);
        trigger.addEvent(event);
        bfr.getModel().add(trigger);
        return event;
    }

    @Override
    protected void execute() {

        Unit unit;
        StandardTask deployTask = new StandardTask();
        StandardTask.RendererThread thread;
        for(int i = 0; i < reinforcements.size; i++) {
            unit = reinforcements.get(i);


            //update model
            bfr.getModel().deploy(deploymentPositions.get(i)[0], deploymentPositions.get(i)[1], unit, false);

            //set renderers
            BattleUnitRenderer bur = bfr.addUnitRenderer(entryPoints.get(i)[0], entryPoints.get(i)[1], unit);
            bur.setVisible(false);

            // push render task
            thread = new StandardTask.RendererThread(bfr.getUnitRenderer(unit));
            thread.addQuery(Notification.Visible.get(true));
            thread.addQuery(bfr.getModel(), new Notification.Walk(unit, paths.get(i), true));
            deployTask.addThread(thread);
        }

        scheduleRenderTask(deployTask);
        handleEvents(null, -1,-1);
    }

    @Override
    protected void unexecute() { }

    @Override
    public boolean isApplicable() {
        paths.clear();
        Array<int[]> path;
        for(int i = 0; i < deploymentPositions.size; i++){

            // check unit requirements
            if(!bfr.getModel().isUnitDeployable(reinforcements.get(i))){
                return false;
            }

            // check tile requirements
            if(bfr.getModel().isTileOccupied(deploymentPositions.get(i)[0], deploymentPositions.get(i)[1])){
                return false;
            }

            // deployment tile should be different for each soldier
            for (int j = 0; j < deploymentPositions.size; j++) {
                if (deploymentPositions.get(j)[0] == deploymentPositions.get(i)[0] && deploymentPositions.get(j)[1] == deploymentPositions.get(i)[0]) {
                    return false;
                }
            }

            // path must be valid
            path = bfr.getModel().getShortestPath(
                    entryPoints.get(i)[0],
                    entryPoints.get(i)[1],
                    deploymentPositions.get(i)[0],
                    deploymentPositions.get(i)[1],
                    reinforcements.get(i).has(Data.Ability.PATHFINDER),
                    reinforcements.get(i).getArmy().getAffiliation());
            if(path.size == 0){
                return false;
            }else{
                paths.add(path);
            }
        }
        return true;
    }

    @Override
    public boolean isUndoable() {
        return false;
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
    public void addStiffeners(Unit unit, int entryRow, int  entryCol, int deploymentRow, int deploymentCol){
        reinforcements.add(unit);
        entryPoints.add(new int[]{entryRow, entryCol});
        deploymentPositions.add(new int[]{deploymentRow, deploymentCol});
    }
}
