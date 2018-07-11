package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Data.*;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.AArmy;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.BattlePhase;

import java.io.IOException;
import java.util.HashMap;

public class BattlefieldLoader {
    private static XmlReader reader = new XmlReader();
    private static Array<String> japaneseNames = new Array<String>();
    private static Array<String> ainuNames = new Array<String>();
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
                        // set the tile type
                        fieldType = colorToFieldTypeMap.get(colorKey);
                        if(fieldType == null){
                            fieldType = TileType.getStandard();
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
            XmlReader.Element battlesElt = reader.parse(Gdx.files.internal(Assets.XML_UNITS_DEPLOYMENT));
            XmlReader.Element battleElt;
            XmlReader.Element armyElt;
            XmlReader.Element squadElt;
            XmlReader.Element unitElt;
            Unit unit;
            AArmy army;
            boolean ally;
            for (int i = 0; i < battlesElt.getChildCount(); i++) {

                battleElt = battlesElt.getChild(i);
                if (battleElt.getInt("battlefieldId") == bfId) {
                    for (int j = 0; j < battleElt.getChildCount(); j++) {

                        armyElt = battleElt.getChild(i);
                        ally = armyElt.getBoolean("ally");
                        army = new Unit.Army((ally) ? Data.ArmyType.ALLY: Data.ArmyType.FOE);

                        // IF: an amry with the relevant battlefield ID

                        for (int k = 0; k < armyElt.getChildCount(); k++) {
                            squadElt = armyElt.getChild(k);

                            for (int n = 0; n < squadElt.getChildCount(); n++) {
                                unitElt = squadElt.getChild(n);
                                unit = instanciateUnit(unitElt);
                                int rowUnit = unitElt.getInt("row");
                                int colUnit = unitElt.getInt("col");

                                // add to the army composition
                                if(n == 0){
                                    if(k == 0){
                                        army.appointWarLord(unit);
                                    }else{
                                        army.appointWarChief(unit);
                                    }
                                }else{
                                    army.appointSoldier(unit, n);
                                }

                                // add to the battlefield
                                if(!bf.isTileDeploymentTile(rowUnit, colUnit)) {
                                    bf.deployUnit(rowUnit, colUnit, unit);
                                }


                            }
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        layoutAtlas.dispose();
        layoutPixmap.dispose();
        layoutTexture.dispose();

        return bf;
    }


    // ------------- CREATE A UNIT ------------------

    public static Unit instanciateUnit(XmlReader.Element unitElt){
        Unit unit;

        boolean standardbearer;
        UnitTemplate templateUnit = UnitTemplate.CONSCRIPT;
        int gainLvl;
        Ethnicity ethnicityUnit = Ethnicity.JAPANESE;
        String nameUnit ="";
        Weapon primaryW = Weapon.NONE;
        Weapon secondaryW = Weapon.NONE;
        boolean homogeousLevels;

        standardbearer = unitElt.getBoolean("standardBearer");
        for(UnitTemplate template: UnitTemplate.values()){
            if(template.name().equals(unitElt.get("template"))){
                templateUnit = template;
            }
        }

        gainLvl = unitElt.getInt("level") - templateUnit.getStartLevel();
        for(Ethnicity eth: Ethnicity.values()){
            if(eth.name().equals(unitElt.get("ethinicity"))){
                ethnicityUnit = eth;
            }
        }
        loadNameDictionaries();
        if(japaneseNames.size > 0 && ainuNames.size >0) {
            nameUnit = (ethnicityUnit == Data.Ethnicity.JAPANESE) ? japaneseNames.random() : ainuNames.random();
        }
        for(Weapon wpn: Weapon.values()){
            if(wpn.name().equals(unitElt.get("primaryWeapon"))){
                primaryW = wpn;
            }
        }
        for(Weapon wpn: Weapon.values()){
            if(wpn.name().equals(unitElt.get("secondaryWeapon"))){
                secondaryW = wpn;
            }
        }
        homogeousLevels = unitElt.getBoolean("homogeneousLevels");



        unit = new Unit(templateUnit, standardbearer, gainLvl, ethnicityUnit, primaryW, secondaryW, homogeousLevels);
        unit.setName(nameUnit);
        XmlReader.Element attributeElt;
        for(int k = 0; k < unitElt.getChildCount(); k++){
            attributeElt = unitElt.getChild(k);

            if(attributeElt.get("id") == "right handed")
                unit.setRightHanded(attributeElt.getBoolean("right handed"));

            if(attributeElt.get("id") == "orientation") {
                for (Orientation orientation : Orientation.values()) {
                    if (orientation.name().equals(attributeElt.get("orientation"))) {
                        unit.setOrientation(orientation);
                        continue;
                    }
                }
            }
            if(attributeElt.get("id") == "behaviour") {
                for (Behaviour behaviour : Behaviour.values()) {
                    if (behaviour.name().equals(unitElt.get("behaviour"))) {
                        unit.setBehaviour(behaviour);
                    }
                }
            }
            if(attributeElt.get("id") == "offensive ability"){
                for(OffensiveAbility ability: OffensiveAbility.values()){
                    if(ability.name().equals(attributeElt.get("value"))){
                        unit.setOffensiveActiveAbility(ability);
                    }
                }
            }else if(attributeElt.get("id") == "passive ability"){
                for(PassiveAbility ability: PassiveAbility.values()){
                    if(ability.name().equals(attributeElt.get("value"))){
                        unit.setPassiveAbility(ability);
                    }
                }
            }else if(attributeElt.get("id") == "support ability"){
                for(SupportAbility ability: SupportAbility.values()){
                    if(ability.name().equals(attributeElt.get("value"))){
                        unit.setSupportActiveAbility(ability);
                    }
                }
            }else if(attributeElt.get("id") == "item"){
                for(Item item: Item.values()){
                    if(item.name().equals(attributeElt.get("value"))){
                        unit.equip(item, unit.getNbItemsEquiped() > 0);
                    }
                }
            }else if(attributeElt.get("id") == "stealable"){
                unit.setItemStealable(attributeElt.getBoolean("value"));
            }else if(attributeElt.get("id") == "banner sign"){
                for(BannerSign sign: BannerSign.values()){
                    if(sign.name().equals(attributeElt.get("value"))){
                        unit.addBannerSign(sign);
                    }
                }
            }
        }

        return unit;
    }


    // ------------- LOAD DICTIONARIES ------------------

    public static void loadNameDictionaries(){
        XmlReader reader = new XmlReader();

        if(!dictionariesLoaded) {
            dictionariesLoaded = true;
            try {
                XmlReader.Element namesElt = reader.parse(Gdx.files.internal(Assets.XML_NAME_DB));
                XmlReader.Element japaneseNamesElt = namesElt.getChild(0);
                XmlReader.Element ainuNamesElt = namesElt.getChild(1);
                XmlReader.Element nameElt;
                for (int i = 0; i < japaneseNamesElt.getChildCount(); i++) {
                    nameElt = japaneseNamesElt.getChild(i);
                    japaneseNames.add(nameElt.get("value"));
                }
                for (int i = 0; i < ainuNamesElt.getChildCount(); i++) {
                    nameElt = ainuNamesElt.getChild(i);
                    ainuNames.add(nameElt.get("value"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
