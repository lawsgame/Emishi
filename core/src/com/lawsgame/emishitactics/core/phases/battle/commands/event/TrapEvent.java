package com.lawsgame.emishitactics.core.phases.battle.commands.event;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification.OOAReport;
import com.lawsgame.emishitactics.core.models.Notification.StepOn;
import com.lawsgame.emishitactics.core.models.Notification.TakeDamage;
import com.lawsgame.emishitactics.core.models.Notification.SetTile;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Model;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class TrapEvent extends BattleCommand{
    private int damage;
    private int row;
    private int col;

    public TrapEvent(BattlefieldRenderer bfr, AnimationScheduler scheduler, int damage, int row, int col) {
        super(bfr, scheduler);
        this.damage = damage;
        this.row = row;
        this.col = col;
    }



    public static TrapEvent addTrigger(final int rowTile, final int colTile, int damage, BattlefieldRenderer bfr, AnimationScheduler scheduler){
        return addTrigger(bfr.getModel().getTile(rowTile, colTile), rowTile, colTile, damage, bfr, scheduler);
    }

    private static TrapEvent addTrigger(Tile tile, final int rowTile, final int colTile, int damage, BattlefieldRenderer bfr, AnimationScheduler scheduler){

        TrapEvent event = new TrapEvent(bfr, scheduler, damage, rowTile, colTile);
        if(bfr.getModel().isTileExisted(rowTile, colTile) && tile != null) {

            Model.Trigger trigger = new Model.Trigger(false, false) {
                @Override
                public boolean isTriggered(Object data) {
                    if (data instanceof StepOn) {
                        StepOn stepOn = (StepOn) data;
                        return stepOn.rowTile == rowTile && stepOn.colTile == colTile;
                    }
                    return false;
                }
            };
            trigger.addEvent(event);
            tile.add(trigger);
        }

        return event;
    }



    @Override
    protected void execute() {

        IUnit victim = bfr.getModel().getUnit(row, col);
        TakeDamage takeDamage = victim.takeDamage(damage, false, true, 1f);
        takeDamage.set(true, false, 0, false, false, victim.getOrientation().getOpposite());

        StandardTask task = new StandardTask();
        task.addThread(new StandardTask.RendererThread(bfr.getUnitRenderer(victim), takeDamage));

        if(bfr.getModel().isTileExisted(row, col) && bfr.getModel().getTile(row, col).getType() != Data.TileType.TRAP) {

            Tile tile = new Tile(Data.TileType.TRAP);
            addTrigger(tile, row, col, damage, bfr, scheduler);
            bfr.getModel().setTile(row, col, tile, false);

            task.addThread(new StandardTask.RendererThread(bfr, new SetTile(row, col, tile)));
        }

        // HOLY TRINITY
        scheduler.addTask(task);
        OOAReport report = removeOutOfActionUnits();
        handleEvents(report, row, col);

    }

    @Override
    protected void unexecute() {

    }

    @Override
    public boolean isApplicable() {
        return bfr.getModel().isTileOccupied(row, col) && !bfr.getModel().getUnit(row, col).isOutOfAction();
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}
