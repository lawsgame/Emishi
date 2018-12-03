package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class FadingPanel extends Panel {
    protected float fadingDuration;

    public FadingPanel(Viewport stageUIViewport, float fadingDuration, int x, int y,  int width, int height) {
        super(stageUIViewport);
        this.fadingDuration = fadingDuration;
        this.setBounds(x, y, width, height);
    }

    public FadingPanel(Viewport stageUIViewport, float fadingDuration, int width, int height) {
        super(stageUIViewport);
        this.fadingDuration = fadingDuration;
        this.setSize(width, height);
        this.getColor().a = 0;
        this.centerPanel();
    }

    @Override
    public void show() {
        awaitingActions.add(Actions.fadeIn(fadingDuration));
    }

    @Override
    public void hide() {
        awaitingActions.add(Actions.fadeOut(fadingDuration));
    }

    @Override
    public boolean isHiding() {
        return getColor().a == 0;
    }

    @Override
    public float getHidingTime() {
        return fadingDuration;
    }

    @Override
    public float getShowingTime() {
        return fadingDuration;
    }
}
