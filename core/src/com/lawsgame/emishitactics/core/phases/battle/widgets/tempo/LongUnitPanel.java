package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Banner;
import com.lawsgame.emishitactics.core.models.Equipment;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
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
    private I18NBundle bundle;

    public LongUnitPanel(Viewport stageViewport, I18NBundle bundle) {
        super(stageViewport);
        setWidth(PANEL_WIDTH);
        setHeight(PANEL_HEIGHT);
        center();
        hide();

        this.mainDescription = "";
        this.statDescription = "";
        this.equipDescription = "";
        this.bundle = bundle;
    }

    @Override
    public void set(Unit unit) {

        builder = new StringBuilder("   MAIN\n");
        builder.append("\nName : " + unit.getName());
        builder.append("\nLevel : " + unit.getLevel());
        builder.append("\nUnitTemplate : " + unit.getTemplate().name());
        if (unit.isWarlord()) {
            builder.append("\nPosition : warlord");
        } else {
            if (unit.isWarChief()) {
                builder.append("\nPosition : war chief");
            } else {
                builder.append("\nPosition : soldier");
            }
        }
        builder.append("\nHit points: " + unit.getCurrentHitPoints() + "/"+unit.getAppHitpoints());
        builder.append("\nMoral: " + unit.getCurrentMoral() + "/" + unit.getAppMoral());
        builder.append("\nAction Points : " + unit.getActionPoints());
        builder.append("\nExperience : "+unit.getExperience());

        if (unit.isMobilized()) {
            MilitaryForce army = unit.getArmy();
            builder.append("\n\n    ARMY\n");
            builder.append("\nWarlord : " + army.getWarlord().getName());
            builder.append(", WCs : "+army.getNbOfSquads()+"/"+army.getWarlord().getMaxWarChiefs());
            builder.append("\nWar chief : " + army.getWarchief(unit).getName());
            builder.append(", Squad: "+army.getSquad(unit, true).size+"/"+unit.getWarchief().getMaxSoldiersAs(unit.getWarchief().isWarlord()));
            Banner banner = army.getSquadBanner(unit, false);
            builder.append("\nBanner");
            builder.append("\n  | mode        : "+banner.getMode().name().toLowerCase());
            builder.append("\n  | points        : "+banner.getRemainingPoints()+"/"+banner.getMaxPoints());
            builder.append("\n  | strength      : "+banner.getValue(Data.BannerBonus.STRENGTH, false));
            builder.append("\n  | range         : "+banner.getValue(Data.BannerBonus.RANGE, false));
            builder.append("\n  | loot          : "+banner.getValue(Data.BannerBonus.LOOT_RATE, false)+"%");
            builder.append("\n  | AP reduc cost : "+banner.getValue(Data.BannerBonus.AP_COST, false));
            builder.append("\n  | moral reduc   : "+banner.getValue(Data.BannerBonus.MORAL_SHIELD, false)+"%");


        }
        mainDescription = builder.toString();
        builder.setLength(0);

        builder.append("    ITEMS\n");
        builder.append("\nWeapon type : "+ unit.getWeaponType()+"\nWeapon : ");
        Array<Weapon> weapons = unit.getWeapons();
        for(int i = 0; i < weapons.size ; i++){
            builder.append("\n  "+weapons.get(i).toString());
        }
        builder.append("\nEquipments : ");
        Array<Equipment> equipments = unit.getEquipments();
        for(int i = 0; i < equipments.size ; i++){
            builder.append("\n  "+equipments.get(i).toString());
        }
        builder.append("\n\n    CURRENT WEAPONS\n");
        builder.append("\nName : "+unit.getCurrentWeapon().toString());
        builder.append("\nBase damage : "+unit.getCurrentWeapon().getTemplate().getDamageMin()+" - "+unit.getCurrentWeapon().getTemplate().getDamageMax());
        builder.append("\nBase accuracy : "+unit.getCurrentWeapon().getTemplate().getAccuracy());
        builder.append("\nRange : ("+unit.getCurrentWeapon().getTemplate().getRangeMin()+", "+unit.getCurrentWeapon().getTemplate().getRangeMax()+")");
        builder.append("\nWeapon type : "+ unit.getCurrentWeapon().getTemplate().getWeaponType().name().toLowerCase());
        builder.append("\nDamage type : "+unit.getCurrentWeapon().getTemplate().getDamageType().name().toLowerCase());
        builder.append("\n\n    ABILITIES\n");
        builder.append("\nAbilities : ");
        Array<Data.Ability> activeAbilities = unit.getAbilities();
        for(int i = 0; i < activeAbilities.size ; i++) {
            builder.append("\n  " + unit.getAbilities().get(i).name().toLowerCase());
        }

        equipDescription = builder.toString();
        builder.setLength(0);

        builder.append("    STATISTICS\n");
        builder.append("\nHPT  :" + unit.getCurrentHitPoints() +" ("+ unit.getHitpoints()+")");
        builder.append("\nSTR  :" + unit.getAppStrength() +" ("+ unit.getStrength()+")");
        builder.append("\nDEX  :" + unit.getAppDexterity() +" ("+ unit.getDexterity()+")");
        builder.append("\nSKI  :" + unit.getAppSkill() +" ("+ unit.getSkill()+")");
        builder.append("\nDEF  : ");
        builder.append("\n  against piercing : "+unit.getAppArmor(Data.DamageType.PIERCING));
        builder.append("\n  against edged    : "+unit.getAppArmor(Data.DamageType.EDGED));
        builder.append("\n  against blunt    : "+unit.getAppArmor(Data.DamageType.BLUNT));
        builder.append("\nAGI  : "+ unit.getAppAgility() +" ("+ unit.getAgility()+")");
        builder.append("\nBRA  : "+unit.getAppBravery() +" ("+ unit.getBravery()+")");
        builder.append("\nCHA  : "+unit.getAppCharisma() +" ("+ unit.getCharisma()+")");
        builder.append("\nLDP  : "+unit.getAppLeadership() +" ("+ unit.getLeadership()+")");
        builder.append("\nMOB  : " + unit.getAppMobility() +" ("+ unit.getMobility()+")");

        builder.append("\n\nattack might : "+unit.getAppAttackMight()[0]+" - "+unit.getAppAttackMight()[1]);
        builder.append("\nattack accuracy : "+unit.getAppAttackAccuracy());
        builder.append("\nAvoidance : "+unit.getAppAvoidance());
        builder.append("\nRange : ("+unit.getAppWeaponRangeMin()+", "+unit.getAppWeaponRangeMax()+")");

        statDescription = builder.toString();
        builder.setLength(0);

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
    public boolean isHiding() {
        return isVisible();
    }

    @Override
    public float getHidingTime() {
        return 0;
    }

    @Override
    public float getShowingTime() {
        return 0;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(TempoSpritePool.get().getBlackBGSprite(),getX(), getY(), getWidth(), getHeight() );
        BattlePhase.testFont.draw(batch, mainDescription, getX() + X_TEXT_OFFSET_1_COL, getY() + getHeight() - Y_TEXT_OFFSET);
        BattlePhase.testFont.draw(batch, statDescription, getX() + X_TEXT_OFFSET_2_COL, getY() + getHeight() - Y_TEXT_OFFSET);
        BattlePhase.testFont.draw(batch, equipDescription, getX() + X_TEXT_OFFSET_3_COL, getY() + getHeight() - Y_TEXT_OFFSET);
    }


}
