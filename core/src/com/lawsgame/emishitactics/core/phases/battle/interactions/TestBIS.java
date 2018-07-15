package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.widgets.TempoArea;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.Area;

public class TestBIS extends BattleInteractionState {
    Area ar;
    Array<int[]> path;
    Unit guineapig;
    Unit warlord;
    Unit warchief1;
    Unit soldier1;
    Unit soldier2;
    Unit soldier3;
    Area bannerArea;
    BattleCommand testCommand;
    boolean commandExecutionMode = false;

    public TestBIS(BattleInteractionMachine bis) {
        super(bis, true, true, true);
        ar = new TempoArea(bis.asm, bis.battlefield, Assets.HighlightedTile.ACTION_RANGE);
        //ar =  new TempoArea(bim.asm, bim.battlefield, Assets.HighlightedTile.ACTION_RANGE, path);
        guineapig = bis.battlefield.getUnit(5,7);
        path = new Array<int[]>();

        //compose test player army

        warlord = new Unit(Data.UnitTemplate.EMISHI_TRIBESMAN, false, 19, Data.Ethnicity.JAPANESE,Data.Weapon.YUMI, Data.Weapon.WARABITE, true);
        warlord.setOrientation(Data.Orientation.SOUTH);
        warlord.setBehaviour(Data.Behaviour.CONTROLLED_BY_PLAYER);
        warlord.setRightHanded(true);
        warlord.equip(Data.Item.NONE, true);
        warlord.equip(Data.Item.NONE, false);
        warlord.setItemStealable(false);
        warlord.setName("Xe Li");
        warlord.setLeadership(15);

        //unit0.addBannerSign(...)

        warchief1 = new Unit(Data.UnitTemplate.EMISHI_TRIBESMAN, false, 15, Data.Ethnicity.JAPANESE,Data.Weapon.WARABITE, Data.Weapon.YARI, true);
        warchief1.equip(Data.Item.NONE, true);
        warchief1.equip(Data.Item.NONE, false);
        warchief1.setItemStealable(false);
        warchief1.setName("Phillipe");
        warchief1.setLeadership(11);
        //unit0.addBannerSign(...)

        soldier1 = new Unit(Data.UnitTemplate.CONSCRIPT, false, 12, Data.Ethnicity.JAPANESE,Data.Weapon.KATANA, Data.Weapon.NODACHI, true);
        soldier1.equip(Data.Item.NONE, true);
        soldier1.equip(Data.Item.NONE, false);
        soldier1.setItemStealable(false);
        //soldier1.addBannerSign(Data.BannerSign.APEHUCI);
        soldier1.setName("Jim");

        soldier2 = new Unit(Data.UnitTemplate.CONSCRIPT, false, 3, Data.Ethnicity.JAPANESE,Data.Weapon.YUMI, Data.Weapon.NODACHI, true);
        soldier2.equip(Data.Item.NONE, true);
        soldier2.equip(Data.Item.NONE, false);
        soldier2.setItemStealable(false);
        soldier2.setName("Johnny");


        soldier3 = new Unit(Data.UnitTemplate.CONSCRIPT, true, 5, Data.Ethnicity.JAPANESE,Data.Weapon.YARI, Data.Weapon.NAGINATA, true);
        soldier3.equip(Data.Item.NONE, true);
        soldier3.equip(Data.Item.NONE, false);
        soldier3.setItemStealable(false);
        soldier3.addBannerSign(Data.BannerSign.AMATERASU);
        soldier3.setName("Juju");




        Unit.Army army = new Unit.Army(Data.ArmyType.PLAYER);
        System.out.println("maximum of units under the warlord autority : "+warlord.getNbMaxUnits(true));
        army.appointWarLord(warlord);
        army.appointWarChief(warchief1);
        army.appointSoldier(soldier1, 0);
        army.appointSoldier(soldier2, 0);
        army.appointSoldier(soldier3, 0);




        System.out.println(army.toString());
        bis.battlefield.deployUnit(6,6,warlord);
        bis.battlefield.deployUnit(6,7,warchief1);
        bis.battlefield.deployUnit(7,7,soldier2);
        bis.battlefield.deployUnit(6,8,soldier3);
        bis.battlefield.deployUnit(8,8,soldier1);

        //BANNER_RANGE TEST
        bannerArea =  new TempoArea(bis.asm, bis.battlefield, Assets.HighlightedTile.COVERING_FIRE);
        bannerArea.addTiles(Utils.getEreaFromRange(bis.battlefield,6,8,1,army.getBannerRange()));
        System.out.println("BANNER_RANGE TEST");
        System.out.println("warlord   : "+warlord.isStandardBearer());
        System.out.println("warchief1 : "+warchief1.isStandardBearer());
        System.out.println("soldier1  : "+soldier1.isStandardBearer());
        System.out.println("soldier2  : "+soldier2.isStandardBearer());
        System.out.println("soldier3  : "+soldier3.isStandardBearer());


        soldier2.applyDamage(3, false, false);
        System.out.println("\nJohnny hitpoints :"+ soldier2.getCurrentHitpoints() +"/"+ soldier2.getAppHitPoints());
        System.out.println("Johnny moral :"+ soldier2.getCurrentMoral() +"/"+soldier2.getAppMoral());
        System.out.println("wounded ? :"+ soldier2.isWounded()+"\n");

        //WIDGETS

    }


    @Override
    public void update1(float dt) {

    }

    @Override
    public void update3(float dt) {

    }

    @Override
    public void update12(float dt) {

    }

    @Override
    public void prerender(SpriteBatch batch) {

    }

    @Override
    public void renderBetween(SpriteBatch batch) {
        ar.render(batch);
        bannerArea.render(batch);

    }

    @Override
    public void renderAhead(SpriteBatch batch) { }

    @Override
    public void init() {    }

    @Override
    public void handleTouchInput(int r, int c) {
        System.out.println("");
        System.out.println("row : "+r);
        System.out.println("col : "+c);
        int[] unitPos = bim.battlefield.getUnitPos(soldier1);

        if(commandExecutionMode){

            //TEST COMMAND
            testCommand = new AttackCommand(bim.bfr);
            testCommand.setActor(unitPos[1], unitPos[2]);
            testCommand.setTarget(r, c);
            if(testCommand.isTargetValid()){
                System.out.println("Chosen target valid!");
                testCommand.execute();
            }


        }else {

            // MOVE TEST
            /*
            if(unitPos != null) {
                ar.reset();
                bim.battlefield.moveUnit(unitPos[0], unitPos[1], r, c);
                path = bim.battlefield.getShortestPath(unitPos[0], unitPos[1], r, c, soldier1.has(Data.PassiveAbility.PATHFINDER), soldier1.getAllegeance());
                bim.battlefield.notifyAllObservers(path);
                ar.addTiles(bim.battlefield.getMoveArea(unitPos[0], unitPos[1]));
            }
            */

            if (unitPos != null) {
                ar.reset();
                bim.battlefield.moveUnit(unitPos[0], unitPos[1], r, c);
                path = bim.battlefield.getShortestPath(unitPos[0], unitPos[1], r, c, soldier1.has(Data.PassiveAbility.PATHFINDER), soldier1.getAllegeance());
                bim.battlefield.notifyAllObservers(new int[]{r, c});
                //ar.addTiles(bim.battlefield.getMoveArea(unitPos[0], unitPos[1]));
            }


                //IMPACT AREA TEST
            /*
            if(unitPos != null) {
                ar.reset();
                unitPos = bim.getBattlefield().getUnitPos(soldier1);
                //path = bim.battlefield.getImpactArea(Data.ActionChoice.USE_SWIRLING_BLOW, unitPos[0], unitPos[1], r, c);
                path = bim.getBattlefield().getTargetFromCollateral(Data.ActionChoice.USE_SWIRLING_BLOW, unitPos[0], unitPos[1], r, c);
                ar.addTiles(path);
            }
            */

                //BUILD TEST
                //bim.getBattlefield().build(unitPos[0], unitPos[1], r, c, true);

                //GUARD TEST


                //TARGET TILE VALIDITY TEST


                //PANEL TEST

            /*
            bim.shortUnitPanel.hide();
            bim.longUnitPanel.hide();
            bim.longTilePanel.hide();
            if(bim.battlefield.isTileOccupied(r,c)) {
                bim.shortUnitPanel.set(bim.battlefield,r , c);
                bim.shortUnitPanel.show();
                bim.longUnitPanel.set(bim.battlefield, r, c);
                bim.longUnitPanel.show();
            }else{
                bim.longTilePanel.set(bim.battlefield.getTile(r, c));
                bim.longTilePanel.show();
            }

            bim.shortTilePanel.hide();
            bim.shortTilePanel.set(bim.battlefield.getTile(r, c));
            bim.shortTilePanel.show();
            */

            //CAMERA TEST
            //bim.gcm.focusOn(c, r, true);


        }



    }

    @Override
    public void end() {

    }

    @Override
    public void update60(float dt) {


        // CHOICES TEST
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
            int[] coords = bim.battlefield.getUnitPos(warlord);
            System.out.println("\nChoices\n");
            for(Data.ActionChoice choice : Data.ActionChoice.values()){
                System.out.println(choice.name()+" : "+ bim.bcm.canActionbePerformedBy(bim.battlefield.getUnit(coords[0], coords[1]) , choice));
            }
        }

        //TEST COMMAND
        if(testCommand != null)
            testCommand.update(dt);


        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            commandExecutionMode = !commandExecutionMode;
            if(commandExecutionMode){
                System.out.println("Command Execution Mode");
            }else{
                System.out.println("Normal Mode");
            }
        }

        //OTHER TESTS
        if(!commandExecutionMode) {

            // DAMAGE DEALING TEST
            if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                System.out.println("Damage received!");
                warlord.applyDamage(3, false, true);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
                System.out.println("Kill!");
                warlord.applyDamage(300, false, true);
            }

            //TEST SWITCH POSITION
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                int[] coords = bim.battlefield.getUnitPos(warlord);
                int[] coords0 = bim.battlefield.getUnitPos(warchief1);
                bim.battlefield.switchUnitsPosition(coords[0], coords[1], coords0[0], coords0[1]);
                bim.battlefield.notifyAllObservers(new int[]{coords[0], coords[1], coords0[0], coords0[1]});

            }

            //TEST HEAL
            if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                warlord.notifyAllObservers(new int[]{warlord.getCurrentMoral(), warlord.getCurrentHitpoints()});
                warlord.treated(warchief1.getCurrentHealPower());
                warchief1.notifyAllObservers(Data.AnimationId.HEAL);
            }

            //

        }
    }
}
