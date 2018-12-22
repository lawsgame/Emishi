package com.lawsgame.emishitactics.core.phases.battle.widgets.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

public abstract class SlidingPanel extends Panel {

    protected float xHiding;
    protected float xShowing;
    protected boolean top;
    protected float yPadding;
    protected float slidingDuration;


    /**
     *
     * @param uiport : viewport of the stage UI
     * @param slidingDuration : duration of the sliding in and out of the screen
     * @param xShowingPadding :
     * @param yPadding :
     * @param width :
     * @param top :  if true, run yPadding from the top, else form the bottom
     * @param left : true if hide in the left side of the screen
     */
    public SlidingPanel(Viewport uiport, float slidingDuration, float xShowingPadding, float yPadding, int width, int height, boolean top, boolean left) {
        super(uiport);
        this.slidingDuration = slidingDuration;
        this.xShowing = (left) ? xShowingPadding : uiport.getWorldWidth() - width - xShowingPadding;
        this.xHiding = (left) ? -width: uiport.getWorldWidth();
        this.top = top;
        this.yPadding = yPadding;
        this.setSize(width, height);
        this.setX(xHiding);
        this.updateY();
    }

    @Override
    public void show() {
        awaitingActions.offer(moveTo(xShowing, getY(), slidingDuration));
    }

    @Override
    public void hide() {
        awaitingActions.offer(moveTo(xHiding, getY(), slidingDuration));
    }

    @Override
    public boolean isHiding() {
        return getX() == xHiding;
    }

    @Override
    public float getHidingTime() {
        return slidingDuration;
    }

    @Override
    public float getShowingTime() {
        return slidingDuration;
    }

    /**
     *
     * update Y according to {@link SlidingPanel#yPadding} and {@link Actor#getHeight()}
     */
    public void updateY(){
        this.setY((top) ? uiport.getWorldHeight() - yPadding - getHeight(): yPadding);
    }

}
