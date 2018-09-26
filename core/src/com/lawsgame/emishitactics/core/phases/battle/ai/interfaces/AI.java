package com.lawsgame.emishitactics.core.phases.battle.ai.interfaces;

import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.ActionPanelPool;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TurnManager;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionInfoPanel;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

import java.util.LinkedList;

public abstract class AI extends Observable implements Runnable {

    protected BattlefieldRenderer bfr;
    protected AnimationScheduler scheduler;
    protected ActionPanelPool app;
    protected Inventory playerInventory;
    protected TurnManager tm;

    public AI(BattlefieldRenderer bfr, AnimationScheduler scheduler, ActionPanelPool app, Inventory playerInventory, TurnManager tm) {
        this.bfr = bfr;
        this.scheduler = scheduler;
        this.app = app;
        this.playerInventory = playerInventory;
        this.tm =tm;
    }


    public abstract int[] nextUnit(IArmy army);
    public abstract CommandBundle getCommandPackage(int[] actor);

    @Override
    public void run() {
        Battlefield bf = bfr.getModel();
        IArmy currentArmy;
        int[] actorPos;
        CommandBundle bundle;
        if(!bf.getSolver().isBattleOver()) {
            loop :
            {
                bf.resetArmyTurnOrder();
                currentArmy = bf.getNextArmy();

                while (!currentArmy.isPlayerControlled()) {
                    tm.beginTurn(currentArmy);
                    while (!currentArmy.isDone()) {
                        actorPos = nextUnit(currentArmy);
                        bundle = getCommandPackage(actorPos);
                        notifyAllObservers(bundle);
                        if (bf.getSolver().isBattleOver()) {
                            break loop;
                        }
                    }
                    tm.endTurn(currentArmy);
                    currentArmy = bf.getNextArmy();
                }
            }
        }
        notifyAllObservers(null);

    }

    public class CommandBundle {
        public LinkedList<BattleCommand> commands;
        public LinkedList<ActionInfoPanel> panels;

        public CommandBundle() {
            this.commands = new LinkedList<BattleCommand>();
            this.panels = new LinkedList<ActionInfoPanel>();
        }

        public boolean isEmpty() {
            return commands.isEmpty() || panels.isEmpty();
        }
    }

}
