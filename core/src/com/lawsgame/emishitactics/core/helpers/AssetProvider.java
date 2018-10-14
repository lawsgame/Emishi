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
import com.lawsgame.emishitactics.core.helpers.spritetree.CharaST;
import com.lawsgame.emishitactics.core.helpers.spritetree.GenUnitST;
import com.lawsgame.emishitactics.core.helpers.spritetree.WeaponBranch;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.AreaColor;
import com.lawsgame.emishitactics.core.models.Data.SpriteSetId;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.Data.UnitTemplate;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.engine.datastructures.Map2;

import java.util.HashMap;

public class AssetProvider implements Disposable{
    private static String EXT_PACK = ".pack";

    public enum AreaSpriteType{
        ANTI_INSIDE_ACUTE,
        ANTI_INSIDE_OBTUSE,
        ANTI_OUTSIDE_ACUTE,
        ANTI_OUTSIDE_OBTUSE,
        ANTI_SIDE,
        ANTI_SIDE_UPSIDE_DOWN,
        BORDER,
        CORNER_ACUTE,
        CORNER_OBTUSE
    }

    private float spriteStdSize;
    private TextureRegion undefinedTileSprite = null;
    private HashMap<TileType, TextureRegion> tileSprites = new HashMap<Data.TileType, TextureRegion>();

    public HashMap<String, TextureRegion> portraits = new HashMap<String, TextureRegion>();
    public GenUnitST genSpriteTree = new GenUnitST();
    public CharaST charaSpriteTree = new CharaST();

    public Map2<AreaColor, AreaSpriteType, TextureRegion> areaTextureRegions = new Map2<AreaColor, AreaSpriteType, TextureRegion>();



    public AssetProvider(float spriteStdSize){
        this.spriteStdSize = spriteStdSize;
    }

    public Array<Sprite> getTileSprite(TileType type) {
        Array<Sprite> sprites = new Array<Sprite>();
        Sprite sprite = (tileSprites.containsKey(type) && tileSprites.get(type) != null) ?
            new Sprite(tileSprites.get(type)) :
            new Sprite(undefinedTileSprite);
        sprite.setSize(spriteStdSize, spriteStdSize);
        sprites.add(sprite);
        return sprites;
    }

    public void set(Battlefield battlefield, AssetManager asm){

        if(asm != null) {
            FileHandle dirHandle;
            FileHandle fileHandle;
            String filepath = "";
            TextureRegion region;
            String regionName;
            TextureAtlas atlas;

            // LOAD TILE SET
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
                this.undefinedTileSprite = atlas.findRegion(Assets.REGION_TERRAINS_UNDEFINED);
                for (Data.TileType tileType : Data.TileType.values()) {
                    regionName = Assets.getRegionTile(tileType);
                    region = atlas.findRegion(regionName);
                    if (region != null) {
                        region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                        this.tileSprites.put(tileType, region);
                    }
                }
            }


            // LOAD AREA SETS

            dirHandle = Gdx.files.internal(Assets.AREA_SPRITES_DIR);
            Data.AreaColor color;
            for(int i = 0; i < AreaColor.values().length; i++ ){

                color = AreaColor.values()[i];
                filepath = String.format("%s/%s%s", Assets.AREA_SPRITES_DIR, color.name().toLowerCase(), EXT_PACK);;
                if(dirHandle.child(AreaColor.values()[i].name().toLowerCase()+EXT_PACK).exists()) {
                    asm.load(filepath, TextureAtlas.class);
                    asm.finishLoading();
                    if (asm.isLoaded(filepath)) {
                        atlas = asm.get(filepath);
                        for (int j = 0; j < AreaSpriteType.values().length; j++) {
                            region = atlas.findRegion(AreaSpriteType.values()[j].name().toLowerCase());
                            if (region != null) {

                                region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                                areaTextureRegions.put(color, AreaSpriteType.values()[j], region);
                            }
                        }
                    }
                }

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
        for(SpriteSetId id : SpriteSetId.values()){

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
                    IUnit unit = battlefield.getUnit(r, c);

                    filename = new String[3];
                    filename[0] = unit.getTemplate().name().toLowerCase();
                    filename[1] = (unit.isCharacter()) ?
                            unit.getWeaponType().name().toLowerCase() :
                            String.format("%s_%s%s",
                                    unit.getWeaponType().name().toLowerCase(),
                                    (unit.isHorseman()) ? "H" : "F",
                                    (unit.isShielbearer()) ? "S" : "");

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
                            filename[1] = String.format("%s_%s%s",
                                    unit.getWeaponType().name().toLowerCase(),
                                    (unit.isHorseman()) ? "H" : "F",
                                    (unit.isShielbearer()) ? "S" : "");
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


    public Sprite getAreaTR(AreaColor color, AreaSpriteType type){
        Sprite sprite;
        TextureRegion region = areaTextureRegions.get(color, type);
        if(region == null) {
            sprite = new Sprite(areaTextureRegions.get(AreaColor.LIGHT_BLUE, type));
        }else{
            sprite = new Sprite(region);
        }
        sprite.setSize(0.5f, 0.25f);
        return sprite;
    }

}
