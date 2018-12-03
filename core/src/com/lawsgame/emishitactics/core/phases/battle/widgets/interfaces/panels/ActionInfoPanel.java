package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.SlidingPanel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public abstract class ActionInfoPanel extends SlidingPanel {



    public ActionInfoPanel(Viewport stageUIViewport, float slidingDuration, float xShowingPadding, float yPadding, int width, int height, boolean top, boolean left) {
        super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
    }

    public static <T extends ActionInfoPanel> ActionInfoPanel create(Viewport uiport, Skin skin, BattlefieldRenderer bfr, Class<T> c){
        ActionInfoPanel panel = null;

        try {
            Class[] paramTypes = new Class[]{Viewport.class, Skin.class, BattlefieldRenderer.class};
            Constructor<T> constructor = c.getConstructor(paramTypes);
            Object[] paramArray = new Object[]{uiport, skin, bfr};
            panel = constructor.newInstance(paramArray);


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return panel;
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
