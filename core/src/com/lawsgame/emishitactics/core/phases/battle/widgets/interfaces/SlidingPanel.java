package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

public abstract class SlidingPanel extends Panel {

    protected Viewport stageUIViewport;
    protected float xHiding;
    protected float xShowing;
    protected boolean top;
    protected float yPadding;
    protected float slidingDuration;


    /**
     *
     * @param stageUIViewport : viewport of the stage UI
     * @param slidingDuration : duration of the sliding in and out of the screen
     * @param xShowingPadding :
     * @param yPadding :
     * @param width :
     * @param top :  if true, apply yPadding from the top, else form the bottom
     * @param left : true if hide in the left side of the screen
     */
    public SlidingPanel(Viewport stageUIViewport, float slidingDuration, float xShowingPadding, float yPadding, int width, int height, boolean top, boolean left) {
        super(stageUIViewport);
        this.stageUIViewport = stageUIViewport;
        this.slidingDuration = slidingDuration;
        this.xShowing = (left) ? xShowingPadding : stageUIViewport.getWorldWidth() - width - xShowingPadding;
        this.xHiding = (left) ? -width: stageUIViewport.getWorldWidth();
        this.top = top;
        this.yPadding = yPadding;
        this.setWidth(width);
        this.setHeight(height);
        this.setX(xHiding);
        this.setY((top) ? stageUIViewport.getWorldWidth() - yPadding - getHeight(): yPadding);


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

}
