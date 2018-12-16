package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.TrapEvent;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class ScanAreaCommand extends SelfInflitedCommand {



    public ScanAreaCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, Data.ActionChoice.SCAN_AREA, scheduler, playerInventory, false);
    }

    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator) && initiator.has(Data.Ability.PATHFINDER);
    }

    @Override
    protected void execute() {
        StandardTask task = new StandardTask();
        Array<int[]> impactArea = getImpactArea();
        int r;
        int c;
        Tile tile;
        boolean getInfo = false;
        Notification.IgniteSparkles igniteSparkles = new Notification.IgniteSparkles();
        for(int i = 0; i < impactArea.size; i++){
            r = impactArea.get(i)[0];
            c = impactArea.get(i)[1];
            if(bfr.getModel().isTileExisted(r, c) && !bfr.getModel().getTile(r, c).isRevealed()){
                tile = bfr.getModel().getTile(r, c);
                tile.setRevealed(true);
                if(tile.searchForEventType(TrapEvent.class)) {
                    getInfo = true;
                    igniteSparkles.add(new int[]{r, c}, Data.SparkleType.TRAP);
                }else if(!tile.isLooted() && tile instanceof Tile.ItemTile){
                    getInfo = true;
                    igniteSparkles.add(new int[]{r, c}, Data.SparkleType.LOOT);
                }
            }
        }
        task.addThread(new StandardTask.RendererThread(bfr, igniteSparkles));
        if(getInfo) task.addThread(new StandardTask.RendererThread(bfr.getUnitRenderer(getInitiator()), Data.AnimId.SCAN_AREA));
        // push render task
        scheduleRenderTask(task);
        // set outcome
        if(getInfo) outcome.add(getInitiator(), choice.getExperience());
        // handle events
        handleEvents(this);
    }


    @Override
    public Array<int[]> getImpactArea(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return Utils.getEreaFromRange(bfr.getModel(), rowTarget0, colTarget0, 1, Data.SCAN_AREA_RANGE);
    }


}
