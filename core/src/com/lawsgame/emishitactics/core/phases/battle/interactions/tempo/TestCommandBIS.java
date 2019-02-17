package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.CoveringFireCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.GuardCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.ActionInfoPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoAIP;

import java.util.LinkedList;

public class TestCommandBIS extends BattleInteractionState {
    Unit sltdUnit;
    BattleCommand cbc;
    ActorCommand cac;
    LinkedList<ActorCommand> historic = new LinkedList<ActorCommand>();
    Area ccActionArea;
    Area ccImpactArea;
    Area ccTargets;
    ActionInfoPanel panel =null;

    public TestCommandBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true, false, false);
        // get standard sltd unit as plyaer's army warlord

        cbc = null;

        cac = new AttackCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        cac.setFree(true);

        ccActionArea = new Area(bim.bfr.getModel(), Data.AreaType.MOVE_AREA);
        ccImpactArea = new Area(bim.bfr.getModel(), Data.AreaType.FOE_ACTION_AREA);
        ccTargets = new Area(bim.bfr.getModel(), Data.AreaType.DEPLOYMENT_AREA);
        bim.bfr.addAreaRenderer(ccImpactArea);
        bim.bfr.addAreaRenderer(ccActionArea);
        bim.bfr.addAreaRenderer(ccTargets);

        // OTHERS PARAMS
        setGuardingArea(true);
        setCoveringArea(false);
        bim.bfr.displayAllTraps();
        bim.bfr.displayAllLoots();
        sltdUnit = bim.player.getArmy().getWarlord();
    }

    public void setGuardingArea(boolean load){
        Unit randomFoe = bim.bfr.getModel().getUnit(13,8);
        randomFoe.addNativeAbility(Data.Ability.GUARD);
        int[] randomFoePos = bim.bfr.getModel().getUnitPos(randomFoe);
        GuardCommand guardCommand = new GuardCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        guardCommand.setFree(true);
        if(!guardCommand.apply(randomFoePos[0], randomFoePos[1])){
            System.out.println("command failed to be applied");
            System.out.println("    initiator ? : " + guardCommand.isInitiatorValid());
            System.out.println("    target ?    : " + guardCommand.isTargetValid());
        }
    }

    public void setCoveringArea(boolean load){
        Unit randomFoe = bim.bfr.getModel().getUnit(13,5);
        int[] randomFoePos = bim.bfr.getModel().getUnitPos(randomFoe);
        CoveringFireCommand cfc = new CoveringFireCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        cfc.setFree(true);
        if(!cfc.apply(randomFoePos[0], randomFoePos[1])){
            System.out.println("command failed to be applied");
            System.out.println("    initiator ? : " + cfc.isInitiatorValid());
            System.out.println("    target ?    : " + cfc.isTargetValid());
        }
    }



    @Override
    public boolean handleTouchInput(int row, int col) {
        // TEST CUSTOMED COMMAND
        if(cac != null) {
            System.out.println("input : " + row + " " + col);
            int[] actorPos = bim.bfr.getModel().getUnitPos(sltdUnit);
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                // I - UPDATE SLTD UNIT POSITION
                if (bim.bfr.getModel().isTileOccupied(row, col)) {
                    sltdUnit = bim.bfr.getModel().getUnit(row, col);
                } else {
                    bim.bfr.getModel().moveUnit(actorPos[0], actorPos[1], row, col, true);
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                // II - APPLY CUSTOMED COMMAND
                // set parameters
                cac.setInitiator(actorPos[0], actorPos[1]);
                cac.setTarget(row, col);
                // update and display action pan
                if(panel != null){
                    panel.remove();
                    panel = null;
                }
                if (bim.pp.isActionPanelAvailable(cac.getActionChoice())) {
                    panel = bim.wf.getActionInfoPanel(cac.getActionChoice());
                    bim.uiStage.addActor(panel);
                    panel.hide();
                    panel.setContent(cac);
                    panel.show();
                }
                // run command
                if (!cac.apply()) {
                    System.out.println("command failed to be applied");
                    System.out.println("    initiator ? : " + cac.isInitiatorValid());
                    System.out.println("    target ?    : " + cac.isTargetValid());
                }
            } else {
                // III - SHOW INFO ABOUT COMMAND
                cac.setInitiator(actorPos[0], actorPos[1]);
                cac.setFree(true);
                if (cac.isInitiatorValid()) {
                    Array<int[]> impact = cac.getImpactArea(actorPos[0], actorPos[1], row, col);
                    ccImpactArea.setTiles(impact, true);
                    ccActionArea.setTiles(cac.getActionArea(), true);
                    ccTargets.setTiles(cac.getTargetsAtRange(), true);

                }
            }
        }
        return true;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if(cbc != null && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            cbc.apply();
        }
    }
}
