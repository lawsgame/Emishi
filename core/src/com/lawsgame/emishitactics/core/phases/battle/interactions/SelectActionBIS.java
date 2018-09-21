package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoCommandChoicePanel;

import java.util.Stack;

public class SelectActionBIS extends BattleInteractionState {
    int rowSltdUnit;
    int colSltdUnit;
    Stack<BattleCommand> historic;
    private ChoicePanel choicePanel;
    private ChoicePanel commandPanel;

    public SelectActionBIS(BattleInteractionMachine bim, int rowSltdUnit, int colSltdUnit, Stack<BattleCommand> historic) {
        super(bim, true, true, true);
        this.rowSltdUnit = rowSltdUnit;
        this.colSltdUnit = colSltdUnit;
        this.historic = historic;
        this.choicePanel = new TempoChoicePanel(bim.asm);
        this.commandPanel = new TempoCommandChoicePanel(bim.asm, 0);

    }

    public SelectActionBIS(BattleInteractionMachine bim, int rowSltdUnit, int colSltdUnit) {
        this(bim, rowSltdUnit, colSltdUnit, new Stack<BattleCommand>());
    }

    @Override
    public void init() {
        System.out.println("SELECT ACTION : "+bim.battlefield.getUnit(rowSltdUnit, colSltdUnit).getName());

        resetChoicePanel();
        bim.focusOn(rowSltdUnit, colSltdUnit, true, true, true, true, true);

    }

    private void resetChoicePanel(){
        choicePanel.build(new ActionButtonHandler(this));
        choicePanel.setVisible(true);
        bim.uiStage.addActor(choicePanel);
    }

    @Override
    public void end() {
        super.end();
        choicePanel.remove();
        commandPanel.remove();

    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        choicePanel.setVisible(false);
        commandPanel.remove();

        if(bim.battlefield.isTileOccupiedByPlayerControlledUnit(row, col)){

            IUnit touchedUnit = bim.battlefield.getUnit(row, col);
            IUnit selectedUnit = bim.battlefield.getUnit(rowSltdUnit, colSltdUnit);
            if(touchedUnit == selectedUnit) {
                choicePanel.setVisible(true);
            }else{

                if (Utils.undoCommands(historic) && !touchedUnit.isDone()) {
                    bim.replace(new SelectActionBIS(bim, row, col));
                    return true;
                } else {
                    // if not all commands are undoable, all the undoable ones are visible, the unit is updated and a new choice panel is provided
                    bim.focusOn(row, col, true, true, true, false, false);
                    resetChoicePanel();
                    choicePanel.setVisible(false);
                    return true;
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

            if (bis.bim.battlefield.isTileOccupied(bis.rowSltdUnit, bis.colSltdUnit)) {

                final IUnit sltdUnit = bis.bim.battlefield.getUnit(bis.rowSltdUnit, bis.colSltdUnit);
                final Array<ActionChoice> choices = bis.bim.bcm.getAvailableChoices(sltdUnit, bis.historic);
                for (int i = 0; i < choices.size; i++) {

                    final ActionChoice choice = choices.get(i);
                    final TextButton button = new TextButton(choice.getName(bis.bim.mainI18nBundle), style);
                    final int buttonIndex = i;
                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {

                            Array<BattleCommand> flavors = bis.bim.bcm.getAvailableCommands(sltdUnit, choice, true);

                            if(flavors.size == 1){

                                if(flavors.get(0).setInitiator(bis.rowSltdUnit, bis.colSltdUnit)) {
                                    bis.bim.replace(new SelectTargetBIS(bis.bim, bis.historic, flavors.get(0)));
                                }
                            }else if(flavors.size > 1){

                                bis.choicePanel.setTouchable(Touchable.disabled);

                                if(bis.commandPanel != null)
                                    bis.commandPanel.remove();

                                bis.commandPanel = new TempoCommandChoicePanel(bis.bim.asm, buttonIndex);
                                bis.commandPanel.build(new CommandChoiceButtonHandler(bis, choice));
                                bis.commandPanel.setVisible(true);
                                bis.bim.uiStage.addActor(bis.commandPanel);
                            }else{

                                try {
                                    throw new BISException("Battle command manager found no command available for the following available action choice : "+choice.getName(bis.bim.mainI18nBundle));
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
            if (bis.bim.battlefield.isTileOccupied(bis.rowSltdUnit, bis.colSltdUnit)) {

                IUnit sltdUnit = bis.bim.battlefield.getUnit(bis.rowSltdUnit, bis.colSltdUnit);
                Array<BattleCommand> flavors = bis.bim.bcm.getAvailableCommands(sltdUnit, actionChoice, true);
                for (int i = 0; i < flavors.size; i++) {

                    final BattleCommand battleCommand = flavors.get(i);
                    if(battleCommand.setInitiator(bis.rowSltdUnit, bis.colSltdUnit)) {
                        TextButton button = new TextButton(battleCommand.getName(bis.bim.mainI18nBundle), style);
                        button.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                bis.bim.replace(new SelectTargetBIS(bis.bim, bis.historic, battleCommand));
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
