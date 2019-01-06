package com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces;

import com.lawsgame.emishitactics.engine.GameElement;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

public interface WindRose extends GameElement {

    void initialize(BattleUnitRenderer target);
    void attach(Observer observer);
    void detach(Observer observer);
    void setEnable(boolean enable);
    void setVisible(boolean visible);
    boolean handleInput(float xGame, float yGame);

}
