package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.ai.DumbAI;
import com.lawsgame.emishitactics.core.phases.battle.ai.interfaces.AI;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

import java.util.LinkedList;

public class AiBIS extends BattleInteractionState {

    private LinkedList<BattleCommand> foeCommands;

    public AiBIS(BattleInteractionMachine bim) {
        super(bim, false, false, false);
        this.foeCommands = new LinkedList<BattleCommand>();
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
