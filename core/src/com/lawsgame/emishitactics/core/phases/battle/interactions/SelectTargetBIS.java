package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.lawsgame.emishitactics.TacticsGame;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.RangedBasedType;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;
import com.lawsgame.emishitactics.engine.utils.Lawgger;

import java.util.Stack;

public class SelectTargetBIS extends BattleInteractionState implements Observer {
    private static Lawgger log = Lawgger.createInstance(SelectTargetBIS.class);

    final Stack<ActorCommand> historic;
    final ActorCommand currentCommand;
    Area actionArea;

    public SelectTargetBIS(BattleInteractionMachine bim, Stack<ActorCommand> historic, ActorCommand command) {
        super(bim, true, true, true, false , false);
        this.historic = historic;
        this.currentCommand = command;

        Data.AreaType type = (currentCommand.getActionChoice().getRangedType() == RangedBasedType.MOVE) ?
            Data.AreaType.MOVE_AREA :
            Data.AreaType.ACTION_AREA;
        this.actionArea = new Area(bim.bfr.getModel(), type, currentCommand.getActionArea());

    }

    @Override
    public void init() {
        log.info("SELECT TARGET : "+currentCommand.getInitiator().getName()+" "+currentCommand.getActionChoice().getName(bim.localization));

        super.init();
        bim.bfr.addAreaRenderer(actionArea);
        bim.focusOn(currentCommand.getRowinitiator(), currentCommand.getColInitiator(), true, true, false, TileHighlighter.SltdUpdateMode.MATCH_TOUCHED_TILE, true);

        if(currentCommand.getActionChoice().isActorIsTarget()){
            currentCommand.setTarget(currentCommand.getRowinitiator(), currentCommand.getColInitiator());
            triggerCurrentCommand();
        }
    }

    @Override
    public void end() {
        super.end();
        currentCommand.detach(this);
        bim.bfr.removeAreaRenderer(actionArea);
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        if(!currentCommand.isExecuting()) {
            currentCommand.setTarget(row, col);
            triggerCurrentCommand();
        }
        return true;
    }

    private void triggerCurrentCommand(){
        int row = currentCommand.getRowTarget();
        int col = currentCommand.getColTarget();
        if(currentCommand.isTargetValid()){
            if(currentCommand.isAppliableWihoutValidation()){
                // ACTION PERFORM

                currentCommand.attach(this);
                currentCommand.highlightTargets(false);
                currentCommand.apply();
                historic.push(currentCommand);
                bim.bfr.getAreaRenderer(actionArea).setVisible(false);
                bim.thl.removeTileHighlighting(false, false);
            }else{

                bim.replace(new ValidateTargetBIS(bim, currentCommand, historic));
            }
        }else{

            Unit touchedPCUnit = null;
            if(bim.bfr.getModel().isTileOccupiedByPlayerControlledUnit(row, col))
                touchedPCUnit = bim.bfr.getModel().getUnit(row, col);

            if (Utils.undoCommands(historic)){

                if(touchedPCUnit != null && !touchedPCUnit.isDone()) {
                    bim.replace(new SelectActionBIS(bim, touchedPCUnit));
                }else{
                    bim.replace(new SelectActionBIS(bim, currentCommand.getInitiator()));
                }
            } else {

                bim.replace(new SelectActionBIS(bim, historic.peek().getInitiator(), historic));
            }
        }
    }

    @Override
    public void getNotification(Observable sender, Object data) {
        if(data instanceof ActorCommand && data == currentCommand){
            bim.replace(new HandleOutcomeBIS(bim, historic, false));
        }
    }
}
