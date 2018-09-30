package com.lawsgame.emishitactics.core.phases.battle.ai.interfaces;

import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.phases.battle.commands.battle.BeginArmyTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.battle.EndArmyTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.ActionPanelPool;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionInfoPanel;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

import java.util.LinkedList;

public abstract class AI extends Observable implements Runnable {

    protected BattlefieldRenderer bfr;
    protected AnimationScheduler scheduler;
    protected ActionPanelPool app;
    protected Inventory playerInventory;

    public AI(BattlefieldRenderer bfr, AnimationScheduler scheduler, ActionPanelPool app, Inventory playerInventory) {
        this.bfr = bfr;
        this.scheduler = scheduler;
        this.app = app;
        this.playerInventory = playerInventory;
    }


    public abstract int[] nextUnit(IArmy army);
    public abstract CommandBundle getCommandPackage(int[] actor);

    @Override
    public void run() {

        Battlefield bf = bfr.getModel();
        IArmy currentArmy;
        int[] actorPos;
        CommandBundle bundle;
        BeginArmyTurnCommand beginCommand;
        EndArmyTurnCommand endCommand;
        if(!bf.getSolver().isBattleOver()) {
            loop :
            {
                bf.resetArmyTurnOrder();
                currentArmy = bf.getNextArmy();

                while (!currentArmy.isPlayerControlled()) {

                    beginCommand = new BeginArmyTurnCommand(bfr, scheduler, currentArmy);
                    beginCommand.setDecoupled(true);
                    beginCommand.apply();
                    bundle = new CommandBundle();
                    bundle.offer(beginCommand, null);
                    notifyAllObservers(bundle);

                    while (!currentArmy.isDone()) {
                        actorPos = nextUnit(currentArmy);
                        bundle = getCommandPackage(actorPos);
                        notifyAllObservers(bundle);
                        if (bf.getSolver().isBattleOver()) {
                            break loop;
                        }
                    }

                    endCommand = new EndArmyTurnCommand(bfr, scheduler, currentArmy);
                    endCommand.setDecoupled(true);
                    endCommand.apply();
                    bundle = new CommandBundle();
                    bundle.offer(endCommand, null);
                    notifyAllObservers(bundle);


                    currentArmy = bf.getNextArmy();
                }
            }
        }
        notifyAllObservers(this);

    }

    public class CommandBundle {
        public LinkedList<BattleCommand> commands;
        public LinkedList<ActionInfoPanel> panels;

        public CommandBundle() {
            this.commands = new LinkedList<BattleCommand>();
            this.panels = new LinkedList<ActionInfoPanel>();
        }

        public void offer(BattleCommand command, ActionInfoPanel panel){
            if(command != null) {
                commands.offer(command);
                panels.offer(panel);
            }
        }

        public boolean isEmpty() {
            return commands.isEmpty() || panels.isEmpty();
        }
    }

}
