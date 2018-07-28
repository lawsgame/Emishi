package com.lawsgame.emishitactics.core.phases.battle.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Banner;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.UnitPanel;

public class LongUnitPanel extends UnitPanel {

    private static float X_TEXT_OFFSET_1_COL = 8f;
    private static float X_TEXT_OFFSET_2_COL = 208f;
    private static float X_TEXT_OFFSET_3_COL = 408f;
    private static float Y_TEXT_OFFSET = 8f;
    private static float PANEL_WIDTH = 650f;
    private static float PANEL_HEIGHT = 400;

    private String mainDescription;
    private String equipDescription;
    private String statDescription;
    private StringBuilder builder;

    public LongUnitPanel(Viewport stageViewport) {
        super(stageViewport);
        setWidth(PANEL_WIDTH);
        setHeight(PANEL_HEIGHT);
        center();
        hide();

        this.mainDescription = "";
        this.statDescription = "";
        this.equipDescription = "";
    }

    @Override
    public void set(Battlefield bf, int rowUnit, int colUnit) {
        if(bf.isTileOccupied(rowUnit, colUnit)) {
            IUnit unit = bf.getUnit(rowUnit, colUnit);

            builder = new StringBuilder("   MAIN\n");
            builder.append("\nName : " + unit.getName());
            builder.append("\nLevel : " + unit.getLevel());
            builder.append("\nJob : " + unit.getJob().name());
            if (unit.isWarlord()) {
                builder.append("\nPosition : warlord");
            } else {
                if (unit.isWarChief()) {
                    builder.append("\nPosition : war chief");
                } else {
                    builder.append("\nPosition : soldier");
                }
            }
            builder.append("\nHit points: " + unit.getCurrentHP() + "/"+unit.getAppHitpoints());
            builder.append("\nMoral: " + unit.getCurrentMoral() + "/" + unit.getAppMoral());
            builder.append("\nAO trigger rate : " + unit.getCurrentOATriggerRate(rowUnit, colUnit, bf));

            if (unit.isMobilized()) {
                IArmy army = unit.getArmy();
                builder.append("\n\n    ARMY\n");
                builder.append("\nWarlord : " + army.getWarlord().getName());
                builder.append("\nWar chief : " + army.getWarchief(unit).getName());
                Banner banner = army.getSquadBanner(unit);
                builder.append("\nBanner");
                builder.append("\n  sign :"+((banner != null && banner.getSign1() != Data.BannerSign.NONE) ? banner.getSign1().name(): ""));
                builder.append("\n  sign :"+((banner != null && banner.getSign2() != Data.BannerSign.NONE) ? banner.getSign2().name(): ""));
                builder.append("\n  sign :"+((banner != null && banner.getSign3() != Data.BannerSign.NONE) ? banner.getSign3().name(): ""));

            }
            mainDescription = builder.toString();
            builder.setLength(0);

            builder.append("    EQUIPEMENT\n");
            builder.append("\nweapon type : "+ unit.getWeaponType());
            Array<Data.Weapon> weapons = unit.getWeapons();
            for(int i = 0; i < weapons.size ; i++){
                builder.append("\nweapon "+i+" : "+weapons.get(i).name().toLowerCase());
            }
            Array<Data.Item> items = unit.getItems();
            for(int i = 0; i < items.size ; i++){
                builder.append("\nitems "+i+" : "+items.get(i).name().toLowerCase());
            }
            builder.append("\n\n    CURRENT WEAPONS\n");
            builder.append("\nName : "+unit.getCurrentWeapon().name().toLowerCase());
            builder.append("\nBase damage : "+unit.getCurrentWeapon().getDamage());
            builder.append("\nBase accuracy : "+unit.getCurrentWeapon().getAccuracy());
            builder.append("\nRange : ("+unit.getCurrentWeapon().getRangeMin()+", "+unit.getCurrentWeapon().getRangeMax()+")");
            builder.append("\nWeapon type : "+ unit.getCurrentWeapon().getWeaponType().name().toLowerCase());
            builder.append("\nDamage type : "+unit.getCurrentWeapon().getDamageType().name().toLowerCase());
            builder.append("\n\n    ABILITIES\n");
            Array<Data.ActiveAbility> activeAbilities = unit.getActiveAbilities();
            for(int i = 0; i < activeAbilities.size ; i++) {
                builder.append("\nActive ability : " + unit.getActiveAbilities().get(i).name().toLowerCase());
            }
            Array<Data.PassiveAbility> passiveAbilities = unit.getPassiveAbilities();
            for(int i = 0; i < passiveAbilities.size ; i++) {
                builder.append("\nPassive ability : " + unit.getPassiveAbilities().get(i).name().toLowerCase());
            }

            equipDescription = builder.toString();
            builder.setLength(0);

            builder.append("    STATISTICS\n");
            builder.append("\nHPT  :" + unit.getCurrentHP() +" ("+ unit.getBaseHitpoints()+")");
            builder.append("\nSTR  :" + unit.getAppStrength() +" ("+ unit.getBaseStrength()+")");
            builder.append("\nDEX  :" + unit.getAppDexterity() +" ("+ unit.getBaseDexterity()+")");
            builder.append("\nSKI  :" + unit.getAppSkill() +" ("+ unit.getBaseSkill()+")");
            builder.append("\nDEF  : ");
            builder.append("\n  against piercing : "+unit.getAppArmor(Data.DamageType.PIERCING));
            builder.append("\n  against edged    : "+unit.getAppArmor(Data.DamageType.EDGED));
            builder.append("\n  against blunt    : "+unit.getAppArmor(Data.DamageType.BLUNT));
            builder.append("\nAGI  : "+ unit.getAppAgility() +" ("+ unit.getBaseAgility()+")");
            builder.append("\nBRA  : "+unit.getAppBravery() +" ("+ unit.getBaseBravery()+")");
            builder.append("\nCHA  : "+unit.getAppCharisma() +" ("+ unit.getBaseCharisma()+")");
            builder.append("\nLDP  : "+unit.getAppLeadership() +" ("+ unit.getBaseLeadership()+")");
            builder.append("\nMOB  : " + unit.getAppMobility() +" ("+ unit.getBaseMobility()+")");

            builder.append("\n\nAttack might : "+unit.getAppAttackMight());
            builder.append("\nAttack accuracy : "+unit.getAppAttackAccuracy());
            builder.append("\nAvoidance : "+unit.getAppAvoidance());
            builder.append("\nRange : ("+unit.getAppWeaponRangeMin()+", "+unit.getAppWeaponRangeMax()+")");

            statDescription = builder.toString();
            builder.setLength(0);

        }
    }

    @Override
    public void show() {
        setVisible(true);
    }

    @Override
    public void hide() {
        setVisible(false);
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(TempoSpritePool.get().getBlackBGSprite(),getX(), getY(), getWidth(), getHeight() );
        BattlePhase.testFont.draw(batch, mainDescription, getX() + X_TEXT_OFFSET_1_COL, getY() + getHeight() - Y_TEXT_OFFSET);
        BattlePhase.testFont.draw(batch, statDescription, getX() + X_TEXT_OFFSET_2_COL, getY() + getHeight() - Y_TEXT_OFFSET);
        BattlePhase.testFont.draw(batch, equipDescription, getX() + X_TEXT_OFFSET_3_COL, getY() + getHeight() - Y_TEXT_OFFSET);
    }


}
