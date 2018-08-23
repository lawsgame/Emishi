package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class ValidateTargetBIS extends BattleInteractionState {
    private Array<BattleCommand> historic;
    private BattleCommand currentCommand;

    public ValidateTargetBIS(BattleInteractionMachine bim, BattleCommand currentCommand, Array<BattleCommand> historic) {
        super(bim, true, false, true);
        this.historic = historic;
        this.currentCommand = currentCommand;
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

    @Override
    public void init() {

    }
}
