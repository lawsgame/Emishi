package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.EndUnitTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoChoicePanel;

public class EndUnitTurnBIS extends BattleInteractionState {
    private Unit actor;
    private EndUnitTurnCommand endUnitTurnCommand;
    private ChoicePanel orientationChoicePanel;

    public EndUnitTurnBIS(BattleInteractionMachine bim, Unit actor) {
        super(bim, true, true, true, false, true);
        this.actor = actor;
        this.orientationChoicePanel = new TempoChoicePanel(bim.asm);
        this.endUnitTurnCommand = new EndUnitTurnCommand(bim.bfr, bim.scheduler, bim.player.getInventory(), bim.thl);
    }

    @Override
    public void init() {
        System.out.println("END TURN of "+actor.getName());

        super.init();
        if(actor.isOutOfAction()){

            proceed();
        }else {

            this.orientationChoicePanel.build(new OrientationButtonHandler(this));
            this.orientationChoicePanel.setVisible(true);
            bim.uiStage.addActor(orientationChoicePanel);

            if(bim.battlefield.isUnitDeployed(actor)) {
                int[] actorPos = bim.battlefield.getUnitPos(actor);
                bim.focusOn(actorPos[0], actorPos[1], true, true, true, TileHighlighter.SltdUpdateMode.MATCH_TOUCHED_TILE, true);
            }
        }
    }

    @Override
    public void end() {
        super.end();
        this.orientationChoicePanel.remove();
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }

    private void proceed(){
        if(actor.isMobilized()){

            if(!actor.isOutOfAction()){
                int[] actorPos = bim.battlefield.getUnitPos(actor);
                this.endUnitTurnCommand.apply(actorPos[0], actorPos[1]);
            }

            if (bim.battlefield.getCurrentArmy().isDone()) {

                bim.replace(new EndArmyTurnBIS(bim));
            } else {

                bim.replace(new SelectActorBIS(bim, false));
            }
        }
    }

    static class OrientationButtonHandler implements ChoicePanel.ButtonHandler{
        private EndUnitTurnBIS bis;

        public OrientationButtonHandler(EndUnitTurnBIS bis) {
            this.bis = bis;
        }

        public Array<TextButton> getButtons(TextButton.TextButtonStyle style){
            Array<TextButton> buttons = new Array<TextButton>();

            TextButton button;
            for(int i = 0; i < Data.Orientation.values().length; i++){
                button = createButton(Data.Orientation.values()[i], style);
                if(button != null){
                    buttons.add(button);
                }
            }

            return buttons;
        }

        private TextButton createButton (final Data.Orientation orientation, TextButton.TextButtonStyle style){
            TextButton button = null;

            if (bis.bim.battlefield.isUnitDeployed(bis.actor)) {
                final int[] actorPos = bis.bim.battlefield.getUnitPos(bis.actor);
                button = new TextButton(orientation.getName(bis.bim.mainI18nBundle), style);
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {

                        // change the unit orientation
                        ChooseOrientationCommand orientationCommand = new ChooseOrientationCommand(bis.bim.bfr, bis.bim.scheduler, bis.bim.player.getInventory(), orientation);
                        orientationCommand.apply(actorPos[0], actorPos[1]);

                        //proceed to the next BIS
                        bis.proceed();
                    }
                });
            }
            return button;
        }

    }
}
