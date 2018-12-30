package com.lawsgame.emishitactics.core.phases.battle.commands.battle;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification.BeginArmyTurn;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class BeginArmyTurnCommand extends BattleCommand {
    protected MilitaryForce army;

    public BeginArmyTurnCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, MilitaryForce army) {
        super(bfr, scheduler, playerInventory);
        this.army = army;
    }


    @Override
    public boolean isApplicable() {
        return true;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    protected void execute() {
        if (army != null) {

            // UPDATE MORAL AND AP OF THE WHOLE ARMY

            army.replenishMoral(true);
            army.updateActionPoints();
            if (army.isPlayerControlled())
                bfr.getModel().incrementTurn();

            // REMOVE AREA ASSOCIATED WITH THE UNITS

            StandardTask task = new StandardTask();
            Area unitArea;
            for(int i =0; i < bfr.getModel().getUnitAreas().size; i++){
                if(bfr.getModel().getUnitAreas().get(i).getActor().getArmy() == army){
                    unitArea = bfr.getModel().getUnitAreas().removeIndex(i);
                    task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr, unitArea));
                }
            }
            scheduleRenderTask(task);


            // HANDLE EVENTS

            handleEvents(new BeginArmyTurn(army), -1, -1);

            // HEAL UNITS NEARBY HEALING TILES

            int rNei;
            int cNei;
            int healPower;
            Array<Task> restTasks = new Array<Task>();
            Array<int[]> neighborTileCoords;
            RestCommand restCommand = new RestCommand(bfr, scheduler, outcome.playerInventory);
            restCommand.setDecoupled(true);
            Battlefield bf = bfr.getModel();
            for(int r = 0; r < bf.getNbRows(); r++){
                for(int c = 0; c < bf.getNbColumns(); c++){
                    if(bf.isTileExisted(r, c)){
                        healPower = bf.getTile(r, c).getType().getHealPower();
                        if(healPower > 0){
                            restCommand.setHealPower(healPower, healPower);
                            neighborTileCoords = bf.getNeighbourTiles(r, c);
                            neighborTileCoords.add(new int[]{r, c});
                            for(int i = 0; i < neighborTileCoords.size; i++){
                                rNei = neighborTileCoords.get(i)[0];
                                cNei = neighborTileCoords.get(i)[1];
                                if(bf.isTileOccupied(rNei, cNei) && bf.getUnit(rNei, cNei).getArmy() == army){
                                    restCommand.setTarget(rNei, cNei);
                                    if(restCommand.apply()){
                                        restTasks.addAll(restCommand.confiscateTasks());
                                        outcome.merge(restCommand.getOutcome());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for(int i = 1; i < restTasks.size; i++){
                restTasks.get(0).merge(restTasks.get(i));
            }
            scheduleMultipleRenderTasks(restTasks);
        }
    }

    @Override
    protected void unexecute() {

    }

}
