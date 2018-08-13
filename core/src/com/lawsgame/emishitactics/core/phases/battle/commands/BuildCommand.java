package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification.Build;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class BuildCommand extends BattleCommand {
    protected Data.TileType buildingType;

    public BuildCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Data.TileType buildingType) {
        super(bfr, ActionChoice.BUILD, scheduler, false, true, false);
        this.buildingType = buildingType;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected void execute() {
        IUnit actor = battlefield.getUnit(rowActor, colActor);

        // update model
        battlefield.setTile(rowTarget, colTarget, buildingType, false);

        // push render task
        scheduler.addTask(new Task(battlefieldRenderer, new Build(rowTarget, colTarget, buildingType, actor)));

        // set outcome
        outcome.receivers.add(actor);
        outcome.experienceGained.add(choice.getExperience());

    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean valid = false;
        if(battlefield.isTileOccupied(rowActor0, colActor0) && battlefield.isTileExisted(rowTarget0, colTarget0)){

            int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
            if (choice.getRangeMin() <= dist && dist <= choice.getRangeMax()) {

                if(buildingType == Data.TileType.WATCH_TOWER
                        && battlefield.isTileOfType(rowTarget0, colTarget0, Data.TileType.PLAIN) ) {

                    valid = true;
                } else if (buildingType == Data.TileType.BRIDGE
                        && battlefield.isTileOfType(rowTarget0, colTarget0, Data.TileType.SHALLOWS)
                        && ((battlefield.isTileOfType(rowTarget0, colTarget0 + 1,Data.TileType.PLAIN) && battlefield.isTileOfType(rowTarget0, colTarget0 - 1, Data.TileType.PLAIN))
                            || (battlefield.isTileOfType(rowTarget0 + 1, colTarget0,Data.TileType.PLAIN) && battlefield.isTileOfType(rowTarget0 - 1, colTarget0, Data.TileType.PLAIN)))) {

                    valid = true;
                }
            }
        }
        return valid;
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        boolean targetAtRange = false;
        int rangeMin = choice.getRangeMin();
        int rangeMax = choice.getRangeMax();
        int dist;
        loop:
        {
            for (int r = row - rangeMin; r <= row + rangeMax; r++) {
                for (int c = col - rangeMin; c <= col + rangeMax; c++) {
                    dist = Utils.dist(row, col, r, c);
                    if (rangeMin <= dist && dist <= rangeMax
                            && battlefield.isTileExisted(r, c)) {


                        if (buildingType == Data.TileType.WATCH_TOWER
                                && battlefield.isTileOfType(r, c, Data.TileType.PLAIN)) {

                            targetAtRange = true;
                            break loop;
                        } else if (buildingType == Data.TileType.BRIDGE
                                && battlefield.isTileOfType(r, c, Data.TileType.SHALLOWS)
                                && ((battlefield.isTileOfType(r, c + 1, Data.TileType.PLAIN) && battlefield.isTileOfType(r, c - 1, Data.TileType.PLAIN))
                                || (battlefield.isTileOfType(r + 1, c, Data.TileType.PLAIN) && battlefield.isTileOfType(r - 1, c, Data.TileType.PLAIN)))) {

                            targetAtRange = true;
                            break loop;
                        }

                    }
                }
            }
        }

        return targetAtRange;
    }

    public Data.TileType getBuildingType() {
        return buildingType;
    }
}
