package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.CommandThread;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionInfoPanel;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

import java.util.Stack;

public class ValidateTargetBIS extends BattleInteractionState implements Observer {
    private Stack<BattleCommand> historic;
    private BattleCommand currentCommand;
    private Area impactArea;

    private ChooseOrientationCommand orientationCommand;
    private SimpleCommand hideActionPanelCommand;
    private SimpleCommand removeActionPanelCommand;
    private float hidingTime;

    public ValidateTargetBIS(BattleInteractionMachine bim, BattleCommand currentCommand, Stack<BattleCommand> historic) {
        super(bim, true, false, false);
        this.historic = historic;
        this.currentCommand = currentCommand;

        Data.AreaType type = (currentCommand.getActionChoice().getRangedType() == Data.RangedBasedType.MOVE) ?
                Data.AreaType.MOVE_AREA :
                Data.AreaType.ACTION_AREA;
        this.impactArea = new Area(bim.battlefield, type, currentCommand.getImpactArea());
    }

    @Override
    public void init() {
        System.out.println("VALIDATE TARGET : "
                +currentCommand.getInitiator().getName()+" => "
                +((currentCommand.getTarget() != null) ? currentCommand.getTarget().getName() : "("+currentCommand.getRowTarget()+", "+currentCommand.getColTarget()+")")+" : "
                +currentCommand.getName(bim.mainI18nBundle));

        bim.bfr.addAreaRenderer(impactArea);
        bim.focusOn(currentCommand.getRowActor(), currentCommand.getColActor(), true, true, false, true, false);
        currentCommand.blink(true);

        //set the new orientation
        if(!currentCommand.getActionChoice().isActorIsTarget()) {
            Data.Orientation actorOrientation = Utils.getOrientationFromCoords(
                    currentCommand.getRowActor(),
                    currentCommand.getColActor(),
                    currentCommand.getRowTarget(),
                    currentCommand.getColTarget());
            orientationCommand = new ChooseOrientationCommand(bim.bfr, bim.scheduler, bim.player.getInventory(), actorOrientation);
            orientationCommand.setFree(true);
            orientationCommand.apply(currentCommand.getRowActor(), currentCommand.getColActor());
        }

        //schedule the panels dance
        StandardTask hideShortPanelTask = new StandardTask();
        hideShortPanelTask.addThread(new CommandThread(bim.hideSTP, 0));
        hideShortPanelTask.addThread(new CommandThread(bim.hideSUP, 0));
        bim.scheduler.addTask(hideShortPanelTask);
        if(bim.app.isPanelAvailable(currentCommand)) {

            final ActionInfoPanel actionInfoPanel = bim.app.getPanel(currentCommand);
            bim.scheduler.addTask(new StandardTask(new ManageActionPanel(bim.uiStage, actionInfoPanel, ManageActionPanel.Request.SHOW), 0));
            hideActionPanelCommand = new ManageActionPanel(bim.uiStage, actionInfoPanel, ManageActionPanel.Request.HIDE);
            removeActionPanelCommand = new ManageActionPanel(bim.uiStage, actionInfoPanel, ManageActionPanel.Request.REMOVE);
            hidingTime = actionInfoPanel.getHidingTime();
        }
    }

    @Override
    public void end() {

        super.end();
        bim.bfr.removeAreaRenderer(impactArea);
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        if(!currentCommand.isExecuting()) {

            if (row == currentCommand.getRowTarget() && col == currentCommand.getColTarget()) {
                //ACTION VALIDATE

                // clear the HUD before hand
                if(bim.app.isPanelAvailable(currentCommand))
                    bim.scheduler.addTask(new StandardTask(hideActionPanelCommand, 0));
                bim.bfr.getAreaRenderer(impactArea).setVisible(false);
                bim.removeTileHighlighting(false);
                currentCommand.blink(false);
                currentCommand.attach(this);

                currentCommand.apply();

                // update the historic
                historic.push(currentCommand);

            } else {
                //ACTION CANCEL

                //reset orientation of the actor
                if(!currentCommand.getActionChoice().isActorIsTarget())
                    orientationCommand.undo();

                // remove blinking effect of the target
                currentCommand.blink(false);

                // hide action panel before removing it
                if(bim.app.isPanelAvailable(currentCommand)) {
                    bim.scheduler.addTask(new StandardTask(hideActionPanelCommand, hidingTime));
                    bim.scheduler.addTask(new StandardTask(removeActionPanelCommand, 0));
                }

                // launch the action selection interaction state
                bim.scheduler.addTask(new StandardTask(new GetBackToSelectAction(currentCommand, bim, historic), 0));

            }
        }
        return true;
    }

    @Override
    public void getNotification(Observable sender, Object data) {
        if(data instanceof BattleCommand && data == currentCommand){
            currentCommand.detach(this);
            if(bim.app.isPanelAvailable(currentCommand))
                bim.scheduler.addTask(new StandardTask(removeActionPanelCommand, 0));
            bim.replace(new HandleOutcomeBIS(bim, historic));
        }
    }


    static class GetBackToSelectAction extends SimpleCommand{
        private BattleCommand currentCommand;
        private BattleInteractionMachine bim;
        private Stack<BattleCommand> historic;

        public GetBackToSelectAction(BattleCommand currentCommand, BattleInteractionMachine bim, Stack<BattleCommand> historic) {
            this.currentCommand = currentCommand;
            this.bim = bim;
            this.historic = historic;
        }

        @Override
        public void apply() {
            bim.replace(new SelectActionBIS(bim, currentCommand.getRowActor(), currentCommand.getColActor(), historic));
        }
    }

    public static class ManageActionPanel extends SimpleCommand{
        private Stage uiStage;
        private ActionInfoPanel actionInfoPanel;
        private Request request;

        public ManageActionPanel(Stage uiStage, ActionInfoPanel actionInfoPanel, Request request) {
            this.uiStage = uiStage;
            this.actionInfoPanel = actionInfoPanel;
            this.request = request;
        }

        enum Request{
            SHOW,
            HIDE,
            REMOVE
        }

        @Override
        public void apply() {
            switch (request){

                case SHOW:
                    uiStage.addActor(actionInfoPanel);
                    actionInfoPanel.hide();
                    actionInfoPanel.show();
                    break;
                case HIDE:
                    actionInfoPanel.hide();
                    break;
                case REMOVE:
                    actionInfoPanel.remove();
                    break;
            }

        }

    }

}
