package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class SelectTargetBIS extends BattleInteractionState {
    int rowSltdUnit;
    int colSltdUnit;
    Array<BattleCommand> historic;
    BattleCommand currentCommand;

    public SelectTargetBIS(BattleInteractionMachine bim, int rowSltdUnit, int colSltdUnit, Array<BattleCommand> historic, BattleCommand command) {
        super(bim, true, true, true);
        this.rowSltdUnit = rowSltdUnit;
        this.colSltdUnit = colSltdUnit;
        this.historic = historic;
        this.currentCommand = command;
    }

    @Override
    public void init() {
        System.out.println("SELECT TARGET : "+currentCommand.getActionChoice().getName(bim.mainStringBundle));

        bim.focusOn(rowSltdUnit, colSltdUnit, true, true, true, true);


    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
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

    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }


}
