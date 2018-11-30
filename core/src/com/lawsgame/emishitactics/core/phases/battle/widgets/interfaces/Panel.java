package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;

public abstract class Panel<C> extends Table implements IPanel<C>{


    protected LinkedList<Action> awaitingActions = new LinkedList<Action>();
    protected Viewport stageViewport;

    public Panel(Viewport stageUIViewport){
        this.stageViewport = stageUIViewport;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(getActions().size == 0 && awaitingActions.size() > 0){
            addAction(awaitingActions.pop());
        }
    }

    //---------------- METHODS ------------------------



    @Override
    public void update(final C content) {
        awaitingActions.add(new Action() {
            @Override
            public boolean act(float delta) {
                Panel p = (Panel) getActor();
                p.setContent(content);
                return true;
            }
        });
    }

    protected abstract void setContent(C content);


    @Override
    public void centerPanel() {
        setX(stageViewport.getWorldWidth()/2f - getWidth()/2f);
        setY(stageViewport.getWorldHeight()/2f - getHeight()/2f);
    }

}
