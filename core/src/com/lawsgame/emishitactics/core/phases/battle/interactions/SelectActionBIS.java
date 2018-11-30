package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces.ChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo.TempoChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo.TempoCommandChoicePanel;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;

import java.util.Stack;

public class SelectActionBIS extends BattleInteractionState {
    private int rowSltdUnit;
    private int colSltdUnit;
    private Stack<ActorCommand> historic;
    private ChoicePanel choicePanel;
    private ChoicePanel commandPanel;

    public SelectActionBIS(BattleInteractionMachine bim, Unit actor, Stack<ActorCommand> historic) {
        super(bim, true, true, true, false, true);
        int[] actorPos = bim.bfr.getModel().getUnitPos(actor);
        this.rowSltdUnit = actorPos[0];
        this.colSltdUnit = actorPos[1];
        this.historic = historic;
        this.choicePanel = new TempoChoicePanel(bim.asm);
        this.commandPanel = new TempoCommandChoicePanel(bim.asm, 0);

    }

    public SelectActionBIS(BattleInteractionMachine bim, Unit actor) {
        this(bim, actor, new Stack<ActorCommand>());
    }

    @Override
    public void init() {
        System.out.println("SELECT ACTION : "+bim.bfr.getModel().getUnit(rowSltdUnit, colSltdUnit).getName());

        super.init();
        choicePanel.set(new ActionButtonHandler(this));
        choicePanel.setVisible(true);
        bim.uiStage.addActor(choicePanel);
        bim.focusOn(rowSltdUnit, colSltdUnit, true, true, false, TileHighlighter.SltdUpdateMode.MATCH_TOUCHED_TILE, true);

    }

    @Override
    public void end() {
        super.end();
        choicePanel.remove();
        commandPanel.remove();

    }

    @Override
    public boolean handleTouchInput(final int row, final int col) {
        choicePanel.setVisible(false);
        commandPanel.remove();

        if(bim.bfr.getModel().isTileOccupiedByPlayerControlledUnit(row, col)){

            Unit touchedUnit = bim.bfr.getModel().getUnit(row, col);
            Unit selectedUnit = bim.bfr.getModel().getUnit(rowSltdUnit, colSltdUnit);
            if(touchedUnit == selectedUnit) {
                choicePanel.setVisible(true);
            }else{

                Utils.undoCommands(historic);
                if(historic.size() > 0){

                    rowSltdUnit = historic.peek().getRowActor();
                    colSltdUnit = historic.peek().getColActor();
                    bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                        @Override
                        public void apply() {
                            bim.focusOn(rowSltdUnit, colSltdUnit, true, true, true, TileHighlighter.SltdUpdateMode.MATCH_TOUCHED_TILE, true);
                        }
                    }, 0f));
                    choicePanel.set(new ActionButtonHandler(this));
                    choicePanel.setVisible(true);
                    return true;
                }else{
                    if(!touchedUnit.isDone()){

                        bim.replace(new SelectActionBIS(bim, touchedUnit));
                        return true;
                    }else{

                        int[] actorPos = bim.bfr.getModel().getUnitPos(selectedUnit);
                        this.rowSltdUnit = actorPos[0];
                        this.colSltdUnit = actorPos[1];
                        this.bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                            @Override
                            public void apply() {
                                bim.focusOn(row, col, true, true, false, rowSltdUnit, colSltdUnit, true);
                            }
                        }, 0f));
                        choicePanel.set(new ActionButtonHandler(this));
                        return true;


                    }
                }
            }
        }
        return false;
    }


    public static class ActionButtonHandler implements ChoicePanel.ButtonHandler{
        private SelectActionBIS bis;

        public ActionButtonHandler(SelectActionBIS bis){
            this.bis = bis;
        }

        public Array<TextButton> getButtons(TextButton.TextButtonStyle style) {
            Array<TextButton> buttons = new Array<TextButton>();

            if (bis.bim.bfr.getModel().isTileOccupied(bis.rowSltdUnit, bis.colSltdUnit)) {

                final Array<ActionChoice> choices = bis.bim.bcm.getAvailableChoices(bis.rowSltdUnit, bis.colSltdUnit, bis.historic);
                for (int i = 0; i < choices.size; i++) {

                    final ActionChoice choice = choices.get(i);
                    final TextButton button = new TextButton(choice.getName(bis.bim.localization), style);
                    final int buttonIndex = i;
                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {

                            Array<ActorCommand> flavors = bis.bim.bcm.getAvailableCommands(bis.rowSltdUnit, bis.colSltdUnit, choice, true);

                            if(flavors.size == 1){
                                flavors.get(0).setInitiator(bis.rowSltdUnit, bis.colSltdUnit);
                                if(flavors.get(0).isInitiatorValid()) {
                                    bis.bim.replace(new SelectTargetBIS(bis.bim, bis.historic, flavors.get(0)));
                                }
                            }else if(flavors.size > 1){

                                bis.choicePanel.setTouchable(Touchable.disabled);

                                if(bis.commandPanel != null)
                                    bis.commandPanel.remove();

                                bis.commandPanel = new TempoCommandChoicePanel(bis.bim.asm, buttonIndex);
                                bis.commandPanel.set(new CommandChoiceButtonHandler(bis, choice));
                                bis.commandPanel.setVisible(true);
                                bis.bim.uiStage.addActor(bis.commandPanel);
                            }else{

                                try {
                                    throw new BISException("Battle command manager found no command available for the following available action choice : "+choice.getName(bis.bim.localization));
                                } catch (BISException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
                    buttons.add(button);
                }
            }
            return buttons;
        }
    }


    public static class CommandChoiceButtonHandler implements ChoicePanel.ButtonHandler{
        private SelectActionBIS bis;
        private ActionChoice actionChoice;

        public CommandChoiceButtonHandler(SelectActionBIS bis, ActionChoice actionChoice) {
            this.bis = bis;
            this.actionChoice = actionChoice;
        }

        public Array<TextButton> getButtons(TextButton.TextButtonStyle style){
            Array<TextButton> buttons = new Array<TextButton>();
            if (bis.bim.bfr.getModel().isTileOccupied(bis.rowSltdUnit, bis.colSltdUnit)) {

                Array<ActorCommand> flavors = bis.bim.bcm.getAvailableCommands(bis.rowSltdUnit, bis.colSltdUnit, actionChoice, true);
                for (int i = 0; i < flavors.size; i++) {

                    final ActorCommand actorCommand = flavors.get(i);
                    actorCommand.setInitiator(bis.rowSltdUnit, bis.colSltdUnit);
                    if(actorCommand.isInitiatorValid()) {
                        TextButton button = new TextButton(actorCommand.getName(bis.bim.localization), style);
                        button.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                bis.bim.replace(new SelectTargetBIS(bis.bim, bis.historic, actorCommand));
                            }
                        });
                        buttons.add(button);
                    }
                }
            }
            return buttons;
        }
    }
}
