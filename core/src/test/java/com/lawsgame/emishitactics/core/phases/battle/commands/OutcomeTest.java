package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Unit;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class OutcomeTest {

    ActorCommand.Outcome outcome;
    ActorCommand.Outcome outcome1;
    Unit unit1;

    @Before
    public void before(){
        outcome = new ActorCommand.Outcome(new Inventory());
        outcome1 = new ActorCommand.Outcome(new Inventory());
        unit1 = new Unit("jin");
    }

    @Test
    public void test(){
        outcome.add(unit1, 56);
        outcome.add(unit1, 46);
        assertTrue(unit1.getExperience() == 0);
        assertTrue(outcome.expHolders.size == 2);
        outcome.clean();
        assertTrue(outcome.expHolders.size == 1);
        outcome.resolve();
        assertTrue(unit1.getExperience() == 56);

    }

    @Test
    public void mergeTest(){


        outcome.add(unit1, 22);
        outcome1.add(unit1, 46);
        outcome.merge(outcome1);
        outcome.clean();
        outcome.resolve();
        assertTrue(outcome.expHolders.size == 1);
        assertTrue(outcome.expHolders.get(0).experience == 46);
    }
}
