package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

public class SceneBIS extends BattleInteractionState {


    public SceneBIS(BattleInteractionMachine BISys) {
        super(BISys, true, false, false, true, false);
    }


    @Override
    public boolean handleTouchInput(int row, int col) {

        bim.replace(new DeploymentBIS(bim));
        return true;
    }

    @Override
    public void init() {
        super.init();
    }

}
