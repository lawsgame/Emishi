package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.WalkCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.EarthquakeEvent;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.TrapEvent;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.ActionInfoPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.ChoicePanel;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;
import com.lawsgame.emishitactics.engine.rendering.Animation;

import java.util.LinkedList;

public class TestBIS extends BattleInteractionState implements Observer, ChoicePanel.CommandReceiver {

    Unit sltdUnit;

    ChoicePanel.CommandChoicePanel ccp;



    public TestBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true, true, false);

        IsoBFR bfr = ((IsoBFR)bim.bfr);
        sltdUnit = bim.player.getArmy().getWarlord();
        sltdUnit.addNativeAbility(Data.Ability.BUILD);
        sltdUnit.addNativeAbility(Data.Ability.HEAL);
        bim.player.getArmy().getSquad(sltdUnit, true).get(1).takeDamage(0, false, 1f);



        // -------***<<< ACP and CCP tests >>>***------------------------

        int[] actorPos = bim.bfr.getModel().getUnitPos(sltdUnit);

        // TEST 0

        /*
        for(Data.Ability a : sltdUnit.getAbilities())
            System.out.println(a.name());

        bim.pp.choicePanel.attach(this);
        bim.pp.choicePanel.setContent(actorPos[0], actorPos[1], bim.bcm, new Stack<ActorCommand>());
        bim.pp.choicePanel.show();
        */

        //TEST 1


        /*
        ccp = new TempoCommandCP(bim.uiStage.getViewport(),skin);

        //SlidingPanel gp = new SlidingPanel(bim.uiStage.getViewport(),0.5f , 0,0 ,0 , 0, true, true) {};
        //SlidingPanel gp = new SlidingPanel(bim.uiStage.getViewport(),0.5f , 10,0 ,0 , 0, true, true) {};
        //SlidingPanel gp = new SlidingPanel(bim.uiStage.getViewport(),0.5f , 10,10 ,0 , 0, true, true) {};
        SlidingPanel gp = new SlidingPanel(bim.uiStage.getViewport(),0.5f , 10,10 ,150 , 0, true, true) {};
        //SlidingPanel gp = new SlidingPanel(bim.uiStage.getViewport(),0.5f , 10,10 ,150 , 0, true, false) {};
        //SlidingPanel gp = new TempoActionCP(bim.uiStage.getViewport(), skin);
        bim.uiStage.addActor(gp);
        gp.addActor(ccp);

        //bim.uiStage.addActor(ccp);
        //ccp.attach(this);

        //choicePanel.addActor(ccp);
        //ccp.attach(choicePanel);

        ccp.setButtonIndex(0);
        ccp.setContent(actorPos[0], actorPos[1], bim.bcm, Data.ActionChoice.BUILD);
        ccp.show();


        System.out.println(bim.uiStage.getViewport().getWorldHeight());
        System.out.println(ccp.getY());
        */



        // -------***<<< WINDROSE >>>***------------------------

        /*
        TextureAtlas atlas = bim.asm.get(Assets.ATLAS_BATTLE_ICONS);
        regions = atlas.findRegions(Assets.getWindroseArrowTexture(true, true));
        //regions = atlas.findRegions("arrow_north_active");
        System.out.println("  region size : "+regions.size);
        animation = new Animation(regions.size, 0.2f, true, true, false);
        animation.play();
        */


        // -------***<<< OTHER TESTS >>>***------------------

        //bfr.getModel().getTile(14,7).setLoot(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        //bfr.getModel().getTile(10,7).setRecruit(new unit("Alfred", Data.UnitTemplate.SOLAR_KNIGHT, Data.WeaponType.SWORD));
        bfr.displayAllTraps();
        bfr.displayAllLoots();

    }


    @Override
    public void update(float dt) {
        super.update(dt);
    }

    @Override
    public void end() {

    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }


    @Override
    public boolean handleTouchInput(float xTouch, float yTouch) {
        return false;
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }


    @Override
    public void getChoicePanelNotification(ActorCommand choice) {

    }

    @Override
    public void getNotification(Observable sender, Object data) {

    }
}
