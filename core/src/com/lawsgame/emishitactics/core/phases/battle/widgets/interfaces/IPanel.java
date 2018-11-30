package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

public interface IPanel <C>{
    void show();
    void hide();
    boolean isHiding();
    float getHidingTime();
    float getShowingTime();

    void update(C content);
    void centerPanel();
}
