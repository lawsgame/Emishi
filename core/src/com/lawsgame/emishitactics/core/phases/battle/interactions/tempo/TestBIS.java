package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.BattleOverPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.ChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoBattleOverPanel;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;
import com.lawsgame.emishitactics.engine.utils.Lawgger;

public class TestBIS extends BattleInteractionState implements Observer, ChoicePanel.CommandReceiver {
    private static Lawgger log = Lawgger.createInstance(TestBIS.class);

    Unit sltdUnit;
    BattleOverPanel bop;


    public TestBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true, true, false);

        IsoBFR bfr = ((IsoBFR)bim.bfr);
        sltdUnit = bim.player.getArmy().getWarlord();
        int[] actorPos = bim.bfr.getModel().getUnitPos(sltdUnit);

        // -------***<<< ACP and CCP tests >>>***------------------------



        // TEST 0

        /*
        for(Data.Ability a : sltdUnit.getAbilities())


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
        //SlidingPanel gp = new TempoActionChoiceP(bim.uiStage.getViewport(), skin);
        bim.uiStage.addActor(gp);
        gp.addActor(ccp);

        //bim.uiStage.addActor(ccp);
        //ccp.attach(this);

        //choicePanel.addActor(ccp);
        //ccp.attach(choicePanel);

        ccp.setButtonIndex(0);
        ccp.setContent(actorPos[0], actorPos[1], bim.bcm, Data.ActionChoice.BUILD);
        ccp.show();


        */



        // -------***<<< WINDROSE >>>***------------------------

        /*
        TextureAtlas atlas = bim.asm.get(Assets.ATLAS_BATTLE_ICONS);
        regions = atlas.findRegions(Assets.getWindroseArrowTexture(true, true));
        //regions = atlas.findRegions("arrow_north_active");
        animation = new Animation(regions.size, 0.2f, true, true, false);
        animation.play();
        */


        // -------***<<< OTHER TESTS >>>***------------------

        //bfr.getModel().getTile(14,7).setLoot(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        //bfr.getModel().getTile(10,7).setRecruit(new unit("Alfred", Data.UnitTemplate.SOLAR_KNIGHT, Data.WeaponType.SWORD));
        bfr.displayAllTraps();
        bfr.displayAllLoots();


        Skin uiBattleSkin = bim.asm.get(Assets.SKIN_UI, Skin.class);
        bop = TempoBattleOverPanel.create(bim.uiStage.getViewport(), uiBattleSkin);
        bim.uiStage.addActor(bop);
        Array<Unit> units = bim.player.getArmy().getMobilizedUnits(true);
        log.debug(units.size);
        bop.update(units, new int[]{150, 350}, new int[]{600, 1500});
        bop.hide();
    }


    @Override
    public void update(float dt) {
        super.update(dt);
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            bop.show();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            bop.hide();
        }
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
