package com.lawsgame.emishitactics.core.phases.battle.oldpan;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;

/**
 * y
 * ^
 * |
 * |
 * |
 * |
 * (0,0)-----------> x
 *
 *
 * features added:
 *  - use a queue to handle actions one after another
 *  - allow to setTiles the position of the widget without knowing the UI viewport dimensions
 *
 *
 */

public abstract class TPanel extends Actor {
    protected LinkedList<Action> awaitingActions = new LinkedList<Action>();
    protected Viewport stageViewport;

    public TPanel(Viewport stageViewport){
        this.stageViewport = stageViewport;
    }

    public abstract void show();
    public abstract void hide();
    public abstract boolean isHiding();
    public abstract float getHidingTime();
    public abstract float getShowingTime();


    @Override
    public void act(float delta) {
        super.act(delta);
        if(getActions().size == 0 && awaitingActions.size() > 0){
            addAction(awaitingActions.pop());
        }
    }



    //---------- CONVIENT METHOD -----------------------------

    public void setX(float x, boolean fromLeft){
        if(fromLeft){
            super.setX(x);
        }else{
            super.setX(stageViewport.getWorldWidth() - x);
        }
    }

    public void setY(float y, boolean fromDown){
        if(fromDown){
            super.setY(y);
        }else{
            super.setY(stageViewport.getWorldHeight() - y);
        }
    }

    public void setPosition(float x, float y, boolean fromLeft, boolean fromdown){
        setX(x, fromLeft);
        setY(y, fromdown);
    }

    public void setPositionAndDimension(float x, float y , float width, float height, boolean fromLeft, boolean fromdown){
        setX(x, fromLeft);
        setY(y, fromdown);
        setWidth(width);
        setHeight(height);
    }

    public void center(){
        setX(stageViewport.getWorldWidth()/2f - getWidth()/2f);
        setY(stageViewport.getWorldHeight()/2f - getHeight()/2f);
    }
}
