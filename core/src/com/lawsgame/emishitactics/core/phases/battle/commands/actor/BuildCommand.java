package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data.Ability;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification.Build;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class BuildCommand extends ActorCommand {
    protected Data.TileType buildingType;

    public BuildCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, Data.TileType buildingType) {
        super(bfr, ActionChoice.BUILD, scheduler,  playerInventory, false);
        this.buildingType = buildingType;
    }

    @Override
    protected void execute() {

        // update model
        bfr.getModel().setTile(rowTarget, colTarget, buildingType, false);

        // push render task
        scheduleRenderTask(new StandardTask(bfr, new Build(rowTarget, colTarget, buildingType, getInitiator())));

        // set outcome
        outcome.expHolders.add(new ExperiencePointsHolder(getInitiator(), choice.getExperience()));

    }

    @Override
    public boolean isInitiatorValid(IUnit actor) {
        return actor.has(Ability.BUILD) && super.isInitiatorValid(actor);
    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean valid = false;
        if(bfr.getModel().isTileOccupied(rowActor0, colActor0) && bfr.getModel().isTileExisted(rowTarget0, colTarget0)){

            int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
            if (choice.getRangeMin() <= dist && dist <= choice.getRangeMax()) {

                if(buildingType == Data.TileType.WATCH_TOWER
                        && bfr.getModel().isTileOfType(rowTarget0, colTarget0, Data.TileType.PLAIN) ) {

                    valid = true;
                } else if (buildingType == Data.TileType.BRIDGE
                        && bfr.getModel().isTileOfType(rowTarget0, colTarget0, Data.TileType.SHALLOWS)
                        && ((bfr.getModel().isTileOfType(rowTarget0, colTarget0 + 1,Data.TileType.PLAIN) && bfr.getModel().isTileOfType(rowTarget0, colTarget0 - 1, Data.TileType.PLAIN))
                            || (bfr.getModel().isTileOfType(rowTarget0 + 1, colTarget0,Data.TileType.PLAIN) && bfr.getModel().isTileOfType(rowTarget0 - 1, colTarget0, Data.TileType.PLAIN)))) {

                    valid = true;
                }
            }
        }
        return valid;
    }

    @Override
    public Array<int[]> getTargetsAtRange(int row, int col, IUnit actor) {
        Array<int[]> targetsAtRange = new Array<int[]>();
        int rangeMin = choice.getRangeMin();
        int rangeMax = choice.getRangeMax();
        int dist;

        for (int r = row - rangeMin; r <= row + rangeMax; r++) {
            for (int c = col - rangeMin; c <= col + rangeMax; c++) {
                dist = Utils.dist(row, col, r, c);
                if (rangeMin <= dist && dist <= rangeMax && bfr.getModel().isTileExisted(r, c)) {

                    if (buildingType == Data.TileType.WATCH_TOWER
                            && bfr.getModel().isTileOfType(r, c, Data.TileType.PLAIN)) {

                        targetsAtRange.add(new int[]{r, c});
                    } else if (buildingType == Data.TileType.BRIDGE
                            && bfr.getModel().isTileOfType(r, c, Data.TileType.SHALLOWS)
                            && ((bfr.getModel().isTileOfType(r, c + 1, Data.TileType.PLAIN) && bfr.getModel().isTileOfType(r, c - 1, Data.TileType.PLAIN))
                            || (bfr.getModel().isTileOfType(r + 1, c, Data.TileType.PLAIN) && bfr.getModel().isTileOfType(r - 1, c, Data.TileType.PLAIN)))) {

                        targetsAtRange.add(new int[]{r, c});
                    }

                }
            }
        }


        return targetsAtRange;
    }

    public Data.TileType getBuildingType() {
        return buildingType;
    }
}
