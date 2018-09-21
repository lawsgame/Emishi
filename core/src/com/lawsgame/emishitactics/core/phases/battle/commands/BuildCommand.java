package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data.Ability;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification.Build;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class BuildCommand extends BattleCommand {
    protected Data.TileType buildingType;

    public BuildCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, Data.TileType buildingType) {
        super(bfr, ActionChoice.BUILD, scheduler,  playerInventory, false);
        this.buildingType = buildingType;
    }

    @Override
    protected void execute() {

        // update model
        battlefield.setTile(rowTarget, colTarget, buildingType, false);

        // push render task
        scheduleRenderTask(new StandardTask(battlefieldRenderer, new Build(rowTarget, colTarget, buildingType, getInitiator())));

        // set outcome
        outcome.expHolders.add(new ExperiencePointsHolder(getInitiator(), choice.getExperience()));

    }

    @Override
    public boolean canbePerformedBy(IUnit actor) {
        return actor.has(Ability.BUILD) && super.canbePerformedBy(actor);
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
