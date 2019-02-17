package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;


public abstract class ActionInfoPanel extends SlidingPanel {



    public ActionInfoPanel(Viewport stageUIViewport, float slidingDuration, float xShowingPadding, float yPadding, int width, int height, boolean top, boolean left) {
        super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
    }


    /**
     *  update content of the action panel
     *
     * @return the coordinates of the next target/actor couple : int[]{rowActor, colActor, rowTarget, colTarget}
     */
    protected abstract int[] updatePanel(boolean nextTarget);

    /**
     *  fill panel with the related pieces of info.
     *  ARCHTUNG! MUST BE CALL JUST BEFORE APPLYING THE ASSOCIATED COMMAND !!
     *
     * @param command: info container
     */
    public abstract void setContent(ActorCommand command);


    /**
     *
     *
     * 1) create and add an action to update the content of the panel accordingly
     * 2) move the camera to the new actor / target displayed
     *
     * @param nextTarget : if true, display panel related to the next target while keeping the same actor, else to the next actor while keeping the same target
     */
    public final void next(final boolean nextTarget, final BattlefieldRenderer bfr){
        final ActionInfoPanel p = this;
        this.awaitingActions.add(new Action() {
            @Override
            public boolean act(float delta) {
                int[] pos = p.updatePanel(nextTarget);
                if(bfr != null && pos != null) {
                    bfr.moveCameraTo(pos[0], pos[1], true);
                }
                return true;
            }
        });
    }

    public final void next(final boolean nextTarget){
        next(nextTarget, null);
    }

    public final void removeAsAction(){
        final ActionInfoPanel p = this;
        this.awaitingActions.add(new Action() {
            @Override
            public boolean act(float delta) {
                p.remove();
                return true;
            }
        });
    }






}
