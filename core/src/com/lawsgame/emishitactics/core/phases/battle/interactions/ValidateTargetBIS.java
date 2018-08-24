package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.CommandThread;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.AreaWidget;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionPanel;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;

import java.util.Stack;

public class ValidateTargetBIS extends BattleInteractionState {
    private Stack<BattleCommand> historic;
    private BattleCommand currentCommand;

    private AreaWidget impactArea;
    private ChooseOrientationCommand orientationCommand;
    private SimpleCommand hideActionPanelCommand;
    private SimpleCommand removeActionPanelCommand;
    private float hidingTime;

    public ValidateTargetBIS(BattleInteractionMachine bim, BattleCommand currentCommand, Stack<BattleCommand> historic) {
        super(bim, true, false, false);
        this.historic = historic;
        this.currentCommand = currentCommand;

        Data.AreaType type = (currentCommand.getActionChoice().getRangeType() == ActionChoice.RangedBasedType.MOVE) ?
                Data.AreaType.MOVE_AREA :
                Data.AreaType.ACTION_AREA;
        this.impactArea = new AreaWidget(bim.battlefield, type, currentCommand.getImpactArea());


    }

    @Override
    public void init() {
        System.out.println("VALIDATE TARGET : "+currentCommand.getActor().getName()+" => "+currentCommand.getTarget().getName()+" : "+currentCommand.getName(bim.mainStringBundle));

        bim.focusOn(currentCommand.getRowActor(), currentCommand.getColActor(), true, true, false, true, false);
        currentCommand.highlightTarget(true);

        //set the new orientation
        Data.Orientation actorOrientation = Utils.getOrientationFromCoords(
                currentCommand.getRowActor(),
                currentCommand.getColActor(),
                currentCommand.getRowTarget(),
                currentCommand.getColTarget());
        orientationCommand = new ChooseOrientationCommand(bim.bfr, bim.scheduler, actorOrientation);
        orientationCommand.apply(currentCommand.getRowActor(), currentCommand.getColActor());

        //schedule the panels dance
        StandardTask hideShortPanelTask = new StandardTask();
        hideShortPanelTask.addThread(new CommandThread(bim.hideSTP, 0));
        hideShortPanelTask.addThread(new CommandThread(bim.hideSUP, 0));
        bim.scheduler.addTask(hideShortPanelTask);
        if(bim.app.isPanelAvailable(currentCommand)) {

            final ActionPanel actionPanel = bim.app.getPanel(currentCommand);
            bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                @Override
                public void apply() {
                    bim.uiStage.addActor(actionPanel);
                    actionPanel.hide();
                    actionPanel.show();
                }
            }, 0));
            this.hideActionPanelCommand = new SimpleCommand() {
                @Override
                public void apply() {
                    actionPanel.hide();
                }
            };
            removeActionPanelCommand = new SimpleCommand() {
                @Override
                public void apply() {
                    actionPanel.remove();
                }
            };
        }
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        if(!currentCommand.isExecuting()) {

            if (row == currentCommand.getRowTarget() && col == currentCommand.getColTarget()) {
                //ACTION VALIDATE

                // clear the HUD before hand
                bim.scheduler.addTask(new StandardTask(hideActionPanelCommand, 0));
                impactArea.setVisible(false);
                bim.removeTileHighlighting(false);

                currentCommand.apply();

                // update the historic
                historic.push(currentCommand);

            } else {
                //ACTION CANCEL

                //reset orientation of the actor
                orientationCommand.undo();

                // remove blinking effect of the target
                currentCommand.highlightTarget(false);

                // hide action panel before removing it
                bim.scheduler.addTask(new StandardTask(hideActionPanelCommand, hidingTime));
                bim.scheduler.addTask(new StandardTask(removeActionPanelCommand, 0));

                // launch the action selection interaction state
                bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                    @Override
                    public void apply() {
                        bim.replace(new SelectActionBIS(bim, currentCommand.getRowActor(), currentCommand.getColActor(), historic));
                    }
                }, 0));

            }
        }
        return true;
    }

    @Override
    public void update60(float dt) {
        if(currentCommand.isCompleted()){
            bim.scheduler.addTask(new StandardTask(removeActionPanelCommand, 0));
            bim.replace(new HandleOutcomeBIS(bim, historic));
        }
    }

    @Override
    public void prerender(SpriteBatch batch) {

    }

    @Override
    public void renderBetween(SpriteBatch batch) {
        if(impactArea != null)
            impactArea.render(batch);
    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }


}
