package com.lawsgame.emishitactics.core.states.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.AbstractArmy;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.renderers.TempoAreaRenderer;
import com.lawsgame.emishitactics.core.renderers.interfaces.AreaRenderer;

public class TestBIS extends BattleInteractionState {
    AreaRenderer ar;
    Array<int[]> path;
    Unit guineapig;
    Unit warlord;
    Unit warchief1;
    Unit soldier1;
    Unit soldier2;
    Unit soldier3;

    public TestBIS(BattleInteractionSystem bis) {
        super(bis);
        ar = new TempoAreaRenderer(bis.asm, bis.battlefield, Assets.TileHighligthingAssetsId.SELECTED_AREA_ATTACK_RANGE);
        //ar =  new TempoAreaRenderer(bis.asm, bis.battlefield, Assets.TileHighligthingAssetsId.SELECTED_AREA_ATTACK_RANGE, path);
        guineapig = bis.battlefield.getUnit(9,8);
        path = new Array<int[]>();

        //compose test player army

        warlord = new Unit(true,
                Data.UnitTemplate.EMISHI_TRIBESMAN,19,
                Data.Ethnicity.JAPANESE,
                Data.Orientation.SOUTH,
                Data.Behaviour.CONTROLLED_BY_PLAYER, Data.Weapon.YUMI, Data.Weapon.KANABO, true);
        warlord.setPasAb1(Data.PassiveAbility.PRAYER);
        warlord.setPasAb2(Data.PassiveAbility.NONE);
        warlord.setOffensiveAbility(Data.OffensiveAbility.CRUNCHING_BLOW);
        warlord.equip(Data.Item.NONE, true);
        warlord.equip(Data.Item.NONE, false);
        warlord.setItemStealable(false);
        warlord.setName("Xe Li");
        warlord.setLeadership(15);
        System.out.println("Xe Li hitpoints :"+ warlord.getCurrentHitpoints());
        System.out.println("Xe Li moral :"+ warlord.getCurrentMoral()+"\n");
        //unit0.addBannerSign(...)

        warchief1 = new Unit(true,
                Data.UnitTemplate.CONSCRIPT,9,
                Data.Ethnicity.JAPANESE,
                Data.Orientation.SOUTH,
                Data.Behaviour.CONTROLLED_BY_PLAYER, Data.Weapon.KATANA, Data.Weapon.NAGINATA, true);
        warchief1.setPasAb1(Data.PassiveAbility.NONE);
        warchief1.setPasAb2(Data.PassiveAbility.NONE);
        warchief1.setOffensiveAbility(Data.OffensiveAbility.NONE);
        warchief1.equip(Data.Item.NONE, true);
        warchief1.equip(Data.Item.NONE, false);
        warchief1.setItemStealable(false);
        warchief1.setName("Phillipe");
        warchief1.setLeadership(11);
        warchief1.receiveDamage(1, false);
        //unit0.addBannerSign(...)

        soldier1 = new Unit(true,
                Data.UnitTemplate.EMISHI_TRIBESMAN,5,
                Data.Ethnicity.JAPANESE,
                Data.Orientation.SOUTH,
                Data.Behaviour.CONTROLLED_BY_PLAYER, Data.Weapon.YARI, Data.Weapon.KANABO, true);
        soldier1.setPasAb1(Data.PassiveAbility.PRAYER);
        soldier1.setPasAb2(Data.PassiveAbility.NONE);
        soldier1.setOffensiveAbility(Data.OffensiveAbility.NONE);
        soldier1.equip(Data.Item.NONE, true);
        soldier1.equip(Data.Item.NONE, false);
        soldier1.setItemStealable(false);
        //soldier1.addBannerSign(Data.BannerSign.APEHUCI);
        soldier1.setName("Jim");

        soldier2 = new Unit(true,
                Data.UnitTemplate.EMISHI_TRIBESMAN,7,
                Data.Ethnicity.JAPANESE,
                Data.Orientation.SOUTH,
                Data.Behaviour.CONTROLLED_BY_PLAYER, Data.Weapon.YARI, Data.Weapon.YUMI, true);
        soldier1.setPasAb1(Data.PassiveAbility.NONE);
        soldier1.setPasAb2(Data.PassiveAbility.NONE);
        soldier1.setOffensiveAbility(Data.OffensiveAbility.NONE);
        soldier1.equip(Data.Item.NONE, true);
        soldier1.equip(Data.Item.NONE, false);
        soldier1.setItemStealable(false);
        //soldier1.addBannerSign(Data.BannerSign.APEHUCI);
        soldier1.setName("Johnny");

        soldier3 = new Unit(true,
                Data.UnitTemplate.EMISHI_TRIBESMAN,7,
                Data.Ethnicity.JAPANESE,
                Data.Orientation.SOUTH,
                Data.Behaviour.CONTROLLED_BY_PLAYER, Data.Weapon.YARI, Data.Weapon.YUMI, true);
        soldier1.setPasAb1(Data.PassiveAbility.NONE);
        soldier1.setPasAb2(Data.PassiveAbility.NONE);
        soldier1.setOffensiveAbility(Data.OffensiveAbility.NONE);
        soldier1.equip(Data.Item.NONE, true);
        soldier1.equip(Data.Item.NONE, false);
        soldier1.setItemStealable(false);
        //soldier1.addBannerSign(Data.BannerSign.APEHUCI);
        soldier1.setName("Juju");


        Unit.Army army = new Unit.Army(AbstractArmy.ArmyType.PLAYER);
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
    public void renderBetween(SpriteBatch batch) {
        ar.render(batch);

    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }

    @Override
    public void renderUI() {

    }

    @Override
    public void initiate() {

    }

    @Override
    public void onTouch(float gameX, float gameY) {
        int r = (int)gameY;
        int c = (int)gameX;
        System.out.println("");
        System.out.println("row : "+r);
        System.out.println("col : "+c);

        int[] unitPos = bis.battlefield.getUnitPos(soldier1);
        if(unitPos != null) {
            ar.reset();
            path = bis.battlefield.moveUnit(unitPos[0], unitPos[1], r, c);
            unitPos = bis.battlefield.getUnitPos(soldier1);
            ar.addTiles(bis.battlefield.getMoveArea(unitPos[0], unitPos[1]));
        }


    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float dt) {
        ar.update(dt);

        if(Gdx.input.isKeyPressed(Input.Keys.UP)) bis.getGameCM().translateGameCam(0, 0.1f);
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) bis.getGameCM().translateGameCam(0, -0.1f);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bis.getGameCM().translateGameCam(-0.1f, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bis.getGameCM().translateGameCam(0.1f, 0);

        if(Gdx.input.isKeyJustPressed(Input.Keys.A)){
            int[] coords = bis.battlefield.getUnitPos(warlord);
            System.out.println("\nChoices\n");
            for(Data.ActionChoice choice : Data.ActionChoice.values()){
                System.out.println(choice.name()+" : "+bis.battlefield.canActionbePerformed(bis.battlefield.getUnit(coords[0], coords[1]), coords[0], coords[1], choice));
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            warlord.receiveDamage(3, false);
            System.out.println("hit points : "+warlord.getCurrentHitpoints()+" & moral : "+warlord.getCurrentMoral());
            System.out.println("hit points : "+warchief1.getCurrentHitpoints()+" & moral : "+warchief1.getCurrentMoral());
            System.out.println("hit points : "+soldier1.getCurrentHitpoints()+" & moral : "+soldier1.getCurrentMoral());
            System.out.println("");
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            warlord.receiveDamage(300, false);
        }
        if(warlord.isOutOfCombat() && bis.battlefieldRenderer.getUnitRenderer(warlord) != null && !bis.battlefieldRenderer.getUnitRenderer(warlord).isProceeding()){
            int[] coords = bis.battlefield.getUnitPos(warlord);
            bis.battlefield.removeUnit(coords[0], coords[1]);
        }

    }
}
