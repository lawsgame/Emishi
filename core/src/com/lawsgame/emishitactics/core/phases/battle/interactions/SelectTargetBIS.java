package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.AreaWidget;

public class SelectTargetBIS extends BattleInteractionState {
    private final Array<BattleCommand> historic;
    private final BattleCommand currentCommand;
    private AreaWidget actionArea;

    public SelectTargetBIS(BattleInteractionMachine bim, Array<BattleCommand> historic, BattleCommand command) {
        super(bim, true, true, true);
        this.historic = historic;
        this.currentCommand = command;
        if(currentCommand != null){

            Data.AreaType type = (currentCommand.getActionChoice().getRangeType() == ActionChoice.RangedBasedType.MOVE) ?
                Data.AreaType.MOVE_AREA :
                Data.AreaType.ACTION_AREA;
            actionArea = new AreaWidget(bim.battlefield, type, currentCommand.getActionArea());
        }
    }

    @Override
    public void init() {
        System.out.println("SELECT TARGET : "+currentCommand.getActionChoice().getName(bim.mainStringBundle));

        bim.focusOn(currentCommand.getRowActor(), currentCommand.getColActor(), true, true, true, true, false);

    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public boolean handleTouchInput(int row, int col) {

        if(actionArea != null && actionArea.contains(row, col)){

        }
        return false;
    }

    @Override
    public void update60(float dt) {

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
