package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.RangedBasedType;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

import java.util.Stack;

public class SelectTargetBIS extends BattleInteractionState implements Observer {
    final Stack<ActorCommand> historic;
    final ActorCommand currentCommand;
    Area actionArea;

    public SelectTargetBIS(BattleInteractionMachine bim, Stack<ActorCommand> historic, ActorCommand command) {
        super(bim, true, true, true);
        this.historic = historic;
        this.currentCommand = command;

        Data.AreaType type = (currentCommand.getActionChoice().getRangedType() == RangedBasedType.MOVE) ?
            Data.AreaType.MOVE_AREA :
            Data.AreaType.ACTION_AREA;
        this.actionArea = new Area(bim.battlefield, type, currentCommand.getActionArea());

    }

    @Override
    public void init() {
        System.out.println("SELECT TARGET : "+currentCommand.getInitiator().getName()+" "+currentCommand.getActionChoice().getName(bim.mainI18nBundle));

        bim.bfr.addAreaRenderer(actionArea);
        bim.focusOn(currentCommand.getRowActor(), currentCommand.getColActor(), true, true, true, true, false);

        if(currentCommand.getActionChoice().isActorIsTarget()){
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


            if(currentCommand.getActionChoice().isUndoable()){

                // remove blinking and other highlighting target affect
                currentCommand.blink(false);
                currentCommand.attach(this);
                currentCommand.apply();
                historic.push(currentCommand);
                bim.bfr.getAreaRenderer(actionArea).setVisible(false);
                bim.removeTileHighlighting(false);
            }else{

                bim.replace(new ValidateTargetBIS(bim, currentCommand, historic));
            }
        }else{

            /*
             * this paragraph is required to initialize the SelectActorBIS below, triggerd if the player :
             * - touch a none valid target tile
             * - the historic is fully clearable
             * - yet, the target tile is not occupied by the active player unit
             * , and those, before clearing the historic obviously
             * which explains why it is visible here.
             */
            int rowInit;
            int colInit;
            if(!historic.isEmpty()){
                rowInit = historic.get(0).getRowActor();
                colInit = historic.get(0).getColActor();
            }else{
                rowInit = currentCommand.getRowActor();
                colInit = currentCommand.getColActor();
            }

            if (Utils.undoCommands(historic)){

                if(bim.battlefield.isTileOccupiedByPlayerControlledUnit(row, col) && !bim.battlefield.getUnit(row, col).isDone()) {
                    bim.replace(new SelectActionBIS(bim, row, col));
                }else{
                    bim.replace(new SelectActorBIS(bim, rowInit, colInit, false));
                }
            } else {

                bim.replace(new SelectActionBIS(bim, historic.peek().getRowActor(), historic.peek().getColActor(), historic));
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
