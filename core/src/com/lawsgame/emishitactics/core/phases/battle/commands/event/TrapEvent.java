package com.lawsgame.emishitactics.core.phases.battle.commands.event;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification.OOAReport;
import com.lawsgame.emishitactics.core.models.Notification.SetTile;
import com.lawsgame.emishitactics.core.models.Notification.StepOn;
import com.lawsgame.emishitactics.core.models.Notification.TakeDamage;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.Model;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.ShortUnitPanel;

public class TrapEvent extends BattleCommand{
    private int damage;
    private int row;
    private int col;
    private ShortUnitPanel shortUnitPanel;

    public TrapEvent(BattleInteractionMachine bim, int damage, int row, int col) {
        this(bim.bfr, bim.scheduler, bim.player.getInventory(), damage, row, col, bim.pp.shortUnitPanel);
    }

    private TrapEvent(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, int damage, int row, int col, ShortUnitPanel unitPanel){
        super(bfr, scheduler, playerInventory);
        this.damage = damage;
        this.row = row;
        this.col = col;
        this.shortUnitPanel = unitPanel;
    }

    /**
     * for Test purpose
     *
     * @param bim
     * @param rowTile
     * @param colTile
     * @param damage
     * @return
     */
    public static TrapEvent addTrigger(BattleInteractionMachine bim, int rowTile, int colTile, int damage){
        TrapEvent event = addTrigger(bim.bfr, bim.scheduler, bim.player.getInventory(), bim.bfr.getModel().getTile(rowTile, colTile), rowTile, colTile, bim.pp.shortUnitPanel);
        event.damage = damage;
        return event;
    }



    public static TrapEvent addTrigger(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, Tile tile, final int rowTile, final int colTile, ShortUnitPanel shortUnitPanel){
        TrapEvent event = new TrapEvent(bfr, scheduler, playerInventory, Data.TRAP_DAMAGE, rowTile, colTile, shortUnitPanel);
        if(bfr.getModel().isTileExisted(rowTile, colTile) && tile != null) {
            Model.Trigger trigger = new Model.Trigger( false, event) {
                @Override
                public boolean isTriggerable(Object data) {
                    if (data instanceof StepOn) {
                        StepOn stepOn = (StepOn) data;
                        return stepOn.rowTile == rowTile && stepOn.colTile == colTile;
                    }
                    return false;
                }

                @Override
                public String toString() {
                    return "triggers when a unit step on the tile "+rowTile+" "+colTile;
                }
            };
            tile.add(trigger);
        }
        return event;
    }


    @Override
    protected void execute() {
        final Unit victim = bfr.getModel().getUnit(row, col);
        TakeDamage takeDamage = victim.takeDamage(damage, false, 0f);
        takeDamage.set(true, false, 0, false, false, victim.getOrientation().getOpposite());
        victim.setCrippled(true);

        StandardTask task = new StandardTask();
        task.addParallelSubTask(new StandardTask.CommandSubTask(0) {
            public void run() {
                shortUnitPanel.hide();
                shortUnitPanel.update(victim);
                shortUnitPanel.show();
            }
        });
        task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(victim), takeDamage));

        if(bfr.getModel().isTileExisted(row, col) && bfr.getModel().getTile(row, col).getType() != Data.TileType.TRAP) {

            Tile tile = new Tile(Data.TileType.TRAP);
            addTrigger(bfr, scheduler, outcome.playerInventory, tile, row, col, shortUnitPanel);
            bfr.getModel().setTile(row, col, tile, false);

            task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr, new SetTile(row, col, tile)));
        }

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

    @Override
    public String toString() {
        return "Trap event";
    }
}
