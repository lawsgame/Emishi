package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.lawsgame.emishitactics.TacticsGame;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.ChoicePanel;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.utils.Lawgger;

import java.util.Stack;

public class SelectActionBIS extends BattleInteractionState implements ChoicePanel.CommandReceiver {
    private static Lawgger log = Lawgger.createInstance(SelectActionBIS.class);

    private int rowSltdUnit;
    private int colSltdUnit;
    private Stack<ActorCommand> historic;

    public SelectActionBIS(BattleInteractionMachine bim, Unit actor, Stack<ActorCommand> historic) {
        super(bim, true, true, true, false, true);
        int[] actorPos = bim.bfr.getModel().getUnitPos(actor);
        this.rowSltdUnit = actorPos[0];
        this.colSltdUnit = actorPos[1];
        this.historic = historic;

    }

    public SelectActionBIS(BattleInteractionMachine bim, Unit actor) {
        this(bim, actor, new Stack<ActorCommand>());
    }

    @Override
    public void init() {
        log.info("SELECT ACTION : "+bim.bfr.getModel().getUnit(rowSltdUnit, colSltdUnit).getName());

        super.init();
        bim.pp.choicePanel.attach(this);
        bim.pp.choicePanel.setContent(rowSltdUnit, colSltdUnit, bim.bcm, historic);
        bim.pp.choicePanel.show();
        bim.focusOn(rowSltdUnit, colSltdUnit, true, true, false, TileHighlighter.SltdUpdateMode.MATCH_TOUCHED_TILE, true);

    }

    @Override
    public void end() {
        super.end();
        bim.pp.choicePanel.detach(this);
        bim.pp.choicePanel.resetPanel(true);

    }

    @Override
    public void getChoicePanelNotification(ActorCommand choice) {
        choice.setInitiator(rowSltdUnit, colSltdUnit);
        bim.replace(new SelectTargetBIS(bim, historic, choice));
    }


    @Override
    public boolean handleTouchInput(final int row, final int col) {

        if(bim.bfr.getModel().isTileOccupiedByPlayerControlledUnit(row, col)){

            Unit touchedUnit = bim.bfr.getModel().getUnit(row, col);
            Unit selectedUnit = bim.bfr.getModel().getUnit(rowSltdUnit, colSltdUnit);
            if(touchedUnit != selectedUnit) {

                Utils.undoCommands(historic);
                if(historic.size() > 0){

                    rowSltdUnit = historic.peek().getRowinitiator();
                    colSltdUnit = historic.peek().getColInitiator();
                    bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                        @Override
                        public void apply() {
                            bim.focusOn(rowSltdUnit, colSltdUnit, true, true, true, TileHighlighter.SltdUpdateMode.MATCH_TOUCHED_TILE, true);
                            bim.pp.choicePanel.setContent(rowSltdUnit, colSltdUnit, bim.bcm, historic);
                            bim.pp.choicePanel.show();
                        }
                    }, 0f));

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
                                bim.pp.choicePanel.setContent(rowSltdUnit, colSltdUnit, bim.bcm, historic);
                                bim.pp.choicePanel.show();
                            }
                        }, 0f));
                        return true;


                    }
                }
            }
        }else{
            bim.pp.choicePanel.resetPanel(false);
        }

        return false;
    }


}
