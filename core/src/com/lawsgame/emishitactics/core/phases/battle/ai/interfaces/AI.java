package com.lawsgame.emishitactics.core.phases.battle.ai.interfaces;

import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
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
    protected IArmy army;

    public AI(
            BattlefieldRenderer bfr,
            AnimationScheduler scheduler,
            ActionPanelPool app,
            Inventory playerInventory,
            IArmy army) {
        this.bfr = bfr;
        this.scheduler = scheduler;
        this.app = app;
        this.playerInventory = playerInventory;
        this.army = army;
    }


    public abstract int[] nextUnit(IArmy army);
    public abstract CommandBundle getCommandPackage(int[] actor);

    @Override
    public void run() {

        Battlefield bf = bfr.getModel();
        int[] actorPos;
        CommandBundle bundle;
        if(!bf.getSolver().isBattleOver()) {


            BeginArmyTurnCommand beginCommand = new BeginArmyTurnCommand(bfr, scheduler,army);
            beginCommand.setDecoupled(true);
            beginCommand.apply();
            bundle = new CommandBundle();
            bundle.offer(beginCommand, null);
            notifyAllObservers(bundle);

            while (!army.isDone()) {
                actorPos = nextUnit(army);
                bundle = getCommandPackage(actorPos);
                notifyAllObservers(bundle);
                if (bf.getSolver().isBattleOver()) {
                    break;
                }
            }
        }

        notifyAllObservers(this);

    }

    /**
     *
     *
     * @param command
     * @param bundle
     * @return true if the command is successfully applied and bundle updated
     */
    protected boolean applyAndStore(ActorCommand command, CommandBundle bundle){
        if(command.isTargetValid()){
            bundle.offer(command, app.getPanel(command));
            command.setDecoupled(true);
            command.apply();
            return true;
        }
        return false;
    }

    protected boolean applyAndStore(ActorCommand command, int rowTarget, int colTarget, CommandBundle bundle){
        command.setTarget(rowTarget, colTarget);
        if(command.isTargetValid()){
            bundle.offer(command, app.getPanel(command));
            command.setDecoupled(true);
            command.apply();
            return true;
        }
        return false;
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

        @Override
        public String toString() {
            String str = "\nAI COMMAND BUNDLE : \n";
            for(int i = 0; i < commands.size(); i++)
                str += commands.get(i).toString();
            return str+"\n";
        }
    }

}
