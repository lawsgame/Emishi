package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Army;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.Affiliation;
import com.lawsgame.emishitactics.core.models.Data.Behaviour;
import com.lawsgame.emishitactics.core.models.Data.Environment;
import com.lawsgame.emishitactics.core.models.Data.EquipmentTemplate;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.Data.UnitTemplate;
import com.lawsgame.emishitactics.core.models.Data.WeaponTemplate;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.Data.Weather;
import com.lawsgame.emishitactics.core.models.Equipment;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;

import java.io.IOException;
import java.util.HashMap;

public class BattlefieldLoader {
    private static XmlReader reader = new XmlReader();
    private static Array<String> NAME_DICTIONARY = new Array<String>();
    private static boolean dictionariesLoaded = false;


    // ------------- LOAD BATTLEFIELD ------------------

    public static Battlefield load(BattlePhase phase, int bfId){

        // LOAD TEXTURE MAPPING THE BATTLEFIELD

        TextureAtlas layoutAtlas = phase.getAsm().get(Assets.ATLAS_MAPS);
        Texture layoutTexture =  layoutAtlas.findRegion(Assets.getRegionMap(bfId)).getTexture();
        if (!layoutTexture.getTextureData().isPrepared()) {
            layoutTexture.getTextureData().prepare();
        }
        Pixmap layoutPixmap = layoutTexture.getTextureData().consumePixmap();


        // FIND BATTLEFIELD DIMENSION

        int rows = 0;
        int cols = 0;
        for(int r = 0; r < layoutPixmap.getHeight(); r++){
            if(layoutPixmap.getPixel(0, r) != 0 ){
                rows++;
            }
        }
        for(int c = 0; c < layoutPixmap.getWidth(); c++){
            if(layoutPixmap.getPixel(c, 0) != 0 ){
                cols++;
            }
        }


        //CREATE BATTLEFIELD INSTANCE

        Battlefield bf = new Battlefield(rows/2,cols/2);



        // LOAD TILES

        HashMap<Integer, TileType> colorToFieldTypeMap = new HashMap<Integer, TileType>();
        for(TileType type: TileType.values()){
            colorToFieldTypeMap.put(Utils.getColor32Bits(type.getR(), type.getG(), type.getB()), type);
        }
        TileType fieldType;
        int colorKey;
        int rowTile;
        int colTile;
        int[] rgb;
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){

                rowTile = bf.getNbRows() -1 - r/2;
                colTile = c/2;
                colorKey = layoutPixmap.getPixel(c, r);
                if(r % 2 == 0){
                    if(c % 2 == 0){
                        // add the buildingType type
                        fieldType = colorToFieldTypeMap.get(colorKey);
                        if(fieldType == null){
                            fieldType = TileType.PLAIN;
                        }
                        bf.setTile(rowTile, colTile, fieldType, true);

                    }else{

                        //TODO: available for unit loading

                    }
                }else{
                    if(c % 2 == 0){
                        // whether or not the buildingType is to be added to the deployment buildingType
                        rgb = Utils.getRGBA(colorKey);
                       if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 0 && bf.isTileReachable(rowTile, colTile, false)){
                           // main deployment area
                            bf.addDeploymentTile(rowTile, colTile, 0, false);
                       }else if(rgb[0] == 255 && rgb[1] == 255 && bf.isTileReachable(rowTile, colTile, false)){
                           // vanguard deployment area
                           bf.addDeploymentTile(rowTile, colTile, 256 - rgb[2], false);
                       }
                    }else{

                        //TODO: available for loot loading (shrine, village, etc...)
                    }
                }
            }
        }



        try {
            loadNameDictionary();

            XmlReader.Element battlesElt = reader.parse(Gdx.files.internal(Assets.XML_BATTLE_PARAMS));
            XmlReader.Element battleElt = null;
            for (int i = 0; i < battlesElt.getChildCount(); i++) {
                if (battlesElt.getChild(i).getInt("battlefieldId") == bfId) {
                    battleElt = battlesElt.getChild(i);
                    break;
                }
            }

            if(battleElt != null){


                // PARAMS

                for(Weather weather : Weather.values()){
                    if(battleElt.get("weather").equals(weather.name())){
                        bf.setWeather(weather);
                        break;
                    }
                }
                for(Environment environment : Environment.values()){
                    if(battleElt.get("env").equals(environment.name())){
                        bf.setEnvironment(environment);
                        break;
                    }
                }


                // DEPLOY ARMIES

                XmlReader.Element armyElt;
                XmlReader.Element squadElt;
                XmlReader.Element unitElt;
                Unit unit;
                MilitaryForce army;
                for (int j = 0; j < battleElt.getChildCount(); j++) {

                    armyElt = battleElt.getChild(j);
                    Affiliation affiliation = Affiliation.ENEMY_0;
                    for(Affiliation a: Affiliation.values()){
                        if(a.name().equals(armyElt.get("affiliation"))){
                            affiliation = a;
                            break;
                        }
                    }
                    army = new Army(affiliation);
                    bf.addArmyId(army);

                    // IF: an amry with the relevant battlefield ID

                    for (int k = 0; k < armyElt.getChildCount(); k++) {
                        squadElt = armyElt.getChild(k);

                        for (int n = 0; n < squadElt.getChildCount(); n++) {
                            unitElt = squadElt.getChild(n);

                            // instanciation unit
                            unit = instanciateUnit(unitElt);

                            // add to the army composition
                            army.add(unit);
                            if(n == 0){
                                if(k == 0){
                                    army.appointWarLord(unit);
                                }else{
                                    army.appointWarChief(unit);
                                }
                            }else{
                                army.appointSoldier(unit, k);
                            }

                            //add  to the battlefield
                            int rowUnit = unitElt.getInt("row");
                            int colUnit = unitElt.getInt("col");
                            if(!bf.isTileDeploymentTile(rowUnit, colUnit)) {
                                bf.deploy(rowUnit, colUnit, unit, true);
                            }
                        }
                    }
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        unloadNameDictionary();
        layoutAtlas.dispose();
        layoutPixmap.dispose();
        layoutTexture.dispose();
        return bf;
    }

    private static Unit instanciateUnit(XmlReader.Element unitElt){
        Unit unit;

        String name = unitElt.get("name");
        String title = unitElt.get("title");
        UnitTemplate unitTemplate = UnitTemplate.getStandard();
        int level = unitElt.getInt("level");
        Weapon weapon;
        Equipment equipement;
        WeaponType weaponType = WeaponType.FIST;
        boolean character = !name.equals("noname");
        boolean shieldbearer = unitElt.getBoolean("shieldbearer");
        boolean horseman = unitElt.getBoolean("horseman");
        boolean horsemanUponPromotion = unitElt.getBoolean("horsemanUponPromotion");
        boolean homogeneousLevels = unitElt.getBoolean("homogeneousLevels");
        if(!character){
            name = NAME_DICTIONARY.get(Data.rand(NAME_DICTIONARY.size));
        }
        for(UnitTemplate j: UnitTemplate.values()){
            if(j.name().equals(unitElt.get("template"))){
                unitTemplate = j;
                break;
            }
        }
        for(WeaponType wt: WeaponType.values()){
            if(wt.name().equals(unitElt.get("weaponType"))){
                weaponType = wt;
                break;
            }
        }


        unit = (character)?
                Unit.createCharacterUnit(name, title, unitTemplate,level, weaponType, shieldbearer, horseman, horsemanUponPromotion):
                Unit.createGenericUnit(name, unitTemplate,level, weaponType, shieldbearer, horseman, horsemanUponPromotion, homogeneousLevels);

        XmlReader.Element attributeElt;
        for(int k = 0; k < unitElt.getChildCount(); k++){
            attributeElt = unitElt.getChild(k);

            if(attributeElt.get("id").equals("weapon")) {
                for (WeaponTemplate value : WeaponTemplate.values()) {
                    if (value.name().equals(attributeElt.get("value"))) {
                        weapon = new Weapon(value);
                        weapon.setStealable(attributeElt.getBoolean("stealable"));
                        unit.addWeapon(weapon);
                        break;
                    }
                }
            }

            if(attributeElt.get("id").equals("right handed"))
                unit.setRightHanded(attributeElt.getBoolean("value"));

            if(attributeElt.get("id").equals("equipement")) {
                for (EquipmentTemplate value : EquipmentTemplate.values()) {
                    if (value.name().equals(attributeElt.get("value"))) {
                        equipement = new Equipment(value, attributeElt.getBoolean("stealable"));
                        unit.addEquipment(equipement);
                        break;
                    }
                }
            }

            if(attributeElt.get("id").equals("banner")) {

                int bonusBanner = attributeElt.getInt("strength");
                for(int i = 0; i < bonusBanner; i++)
                    unit.getBanner().increment(Data.BannerBonus.STRENGTH);
                bonusBanner = attributeElt.getInt("range");
                for(int i = 0; i < bonusBanner; i++)
                    unit.getBanner().increment(Data.BannerBonus.RANGE);
                bonusBanner = attributeElt.getInt("lootrate");
                for(int i = 0; i < bonusBanner; i++)
                    unit.getBanner().increment(Data.BannerBonus.LOOT_RATE);
                bonusBanner = attributeElt.getInt("apcost");
                for(int i = 0; i < bonusBanner; i++)
                    unit.getBanner().increment(Data.BannerBonus.AP_COST);
                bonusBanner = attributeElt.getInt("moralshield");
                for(int i = 0; i < bonusBanner; i++)
                    unit.getBanner().increment(Data.BannerBonus.MORAL_SHIELD);
            }

            if(attributeElt.get("id").equals("Orientation")) {
                for (Orientation value : Orientation.values()) {
                    if (value.name().equals(attributeElt.get("value"))) {
                        unit.setOrientation(value);
                        break;
                    }
                }
            }

            if(attributeElt.get("id").equals("behaviour")) {
                for (Behaviour value : Behaviour.values()) {
                    if (value.name().equals(attributeElt.get("value"))) {
                        unit.setBehaviour(value);
                        break;
                    }
                }
            }
        }

        return unit;
    }


    // ------------- LOAD DICTIONARIES ------------------

    public static void loadNameDictionary(){
        XmlReader reader = new XmlReader();

        if(!dictionariesLoaded) {
            dictionariesLoaded = true;
            try {
                XmlReader.Element namesElt = reader.parse(Gdx.files.internal(Assets.XML_NAME_DB));
                XmlReader.Element nameElt;
                for (int i = 0; i < namesElt.getChildCount(); i++) {
                    nameElt = namesElt.getChild(i);
                    NAME_DICTIONARY.add(nameElt.get("value"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void unloadNameDictionary(){
        NAME_DICTIONARY.clear();
    }

}
