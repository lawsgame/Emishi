package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class RevealRecruitCommand extends SelfInflitedCommand {

    public RevealRecruitCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, Data.ActionChoice.REVEAL_RECRUIT , scheduler, playerInventory);
        setRegisterAction(false);
    }

    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator) && initiator.isMobilized();
    }

    @Override
    public boolean isTargetValid(Unit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean targetValid = false;
        if(bfr.getModel().isTileExisted(rowTarget0, colTarget0) && bfr.getModel().getTile(rowTarget0, colTarget0).isHidingRecruit()) {
            Unit recruit = bfr.getModel().getTile(rowTarget0, colTarget0).getRecruit(false);
            if(!bfr.getModel().isUnitDeployed(recruit)) {
                targetValid = bfr.getModel().isTileAvailable(rowTarget0 + 1, colTarget0, recruit.has(Data.Ability.PATHFINDER));
                targetValid = targetValid || bfr.getModel().isTileAvailable(rowTarget0 - 1, colTarget0, recruit.has(Data.Ability.PATHFINDER));
                targetValid = targetValid || bfr.getModel().isTileAvailable(rowTarget0, colTarget0 + 1, recruit.has(Data.Ability.PATHFINDER));
                targetValid = targetValid || bfr.getModel().isTileAvailable(rowTarget0, colTarget0 - 1, recruit.has(Data.Ability.PATHFINDER));
            }
        }
        return targetValid && super.isTargetValid(initiator, rowActor0, colActor0, rowTarget0, colTarget0);

    }

    @Override
    protected void execute() {
        // update model
        Unit recruit = bfr.getModel().getTile(rowTarget, colTarget).getRecruit(true);
        MilitaryForce army = getInitiator().getArmy();
        army.add(recruit);
        army.appointSoldier(recruit, getInitiator().getSquadIndex());
        int[] deploymentTile = bfr.getModel().getAvailableNeighbourTiles(rowTarget, colTarget, getInitiator().has(Data.Ability.PATHFINDER)).random();
        bfr.getModel().deploy(deploymentTile[0], deploymentTile[1], recruit, false);
        Array<int[]> path = new Array<int[]>();
        path.add(deploymentTile);
        //instanciate recruit renderer
        BattleUnitRenderer recruitRenderer = bfr.addUnitRenderer(rowTarget, colTarget, recruit);
        recruitRenderer.setVisible(false);
        // push render task
        StandardTask task = new StandardTask();
        StandardTask.RendererSubTaskQueue recruitThread = new StandardTask.RendererSubTaskQueue(recruitRenderer);
        recruitThread.addQuery(Notification.Visible.get(true));
        recruitThread.addQuery(bfr.getModel(), new Notification.Walk(recruit, path, true));
        task.addParallelSubTask(recruitThread);
        scheduleRenderTask(task);
        // notif triggers
        Notification.UnitAppears notif = new Notification.UnitAppears();
        notif.newlyDeployed.add(recruit);
        handleEvents(notif);
    }

}
