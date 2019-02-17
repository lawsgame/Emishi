package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lawsgame.emishitactics.TacticsGame;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.CommandSubTask;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.ActionInfoPanel;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

import java.util.Stack;

public class ValidateTargetBIS extends BattleInteractionState implements Observer {
    private Stack<ActorCommand> historic;
    private ActorCommand currentCommand;
    private Area impactArea;

    private ChooseOrientationCommand orientationCommand;
    private SimpleCommand hideActionPanelCommand;
    private SimpleCommand removeActionPanelCommand;
    private float hidingTime;

    public ValidateTargetBIS(BattleInteractionMachine bim, ActorCommand currentCommand, Stack<ActorCommand> historic) {
        super(bim, true, false, false, true, false);
        this.historic = historic;
        this.currentCommand = currentCommand;

        Data.AreaType type = (currentCommand.getActionChoice().getRangedType() == Data.RangedBasedType.MOVE) ?
                Data.AreaType.MOVE_AREA :
                Data.AreaType.ACTION_AREA;
        this.impactArea = new Area(bim.bfr.getModel(), type, currentCommand.getImpactArea());
    }

    @Override
    public void init() {
        TacticsGame.debug(this.getClass(), "VALIDATE TARGET : "
                +currentCommand.getInitiator().getName()+" => "
                +((currentCommand.getTarget() != null) ? currentCommand.getTarget().getName() : "("+currentCommand.getRowTarget()+", "+currentCommand.getColTarget()+")")+" : "
                +currentCommand.getName(bim.localization));

        super.init();
        bim.bfr.addAreaRenderer(impactArea);
        bim.focusOn(currentCommand.getRowinitiator(), currentCommand.getColInitiator(), true, true, false, TileHighlighter.SltdUpdateMode.MATCH_TOUCHED_TILE, true);
        currentCommand.highlightTargets(true);

        //set the new orientation
        if(!currentCommand.getActionChoice().isActorIsTarget()) {
            Data.Orientation actorOrientation = Utils.getOrientationFromCoords(
                    currentCommand.getRowinitiator(),
                    currentCommand.getColInitiator(),
                    currentCommand.getRowTarget(),
                    currentCommand.getColTarget());
            orientationCommand = new ChooseOrientationCommand(bim.bfr, bim.scheduler, bim.player.getInventory(), actorOrientation);
            orientationCommand.setFree(true);
            orientationCommand.apply(currentCommand.getRowinitiator(), currentCommand.getColInitiator());
        }

        //schedule the panels dance
        StandardTask hideShortPanelTask = new StandardTask();
        hideShortPanelTask.addParallelSubTask(new CommandSubTask(0){
            @Override
            public void run() {
                bim.pp.hideSTP.apply();
            }
        });
        hideShortPanelTask.addParallelSubTask(new CommandSubTask(0) {
            @Override
            public void run() {
                bim.pp.hideSUP.apply();
            }
        });
        bim.scheduler.addTask(hideShortPanelTask);
        if(bim.pp.isActionPanelAvailable(currentCommand.getActionChoice())) {

            final ActionInfoPanel actionInfoPanel = bim.pp.getActionPanel(currentCommand);
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

                if(bim.pp.isActionPanelAvailable(currentCommand.getActionChoice()))
                    bim.scheduler.addTask(new StandardTask(hideActionPanelCommand, 0));

                currentCommand.attach(this);
                currentCommand.highlightTargets(false);
                currentCommand.apply();
                bim.bfr.getAreaRenderer(impactArea).setVisible(false);
                bim.thl.removeTileHighlighting(false, false);
                historic.push(currentCommand);

            } else {
                //ACTION CANCEL

                //reset orientation of the actor
                if(!currentCommand.getActionChoice().isActorIsTarget())
                    orientationCommand.undo();

                // remove blinking effect of the target
                currentCommand.highlightTargets(false);

                // hide action panel before removing it
                if(bim.pp.isActionPanelAvailable(currentCommand.getActionChoice())) {
                    bim.scheduler.addTask(new StandardTask(hideActionPanelCommand, hidingTime));
                    bim.scheduler.addTask(new StandardTask(removeActionPanelCommand, 0));
                }

                // launch the action selection interaction state
                bim.replace(new SelectActionBIS(bim, currentCommand.getInitiator(), historic));

            }
        }
        return true;
    }

    @Override
    public void getNotification(Observable sender, Object data) {
        if(data instanceof ActorCommand && data == currentCommand){
            currentCommand.detach(this);
            if(bim.pp.isActionPanelAvailable(currentCommand.getActionChoice()))
                bim.scheduler.addTask(new StandardTask(removeActionPanelCommand, 0));
            bim.replace(new HandleOutcomeBIS(bim, historic, false));
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
