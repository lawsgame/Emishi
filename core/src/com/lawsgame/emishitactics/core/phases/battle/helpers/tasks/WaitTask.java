package com.lawsgame.emishitactics.core.phases.battle.helpers.tasks;

import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.engine.timers.CountDown;

public class WaitTask implements AnimationScheduler.Task {
    protected CountDown countDown;
    protected boolean initiated = false;

    public WaitTask(float delay){
        countDown = new CountDown(delay);
    }

    @Override
    public void init() {
        initiated = true;
        countDown.run();
    }

    @Override
    public boolean isInitiazed() {
        return initiated;
    }

    @Override
    public boolean isCompleted() {
        return countDown.isFinished();
    }

    @Override
    public boolean isIrrelevant() {
        return countDown.getDelay() == 0;
    }

    @Override
    public void update(float dt) {
        countDown.update(dt);
    }

    @Override
    public String toString() {
        return "WAITING TASK : "+countDown.getDelay()+"\n";
    }
}
