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
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.LongUnitPanel;

public class TempoLongUnitPanel extends LongUnitPanel {
    private static int WIDTH = 650;
    private static int HEIGHT = 450;
    private static float FADE_DURATION = 0.2f;

    private Label generalLabel;
    private Label statisticLabel;
    private Label equipmentLabel;

    public TempoLongUnitPanel(Viewport stageUIViewport, float fadingDuration, int width, int height,
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
            builder.append("\n  | strength      : "+banner.getValue(Data.BannerBonus.ATTACK_MIGHT));
            builder.append("\n  | range         : "+banner.getValue(Data.BannerBonus.RANGE));
            builder.append("\n  | loot          : "+banner.getValue(Data.BannerBonus.LOOT_RATE)+"%");
            builder.append("\n  | AP reduc cost : "+banner.getValue(Data.BannerBonus.AP_COST));
            builder.append("\n  | moral reduc   : "+banner.getValue(Data.BannerBonus.MORAL_SHIELD)+"%");
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
        builder.append("\nMOB  : " + unit.getAppMobility() +" ("+ unit.getMobility()+")");
        builder.append("\nLDP  : "+unit.getAppLeadership() +" ("+ unit.getLeadership()+")");
        builder.append("\nCHA  : "+unit.getAppCharisma() +" ("+ unit.getCharisma()+")");
        builder.append("\nHPT  :" + unit.getCurrentHitPoints() +" ("+ unit.getHitpoints()+")");
        builder.append("\nBRA  : "+unit.getAppBravery() +" ("+ unit.getBravery()+")");
        builder.append("\nSTR  :" + unit.getAppStrength() +" ("+ unit.getStrength()+")");
        builder.append("\nDEF  : ");
        builder.append("\n  against piercing : "+unit.getAppArmor(Data.DamageType.PIERCING));
        builder.append("\n  against edged    : "+unit.getAppArmor(Data.DamageType.EDGED));
        builder.append("\n  against blunt    : "+unit.getAppArmor(Data.DamageType.BLUNT));
        builder.append("\nAGI  : "+ unit.getAppAgility() +" ("+ unit.getAgility()+")");
        builder.append("\nDEX  :" + unit.getAppDexterity() +" ("+ unit.getDexterity()+")");
        builder.append("\nSKI  :" + unit.getAppSkill() +" ("+ unit.getSkill()+")");
        builder.append("\nLUC  : "+unit.getAppLuck() +" ("+ unit.getLuck()+")");
        builder.append("\n\nattack might : "+unit.getAppAttackMight()[0]+" - "+unit.getAppAttackMight()[1]);
        builder.append("\nattack accuracy : "+unit.getAppAttackAccuracy());
        builder.append("\nAvoidance : "+unit.getAppAvoidance());
        builder.append("\nRange : ("+unit.getAppWeaponRangeMin()+", "+unit.getAppWeaponRangeMax()+")");

        equipmentLabel.setText(builder.toString());
        builder.setLength(0);
    }
}
