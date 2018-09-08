package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.ai.DumbAI;
import com.lawsgame.emishitactics.core.phases.battle.ai.interfaces.AI;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class AiBIS extends BattleInteractionState {

    public AiBIS(BattleInteractionMachine bim) {
        super(bim, false, false, false);
    }

    @Override
    public void init() {
        System.out.println("AI");

        bim.replace( new SelectActorBIS(bim, true));
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }




}
