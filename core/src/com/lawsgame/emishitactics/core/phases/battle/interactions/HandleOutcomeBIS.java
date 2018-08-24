package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ExperiencePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.LevelUpPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.LootPanel;

import java.util.Stack;

public class HandleOutcomeBIS extends BattleInteractionState{
    private Stack<BattleCommand> historic;

    private static ExperiencePanel experiencePanel;
    private static LevelUpPanel levelUpPanel;
    private static LootPanel lootPanel;

    public HandleOutcomeBIS(BattleInteractionMachine bim, Stack<BattleCommand> historic) {
        super(bim, true, false, false);
        this.historic = historic;

    }

    @Override
    public void init() {
        System.out.println("HANDLE OUTCOME : "+historic.peek().getActor().getName()+" => "+historic.peek().getTarget().getName()+" : "+historic.peek().getName(bim.mainStringBundle));

        if(historic.size() > 0){

        }
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return false;
    }

    @Override
    public void update60(float dt) {

    }

    @Override
    public void prerender(SpriteBatch batch) {

    }

    @Override
    public void renderBetween(SpriteBatch batch) {

    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }


}
