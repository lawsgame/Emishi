package com.lawsgame.emishitactics.core.helpers.implementations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.TacticsGame;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.interfaces.SpriteProvider;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.AnimId;
import com.lawsgame.emishitactics.core.models.Data.AreaColor;
import com.lawsgame.emishitactics.core.models.Data.Environment;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.SparkleType;
import com.lawsgame.emishitactics.core.models.Data.TileSpriteSetId;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.Data.UnitTemplate;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.Data.AnimUnitSSId;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.engine.datastructures.maps.Map2;
import com.lawsgame.emishitactics.engine.datastructures.maps.Map7;
import com.lawsgame.emishitactics.engine.utils.Lawgger;

import java.util.HashMap;

public class SpriteProviderImp implements SpriteProvider {
    private static Lawgger log = Lawgger.createInstance(SpriteProviderImp.class);

    // dimension of the standard  unit /tile sprite
    private float spriteStdSize;
    private HashMap<TileSpriteSetId , Array<TextureRegion>> tileTRs;
    private Map2<AreaColor, AreaSpriteType, Array<TextureRegion>> areaTRs;
    private HashMap<SparkleType, Array<TextureRegion>> sparkleTRs;
    private HashMap<String, TextureRegion> portraitTRs;
    // b1 = promoted?, b2 = east or north oriented?, b3 = warchief?
    private Map7<UnitTemplate, WeaponType, Boolean, Boolean, Data.AnimUnitSSId, Boolean, Flavor, Array<AtlasRegion>> characterTRs;
    // b1 = player controlled?, b2 = east or north oriented?, b3 = warchief?
    private Map7<UnitTemplate, WeaponType, Boolean, Boolean, Data.AnimUnitSSId, Boolean, Flavor, Array<AtlasRegion>> unitTRs;

    public SpriteProviderImp(float spriteStdSize){
        this.spriteStdSize = spriteStdSize;
        this.tileTRs = new HashMap<TileSpriteSetId, Array<TextureRegion>> ();
        this.areaTRs = new Map2<AreaColor, AreaSpriteType, Array<TextureRegion>>();
        this.sparkleTRs = new HashMap<SparkleType, Array<TextureRegion>>();
        this.portraitTRs = new HashMap<String, TextureRegion>();
        this.characterTRs = new Map7<UnitTemplate, WeaponType, Boolean, Boolean, Data.AnimUnitSSId, Boolean, Flavor, Array<AtlasRegion>>(
                Data.CharacterTemplate.getDefaultValue().getTemplate(),
                WeaponType.SWORD,
                false, // recruit
                false, // east or north
                AnimUnitSSId.REST,
                false, // not a warchief but a mere soldier
                Flavor.NORMAL
        );
        this.unitTRs = new Map7<UnitTemplate, WeaponType, Boolean, Boolean, Data.AnimUnitSSId, Boolean, Flavor, Array<AtlasRegion>>(
                UnitTemplate.getDefaultValue(),
                WeaponType.SWORD,
                false, // pc
                false, // east or north
                AnimUnitSSId.REST,
                false, // not a warchief but a mere soldier
                Flavor.NORMAL
        );
    }


    @Override
    public Sprite getPortrait(BattleUnitRenderer bur) {
        return bur.getModel().isCharacter() ?
                getPortrait(bur.getModel().getTemplate(), bur.isPromoted()):
                getPortrait(bur.getModel().belongToAnArmy() && bur.getModel().getArmy().isPlayerControlled(), bur.getModel().getTemplate());
    }

    @Override
    public Sprite getPortrait(UnitTemplate template, boolean promoted) {
        TextureRegion portraitTR = portraitTRs.get(String.format("%s_%s", template.name().toLowerCase(), (promoted)? "promoted" : "recruit"));
        if(portraitTR == null){
            portraitTR = portraitTRs.get(String.format("%s_%s", UnitTemplate.SOLAIRE.name().toLowerCase(), (promoted)? "promoted" : "recruit"));
        }
        return new Sprite(portraitTR);
    }

    @Override
    public Sprite getPortrait(boolean playerControlled, UnitTemplate template) {
        TextureRegion portraitTR = portraitTRs.get(String.format("%s_%s", template.name().toLowerCase(), (playerControlled)? "pc" : "ai"));
        if(portraitTR == null){
            portraitTR = portraitTRs.get(String.format("%s_%s", UnitTemplate.SOLAR_KNIGHT.name().toLowerCase(), (playerControlled)? "pc" : "ai"));
        }
        return new Sprite(portraitTR);
    }

    @Override
    public Array<Sprite> getTileSS(TileType type, boolean upper) {
        return getTileSS((upper) ? type.getUpperPart() : type.getLowerPart());
    }

    /**
     * allow to proceed to two type of tile sprites :
     *  - short 1x1
     *  - long  1x1.5 = 2 * short ones
     *
     * @param id : tile ss id
     * @return : ss of the given id
     */
    @Override
    public Array<Sprite> getTileSS(TileSpriteSetId id) {
        Array<Sprite> sprites = new Array<Sprite>();
        if(id != null) {
            Array<TextureRegion> regions;
            regions = tileTRs.get(id);
            if(regions.size == 0 && !id.isUpper()) {
                regions = tileTRs.get(TileSpriteSetId.UNDEFINED);
            }
            if (regions != null) {
                Sprite sprite;
                boolean shortSprite;
                for (int i = 0; i < regions.size; i++) {
                    shortSprite = regions.get(i).getRegionHeight() == regions.get(i).getRegionWidth();
                    sprite = new Sprite(regions.get(i));
                    sprite.setSize(spriteStdSize, ((shortSprite) ?  1f : 1.5f) * spriteStdSize);
                    sprites.add(sprite);
                }
            }
        }
        return sprites;
    }

    @Override
    public Array<Sprite> getUnitAnimationSS(BattleUnitRenderer bur, AnimId id, Flavor flavor) {
        return getUnitAnimationSS(
                bur.getModel().getTemplate(),
                bur.getCurrentWeaponType(),
                bur.getModel().isCharacter(),
                bur.isPromoted(),
                bur.getModel().belongToAnArmy() && bur.getModel().getArmy().isPlayerControlled(),
                bur.getOrientation(),
                id,
                bur.getModel().isWarChief(),
                flavor
        );
    }

    @Override
    public Array<Sprite> getUnitAnimationSS(UnitTemplate template, WeaponType weaponType,
                                            boolean character, boolean promoted, boolean playerControlled, Orientation or,
                                            AnimId id, boolean warchief, Flavor param) {
        // get regions
        Array<Sprite> sprites = new Array<Sprite>();
        boolean eastNorth = or == Orientation.EAST || or == Data.Orientation.NORTH;
        Array<AtlasRegion> regions = (character) ?
                this.characterTRs.get(template, weaponType, promoted, eastNorth, id.getSpriteSetId(), warchief, param):
                this.unitTRs.get(template, weaponType, playerControlled, eastNorth, id.getSpriteSetId(), warchief, param);
        // create sprite set
        boolean shortSprite;
        boolean flipped = or == Data.Orientation.WEST || or == Data.Orientation.NORTH;
        Sprite sprite;
        for(int i = 0; i < regions.size; i++){
            shortSprite = regions.get(i).getRegionWidth() != regions.get(i).getRegionHeight();
            sprite = new Sprite(regions.get(i));
            sprite.setSize(((shortSprite) ? 0.5f: 1f) *spriteStdSize, spriteStdSize);
            if (flipped) {
                sprite.flip(true, false);
            }
            sprites.add(sprite);
        }
        return sprites;
    }

    @Override
    public Array<TextureAtlas.AtlasRegion> getStandardUnitAnimationSS(boolean character) {
        return (character) ?
                characterTRs.getStandard():
                unitTRs.getStandard();

    }

    @Override
    public Array<Sprite> getSparkleSS(SparkleType type) {
        Array<Sprite> sprites = new Array<Sprite>();
        Array<TextureRegion> trs = sparkleTRs.get(type);
        if(sparkleTRs == null || sparkleTRs.size() == 0)
            trs = sparkleTRs.get(SparkleType.LOOT);
        Sprite sprite;
        for(int i = 0;i < trs.size; i++){
            sprite = new Sprite(trs.get(i));
            sprite.setSize(0.25f, 0.25f);
            sprites.add(sprite);
        }
        return sprites;
    }

    @Override
    public Array<Sprite> getAreaSS(AreaColor color, AreaSpriteType type) {
        Array<Sprite> sprites = new Array<Sprite>();
        Array<TextureRegion> regions = areaTRs.get(color, type);
        if(regions == null || regions.size == 0){
            regions = areaTRs.get(AreaColor.LIGHT_BLUE, type);
        }
        Sprite sprite;
        for(int i =0; i < regions.size; i++){
            sprite = new Sprite(regions.get(i));
            sprite.setSize(0.5f, 0.25f);
            sprites.add(sprite);

        }
        return sprites;
    }

    @Override
    public void load(AssetManager asm, Battlefield battlefield) {
        if(asm != null && battlefield != null) {
            String EXT_PACK = ".pack";
            FileHandle dirHandle;
            FileHandle fileHandle;
            String filepath;
            String filename;
            TextureRegion region;
            String regionName;
            TextureAtlas atlas;
            Array<TextureAtlas.AtlasRegion> atlasRegions;
            Array<TextureRegion> regions;


            // LOAD TILE SS

            // fetch the file path of the tile sprites
            dirHandle = Gdx.files.internal(Assets.TILE_SPRITES_DIR);
            filename  = battlefield.getEnvironment().name().toLowerCase() + EXT_PACK;
            filepath = String.format("%s/%s", Assets.TILE_SPRITES_DIR, (dirHandle.child(filename).exists()) ? filename : Environment.getDefaultValue().name().toLowerCase() + EXT_PACK);
            // load texture regions from the appointed file
            asm.load(filepath, TextureAtlas.class);
            asm.finishLoading();
            if (asm.isLoaded(filepath)) {
                atlas = asm.get(filepath);
                for (TileSpriteSetId tileId : TileSpriteSetId.values()) {
                    regionName = Assets.getRegionTile(tileId);
                    atlasRegions = atlas.findRegions(regionName);
                    if(atlasRegions.size > 0) {
                        regions = new Array<TextureRegion>();
                        regions.addAll(atlasRegions);
                        this.tileTRs.put(tileId, regions);
                    }

                }
            }


            // LOAD AREA SS

            dirHandle = Gdx.files.internal(Assets.AREA_SPRITES_DIR);
            Data.AreaColor color;
            for(int i = 0; i < AreaColor.values().length; i++ ){
                color = AreaColor.values()[i];
                filename = color.name().toLowerCase() + EXT_PACK;
                filepath = String.format("%s/%s", Assets.AREA_SPRITES_DIR, filename);
                if(dirHandle.child(filename).exists()) {
                    asm.load(filepath, TextureAtlas.class);
                    asm.finishLoading();
                    if (asm.isLoaded(filepath)) {
                        atlas = asm.get(filepath);
                        for (int j = 0; j < AreaSpriteType.values().length; j++) {
                            atlasRegions = atlas.findRegions(AreaSpriteType.values()[j].name().toLowerCase());
                            if (atlasRegions.size > 0 ) {
                                regions = new Array<TextureRegion>();
                                regions.addAll(atlasRegions);
                                areaTRs.put(color, AreaSpriteType.values()[j], regions);
                            }
                        }
                    }
                }
            }


            // LOAD SPARKLE SS

            atlas = asm.get(Assets.ATLAS_BATTLE_ICONS, TextureAtlas.class);
            for(int i = 0; i < SparkleType.values().length; i++) {
                atlasRegions = atlas.findRegions(Assets.getSparkleTR(SparkleType.values()[i]));
                regions = new Array<TextureRegion>();
                regions.addAll(atlasRegions);
                if(regions.size > 0) {
                    sparkleTRs.put(SparkleType.values()[i], regions);
                }
            }

            // LOAD UNITS

            Array<String[]> filenamesArray = getCheckedFilePathFromDeployedUnits(battlefield);
            TextureRegion portraitTR;
            String ext = ".pack";
            for(int i = 0; i < filenamesArray.size; i++){
                filepath = String.format("%s/%s/%s/%s%s", Assets.UNIT_SPRITES_DIR, filenamesArray.get(i)[0], filenamesArray.get(i)[1], filenamesArray.get(i)[2], ext);
                fileHandle = Gdx.files.internal(filepath);
                if(fileHandle.exists()){
                    log.info(filepath);
                    asm.load(filepath, TextureAtlas.class);
                    asm.finishLoading();
                    atlas =  asm.get(filepath, TextureAtlas.class);
                    if(atlas != null){
                        portraitTR = atlas.findRegion(Assets.REGION_UNIT_PORTRAIT);
                        if (portraitTR != null) {
                            this.portraitTRs.put(filenamesArray.get(i)[0] + "_" + filenamesArray.get(i)[2], portraitTR);
                        }
                        fetchAndStoreUnitSpriteSetsIntoMaps(filenamesArray.get(i), atlas);
                    }
                }else{
                    log.info(filepath+" > not found");
                }
            }
        }
    }


    /**
     * from the filenames, fill maps with all relevant SS for the unit
     * from its associated atlas given as parameter
     *
     * @param filenames
     * @param unitAtlas
     */
    private void fetchAndStoreUnitSpriteSetsIntoMaps(String[] filenames, TextureAtlas unitAtlas){
        boolean isCharacter = filenames[2].equals("promoted") || filenames[2].equals("recruit");
        UnitTemplate template = UnitTemplate.valueOf(filenames[0].toUpperCase());
        WeaponType weaponType = WeaponType.valueOf(filenames[1].toUpperCase());
        boolean promotedPC = filenames[2].equals("promoted") || filenames[2].equals("pc");
        AnimUnitSSId ssMapid;
        Flavor flavor;
        String ssAtlasid;
        Array<TextureAtlas.AtlasRegion> ss;
        for(int i = 0; i < Data.AnimUnitSSId.values().length; i++){
            for(int j = 0; j < Flavor.values().length; j++) {
                ssMapid = AnimUnitSSId.values()[i];
                flavor = Flavor.values()[j];
                // for each couple (or, warchief) => put the SS if exists
                boolean eastNorth;
                boolean warchief;
                for(int k = 0; k < 4; k++) {
                    eastNorth = (k & 1) == 1;
                    warchief = (k >> 1) == 1;
                    ssAtlasid = Assets.getRegionUnit(ssMapid, eastNorth, warchief, flavor);
                    ss = unitAtlas.findRegions(ssAtlasid);
                    if (ss != null && ss.size > 0) {
                        if (isCharacter) {
                            characterTRs.put(template, weaponType, promotedPC, eastNorth, ssMapid, warchief, flavor, ss);
                        } else {
                            unitTRs.put(template, weaponType, promotedPC, eastNorth, ssMapid, warchief, flavor, ss);
                        }
                    }
                }
            }
        }
    }


    /**
     * Taking the state of the battlefield,
     * this method create all assoicated filepaths which would point to the altases
     * which holds the sprites to render the deployed units
     *
     *
     * @param battlefield
     * @return
     */
    private Array<String[]> getCheckedFilePathFromDeployedUnits(Battlefield battlefield) {
        Array<String[]> filenames = new Array<String[]>();

        // default unit sprites sheet location
        filenames.add(new String[]{"solaire", "sword", "promoted"});
        filenames.add(new String[]{"solaire", "sword", "recruit"});
        filenames.add(new String[]{"solar_knight", "sword", "ai"});
        filenames.add(new String[]{"solar_knight", "sword", "pc"});
        // fetch all units deployed and add their spritesheet locations.
        Unit unit;
        Unit.CharacterUnit chara;
        for(int r = 0; r < battlefield.getNbRows(); r++){
            for (int c = 0; c < battlefield.getNbColumns(); c++) {
                // fetch deployed units
                if (battlefield.isTileOccupied(r, c)) {
                    unit = battlefield.getUnit(r, c);
                    addUnitAssociatedFilePath(unit, filenames, unit.getWeaponType());
                }
                //fetch to be recruited units
                if(battlefield.isTileExisted(r, c) && battlefield.getTile(r, c).isHidingRecruit()){
                    chara = battlefield.getTile(r,c).getRecruit(false);
                    for(int i = 0; i < chara.getUsableWeaponType().length; i++) {
                        addUnitAssociatedFilePath(chara, filenames, chara.getUsableWeaponType()[0]);
                    }
                }
                //fetch reinforcements
                //TODO: add reinforcements


            }
        }
        // remove clone paths'names
        return Utils.arrayRemoveClassTableClones(filenames);
    }

    /**
     * add the array of designations of the parent folders
     * which contains the atlas holding the sprites to render the given unit
     * to the array filenames
     *
     * @param unit
     * @param filenames
     */
    private void addUnitAssociatedFilePath(Unit unit, Array<String[]> filenames, WeaponType weaponType){
        // create filepath
        String[] filename = new String[3];
        filename[0] = unit.getTemplate().name().toLowerCase();
        filename[1] = weaponType.name().toLowerCase();
        if(!unit.isCharacter()){
            if(unit.belongToAnArmy()) {
                filename[2] = (unit.getArmy().isPlayerControlled()) ? "pc" : "ai";
            }
        }else{
            filename[2] = unit.isPromoted() ? "promoted" : "recruit";
        }
        // add the location if not yet inserted
        if (!Utils.arrayContains(filenames, filename)) {
            filenames.add(filename);
            if (unit.isCharacter() && !unit.isPromoted()) {
                filename = new String[]{filename[0], filename[1], "promoted"};
                if (!Utils.arrayContains(filenames, filename)) {
                    filenames.add(filename);
                }
            }
        }
    }

    @Override
    public void dispose() {

    }
}
