package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.AreaWidget;

import java.util.Stack;

public class SelectTargetBIS extends BattleInteractionState {
    private final Stack<BattleCommand> historic;
    private final BattleCommand currentCommand;
    private AreaWidget actionArea;

    public SelectTargetBIS(BattleInteractionMachine bim, Stack<BattleCommand> historic, BattleCommand command) {
        super(bim, true, true, true);
        this.historic = historic;
        this.currentCommand = command;

        Data.AreaType type = (currentCommand.getActionChoice().getRangeType() == ActionChoice.RangedBasedType.MOVE) ?
            Data.AreaType.MOVE_AREA :
            Data.AreaType.ACTION_AREA;
        this.actionArea = new AreaWidget(bim.battlefield, type, currentCommand.getActionArea());

    }

    @Override
    public void init() {
        System.out.println("SELECT TARGET : "+currentCommand.getActor().getName()+" "+currentCommand.getActionChoice().getName(bim.mainStringBundle));

        bim.focusOn(currentCommand.getRowActor(), currentCommand.getColActor(), true, true, true, true, false);

    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        if(!currentCommand.isExecuting()) {
            currentCommand.setTarget(row, col);
            if(currentCommand.isTargetValid()){
                currentCommand.init();
                if(currentCommand.isUndoable()){

                    currentCommand.apply();
                    historic.push(currentCommand);
                    actionArea.setVisible(false);
                    bim.removeTileHighlighting(false);
                }else{

                    bim.replace(new ValidateTargetBIS(bim, currentCommand, historic));
                }
            }else if(Utils.undoCommands(historic)
                    && bim.battlefield.isTileOccupiedByPlayerControlledUnit(row, col)
                    && !bim.battlefield.getUnit(row, col).isDone()){

                bim.replace(new SelectActionBIS(bim, row, col));
            }else {

                bim.replace(new SelectActionBIS(bim, currentCommand.getRowActor(), currentCommand.getColActor(), historic));
            }
        }
        return true;
    }

    @Override
    public void update60(float dt) {
        if(currentCommand.isCompleted()){
            System.out.println("GO TO HandleOutcomeBIS");
        }
    }

    @Override
    public void prerender(SpriteBatch batch) {

    }

    @Override
    public void renderBetween(SpriteBatch batch) {
        if(actionArea != null)
            actionArea.render(batch);
    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }


}
