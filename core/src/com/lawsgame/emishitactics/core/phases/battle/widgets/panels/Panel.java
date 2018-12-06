package com.lawsgame.emishitactics.core.phases.battle.widgets.panels;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;

public abstract class Panel extends Table {



    protected LinkedList<Action> awaitingActions = new LinkedList<Action>();
    protected Viewport uiport;

    public Panel(Viewport uiport){
        this.uiport = uiport;
    }

    public abstract void show();
    public abstract void hide();
    public abstract boolean isHiding();
    public abstract float getHidingTime();
    public abstract float getShowingTime();

    public void removeAsAction(){
        final Panel p = this;
        awaitingActions.add(new Action() {
            @Override
            public boolean act(float delta) {
                p.remove();
                return false;
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(getActions().size == 0 && awaitingActions.size() > 0){
            addAction(awaitingActions.pop());
        }
    }

    //---------------- METHODS ------------------------


    public void centerPanel() {
        setX(uiport.getWorldWidth()/2f - getWidth()/2f);
        setY(uiport.getWorldHeight()/2f - getHeight()/2f);
    }

    protected void setX(float xPadding, boolean left) {
        setX((left) ? xPadding : uiport.getWorldWidth() - xPadding - getWidth());
    }



    //--------------- EXCEPTION --------------------

    public static class PanelException extends Exception{
        public PanelException(String s) {
            super(s);
        }
    }

}
