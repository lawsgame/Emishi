package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.helpers.interfaces.SpriteProvider.AreaSpriteType;
import com.lawsgame.emishitactics.core.helpers.spritetree.CharaST;
import com.lawsgame.emishitactics.core.helpers.spritetree.GenUnitST;
import com.lawsgame.emishitactics.core.helpers.spritetree.WeaponBranch;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.AnimSpriteSetId;
import com.lawsgame.emishitactics.core.models.Data.AreaColor;
import com.lawsgame.emishitactics.core.models.Data.SparkleType;
import com.lawsgame.emishitactics.core.models.Data.TileSpriteSetId;
import com.lawsgame.emishitactics.core.models.Data.UnitTemplate;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.engine.datastructures.maps.Map2;

import java.util.HashMap;

public class AssetProvider implements Disposable{
    private static String EXT_PACK = ".pack";


    private float spriteStdSize;
    private Array<TextureRegion> undefinedTileSprites = new Array<TextureRegion>();
    private HashMap<TileSpriteSetId, Array<TextureRegion>> tileTRs = new HashMap<Data.TileSpriteSetId, Array<TextureRegion>> ();
    public HashMap<String, TextureRegion> portraits = new HashMap<String, TextureRegion>();
    public GenUnitST genSpriteTree = new GenUnitST();
    public CharaST charaSpriteTree = new CharaST();
    public Map2<AreaColor, AreaSpriteType, TextureRegion> areaTR = new Map2<AreaColor, AreaSpriteType, TextureRegion>();
    public HashMap<SparkleType, Array<TextureRegion>> sparkleTR = new HashMap<SparkleType, Array<TextureRegion>>();



    public AssetProvider(float spriteStdSize){
        this.spriteStdSize = spriteStdSize;
    }


    public Array<Sprite> getTileSpriteSet(TileSpriteSetId id) {
        Array<Sprite> sprites = new Array<Sprite>();
        if(id != null) {
            Array<TextureRegion> regions;
            if (id.isUpper()) {
                regions = tileTRs.get(id);
            } else {
                if(tileTRs.containsKey(id) && tileTRs.get(id) != null) {
                    regions = tileTRs.get(id);
                }else{
                    regions = undefinedTileSprites;
                }
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


    public Sprite getAreaSprite(AreaColor color, AreaSpriteType type){
        Sprite sprite;
        TextureRegion region = areaTR.get(color, type);
        if(region == null) {
            sprite = new Sprite(areaTR.get(AreaColor.LIGHT_BLUE, type));
        }else{
            sprite = new Sprite(region);
        }
        sprite.setSize(0.5f, 0.25f);
        return sprite;
    }


    // --------------- LOAR ASSETS --------------------------------

    public void set(Battlefield battlefield, AssetManager asm){

        if(asm != null) {
            FileHandle dirHandle;
            FileHandle fileHandle;
            String filepath;
            TextureRegion region;
            String regionName;
            TextureAtlas atlas;
            Array<TextureAtlas.AtlasRegion> atlasRegions;
            Array<TextureRegion> regions;

            // LOAD TILE TR

            dirHandle = Gdx.files.internal(Assets.TILE_SPRITES_DIR);
            if(dirHandle.child(battlefield.getEnvironment().name().toLowerCase()+EXT_PACK).exists()) {
                filepath = String.format("%s/%s%s", Assets.TILE_SPRITES_DIR, battlefield.getEnvironment().name().toLowerCase(), EXT_PACK);
            }else{
                filepath = String.format("%s/%s%s", Assets.TILE_SPRITES_DIR, Data.Environment.getStandard().name().toLowerCase(), EXT_PACK);
            }

            asm.load(filepath, TextureAtlas.class);
            asm.finishLoading();
            if (asm.isLoaded(filepath)) {
                atlas = asm.get(filepath);
                atlasRegions = atlas.findRegions(Assets.REGION_TERRAINS_UNDEFINED);
                for(int i = 0; i < atlasRegions.size; i++)
                    this.undefinedTileSprites.add(atlasRegions.get(i));

                for (TileSpriteSetId tileId : TileSpriteSetId.values()) {
                    regions = new Array<TextureRegion>();
                    regionName = Assets.getRegionTile(tileId);
                    atlasRegions = atlas.findRegions(regionName);
                    if(atlasRegions.size > 0) {
                        for (int i = 0; i < atlasRegions.size; i++) {
                            atlasRegions.get(i).getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                            regions.add(atlasRegions.get(i));
                        }
                        this.tileTRs.put(tileId, regions);
                    }

                }
            }


            // LOAD AREA TR

            dirHandle = Gdx.files.internal(Assets.AREA_SPRITES_DIR);
            Data.AreaColor color;
            for(int i = 0; i < AreaColor.values().length; i++ ){

                color = AreaColor.values()[i];
                filepath = String.format("%s/%s%s", Assets.AREA_SPRITES_DIR, color.name().toLowerCase(), EXT_PACK);
                if(dirHandle.child(AreaColor.values()[i].name().toLowerCase()+EXT_PACK).exists()) {
                    asm.load(filepath, TextureAtlas.class);
                    asm.finishLoading();
                    if (asm.isLoaded(filepath)) {
                        atlas = asm.get(filepath);
                        for (int j = 0; j < AreaSpriteType.values().length; j++) {
                            region = atlas.findRegion(AreaSpriteType.values()[j].name().toLowerCase());
                            if (region != null) {

                                region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                                areaTR.put(color, AreaSpriteType.values()[j], region);
                            }
                        }
                    }
                }

            }


            // LOAD SPARKLE TR

            atlas = asm.get(Assets.ATLAS_BATTLE_ICONS, TextureAtlas.class);
            for(int i = 0; i < SparkleType.values().length; i++) {
                atlasRegions = atlas.findRegions(Assets.getSparkleTR(SparkleType.values()[i]));
                regions = new Array<TextureRegion>();
                for(int j = 0; j < atlasRegions.size; j++){
                    regions.add(atlasRegions.get(j));
                }
                sparkleTR.put(SparkleType.values()[i], regions);
            }


            // LOAD UNITS SS

            Array<String[]> filenames = getFileNames(battlefield);
            dirHandle = Gdx.files.internal(Assets.UNIT_SPRITES_DIR);
            TextureAtlas unitAtlas;
            TextureRegion unitPortrait;
            WeaponBranch weaponTypeNode;
            for(int i = 0; i < filenames.size; i++){

                // CHECK FILE EXISTENCE

                fileHandle = dirHandle.child(filenames.get(i)[0]).child(filenames.get(i)[1]).child(filenames.get(i)[2]+EXT_PACK);
                if(fileHandle.exists()) {

                    // LOAD FILE

                    filepath = String.format("%s/%s/%s/%s%s", Assets.UNIT_SPRITES_DIR, filenames.get(i)[0], filenames.get(i)[1], filenames.get(i)[2], EXT_PACK);
                    asm.load(filepath, TextureAtlas.class);
                    asm.finishLoading();

                    // HANDLE FILE CONTENT

                    unitAtlas = asm.get(filepath);
                    if (unitAtlas != null) {
                        unitPortrait = unitAtlas.findRegion(Assets.REGION_UNIT_PORTRAIT);
                        if (unitPortrait != null) {
                            portraits.put(filenames.get(i)[0] + "_" + filenames.get(i)[2].split("_")[0], unitPortrait);
                        }

                        boolean character = filenames.get(i)[2].split("_")[0].equals("promoted")
                                || filenames.get(i)[2].split("_")[0].equals("recruit");

                        if (character) {

                            weaponTypeNode = charaSpriteTree.initSpriteSheet(filenames.get(i)[2].split("_")[0].equals("promoted"),
                                    UnitTemplate.valueOf(filenames.get(i)[0].toUpperCase()),
                                    WeaponType.valueOf(filenames.get(i)[1].toUpperCase()));
                            loadSpriteSet(weaponTypeNode, unitAtlas, filenames.get(i)[2].split("_")[1].equals("done"));
                        } else {

                            boolean shieldbearer = filenames.get(i)[1].contains("S");
                            boolean horseman = filenames.get(i)[1].contains("H");
                            if (!filenames.get(i)[2].equals("done")) {

                                // active AI / PC sprite set
                                weaponTypeNode = genSpriteTree.initSpriteSheet(
                                        filenames.get(i)[2].equals("pc"),
                                        shieldbearer,
                                        horseman,
                                        UnitTemplate.valueOf(filenames.get(i)[0].toUpperCase()),
                                        WeaponType.valueOf(filenames.get(i)[1].toUpperCase().split("_")[0]));
                                loadSpriteSet(weaponTypeNode, unitAtlas, false);
                            } else {

                                // done sprite sets
                                weaponTypeNode = genSpriteTree.initSpriteSheet(
                                        false,
                                        shieldbearer,
                                        horseman,
                                        UnitTemplate.valueOf(filenames.get(i)[0].toUpperCase()),
                                        WeaponType.valueOf(filenames.get(i)[1].toUpperCase().split("_")[0]));
                                loadSpriteSet(weaponTypeNode, unitAtlas, true);
                                weaponTypeNode = genSpriteTree.initSpriteSheet(
                                        true,
                                        shieldbearer,
                                        horseman,
                                        UnitTemplate.valueOf(filenames.get(i)[0].toUpperCase()),
                                        WeaponType.valueOf(filenames.get(i)[1].toUpperCase().split("_")[0]));
                                loadSpriteSet(weaponTypeNode, unitAtlas, true);
                            }
                        }
                    }
                }
            }
        }
    }

    private void loadSpriteSet(WeaponBranch node, TextureAtlas unitAtlas, boolean done) {

        Array<TextureAtlas.AtlasRegion> animationSet;
        TextureRegion[] animationArray;
        String spriteSetName;
        for(AnimSpriteSetId id : AnimSpriteSetId.values()){

            spriteSetName = Assets.getRegionUnitAction(id, false);
            animationSet = unitAtlas.findRegions(spriteSetName);
            if(animationSet.size > 0) {
                animationArray = new TextureRegion[animationSet.size];
                for(int i = 0; i < animationSet.size; i++) {
                    animationArray[i] = animationSet.get(i);
                }
                node.populate(false, id, done, animationArray);
            }

            spriteSetName = Assets.getRegionUnitAction(id, true);
            animationSet = unitAtlas.findRegions(spriteSetName);
            if(animationSet.size > 0) {

                animationArray = new TextureRegion[animationSet.size];
                for(int i = 0; i < animationSet.size; i++) {
                    animationArray[i] = animationSet.get(i);
                }
                node.populate(true, id, done, animationArray);
            }
        }
    }

    private Array<String[]> getFileNames(Battlefield battlefield) {
        Array<String[]> filenames = new Array<String[]>();

        // default unit sprites
        filenames.add(new String[]{ "solaire", "sword", "promoted_active"});
        filenames.add(new String[]{ "solaire", "sword", "promoted_done"});
        filenames.add(new String[]{ "solaire", "sword", "recruit_active"});
        filenames.add(new String[]{ "solaire", "sword", "recruit_done"});
        filenames.add(new String[]{ "solar_knight", "sword_F", "ai"});
        filenames.add(new String[]{ "solar_knight", "sword_F", "pc"});
        filenames.add(new String[]{ "solar_knight", "sword_F", "done"});

        String[] filename;
        for(int r = 0; r < battlefield.getNbRows(); r++){
            for (int c = 0; c < battlefield.getNbColumns(); c++) {
                if (battlefield.isTileOccupied(r, c)) {
                    Unit unit = battlefield.getUnit(r, c);

                    filename = new String[3];
                    filename[0] = unit.getTemplate().name().toLowerCase();
                    filename[1] = (unit.isCharacter()) ?
                            unit.getWeaponType().name().toLowerCase() :
                            String.format("%s_%s",
                                    unit.getWeaponType().name().toLowerCase(),
                                    (unit.isHorseman()) ? "H" : "F");

                    if(!unit.isCharacter()){
                        filename[2] = (unit.getArmy().isPlayerControlled()) ? "pc" : "ai";
                    }


                    boolean notRegistered = true;
                    for (int i = 0; i < filenames.size; i++) {
                        if (filename[0].equals(filenames.get(i)[0])
                                && filename[1].equals(filenames.get(i)[1])
                                && (unit.isCharacter() || filename[2].equals(filenames.get(i)[2]))) {
                            notRegistered = false;
                            break;
                        }
                    }

                    if(notRegistered) {
                        if(unit.isCharacter()){

                            filename[2] = "recruit_active";
                            filenames.add(filename);

                            filename = new String[3];
                            filename[0] = unit.getTemplate().name().toLowerCase();
                            filename[1] = unit.getWeaponType().name().toLowerCase();
                            filename[2] = "promoted_active";
                            filenames.add(filename);

                            filename = new String[3];
                            filename[0] = unit.getTemplate().name().toLowerCase();
                            filename[1] = unit.getWeaponType().name().toLowerCase();
                            filename[2] = "recruit_done";
                            filenames.add(filename);

                            filename = new String[3];
                            filename[0] = unit.getTemplate().name().toLowerCase();
                            filename[1] = unit.getWeaponType().name().toLowerCase();
                            filename[2] = "promoted_done";
                            filenames.add(filename);
                        }else {

                            filenames.add(filename);

                            filename = new String[3];
                            filename[0] = unit.getTemplate().name().toLowerCase();
                            filename[1] = String.format("%s_%s",
                                    unit.getWeaponType().name().toLowerCase(),
                                    (unit.isHorseman()) ? "H" : "F");
                            filename[2] = "done";
                            filenames.add(filename);

                        }
                    }

                }
            }
        }
        return filenames;
    }

    @Override
    public void dispose() { }


}
