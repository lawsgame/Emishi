package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.EndTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoCommandChoicePanel;
import com.lawsgame.emishitactics.engine.GameRenderableEntity;

import java.util.HashMap;

public class EndTurnBIS extends BattleInteractionState {
    private int colSltdUnit;
    private int rowSltdUnit;
    private ChoicePanel orientationChoicePanel;

    public EndTurnBIS(BattleInteractionMachine bim, int rowSltdUnit, int colSltdUnit) {
        super(bim, true, false, true);
        this.rowSltdUnit = rowSltdUnit;
        this.colSltdUnit = colSltdUnit;
        this.orientationChoicePanel = new TempoChoicePanel(bim.asm);
    }

    @Override
    public void init() {
        System.out.println("END TURN : "+rowSltdUnit+" "+colSltdUnit+" => "+bim.battlefield.getUnit(rowSltdUnit, colSltdUnit).getName());

        this.orientationChoicePanel.build(new OrientationButtonHandler(this));
        this.orientationChoicePanel.setVisible(true);
        bim.uiStage.addActor(orientationChoicePanel);

        bim.focusOn(rowSltdUnit, colSltdUnit, true, false, false,true, false);
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
        if(bim.battlefield.isTileOccupied(rowSltdUnit, colSltdUnit) && bim.battlefield.getUnit(rowSltdUnit, colSltdUnit).isMobilized()){

            IUnit sltdUnit =  bim.battlefield.getUnit(rowSltdUnit, colSltdUnit);
            IArmy currentArmy = sltdUnit.getArmy();
            if(currentArmy.isDone()){
                bim.end(currentArmy);
                bim.scheduler.addTask(new StandardTask(bim.bfr.getUnitRenderer(sltdUnit), Notification.Done.get(false)));
                bim.replace(new AiBIS(bim));
            }else{

                bim.replace(new SelectActorBIS(bim, rowSltdUnit, colSltdUnit, false));
            }
        }
    }

    static class OrientationButtonHandler implements ChoicePanel.ButtonHandler{
        private EndTurnBIS bis;

        public OrientationButtonHandler(EndTurnBIS bis) {
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
            if (bis.bim.battlefield.isTileOccupied(bis.rowSltdUnit, bis.colSltdUnit)) {
                button = new TextButton(orientation.getName(bis.bim.mainI18nBundle), style);
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {

                        // change the unit orientation
                        ChooseOrientationCommand orientationCommand = new ChooseOrientationCommand(bis.bim.bfr, bis.bim.scheduler, orientation);
                        orientationCommand.apply(bis.rowSltdUnit, bis.colSltdUnit);

                        //proceed to the next BIS
                        bis.proceed();
                    }
                });
            }
            return button;
        }

    }
}
