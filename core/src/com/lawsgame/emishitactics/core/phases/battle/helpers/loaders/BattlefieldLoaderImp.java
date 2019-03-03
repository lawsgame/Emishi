package com.lawsgame.emishitactics.core.phases.battle.helpers.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.StringKey;
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
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.battlesolvers.Endless;
import com.lawsgame.emishitactics.core.models.interfaces.BattleSolver;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.EarthquakeEvent;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.ReinforcementEvent;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.ReinforcementEvent.StiffenerData;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.TrapEvent;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattlefieldLoader;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.ShortUnitPanel;
import com.lawsgame.emishitactics.engine.utils.ClassInstanciator;

import org.apache.commons.lang3.EnumUtils;

import java.io.IOException;
import java.util.HashMap;

public class BattlefieldLoaderImp implements BattlefieldLoader {
    private XmlReader reader = new XmlReader();
    private Array<String> nameDictionary = new Array<String>();
    private boolean dictionariesLoaded = false;
    private HashMap<Integer, Array<StiffenerData>> stiffeners = new HashMap<Integer, Array<StiffenerData>>();



    // ------------- LOAD BATTLEFIELD ------------------

    public Battlefield load(AssetManager asm, int bfId){

        // LOAD TEXTURE MAPPING THE BATTLEFIELD

        TextureAtlas layoutAtlas = asm.get(Assets.ATLAS_MAPS);
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

        Battlefield bf = new Battlefield(bfId, rows/2,cols/2);

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
                        // UNUSED
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
                        // UNUSED
                    }
                }
            }
        }

        try {
            loadNameDictionary();

            // GET DATA FROM battlefields-config.xml

            XmlReader.Element battlesElt = reader.parse(Gdx.files.internal(Assets.XML_BATTLE_PARAMS));
            XmlReader.Element battleElt = null;
            for (int i = 0; i < battlesElt.getChildCount(); i++) {
                if (battlesElt.getChild(i).getInt("battlefieldId") == bfId) {
                    battleElt = battlesElt.getChild(i);
                    break;
                }
            }

            if(battleElt != null){

                // SET GLOBAL BATTLEFIELD PARAMETERS

                Weather weather = EnumUtils.getEnum(Weather.class, battleElt.get("weather"));
                bf.setWeather((weather != null) ? weather : Weather.getDefaultValue());
                Environment environment = EnumUtils.getEnum(Environment.class, battleElt.get("env"));
                bf.setEnvironment((environment != null) ? environment  : Environment.getDefaultValue());

                // SET BATTEL SOLVER
                BattleSolver solver = ClassInstanciator.parseXmlIntoInstanceOf(BattleSolver.class, battleElt.getChildByName("Solver"));
                solver = (solver != null) ? solver : new Endless();
                bf.setSolver(solver);

                // DEPLOY ARMIES

                Array<XmlReader.Element> armyElts = battleElt.getChildrenByName("Army");
                XmlReader.Element armyElt;
                MilitaryForce army;
                for (int j = 0; j < armyElts.size; j++) {
                    armyElt = armyElts.get(j);
                    Affiliation affiliation = Affiliation.ENEMY_0;
                    for(Affiliation a: Affiliation.values()){
                        if(a.name().equals(armyElt.get("affiliation"))){
                            affiliation = a;
                            break;
                        }
                    }
                    army = new Army(affiliation, armyElt.get("keyname", StringKey.UNNAMED_ARMY_NAME));
                    Unit unit;
                    XmlReader.Element unitElt;
                    Array<XmlReader.Element> squadElts = armyElt.getChildrenByName("Squad");
                    for (int k = 0; k < squadElts.size; k++) {
                        for (int n = 0; n < squadElts.get(k).getChildCount(); n++) {
                            unitElt = squadElts.get(k).getChild(n);
                            // instanciation unit
                            unit = instanciate(unitElt);
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
                            addUniToBattlefield(unit, unitElt, bf);
                        }
                    }
                    Array<XmlReader.Element> skirmisherElts = armyElt.getChildByName("Skirmishers").getChildrenByName("Unit");
                    for (int k = 0; k < skirmisherElts.size; k++) {
                        unitElt = skirmisherElts.get(k);
                        unit = instanciate(unitElt);
                        army.add(unit);
                        army.appointSkirmisher(unit);
                        addUniToBattlefield(unit, unitElt, bf);
                    }

                }

                // ADD LOOTS

                Array< XmlReader.Element> lootElts = battleElt.getChildrenByName("Loot");
                XmlReader.Element lootElt;
                int lootRow;
                int lootCol;
                Item loot = null;
                for(int i = 0; i < lootElts.size; i++) {
                    lootElt  = lootElts.get(i);
                    lootRow = lootElt.getInt("row", -1);
                    lootCol = lootElt.getInt("col", -1);
                    for(EquipmentTemplate eqt : EquipmentTemplate.values()){
                        if(eqt.name().equals(lootElt.get("id"))){
                            loot = new Equipment(eqt, false);
                        }
                    }
                    for(WeaponTemplate wt : WeaponTemplate.values()){
                        if(wt.name().equals(lootElt.get("id"))){
                            loot = new Weapon(wt, false, false);
                        }
                    }
                    if(bf.isTileExisted(lootRow, lootCol) && loot != null) {
                        bf.getTile(lootRow, lootCol).setLoot(loot);
                    }
                }


                // ADD RECRUITS

                Array< XmlReader.Element> recruitElts = battleElt.getChildrenByName("Recruit");
                XmlReader.Element recruitElt;
                int recruitRow;
                int recruitCol;
                Unit recruit;
                for(int i = 0; i < recruitElts.size; i++) {
                    recruitElt  = recruitElts.get(i);
                    recruitRow = recruitElt.getInt("row", -1);
                    recruitCol = recruitElt.getInt("col", -1);
                    recruit = instanciate(recruitElt);
                    if(bf.isTileExisted(recruitRow, recruitCol)
                            && recruit instanceof Unit.CharacterUnit) {
                        bf.getTile(recruitRow, recruitCol).setRecruit((Unit.CharacterUnit)recruit);
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

    private void addUniToBattlefield(Unit unit, XmlReader.Element unitElt, Battlefield bf){
        int rowUnit = unitElt.getInt("row");
        int colUnit = unitElt.getInt("col");
        if(unitElt.getName().equals("Unit")) {
            if (!bf.isTileDeploymentTile(rowUnit, colUnit)) {
                bf.deploy(rowUnit, colUnit, unit, true);
            }
        }else{
            int reinforcmentId = unitElt.getInt("idReinforcement");
            int entryRow = unitElt.getInt("entryRow");
            int entryCol = unitElt.getInt("entryCol");
            if(stiffeners.get(reinforcmentId) == null){
                stiffeners.put(reinforcmentId, new Array<StiffenerData>());
            }
            stiffeners.get(reinforcmentId).add(new StiffenerData(entryRow, entryCol, rowUnit, colUnit, unit));
        }
    }

    private Unit instanciate(XmlReader.Element unitElt){
        Unit unit;

        if(unitElt.getBooleanAttribute("character")) {
            // the unit is a legendary figure
            Data.CharacterTemplate cTemplate = null;
            for(Data.CharacterTemplate characterTemplate : Data.CharacterTemplate.values()){
                if(characterTemplate.name().equals(unitElt.get("template"))){
                    cTemplate = characterTemplate;
                }
            }
            if(cTemplate == null){
                cTemplate = Data.CharacterTemplate.getDefaultValue();
            }
            unit = Unit.createCharacterUnit(cTemplate, unitElt.getName().equals("Recruit"));
        }else{
            // the unit is a generic soldier
            String name = nameDictionary.get(Data.rand(nameDictionary.size));
            int level = unitElt.getInt("level");
            UnitTemplate unitTemplate = EnumUtils.getEnum(UnitTemplate.class, unitElt.get("template"));
            if (unitTemplate == null) unitTemplate = UnitTemplate.getDefaultValue();
            WeaponType weaponType = EnumUtils.getEnum(WeaponType.class, unitElt.get("weaponType"));
            if (weaponType == null) weaponType = WeaponType.getDefaultValue();
            unit = Unit.createGenericUnit(name, unitTemplate, level, weaponType, false);
        }

        // fill unit instance
        Weapon weapon;
        Equipment equipement;
        XmlReader.Element attributeElt;
        for(int k = 0; k < unitElt.getChildCount(); k++){
            attributeElt = unitElt.getChild(k);
            if(attributeElt.get("id").equals("weapon")) {
                for (WeaponTemplate value : WeaponTemplate.values()) {
                    if (value.name().equals(attributeElt.get("value"))) {
                        weapon = new Weapon(value);
                        weapon.setStealable(attributeElt.getBoolean("stealable"));
                        weapon.setDroppable(true);
                        unit.addWeapon(weapon);
                        break;
                    }
                }
            }
            if(attributeElt.get("id").equals("leadership")) {
                unit.setLeadership(attributeElt.getInt("leadership", Data.EXP_REQUIRED_LEADERSHIP.length));
            }
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
                    unit.getBanner().increment(Data.BannerBonus.ATTACK_MIGHT);
                bonusBanner = attributeElt.getInt("range");
                for(int i = 0; i < bonusBanner; i++)
                    unit.getBanner().increment(Data.BannerBonus.RANGE);
                bonusBanner = attributeElt.getInt("lootrate");
                for(int i = 0; i < bonusBanner; i++)
                    unit.getBanner().increment(Data.BannerBonus.LOOT_RATE);
                bonusBanner = attributeElt.getInt("movement");
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

    public void addEvents(AssetManager asm, BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory inventory, ShortUnitPanel sup) {
        try {

            XmlReader.Element battlesElt = reader.parse(Gdx.files.internal(Assets.XML_BATTLE_PARAMS));
            XmlReader.Element battleElt = null;
            for (int i = 0; i < battlesElt.getChildCount(); i++) {
                if (battlesElt.getChild(i).getInt("battlefieldId") == bfr.getModel().getId()) {
                    battleElt = battlesElt.getChild(i);
                    break;
                }
            }

            if(battleElt != null) {

                // SET TRAPS

                int[] pos;
                int traps;
                Array<int[]> trappedArea = new Array<int[]>();
                XmlReader.Element trapAreaElt;
                XmlReader.Element posElt;
                Array< XmlReader.Element> trapAreaElts = battleElt.getChildrenByName("TrapArea");
                for (int i = 0; i< trapAreaElts.size; i++) {
                    // clear previous trapped area
                    trappedArea.clear();
                    trapAreaElt = trapAreaElts.get(i);
                    // get the number of traps
                    traps = trapAreaElt.getInt("nb", 0);
                    // get trapped area
                    for(int j = 0; j < trapAreaElt.getChildCount(); j++){
                           posElt = trapAreaElt.getChild(j);
                           pos = new int[]{posElt.getInt("row", -1), posElt.getInt("col", -1)};
                           trappedArea.add(pos);
                    }
                    // set traps
                    while (trappedArea.size > 0 && traps > 0) {
                        pos = trappedArea.random();
                        trappedArea.removeValue(pos, true);
                        if (bfr.getModel().isTileReachable(pos[0], pos[1], false)
                                && !bfr.getModel().getTile(pos[0], pos[1]).getType().isUrbanArea()) {
                            TrapEvent.addTrigger(bfr, scheduler, inventory, bfr.getModel().getTile(pos[0], pos[1]), pos[0], pos[1], sup);
                            traps--;
                        }
                    }
                }

                // EARTHQUAKE

                Array<XmlReader.Element> earthquakeEventElts = battleElt.getChildrenByName("Earthquake");
                int turn;
                for(int i = 0; i < earthquakeEventElts.size; i++){
                    turn = earthquakeEventElts.get(i).getInt("turn");
                    if(turn > 0){
                        EarthquakeEvent.addTrigger(bfr, scheduler, inventory, turn);
                    }
                }

                // REINFORCEMENTS


                Array<XmlReader.Element> reienforcementEvents = battleElt.getChildrenByName("Rienforcement");
                ReinforcementEvent reienforcementEvent;
                String armyTurn;
                int idReinforcement;
                Array<StiffenerData> data;
                for(int i = 0; i < reienforcementEvents.size; i++){
                    turn = reienforcementEvents.get(i).getInt("turn");
                    armyTurn = reienforcementEvents.get(i).get("armyTurn");
                    idReinforcement = reienforcementEvents.get(i).getInt("id");
                    if(turn > 0){
                        reienforcementEvent = ReinforcementEvent.addTrigger(turn, bfr, scheduler, inventory, bfr.getModel().getBattleTurnManager().getArmyByName(armyTurn));
                        data = stiffeners.get(idReinforcement);
                        for(int j = 0; j < data.size; j++){
                            reienforcementEvent.addStiffener(data.get(j));
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // ------------- LOAD DICTIONARIES ------------------

    public void loadNameDictionary(){
        XmlReader reader = new XmlReader();

        if(!dictionariesLoaded) {
            dictionariesLoaded = true;
            try {
                XmlReader.Element namesElt = reader.parse(Gdx.files.internal(Assets.XML_NAME_DB));
                XmlReader.Element nameElt;
                for (int i = 0; i < namesElt.getChildCount(); i++) {
                    nameElt = namesElt.getChild(i);
                    nameDictionary.add(nameElt.get("value"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



}
