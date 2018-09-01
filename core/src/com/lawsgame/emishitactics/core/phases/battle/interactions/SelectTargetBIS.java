package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.RangedBasedType;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
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

        Data.AreaType type = (currentCommand.getActionChoice().getRangedType() == RangedBasedType.MOVE) ?
            Data.AreaType.MOVE_AREA :
            Data.AreaType.ACTION_AREA;
        this.actionArea = new AreaWidget(bim.battlefield, type, currentCommand.getActionArea());

    }

    @Override
    public void init() {
        System.out.println("SELECT TARGET : "+currentCommand.getActor().getName()+" "+currentCommand.getActionChoice().getName(bim.mainI18nBundle));

        bim.focusOn(currentCommand.getRowActor(), currentCommand.getColActor(), true, true, true, true, false);

        if(currentCommand.getActionChoice().isActorIsTarget()){
            triggerCurrentCommand();
        }
    }

    @Override
    public void end() {
        super.end();
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
                currentCommand.apply();
                historic.push(currentCommand);
                actionArea.setVisible(false);
                bim.removeTileHighlighting(false);
            }else{

                bim.replace(new ValidateTargetBIS(bim, currentCommand, historic));
            }
        }else{

            /*
             * this paragraph is required to initialize the SelectActorBIS below, triggerd if the player if:
             * - touch a none valid target tile
             * - the historic is fully clearable
             * - yet, the target tile is not occupied by the active player unit
             * , and those, before clearing the historic obviously
             * which explains why it is done here.
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
    public void update60(float dt) {
        //System.out.println(currentCommand.isExecuting() +" "+currentCommand.isCompleted());
        if(currentCommand.isCompleted()){
            bim.replace(new HandleOutcomeBIS(bim, historic));
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
