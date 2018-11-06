package com.lawsgame.emishitactics.core.phases.battle.commands.event;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Model;
import com.lawsgame.emishitactics.core.models.interfaces.Trigger;
import com.lawsgame.emishitactics.core.phases.battle.commands.EventCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class TrapEvent extends EventCommand{
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

            Trigger trigger = new Trigger(false) {
                @Override
                public boolean isTriggered(Object data) {
                    if (data instanceof Notification.StepOn) {
                        Notification.StepOn stepOn = (Notification.StepOn) data;
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

        // update model
        IUnit victim = bfr.getModel().getUnit(row, col);
        Notification.ApplyDamage applyDamage = victim.applyDamage(damage, false);
        applyDamage.set(true, false, 0, false, false, victim.getOrientation().getOpposite());

        // push render task
        StandardTask task = new StandardTask();
        task.addThread(new StandardTask.RendererThread(bfr.getUnitRenderer(victim), applyDamage));

        if(bfr.getModel().isTileExisted(row, col) && bfr.getModel().getTile(row, col).getType() != Data.TileType.TRAP) {

            // update model
            Tile tile = new Tile(Data.TileType.TRAP);
            addTrigger(tile, row, col, damage, bfr, scheduler);
            bfr.getModel().setTile(row, col, tile, false);

            // push render task
            task.addThread(new StandardTask.RendererThread(bfr, new Notification.SetTile(row, col, tile)));
        }

        scheduler.addTask(task);
    }

    @Override
    public boolean isApplicable() {
        return bfr.getModel().isTileOccupied(row, col) && !bfr.getModel().getUnit(row, col).isOutOfAction();
    }
}
