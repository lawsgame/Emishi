package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.ai.interfaces.AI;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionInfoPanel;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

public class AiBIS extends BattleInteractionState implements Observer {

    private Thread threadAI;

    public AiBIS(BattleInteractionMachine bim) {
        super(bim, true, false, false);
        this.threadAI = new Thread(bim.ai, "AI Thread");
    }

    @Override
    public void init() {
        System.out.println("AI BIS");

        threadAI.start();
        bim.ai.attach(this);

    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }

    @Override
    public synchronized void getNotification(Observable sender, Object data) {

        if (data instanceof AI.CommandBundle) {
            AI.CommandBundle bundle = (AI.CommandBundle) data;
            while (!bundle.isEmpty()) {
                final BattleCommand command = bundle.commands.pop();
                final ActionInfoPanel panel = bundle.panels.pop();

                if(command.isTargetValid()) {

                    bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                        @Override
                        public void apply() {
                            bim.focusOn(command.getRowActor(), command.getColActor(), true, false, false, true, false);
                            if (panel != null) {
                                bim.uiStage.addActor(panel);
                                panel.show();
                            }
                        }
                    }, Data.ACTION_PANEL_DURATION_APPEARANCE));
                    command.pushRenderTasks();

                    //TODO : manage outcomes => HandleOutcomeBIS





                }
            }

        } else {
            bim.ai.detach(this);
            bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                @Override
                public void apply() {
                    bim.replace( new SelectActorBIS(bim, true));
                }
            }, 0f));
        }

    }
}
