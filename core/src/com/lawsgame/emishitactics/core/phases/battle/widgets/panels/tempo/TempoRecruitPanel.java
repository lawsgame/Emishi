package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Banner;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Equipment;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.RecruitPanel;

public class TempoRecruitPanel extends RecruitPanel {
    private static int WIDTH = 650;
    private static int HEIGHT = 450;
    private static float FADE_DURATION = 0.2f;

    private Label generalLabel;
    private Label statisticLabel;
    private Label equipmentLabel;


    public TempoRecruitPanel(Viewport stageUIViewport, float fadingDuration, int width, int height,
                              Label generalLabel,
                              Label statisticLabel,
                              Label equipmentLabel) {

        super(stageUIViewport, fadingDuration, width, height);
        this.generalLabel = generalLabel;
        this.statisticLabel = statisticLabel;
        this.equipmentLabel = equipmentLabel;
    }

    public static TempoLongUnitPanel create(Viewport stageUIViewport, Skin skin){
        Label gLabel = new Label("GENERAL", skin, "default");
        Label sLabel = new Label("STATS", skin, "default");
        Label eLabel = new Label("ITEMS", skin, "default");

        TempoLongUnitPanel panel = new TempoLongUnitPanel(stageUIViewport, FADE_DURATION, WIDTH, HEIGHT, gLabel, sLabel, eLabel);
        panel.setBackground(skin.getDrawable(Assets.UI_BLACK_BACKGROUND));
        panel.add(gLabel).center().expand().top().padTop(10);
        panel.add(sLabel).center().expand().top().padTop(10);
        panel.add(eLabel).center().expand().top().padTop(10);

        return panel;
    }

    @Override
    protected void setContent(Unit unit) {


        StringBuilder builder = new StringBuilder("   MAIN\n");
        builder.append("\nName : " + unit.getName());
        builder.append("\nLevel : " + unit.getLevel());
        builder.append("\nUnitTemplate : " + unit.getTemplate().name());
        if (unit.isWarlord()) {
            builder.append("\nPosition : warlord");
        } else if (unit.isWarChief()) {
            builder.append("\nPosition : war chief");
        } else if(unit.isRegular()){
            builder.append("\nPosition : soldier");
        } else if(unit.isSkirmisher()){
            builder.append("\nPosition: skirmisher");
        }
        builder.append("\nHPT: " + unit.getCurrentHitPoints() + "/"+unit.getAppStat(Data.UnitStat.HIT_POINTS));
        builder.append("\nMoral: " + unit.getCurrentMoral() + "/" + unit.getAppMoral());
        builder.append("\nAP : " + unit.getActionPoints());
        builder.append("\nEXP : "+unit.getExperience());
        if (unit.belongToAnArmy()) {
            MilitaryForce army = unit.getArmy();
            builder.append("\n\n    ARMY\n");
            builder.append("\nWarlord : " + army.getWarlord().getName());
            builder.append(" : "+army.getNbOfSquads()+"/"+army.getWarlord().getMaxWarChiefs()+" squads");
            builder.append("\nskirmisher : "+army.getAllSkirmisher(true).size+" ("+army.getAllSkirmisher(false).size+")");
            if(unit.isRegular()) {
                builder.append("\nWar chief : " + army.getWarchief(unit).getName());
                builder.append(" : " + army.getSquad(unit, true).size + "/" + unit.getWarchief().getMaxSoldiersAs(unit.getWarchief().isWarlord())+" soldiers");
                Banner banner = army.getSquadBanner(unit, false);
                builder.append("\nBanner");
                builder.append("\n  | mode        : " + banner.getMode().name().toLowerCase());
                builder.append("\n  | points        : " + banner.getRemainingPoints() + "/" + banner.getMaxPoints());
                builder.append("\n  | strength      : " + banner.getValue(Data.BannerBonus.ATTACK_MIGHT));
                builder.append("\n  | range         : " + banner.getValue(Data.BannerBonus.RANGE));
                builder.append("\n  | loot          : " + banner.getValue(Data.BannerBonus.LOOT_RATE) + "%");
                builder.append("\n  | AP reduc cost : " + banner.getValue(Data.BannerBonus.AP_COST));
                builder.append("\n  | moral reduc   : " + banner.getValue(Data.BannerBonus.MORAL_SHIELD) + "%");
            }else if(unit.isSkirmisher()){
                builder.append("\n => belongs to the skirmisher squad : "+army.getAllSkirmisher(true).size);
            }
        }
        generalLabel.setText(builder.toString());
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
        builder.append("\nName : "+unit.getCurrentWeapon().getTemplate().name().toLowerCase());
        builder.append("\nBase damage : "+unit.getCurrentWeapon().getTemplate().getDamageMin()+" - "+unit.getCurrentWeapon().getTemplate().getDamageMax());
        builder.append("\nBase accuracy : "+unit.getCurrentWeapon().getTemplate().getAccuracy());
        builder.append("\nRange : ("+unit.getCurrentWeapon().getTemplate().getRangeMin()+", "+unit.getCurrentWeapon().getTemplate().getRangeMax()+")");
        builder.append("\nWeapon type : "+ unit.getCurrentWeapon().getTemplate().getWeaponType().name().toLowerCase());
        builder.append("\nDamage type : "+unit.getCurrentWeapon().getTemplate().getDamageType().name().toLowerCase());
        builder.append("\n\n    ABILITIES\n");
        builder.append("\nAbilities : \n    ");
        Array<Data.Ability> activeAbilities = unit.getAbilities();
        for(int i = 0; i < activeAbilities.size ; i++) {
            builder.append(activeAbilities.get(i).name().toLowerCase()+", ");
        }

        statisticLabel.setText(builder.toString());
        builder.setLength(0);


        builder.append("    STATISTICS\n");
        builder.append("\nMOB  : " + unit.getAppStat(Data.UnitStat.MOBILITY) +" ("+ unit.getBaseStat(Data.UnitStat.MOBILITY)+")");
        builder.append("\nLDP  : "+unit.getAppStat(Data.UnitStat.LEADERSHIP) +" ("+ unit.getBaseStat(Data.UnitStat.LEADERSHIP)+")");
        builder.append("\nCHA  : "+unit.getAppStat(Data.UnitStat.CHARISMA) +" ("+ unit.getBaseStat(Data.UnitStat.CHARISMA)+")");
        builder.append("\nHPT  :" + unit.getCurrentHitPoints() +" ("+ unit.getBaseStat(Data.UnitStat.HIT_POINTS)+")");
        builder.append("\nBRA  : "+unit.getAppStat(Data.UnitStat.BRAVERY) +" ("+ unit.getBaseStat(Data.UnitStat.BRAVERY)+")");
        builder.append("\nSTR  :" + unit.getAppStat(Data.UnitStat.STRENGTH) +" ("+ unit.getBaseStat(Data.UnitStat.STRENGTH)+")");
        builder.append("\nDEF  : ");
        builder.append("\n  against piercing : "+unit.getAppStat(Data.UnitStat.ARMOR_PIERCING));
        builder.append("\n  against edged    : "+unit.getAppStat(Data.UnitStat.ARMOR_EDGED));
        builder.append("\n  against blunt    : "+unit.getAppStat(Data.UnitStat.ARMOR_BLUNT));
        builder.append("\nAGI  : "+ unit.getAppStat(Data.UnitStat.AGILITY) +" ("+ unit.getBaseStat(Data.UnitStat.AGILITY)+")");
        builder.append("\nDEX  :" + unit.getAppStat(Data.UnitStat.DEXTERITY) +" ("+ unit.getBaseStat(Data.UnitStat.DEXTERITY)+")");
        builder.append("\nSKI  :" + unit.getAppStat(Data.UnitStat.SKILL) +" ("+ unit.getBaseStat(Data.UnitStat.SKILL)+")");
        builder.append("\nLUC  : "+unit.getAppStat(Data.UnitStat.LUCK) +" ("+ unit.getBaseStat(Data.UnitStat.LUCK)+")");
        builder.append("\n\nattack might : "+unit.getAppAttackMight()[0]+" - "+unit.getAppAttackMight()[1]);
        builder.append("\nattack accuracy : "+unit.getAppAttackAccuracy());
        builder.append("\nAvoidance : "+unit.getAppAvoidance());
        builder.append("\nRange : ("+unit.getAppWeaponRangeMin()+", "+unit.getAppWeaponRangeMax()+")");

        equipmentLabel.setText(builder.toString());
        builder.setLength(0);

    }
}
