package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattleCommandManager;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.SlidingPanel;

import java.util.Stack;

public abstract class ChoicePanel<P> extends SlidingPanel {
    protected Array<CommandReceiver> receivers;

    public ChoicePanel(Viewport stageUIViewport, float slidingDuration, float xShowingPadding, float yPadding, int width, int height, boolean top, boolean left) {
        super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
        this.receivers = new Array<CommandReceiver>();
    }

    public abstract void setContent(int rowActor, int colActor, BattleCommandManager bcm, P param);

    public void attach(CommandReceiver receiver){
        receivers.add(receiver);
    }

    public void detach(CommandReceiver receiver){
        receivers.removeValue(receiver, true);
    }

    public void notifyReceiver(ActorCommand command){
        for(int i = 0; i < receivers.size; i++){
            receivers.get(i).getChoicePanelNotification(command);
        }
    }

    //--------------------  UTILITY CLASSES --------------------------------------------


    public interface CommandReceiver{
        void getChoicePanelNotification(ActorCommand choice);
    }

    public static abstract class CommandChoicePanel extends ChoicePanel<Data.ActionChoice>{

        public CommandChoicePanel(Viewport stageUIViewport, float slidingDuration, float xShowingPadding, float yPadding, int width, int height, boolean top, boolean left) {
            super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
        }

        /**
         * must be called before setContent !!
         *
         * @param buttonIndex : index of the button which trigger the appearance of the command panel
         */
        public abstract void setButtonIndex(int buttonIndex);
    }

    public static abstract class ActionChoicePanel extends ChoicePanel<Stack<ActorCommand>> implements CommandReceiver{

        public ActionChoicePanel(Viewport stageUIViewport, float slidingDuration, float xShowingPadding, float yPadding, int width, int height, boolean top, boolean left) {
            super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
        }


        public abstract void resetPanel(boolean hideCPAsWell);
    }

}
