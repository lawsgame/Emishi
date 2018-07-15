package com.lawsgame.emishitactics.core.phases.battle.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.AArmy;
import com.lawsgame.emishitactics.core.models.Banner;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.UnitPanel;

public class LongUnitPanel extends UnitPanel {

    private static float X_TEXT_OFFSET_1_COL = 8f;
    private static float X_TEXT_OFFSET_2_COL = 208f;
    private static float X_TEXT_OFFSET_3_COL = 408f;
    private static float Y_TEXT_OFFSET = 8f;
    private static float PANEL_WIDTH = 650f;
    private static float PANEL_HEIGHT = 500;

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
            Unit unit = bf.getUnit(rowUnit, colUnit);

            builder = new StringBuilder("   MAIN\n");
            builder.append("\nName : " + unit.getName());
            builder.append("\nLevel : " + unit.getLevel());
            builder.append("\nJob : " + unit.getJobName());
            if (unit.isWarlord()) {
                builder.append("\nPosition : warlord");
            } else {
                if (unit.isWarChief()) {
                    builder.append("\nPosition : war chief");
                } else {
                    builder.append("\nPosition : soldier");
                }
            }
            builder.append("\nMoral (HP) : " + unit.getCurrentMoral() + "/" + unit.getAppMoral() + " (" + unit.getCurrentHitpoints() + ")");
            builder.append("\nAO trigger rate : " + unit.getAppAbilityTriggerRate());

            if (unit.isMobilized()) {
                AArmy army = unit.getArmy();
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
            builder.append("\nPrimary weapon : "+unit.getPrimaryWeapon().name().toLowerCase());
            builder.append("\nSecondary weapon : "+unit.getSecondaryWeapon().name().toLowerCase());
            builder.append("\nItem : "+unit.getItem1().name().toLowerCase());
            builder.append("\nItem : "+unit.getItem2().name().toLowerCase());
            builder.append("\nPassive ability : "+unit.getSupportAbility().name().toLowerCase());
            builder.append("\nPassive ability : "+unit.getPassiveAbility().name().toLowerCase());
            builder.append("\nOffensive ability : "+unit.getOffensiveAbility().name().toLowerCase());
            builder.append("\n\nCURRENT WEAPONS\n");
            builder.append("\nName : "+unit.getCurrentWeapon().name().toLowerCase());
            builder.append("\nFootman only : "+unit.getCurrentWeapon().isFootmanOnly());
            builder.append("\nBase damage : "+unit.getCurrentWeapon().getDamage());
            builder.append("\nBase accuracy : "+unit.getCurrentWeapon().getAccuracy());
            builder.append("\nDamage type : "+unit.getCurrentWeapon().getType().name().toLowerCase());
            builder.append("\nRange : ("+unit.getCurrentWeapon().getRangeMin()+", "+unit.getCurrentWeapon().getRangeMax()+")");
            builder.append("\nSpecial move : "+unit.getCurrentWeapon().getArt().name().toLowerCase());

            equipDescription = builder.toString();
            builder.setLength(0);

            builder.append("    STATISTICS\n");
            builder.append("\nHPT  :" + unit.getCurrentHitpoints() +" ("+ unit.getBaseHitPoints()+")");
            builder.append("\nSTR  :" + unit.getAppStrength() +" ("+ unit.getBaseStrength()+")");
            builder.append("\nDEX  :" + unit.getAppDexterity() +" ("+ unit.getBaseDexterity()+")");
            builder.append("\nSKI  :" + unit.getAppSkill() +" ("+ unit.getBaseSkill()+")");
            builder.append("\nDEF  : "+ unit.getBaseDefense());
            builder.append("\n  against piercing : "+unit.getAppDefense(Data.DamageType.PIERCING));
            builder.append("\n  against edged    : "+unit.getAppDefense(Data.DamageType.EDGED));
            builder.append("\n  against blunt    : "+unit.getAppDefense(Data.DamageType.BLUNT));
            builder.append("\nAGI  : "+ unit.getBaseAgility());
            builder.append("\n  against piercing : "+unit.getAppAgility(Data.DamageType.PIERCING));
            builder.append("\n  against edged    : "+unit.getAppAgility(Data.DamageType.EDGED));
            builder.append("\n  against blunt    : "+unit.getAppAgility(Data.DamageType.BLUNT));
            builder.append("\nBRA  : "+unit.getAppBravery() +" ("+ unit.getBaseBravery()+")");
            builder.append("\nCHA  : "+unit.getAppCharisma() +" ("+ unit.getBaseCharisma()+")");
            builder.append("\nLDP  : "+unit.getAppLaedership() +" ("+ unit.getBaseLeadership()+")");
            builder.append("\nMOB  : " + unit.getAppMobility() +" ("+ unit.getBaseMobility()+")");

            builder.append("\n\nAttack might : "+unit.getAppAttackDamage());
            builder.append("\nAttack accuracy : "+unit.getAppAttackAccuracy());
            builder.append("\nRange : ("+unit.getAppCurrentWeaponRangeMin()+", "+unit.getAppCurrentWeaponRangeMax()+")");
            builder.append("\nDODGE ABILITY : ");
            builder.append("\n  against piercing : "+unit.getAppAvoidance(Data.DamageType.PIERCING));
            builder.append("\n  against edged    : "+unit.getAppAvoidance(Data.DamageType.EDGED));
            builder.append("\n  against blunt    : "+unit.getAppAvoidance(Data.DamageType.BLUNT));


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
