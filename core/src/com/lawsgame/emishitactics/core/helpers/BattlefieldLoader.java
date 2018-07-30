package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.*;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Army;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Equipment;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.BattlePhase;

import java.io.IOException;
import java.util.HashMap;

public class BattlefieldLoader {
    private static XmlReader reader = new XmlReader();
    private static Array<String> NAME_DICTIONARY = new Array<String>();
    private static boolean dictionariesLoaded = false;


    // ------------- LOAD BATTLEFIELD ------------------

    public static Battlefield load(BattlePhase phase, int bfId){

        // load required files

        TextureAtlas layoutAtlas = phase.getAsm().get(Assets.ATLAS_MAPS);
        Texture layoutTexture =  layoutAtlas.findRegion(Assets.getRegionMap(bfId)).getTexture();
        if (!layoutTexture.getTextureData().isPrepared()) {
            layoutTexture.getTextureData().prepare();
        }
        Pixmap layoutPixmap = layoutTexture.getTextureData().consumePixmap();


        // SET BATTLEFIELD DIMENSION

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
                        // addExpGained the tile type
                        fieldType = colorToFieldTypeMap.get(colorKey);
                        if(fieldType == null){
                            fieldType = TileType.PLAIN;
                        }
                        bf.setTile(rowTile, colTile, fieldType);

                    }else{

                        //TODO: available for unit loading

                    }
                }else{
                    if(c % 2 == 0){
                        // whether or not the tile is to be added to the deployment tile
                        rgb = Utils.getRGBA(colorKey);
                       if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 0 && bf.isTileReachable(rowTile, colTile, false)){
                            bf.addDeploymentTile(rowTile, colTile);
                        }
                    }else{

                        //TODO: available for loot loading (shrine, village, etc...)
                    }
                }
            }
        }


        // DEPLOY UNITS


        try {
            loadNameDictionary();

            XmlReader.Element battlesElt = reader.parse(Gdx.files.internal(Assets.XML_UNITS_DEPLOYMENT));
            XmlReader.Element battleElt;
            XmlReader.Element armyElt;
            XmlReader.Element squadElt;
            XmlReader.Element unitElt;
            IUnit unit;
            IArmy army;
            for (int i = 0; i < battlesElt.getChildCount(); i++) {

                battleElt = battlesElt.getChild(i);
                if (battleElt.getInt("battlefieldId") == bfId) {
                    for (int j = 0; j < battleElt.getChildCount(); j++) {

                        armyElt = battleElt.getChild(i);
                        Data.Allegeance allegeance = Data.Allegeance.ENEMY;
                        for(Data.Allegeance a: Data.Allegeance.values()){
                            if(a.name().equals(armyElt.get("allegeance"))){
                                allegeance = a;
                            }
                        }
                        army = new Army(allegeance, false);

                        // IF: an amry with the relevant battlefield ID

                        for (int k = 0; k < armyElt.getChildCount(); k++) {
                            squadElt = armyElt.getChild(k);

                            for (int n = 0; n < squadElt.getChildCount(); n++) {
                                unitElt = squadElt.getChild(n);
                                unit = instanciateUnit(unitElt);
                                int rowUnit = unitElt.getInt("row");
                                int colUnit = unitElt.getInt("col");

                                // addExpGained to the army composition
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

                                // addExpGained to the battlefield
                                if(!bf.isTileDeploymentTile(rowUnit, colUnit)) {
                                    bf.deployUnit(rowUnit, colUnit, unit);
                                }
                            }
                        }
                    }
                }
            }

            unloadNameDictionary();
        } catch (IOException e) {
            e.printStackTrace();
        }

        layoutAtlas.dispose();
        layoutPixmap.dispose();
        layoutTexture.dispose();
        return bf;
    }


    // ------------- CREATE A UNIT ------------------

    public static IUnit instanciateUnit(XmlReader.Element unitElt){
        IUnit unit;

        String name = unitElt.get("name");
        Job job = Job.getStandard();
        int level = unitElt.getInt("level");
        Weapon weapon;
        Equipment equipement;
        WeaponType weaponType = WeaponType.FIST;
        boolean horseman = unitElt.getBoolean("horseman");
        boolean horsemanUponPromotion = unitElt.getBoolean("horsemanUponPromotion");
        boolean standardbearer = unitElt.getBoolean("standardBearer");
        boolean homogeneousLevels = unitElt.getBoolean("homogeneousLevels");
        if(name.equals("noname")){
            name = NAME_DICTIONARY.get(Data.rand(NAME_DICTIONARY.size));
        }
        for(Job j: Job.values()){
            if(j.name().equals(unitElt.get("job"))){
                job = j;
                continue;
            }
        }
        for(WeaponType wt: WeaponType.values()){
            if(wt.name().equals(unitElt.get("weaponType"))){
                weaponType = wt;
                continue;
            }
        }

        unit = new Unit(name, job ,level, weaponType, horseman, horsemanUponPromotion, standardbearer, homogeneousLevels);


        XmlReader.Element attributeElt;
        for(int k = 0; k < unitElt.getChildCount(); k++){
            attributeElt = unitElt.getChild(k);

            if(attributeElt.get("id").equals("weapon")) {
                for (WeaponTemplate value : WeaponTemplate.values()) {
                    if (value.name().equals(attributeElt.get("value"))) {
                        weapon = new Weapon(value);
                        weapon.setStealable(attributeElt.getBoolean("stealable"));
                        unit.addWeapon(weapon);
                        continue;
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
                        continue;
                    }
                }
            }

            if(attributeElt.get("id").equals("banner sign")) {
                for (BannerSign value : BannerSign.values()) {
                    if (value.name().equals(attributeElt.get("value"))) {
                        unit.getBanner().addSign(value);
                        continue;
                    }
                }
            }

            if(attributeElt.get("id").equals("Orientation")) {
                for (Orientation value : Orientation.values()) {
                    if (value.name().equals(attributeElt.get("value"))) {
                        unit.setOrientation(value);
                        continue;
                    }
                }
            }

            if(attributeElt.get("id").equals("behaviour")) {
                for (Behaviour value : Behaviour.values()) {
                    if (value.name().equals(attributeElt.get("value"))) {
                        unit.setBehaviour(value);
                        continue;
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
