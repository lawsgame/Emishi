package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

import java.util.Stack;

public class SelectActionBIS extends BattleInteractionState {
    int rowSltdUnit;
    int colSltdUnit;
    Stack<BattleCommand> historic;
    ActionChoicePanel choicePanel;
    CommandChoicePanel commandPanel;

    public SelectActionBIS(BattleInteractionMachine bim, int rowSltdUnit, int colSltdUnit, Stack<BattleCommand> historic) {
        super(bim, true, true, true);
        this.rowSltdUnit = rowSltdUnit;
        this.colSltdUnit = colSltdUnit;
        this.historic = historic;
        this.choicePanel = new TempoActionChoicePanel(bim.asm);
        this.commandPanel = new TempoCommandChoicePanel(bim.asm);

    }

    public SelectActionBIS(BattleInteractionMachine bim, int rowSltdUnit, int colSltdUnit) {
        this(bim, rowSltdUnit, colSltdUnit, new Stack<BattleCommand>());
    }

    @Override
    public void init() {

        if(bim.battlefield.isTileOccupied(rowSltdUnit, colSltdUnit)) {
            System.out.println("SELECT ACTION : "+bim.battlefield.getUnit(rowSltdUnit, colSltdUnit).getName());

            //set choice panel
            choicePanel.clear();
            choicePanel.setTouchable(Touchable.childrenOnly);
            choicePanel.setChoiceButtons(this);
            choicePanel.setVisible(true);
            choicePanel.setLayout();
            bim.uiStage.addActor(choicePanel);
            commandPanel.setVisible(false);
            bim.uiStage.addActor(commandPanel);

            //focus on the sltd unit
            bim.focusOn(rowSltdUnit, colSltdUnit, true, true, true, true, true);
        }
    }

    @Override
    public void end() {
        super.end();
        choicePanel.remove();
        commandPanel.remove();

    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        choicePanel.setTouchable(Touchable.childrenOnly);
        choicePanel.setVisible(false);
        commandPanel.remove();

        if(bim.battlefield.isTileOccupiedByPlayerControlledUnit(row, col) && bim.battlefield.isTileOccupied(rowSltdUnit, colSltdUnit)){

            IUnit touchedUnit = bim.battlefield.getUnit(row, col);
            IUnit selecetedUnit = bim.battlefield.getUnit(rowSltdUnit, colSltdUnit);
            if(touchedUnit == selecetedUnit) {
                choicePanel.setVisible(true);
            }else{
                if (Utils.undoCommands(historic) && !touchedUnit.isDone()) {
                    bim.replace(new SelectActionBIS(bim, row, col));
                    return true;
                } else {
                    // if not all commands are undoable, all the undoable ones are done, the unit is updated and a new choice panel is provided
                    init();
                    return true;
                }
            }
        }
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

    public static abstract class ChoicePanel extends Table{
        protected static TextButton.TextButtonStyle style;


        public ChoicePanel(){
            super();
            setTouchable(Touchable.childrenOnly);

        }

        public abstract void addButton(TextButton button);
        public abstract void setLayout();


    }

    public static abstract class ActionChoicePanel extends ChoicePanel{

        public void setChoiceButtons(final SelectActionBIS bis) {

            if (bis.bim.battlefield.isTileOccupied(bis.rowSltdUnit, bis.colSltdUnit)) {

                final IUnit sltdUnit = bis.bim.battlefield.getUnit(bis.rowSltdUnit, bis.colSltdUnit);
                final Array<ActionChoice> choices = bis.bim.bcm.getAvailableChoices(sltdUnit, bis.historic);
                for (int i = 0; i < choices.size; i++) {

                    final ActionChoice choice = choices.get(i);
                    final TextButton button = new TextButton(choice.getName(bis.bim.mainI18nBundle), style);
                    final int buttonIndex = i;
                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {

                            Array<BattleCommand> flavors = bis.bim.bcm.getAvailableCommands(sltdUnit, choice, true);

                            if(flavors.size == 1){

                                if(flavors.get(0).setActor(bis.rowSltdUnit, bis.colSltdUnit)) {
                                    bis.bim.replace(new SelectTargetBIS(bis.bim, bis.historic, flavors.get(0)));
                                }
                            }else if(flavors.size > 1){

                                setTouchable(Touchable.disabled);
                                bis.commandPanel.clear();
                                bis.commandPanel.setChoiceButtons(bis, choice);
                                bis.commandPanel.setButtonIndex(buttonIndex);
                                bis.commandPanel.setLayout();
                                bis.commandPanel.setVisible(true);
                            }else{

                                try {
                                    throw new BISException("Battle command manager found no command available for the following available action choice : "+choice.getName(bis.bim.mainI18nBundle));
                                } catch (BISException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
                    addButton(button);
                }
            }
        }
    }


    public static abstract class CommandChoicePanel extends ChoicePanel{

        public abstract void setButtonIndex(int buttonIndex);

        public void setChoiceButtons(final SelectActionBIS bis, ActionChoice actionChoice){
            if (bis.bim.battlefield.isTileOccupied(bis.rowSltdUnit, bis.colSltdUnit)) {

                IUnit sltdUnit = bis.bim.battlefield.getUnit(bis.rowSltdUnit, bis.colSltdUnit);
                Array<BattleCommand> flavors = bis.bim.bcm.getAvailableCommands(sltdUnit, actionChoice, true);
                for (int i = 0; i < flavors.size; i++) {

                    final BattleCommand battleCommand = flavors.get(i);
                    if(battleCommand.setActor(bis.rowSltdUnit, bis.colSltdUnit)) {
                        TextButton button = new TextButton(battleCommand.getName(bis.bim.mainI18nBundle), style);
                        button.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                bis.bim.replace(new SelectTargetBIS(bis.bim, bis.historic, battleCommand));
                            }
                        });
                        addButton(button);
                    }
                }
            }
        }
    }

    public static class TempoActionChoicePanel extends ActionChoicePanel{

        public TempoActionChoicePanel(AssetManager asm) {
            super();
            if(style == null) {
                TextureAtlas uiAtlas = asm.get(Assets.ATLAS_UI);
                Skin skin = new Skin(uiAtlas);

                //set button style
                style = new TextButton.TextButtonStyle();
                style.up = skin.getDrawable(Assets.UI_BUTTON_UP);
                style.down = skin.getDrawable(Assets.UI_BUTTON_DOWN);
                style.font = BattlePhase.testFont;
            }
        }

        @Override
        public void setLayout() {
            setFillParent(true);
            align(Align.right | Align.top);
            padTop(10f);
            padRight(25f);
        }

        public void addButton(TextButton button){
            add(button).width(150f).height(30);
            row();
        }
    }

    public static class TempoCommandChoicePanel extends CommandChoicePanel{
        protected int buttonIndex;

        public TempoCommandChoicePanel(AssetManager asm) {
            super();
            this.buttonIndex = 0;
            if(style == null) {
                TextureAtlas uiAtlas = asm.get(Assets.ATLAS_UI);
                Skin skin = new Skin(uiAtlas);

                //set button style
                style = new TextButton.TextButtonStyle();
                style.up = skin.getDrawable(Assets.UI_BUTTON_UP);
                style.down = skin.getDrawable(Assets.UI_BUTTON_DOWN);
                style.font = BattlePhase.testFont;
            }
        }

        public void setButtonIndex(int buttonIndex) {
            this.buttonIndex = buttonIndex;
        }

        @Override
        public void setLayout() {
            setFillParent(true);
            align(Align.right | Align.top);
            padTop(15f + buttonIndex*30f);
            padRight(15f);
        }

        public void addButton(TextButton button){
            add(button).width(150f).height(30);
            row();
        }
    }
}
