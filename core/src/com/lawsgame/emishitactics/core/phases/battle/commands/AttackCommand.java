package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AttackCommand extends BattleCommand {
    boolean executing;
    boolean executionCompleted;

    public AttackCommand(BattlefieldRenderer bfr) {
        super(bfr, Data.ActionChoice.ATTACK);
        init();
    }

    @Override
    public void init() {
        executing = false;
        executionCompleted = false;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public boolean isFree() {
        return false;
    }

    @Override
    public boolean isEndTurnCommandOnly() {
        return false;
    }

    @Override
    public boolean isExecuting() {
        return executing;
    }

    @Override
    public boolean isExecutionCompleted() {
        return executionCompleted;
    }

    @Override
    public boolean isTargetValid() {
        boolean validate = false;
        if(battlefield.isTileOccupied(rowActor, colActor)){

            Unit actor = battlefield.getUnit(rowActor, colActor);
            int dist = Utils.dist(rowActor, colActor, rowTarget, colTarget);
            int rangeMin = actor.getCurrentRangeMin();
            Data.TileType tile = battlefield.getTile(rowActor, colActor);
            boolean bannerAtRange = battlefield.isStandardBearerAtRange(actor, rowActor, colActor);
            int rangeMax = actor.getCurrentRangeMax(tile, bannerAtRange);
            if(battlefield.isTileOccupiedByFoe(rowActor, colActor, actor.getAllegeance()) && rangeMin <= dist && dist <= rangeMax){

                validate = true;
            }
        }
        return validate;
    }

    @Override
    public boolean atActionRange(int row, int col) {
        boolean atRange = false;
        if (battlefield.isTileOccupied(rowActor, colActor)) {

            Unit actor = battlefield.getUnit(rowActor, colActor);
            if(battlefield.isTileAvailable(row, col, actor.has(Data.PassiveAbility.PATHFINDER))) {

                Data.TileType tile = battlefield.getTile(row, col);
                boolean bannerAtRange = battlefield.isStandardBearerAtRange(actor, row, col);
                int rangeMin = actor.getCurrentRangeMin();
                int rangeMax = actor.getCurrentRangeMax(tile, bannerAtRange);

                int dist;
                for (int r = row - rangeMin; r <= row + rangeMax; row++) {
                    for (int c = row - rangeMin; c <= row + rangeMax; row++) {
                        dist = Utils.dist(row, col, r, c);
                        if ( rangeMin <= dist && dist <= rangeMax && battlefield.isTileOccupiedByFoe(r, c, actor.getAllegeance())) {
                            atRange = true;
                        }
                    }
                }
            }
        }
        return atRange;
    }

    @Override
    public void execute() {

        if(battlefield.isTileOccupied(rowActor, colActor) && battlefield.isTileOccupied(rowTarget, colTarget)){
            Unit actor = battlefield.getUnit(rowActor, colActor);
            Unit target = battlefield.getUnit(rowTarget, colTarget);
            Data.TileType actorTile = battlefield.getTile(rowActor, colActor);
            Data.TileType targetTile = battlefield.getTile(rowTarget, colTarget);
            boolean actorBannerAtRange = battlefield.isStandardBearerAtRange(actor, rowActor, colActor);
            boolean targetBannerAtRange = battlefield.isStandardBearerAtRange(target, rowActor, colActor);
            int hitrate = actor.getHitRate(target, targetTile, actorBannerAtRange, targetBannerAtRange);
            int criticalHitRate = actor.getCriticalHitRate(actorBannerAtRange, target, hitrate);

            // first phase, the attacker
            int diceResult = Utils.getMean(2,100);
            if(diceResult < hitrate){

            }
        }
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }

    @Override
    public void update(float dt) {

    }
}
