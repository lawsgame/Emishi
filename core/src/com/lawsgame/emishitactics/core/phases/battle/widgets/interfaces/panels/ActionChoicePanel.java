package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.phases.battle.helpers.PanelPool;

public abstract class ActionChoicePanel extends ChoicePanel<ActionChoice, B> {
    protected CommandPanel subpanel;
    protected Array<ActionChoice> actionChoices;
    protected Array<Button> buttonChoices;

    public ActionChoicePanel(Viewport stageUIViewport, float slidingDuration, float xShowingPadding, float yPadding, int width, int height, boolean top, boolean left) {
        super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
        this.buttonChoices = new Array<Button>();
        this.actionChoices = new Array<ActionChoice>();
    }

    public void invokeCommandPanel(ActionChoice choice, PanelPool pool){

        if(pool.isCommandPanelAvailable()) {
            setTouchable(Touchable.disabled);
            subpanel = pool.getCommandPanel(choice, )
        }

        /*
        bis.bim.pp.commandPanel = new TempoCommandChoicePanel(bis.bim.asm, buttonIndex);
        bis.commandPanel.set(new CommandChoiceButtonHandler(bis, choice));
        bis.commandPanel.setVisible(true);
        bis.bim.uiStage.addActor(bis.commandPanel);
        */
    }

    public void resetACP() {
        setTouchable(Touchable.childrenOnly);
        if(subpanel != null) {
            subpanel.hide();
            subpanel.removeAsAction();
        }
        hide();
    }




}
