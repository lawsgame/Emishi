package com.lawsgame.emishitactics.engine.patterns.observer;

public interface Event {
    boolean isTriggered(Object data);
    boolean isFinished();
    void execute(Object data);
}
