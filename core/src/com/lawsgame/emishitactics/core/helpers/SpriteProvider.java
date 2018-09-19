package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.SpriteSetId;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.Data.UnitTemplate;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;

import java.util.HashMap;

public class SpriteProvider implements Disposable{
    private static String EXT_PACK = ".pack";
    private static int PORTRAIT_TEXTURE_SIZE = 128;


    private Array<Texture> madeupTextures;

    public SpriteProvider(){
        this.madeupTextures = new Array<Texture>();
    }

    public TextureRegion undefinedTileSprite = null;
    private HashMap<TileType, TextureRegion> tileSprites = new HashMap<Data.TileType, TextureRegion>();
    public HashMap<String, TextureRegion> portraits = new HashMap<String, TextureRegion>();
    public GenericUnitSpriteTree genSpriteTree = new GenericUnitSpriteTree();
    public CharacterSpriteTree charaSpriteTree = new CharacterSpriteTree();

    public TextureRegion getTileSprite(TileType type) {
        if(tileSprites.containsKey(type) && tileSprites.get(type) != null){
            return tileSprites.get(type);
        }
        return undefinedTileSprite;
    }

    public void set(Battlefield battlefield, AssetManager asm){

        if(asm != null) {
            TextureRegion region;
            String regionName;
            TextureAtlas atlas;

            // set tiles sprite pool
            if (asm.isLoaded(Assets.ATLAS_TILES)) {
                atlas = asm.get(Assets.ATLAS_TILES);
                this.undefinedTileSprite = atlas.findRegion(Assets.REGION_TERRAINS_UNDEFINED);
                for (Data.TileType tileType : Data.TileType.values()) {
                    regionName = Assets.getRegionTile(tileType);
                    region = atlas.findRegion(regionName);
                    if (region != null) {
                        this.tileSprites.put(tileType, region);
                    }
                }
            }


            // load units
            Array<String[]> filenames = getFileNames(battlefield);
            FileHandle unitsDirHandle = Gdx.files.internal(Assets.UNIT_SPRITES_DIR);
            FileHandle unitFileHandle;
            String filepath;
            TextureAtlas unitAtlas;
            TextureRegion unitRegion;
            WeaponTypeNode weaponTypeNode;
            for(int i = 0; i < filenames.size; i++){

                // CHECK FILE EXISTENCE

                unitFileHandle = unitsDirHandle.child(filenames.get(i)[0]).child(filenames.get(i)[1]).child(filenames.get(i)[2]+EXT_PACK);

                if(unitFileHandle.exists()){

                    // LOAD FILE

                    filepath = String.format("%s/%s/%s/%s%s", Assets.UNIT_SPRITES_DIR, filenames.get(i)[0], filenames.get(i)[1], filenames.get(i)[2], EXT_PACK);

                    asm.load(filepath, TextureAtlas.class);
                    asm.finishLoading();


                    // HANDLE FILE CONTENT

                    unitAtlas = asm.get(filepath);
                    unitRegion = unitAtlas.findRegion(Assets.REGION_UNIT_SPRITE_SHEET);
                    if(unitRegion.getRegionWidth() == PORTRAIT_TEXTURE_SIZE &&  unitRegion.getRegionHeight() == PORTRAIT_TEXTURE_SIZE){

                        portraits.put(filenames.get(i)[0], unitRegion);
                    }else {
                        boolean character = filenames.get(i)[2].equals("promoted") || filenames.get(i)[2].equals("recruit");
                        if(character){
                            weaponTypeNode = charaSpriteTree.initSpriteSheet(filenames.get(i)[2].equals("promoted") ,
                                    UnitTemplate.valueOf(filenames.get(i)[0].toUpperCase()),
                                    WeaponType.valueOf(filenames.get(i)[1].toUpperCase()));
                            loadSpriteSet(filenames.get(i)[0], weaponTypeNode, unitRegion, asm);
                        }else{
                            boolean shieldbearer = filenames.get(i)[1].contains("S");
                            boolean horseman = filenames.get(i)[1].contains("H");
                            weaponTypeNode = genSpriteTree.initSpriteSheet(
                                    filenames.get(i)[2].equals("pc"),
                                    shieldbearer,
                                    horseman,
                                    UnitTemplate.valueOf(filenames.get(i)[0].toUpperCase()),
                                    WeaponType.valueOf(filenames.get(i)[1].toUpperCase().split("_")[0]));
                            loadSpriteSet(filenames.get(i)[0], weaponTypeNode, unitRegion, asm);
                        }
                    }
                }
            }
        }
    }

    private void loadSpriteSet(String portraitId, WeaponTypeNode node, TextureRegion unitRegion, AssetManager asm) {
        TextureRegion[][] spriteSheet16x8 = unitRegion.split(unitRegion.getRegionWidth()/16, unitRegion.getRegionHeight()/8);
        TextureRegion[][] spriteSheet8x8 = unitRegion.split(unitRegion.getRegionWidth()/8, unitRegion.getRegionHeight()/8);
        boolean footman = unitRegion.getRegionHeight() == unitRegion.getRegionWidth();


        //DONE SPRITE SETS

        Texture doneTexture = createDoneTexture(unitRegion, asm);
        TextureRegion unitDoneRegion = new TextureRegion(doneTexture);
        TextureRegion[][] spriteSheetDone16x8 = unitDoneRegion.split(unitDoneRegion.getRegionWidth() / 16, unitDoneRegion.getRegionHeight() / 8);
        TextureRegion[][] spriteSheetDone8x8 = unitDoneRegion.split(unitDoneRegion.getRegionWidth() / 8, unitDoneRegion.getRegionHeight() / 8);
        madeupTextures.add(doneTexture);

        node.populate(true, SpriteSetId.REST, false, true, new TextureRegion[]{
                spriteSheetDone16x8[0][0],
                spriteSheetDone16x8[0][1],
                spriteSheetDone16x8[0][2]

        });
        node.populate(false, SpriteSetId.REST, false, true, new TextureRegion[]{
                spriteSheetDone16x8[1][0],
                spriteSheetDone16x8[1][1],
                spriteSheetDone16x8[1][2]

        });
        node.populate(true, SpriteSetId.REST, true, true, new TextureRegion[]{
                (footman) ? spriteSheetDone8x8[2][0] : spriteSheetDone16x8[2][0],
                (footman) ? spriteSheetDone8x8[2][1] : spriteSheetDone16x8[2][1],
                (footman) ? spriteSheetDone8x8[2][2] : spriteSheetDone16x8[2][2]
        });
        node.populate(false, SpriteSetId.REST, true, true, new TextureRegion[]{
                (footman) ? spriteSheetDone8x8[3][0] : spriteSheetDone16x8[3][0],
                (footman) ? spriteSheetDone8x8[3][1] : spriteSheetDone16x8[3][1],
                (footman) ? spriteSheetDone8x8[3][2] : spriteSheetDone16x8[3][2]
        });



        // PORTRAIT

        portraits.put(portraitId, (footman) ?  spriteSheet8x8[7][7]: spriteSheet16x8[7][15]);


        // ACTIVE SPRITE SETS

        //ROW 0
        node.populate(true, SpriteSetId.REST, false, false, new TextureRegion[]{
                spriteSheet16x8[0][0],
                spriteSheet16x8[0][1],
                spriteSheet16x8[0][2]

        });
        node.populate(false, SpriteSetId.REST, false, false, new TextureRegion[]{
                spriteSheet16x8[1][0],
                spriteSheet16x8[1][1],
                spriteSheet16x8[1][2]

        });
        node.populate(true, SpriteSetId.HEAL, new TextureRegion[]{
                spriteSheet16x8[0][3],
                spriteSheet16x8[0][4]

        });
        node.populate(false, SpriteSetId.HEAL, new TextureRegion[]{
                spriteSheet16x8[1][3],
                spriteSheet16x8[1][4]

        });
        node.populate(true, SpriteSetId.WALK_FLEE_SWITCHPOSITION, new TextureRegion[]{
                spriteSheet16x8[0][5],
                spriteSheet16x8[0][6],
                spriteSheet16x8[0][7]

        });
        node.populate(false, SpriteSetId.WALK_FLEE_SWITCHPOSITION, new TextureRegion[]{
                spriteSheet16x8[1][5],
                spriteSheet16x8[1][6],
                spriteSheet16x8[1][7]

        });
        node.populate(true, SpriteSetId.BACKSTABED_PUSHED, new TextureRegion[]{
                spriteSheet16x8[0][8]
        });
        node.populate(false, SpriteSetId.BACKSTABED_PUSHED, new TextureRegion[]{
                spriteSheet16x8[1][8]
        });
        node.populate(true, SpriteSetId.TAKE_HIT, new TextureRegion[]{
                spriteSheet16x8[0][9]
        });
        node.populate(false, SpriteSetId.TAKE_HIT, new TextureRegion[]{
                spriteSheet16x8[1][9]
        });
        node.populate(true, SpriteSetId.BUILD, new TextureRegion[]{
                (footman) ?  spriteSheet8x8[0][5] : spriteSheet16x8[0][13],
                (footman) ?  spriteSheet8x8[0][6] : spriteSheet16x8[0][14],
                (footman) ?  spriteSheet8x8[0][7] : spriteSheet16x8[0][15]
        });
        node.populate(false, SpriteSetId.BUILD, new TextureRegion[]{
                (footman) ?  spriteSheet8x8[1][5] : spriteSheet16x8[1][13],
                (footman) ?  spriteSheet8x8[1][6] : spriteSheet16x8[1][14],
                (footman) ?  spriteSheet8x8[1][7] : spriteSheet16x8[1][15]
        });

        // ROW 1
        node.populate(true, SpriteSetId.REST, true, false, new TextureRegion[]{
                (footman) ?  spriteSheet8x8[2][0] : spriteSheet16x8[2][0],
                (footman) ?  spriteSheet8x8[2][1] : spriteSheet16x8[2][1],
                (footman) ?  spriteSheet8x8[2][2] : spriteSheet16x8[2][2]
        });
        node.populate(false, SpriteSetId.REST, true, false, new TextureRegion[]{
                (footman) ?  spriteSheet8x8[3][0] : spriteSheet16x8[3][0],
                (footman) ?  spriteSheet8x8[3][1] : spriteSheet16x8[3][1],
                (footman) ?  spriteSheet8x8[3][2] : spriteSheet16x8[3][2]
        });
        node.populate(true, SpriteSetId.REGULAR_ATTACK, new TextureRegion[]{
                (footman) ?  spriteSheet8x8[2][3] : spriteSheet16x8[2][3],
                (footman) ?  spriteSheet8x8[2][4] : spriteSheet16x8[2][4],
                (footman) ?  spriteSheet8x8[2][5] : spriteSheet16x8[2][5],
                (footman) ?  spriteSheet8x8[2][6] : spriteSheet16x8[2][6]
        });
        node.populate(false, SpriteSetId.REGULAR_ATTACK, new TextureRegion[]{
                (footman) ?  spriteSheet8x8[3][3] : spriteSheet16x8[3][3],
                (footman) ?  spriteSheet8x8[3][4] : spriteSheet16x8[3][4],
                (footman) ?  spriteSheet8x8[3][5] : spriteSheet16x8[3][5],
                (footman) ?  spriteSheet8x8[3][6] : spriteSheet16x8[3][6]
        });
        node.populate(true, SpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED, new TextureRegion[]{
                spriteSheet16x8[2][14],
                spriteSheet16x8[2][15]
        });
        node.populate(false, SpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED, new TextureRegion[]{
                spriteSheet16x8[3][14],
                spriteSheet16x8[3][15]
        });

        //ROW 2
        node.populate(true, SpriteSetId.DIE, new TextureRegion[]{
                spriteSheet16x8[4][0],
                (footman) ?  spriteSheet8x8[4][1] : spriteSheet16x8[4][2],
                (footman) ?  spriteSheet8x8[4][2] : spriteSheet16x8[4][3],
        });
        node.populate(false, SpriteSetId.DIE, new TextureRegion[]{
                spriteSheet16x8[5][0],
                (footman) ?  spriteSheet8x8[5][1] : spriteSheet16x8[5][2],
                (footman) ?  spriteSheet8x8[5][2] : spriteSheet16x8[5][3]
        });
        node.populate(true, SpriteSetId.PUSH, new TextureRegion[]{
                spriteSheet16x8[4][1],
                (footman) ?  spriteSheet8x8[4][3] : spriteSheet16x8[4][4],
                (footman) ?  spriteSheet8x8[4][4] : spriteSheet16x8[4][5],
        });
        node.populate(false, SpriteSetId.PUSH, new TextureRegion[]{
                spriteSheet16x8[5][1],
                (footman) ?  spriteSheet8x8[5][3] : spriteSheet16x8[5][4],
                (footman) ?  spriteSheet8x8[5][4] : spriteSheet16x8[5][5]
        });
        node.populate(true, SpriteSetId.STEAL, new TextureRegion[]{
                (footman) ?  spriteSheet8x8[4][5] : spriteSheet16x8[4][6],
                (footman) ?  spriteSheet8x8[4][6] : spriteSheet16x8[4][7],
                (footman) ?  spriteSheet8x8[4][7] : spriteSheet16x8[4][8],
        });
        node.populate(false, SpriteSetId.STEAL, new TextureRegion[]{
                (footman) ?  spriteSheet8x8[5][5] : spriteSheet16x8[5][6],
                (footman) ?  spriteSheet8x8[5][6] : spriteSheet16x8[5][7],
                (footman) ?  spriteSheet8x8[5][7] : spriteSheet16x8[5][8]
        });

        //ROW 3
        node.populate(true, SpriteSetId.DODGE, new TextureRegion[]{
                (footman) ?  spriteSheet8x8[6][0] : spriteSheet16x8[6][0],
                (footman) ?  spriteSheet8x8[6][1] : spriteSheet16x8[6][1]
        });
        node.populate(false, SpriteSetId.DODGE, new TextureRegion[]{
                (footman) ?  spriteSheet8x8[7][0] : spriteSheet16x8[7][0],
                (footman) ?  spriteSheet8x8[7][1] : spriteSheet16x8[7][1]
        });
        node.populate(true, SpriteSetId.SPECIAL_MOVE, new TextureRegion[]{
                (footman) ?  spriteSheet8x8[6][2] : spriteSheet16x8[6][2],
                (footman) ?  spriteSheet8x8[6][3] : spriteSheet16x8[6][3],
                (footman) ?  spriteSheet8x8[6][4] : spriteSheet16x8[6][4],
                (footman) ?  spriteSheet8x8[6][5] : spriteSheet16x8[6][5]
        });
        node.populate(false, SpriteSetId.SPECIAL_MOVE, new TextureRegion[]{
                (footman) ?  spriteSheet8x8[7][2] : spriteSheet16x8[7][2],
                (footman) ?  spriteSheet8x8[7][3] : spriteSheet16x8[7][3],
                (footman) ?  spriteSheet8x8[7][4] : spriteSheet16x8[7][4],
                (footman) ?  spriteSheet8x8[7][5] : spriteSheet16x8[7][5]
        });

    }

    private Texture createDoneTexture(TextureRegion unitRegion, AssetManager asm) {
        final Texture originalTexture = unitRegion.getTexture();
        originalTexture.getTextureData().prepare();
        Pixmap pixmap = originalTexture.getTextureData().consumePixmap();
        int colorInt;
        int average;
        int[] colorArray;
        for(int r = 0; r < pixmap.getHeight(); r++){
            for(int c = 0; c < pixmap.getWidth(); c++){
                colorInt = pixmap.getPixel(r, c);
                colorArray = Utils.getRGBA(colorInt);
                average = (colorArray[0] + colorArray[1] + colorArray[2])/3;
                colorInt = Utils.getColor32Bits(average, average, average, colorArray[3]);
                pixmap.drawPixel(r, c, colorInt);
            }
        }
        pixmap.getPixels().rewind();
        Texture madeupTexture = new Texture(pixmap);
        pixmap.dispose();
        return madeupTexture;
    }

    private Array<String[]> getFileNames(Battlefield battlefield) {
        Array<String[]> filenames = new Array<String[]>();
        String[] filename;
        for(int r = 0; r < battlefield.getNbRows(); r++){
            for (int c = 0; c < battlefield.getNbColumns(); c++) {
                if (battlefield.isTileOccupied(r, c)) {
                    IUnit unit = battlefield.getUnit(r, c);

                    filename = new String[3];
                    filename[0] = unit.getTemplate().name().toLowerCase();
                    filename[1] = (unit.isCharacter()) ?
                            String.format("%s", unit.getWeaponType().name().toLowerCase()) :
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
                            filename[2] = "recruit";
                            filenames.add(filename);
                            filename = new String[3];
                            filename[0] = unit.getTemplate().name().toLowerCase();
                            filename[1] = unit.getWeaponType().name().toLowerCase();
                            filename[2] = "promoted";
                            filenames.add(filename);
                        }else {
                            filenames.add(filename);
                        }
                    }

                }
            }
        }
        return filenames;
    }

    @Override
    public void dispose() {
        for(int i = 0; i < madeupTextures.size; i++){
            madeupTextures.get(i).dispose();
        }
    }








    // ------------------ SPRITE TREE CLASS -------------------------


    public static class GenericUnitSpriteTree{
        private final Array<SideNode> children;


        GenericUnitSpriteTree(){
            this.children = new Array<SideNode>();
        }

        boolean isChildExist(boolean playerControlled){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).pc == playerControlled){
                    return true;
                }
            }
            return false;
        }

        SideNode getChild(boolean playerControlled){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).pc == playerControlled){
                    return children.get(i);
                }
            }
            return null;
        }

        void populate(boolean playerControlled,
                      boolean shield,
                      boolean horseman,
                      UnitTemplate template,
                      WeaponType type,
                      boolean east,
                      SpriteSetId spriteSetId,
                      boolean warchief,
                      boolean done,
                      TextureRegion[] spriteSet){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).pc == playerControlled){
                    children.get(i).populate(shield, horseman, template, type, east, spriteSetId, warchief, done, spriteSet);
                    return;
                }
            }
            SideNode node = new SideNode(shield);
            node.populate(shield, horseman, template, type, east, spriteSetId, warchief, done, spriteSet);
            children.add(node);
        }

        void populate(boolean playerControlled, boolean shield, boolean horseman, UnitTemplate template, WeaponType type, boolean east, SpriteSetId spriteSetId, TextureRegion[] spriteSet){
            populate(playerControlled, shield, horseman, template, type, east, spriteSetId, true, true, spriteSet);
        }


        WeaponTypeNode initSpriteSheet(boolean playerControlled, boolean shield, boolean horseman, UnitTemplate template, WeaponType type){

            SideNode sideNode;
            ShieldNode shieldNode;
            HorsmanNode horsemanNode;
            UnitTemplateNode templateNode;
            WeaponTypeNode weaponTypeNode;

            if(isChildExist(playerControlled)){
                sideNode = getChild(playerControlled);
            }else{
                sideNode = new SideNode(playerControlled);
                children.add(sideNode);
            }

            if(sideNode.isChildExist(shield)){
                shieldNode = sideNode.getChild(shield);
            }else{
                shieldNode = new ShieldNode(shield);
                sideNode.children.add(shieldNode);
            }

            if(shieldNode.isChildExist(horseman)){
                horsemanNode = shieldNode.getChild(horseman);
            }else{
                horsemanNode = new HorsmanNode(horseman);
                shieldNode.children.add(horsemanNode);
            }

            if(horsemanNode.isChildExist(template)){
                templateNode = horsemanNode.getChild(template);
            }else{
                templateNode = new UnitTemplateNode(template);
                horsemanNode.children.add(templateNode);
            }

            if(templateNode.isChildExist(type)){
                weaponTypeNode = templateNode.getChild(type);
            }else{
                weaponTypeNode = new WeaponTypeNode(type);
                templateNode.children.add(weaponTypeNode);
            }

            return weaponTypeNode;
        }


        public Array<Sprite> getSpriteSet(boolean playerControlled, boolean shield, boolean horseman, UnitTemplate template, WeaponType type, Orientation or, boolean warchief, boolean done, SpriteSetId id){
            Array<Sprite> spriteset;
            TextureRegion[] tra = null;
            for(int i = 0; i < children.size; i++){
                if(children.get(i).pc == playerControlled){
                    tra = children.get(i).getSpriteSet(shield, horseman, template, type, or, warchief, done, id);
                }
            }
            if(tra == null) {
                spriteset = getSpriteSet(playerControlled, or, warchief, id);
            }else {
                spriteset = new Array<Sprite>();
                Sprite sprite;
                float spriteWidth;
                for (int i = 0; i < tra.length; i++) {
                    sprite = new Sprite(tra[i]);
                    spriteWidth = (tra[i].getRegionWidth() != tra[i].getRegionHeight()) ? IsoBFR.SPRITE_STD_SIZE / 2f : IsoBFR.SPRITE_STD_SIZE;
                    sprite.setSize(spriteWidth, IsoBFR.SPRITE_STD_SIZE);
                    if (or == Orientation.WEST || or == Orientation.NORTH) {
                        sprite.flip(true, false);
                    }
                    spriteset.add(sprite);
                }
            }
            return spriteset;
        }

        private Array<Sprite> getSpriteSet(boolean playerControlled, Orientation or, boolean warchief, SpriteSetId id) {
            return getSpriteSet(playerControlled,false, false, UnitTemplate.SOLAR_KNIGHT, WeaponType.SWORD, or, warchief, false, id);
        }


        public String toString(){
            String str = "\nGENERIC UNIT TREE : ";
            for(int i = 0; i < children.size; i++){
                str += children.get(i).toString();
            }
            return str;
        }
    }

    public static class SideNode{
        private boolean pc;
        private final Array<ShieldNode> children;


        SideNode(boolean pc){
            this.pc = pc;
            this.children = new Array<ShieldNode>();
        }

        boolean isChildExist(boolean shield){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).shieldbearer == shield){
                    return true;
                }
            }
            return false;
        }

        ShieldNode getChild(boolean shield){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).shieldbearer == shield){
                    return children.get(i);
                }
            }
            return null;
        }

        void populate(boolean shield,
                             boolean horseman,
                             UnitTemplate template,
                             WeaponType type,
                             boolean east,
                             SpriteSetId spriteSetId,
                             boolean warchief,
                             boolean done,
                             TextureRegion[] spriteSet){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).shieldbearer == shield){
                    children.get(i).populate(horseman, template, type, east, spriteSetId, spriteSet, warchief, done);
                    return;
                }
            }
            ShieldNode node = new ShieldNode(shield);
            node.populate(horseman, template, type, east, spriteSetId, spriteSet, warchief, done);
            children.add(node);
        }

        void populate(boolean shield, boolean horseman, UnitTemplate template, WeaponType type, boolean east, SpriteSetId spriteSetId, TextureRegion[] spriteSet){
            populate(shield, horseman, template, type, east, spriteSetId, true, true, spriteSet);
        }

        public TextureRegion[] getSpriteSet(boolean shield, boolean horseman, UnitTemplate template, WeaponType type, Orientation or, boolean warchief, boolean done, SpriteSetId id){
            TextureRegion[] res = null;
            for(int i = 0; i < children.size; i++){
                if(children.get(i).shieldbearer == shield){
                    res = children.get(i).getSpriteSet(horseman, template, type, or, warchief, done, id);
                }
            }
            if(res == null) {
                res = getSpriteSet(or, warchief, id);
            }
            return res;
        }

        private TextureRegion[] getSpriteSet(Orientation or, boolean warchief, SpriteSetId id) {
            return getSpriteSet(false, false, UnitTemplate.SOLAR_KNIGHT, WeaponType.SWORD, or, warchief, false, id);
        }


        public String toString(){
            String str = "\nPC ? : "+pc;
            for(int i = 0; i < children.size; i++){
                str += children.get(i).toString();
            }
            return str;
        }
    }

    static class ShieldNode {
        private final boolean shieldbearer;
        private final Array<HorsmanNode> children;


        public ShieldNode(boolean shieldbearer){
            this.shieldbearer = shieldbearer;
            this.children = new Array<HorsmanNode>();
        }

        public boolean isChildExist(boolean horseman){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).horseman == horseman){
                    return true;
                }
            }
            return false;
        }

        public HorsmanNode getChild(boolean horseman){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).horseman == horseman){
                    return children.get(i);
                }
            }
            return null;
        }

        public void populate(boolean horseman,
                             UnitTemplate template,
                             WeaponType type,
                             boolean east,
                             SpriteSetId id,
                             TextureRegion[] spriteSet,
                             boolean warchief,
                             boolean done){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).horseman == horseman){
                    children.get(i).populate(template, type, east, id, spriteSet, warchief, done);
                    return;
                }
            }
            HorsmanNode node = new HorsmanNode(horseman);
            node.populate(template, type, east, id, spriteSet, warchief, done);
            children.add(node);
        }

        public TextureRegion[] getSpriteSet(boolean horseman, UnitTemplate template, WeaponType type, Orientation or, boolean warchief, boolean done, SpriteSetId id){
            TextureRegion[] res = null;
            for(int i = 0; i < children.size; i++){
                if(children.get(i).horseman == horseman){
                    res = children.get(i).getSpriteSet(template, type, or, warchief, done, id);
                }
            }
            return res;
        }

        public String toString(){
            String str = "\n| SHIED Node : "+ shieldbearer;
            for(int i = 0; i < children.size; i++){
                str += children.get(i).toString();
            }
            return str;
        }

    }

    static class HorsmanNode {
        private final boolean horseman;
        private final Array<UnitTemplateNode> children;


        public HorsmanNode(boolean horseman){
            this.horseman = horseman;
            this.children = new Array<UnitTemplateNode>();
        }

        public boolean isChildExist(UnitTemplate template){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).template == template){
                    return true;
                }
            }
            return false;
        }

        public UnitTemplateNode getChild(UnitTemplate template){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).template == template){
                    return children.get(i);
                }
            }
            return null;
        }

        public void populate(UnitTemplate template,
                             WeaponType type,
                             boolean east,
                             SpriteSetId id,
                             TextureRegion[] spriteSet,
                             boolean warchief,
                             boolean done){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).template == template){
                    children.get(i).populate(type, east, id, spriteSet, warchief, done);
                    return;
                }
            }
            UnitTemplateNode node = new UnitTemplateNode(template);
            node.populate(type, east, id, spriteSet, warchief, done);
            children.add(node);
        }

        public TextureRegion[] getSpriteSet(UnitTemplate template, WeaponType type, Orientation or, boolean warchief, boolean done,SpriteSetId id){
            TextureRegion[] res = null;
            for(int i = 0; i < children.size; i++){
                if(children.get(i).template == template){
                    res = children.get(i).getSpriteSet(type, or, warchief, done, id);
                }
            }
            return res;
        }

        public String toString(){
            String str = "\n|| Horseman Node : "+ horseman;
            for(int i = 0; i < children.size;i++){
                str += children.get(i).toString();
            }
            return str;
        }

    }


    public static class CharacterSpriteTree{
        private final Array<PromotedNode> children;

        CharacterSpriteTree(){
            this.children = new Array<PromotedNode>();
        }

        boolean isChildExist(boolean promoted){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).promoted == promoted){
                    return true;
                }
            }
            return false;
        }

        PromotedNode getChild(boolean promoted){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).promoted == promoted){
                    return children.get(i);
                }
            }
            return null;
        }

        WeaponTypeNode initSpriteSheet(boolean promoted, UnitTemplate template, WeaponType type){

            PromotedNode promotedNode;
            UnitTemplateNode templateNode;
            WeaponTypeNode weaponTypeNode;


            if(isChildExist(promoted)){
                promotedNode = getChild(promoted);
            }else{
                promotedNode = new PromotedNode(promoted);
                children.add(promotedNode);
            }

            if(promotedNode.isChildExist(template)){
                templateNode = promotedNode.getChild(template);
            }else{
                templateNode = new UnitTemplateNode(template);
                promotedNode.children.add(templateNode);
            }

            if(templateNode.isChildExist(type)){
                weaponTypeNode = templateNode.getChild(type);
            }else{
                weaponTypeNode = new WeaponTypeNode(type);
                templateNode.children.add(weaponTypeNode);
            }

            return weaponTypeNode;
        }


        void populate(
                boolean promoted,
                UnitTemplate template,
                WeaponType type,
                boolean east,
                SpriteSetId id,
                boolean warchief,
                boolean done,
                TextureRegion[] spriteSet){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).promoted == promoted){
                    children.get(i).populate(template, type, east, id, spriteSet, warchief, done);
                    return;
                }
            }
            PromotedNode node = new PromotedNode(promoted);
            node.populate(template, type, east, id, spriteSet, warchief, done);
            children.add(node);
        }

        void populate(boolean promoted, UnitTemplate template, WeaponType type, boolean east, SpriteSetId id, TextureRegion[] spriteSet){
            populate(promoted, template, type, east, id, false, false, spriteSet);
        }

        public Array<Sprite> getSpriteSet(boolean promoted, UnitTemplate template, WeaponType type, Orientation or, boolean warchief, boolean done, SpriteSetId id){

            Array<Sprite> spriteset;
            TextureRegion[] tra = null;
            for(int i = 0; i < children.size; i++){
                if(children.get(i).promoted == promoted){
                    tra = children.get(i).getSpriteSet(template, type, or, warchief, done, id);
                }
            }
            if(tra == null) {
                spriteset = getSpriteSet(or, warchief, id);
            }else {
                spriteset = new Array<Sprite>();
                Sprite sprite;
                float spriteWidth;
                for (int i = 0; i < tra.length; i++) {
                    sprite = new Sprite(tra[i]);
                    spriteWidth = (tra[i].getRegionWidth() != tra[i].getRegionHeight()) ? IsoBFR.SPRITE_STD_SIZE / 2f : IsoBFR.SPRITE_STD_SIZE;
                    sprite.setSize(spriteWidth, IsoBFR.SPRITE_STD_SIZE);
                    if (or == Orientation.WEST || or == Orientation.NORTH) {
                        sprite.flip(true, false);
                    }
                    spriteset.add(sprite);
                }
            }
            return spriteset;
        }

        private Array<Sprite> getSpriteSet(Orientation or, boolean warchief, SpriteSetId id) {
            return getSpriteSet(false, UnitTemplate.SOLAIRE, WeaponType.SWORD, or, warchief, false, id);
        }

        public String toString(){
            String str = "\nCHARAC TREE : ";
            for(int i = 0; i < children.size;i++){
                str += children.get(i).toString();
            }
            return str;
        }
    }


    static class PromotedNode{
        private final boolean promoted;
        private final Array<UnitTemplateNode> children;


        public PromotedNode(boolean promoted){
            this.promoted = promoted;
            this.children = new Array<UnitTemplateNode>();
        }

        public boolean isChildExist(UnitTemplate template){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).template == template){
                    return true;
                }
            }
            return false;
        }

        public UnitTemplateNode getChild(UnitTemplate template){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).template == template){
                    return children.get(i);
                }
            }
            return null;
        }

        public void populate(UnitTemplate template, WeaponType type, boolean east, SpriteSetId id, TextureRegion[] spriteSet, boolean warchief, boolean done){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).template == template){
                    children.get(i).populate(type, east, id, spriteSet, warchief, done);
                    return;
                }
            }
            UnitTemplateNode node = new UnitTemplateNode(template);
            node.populate(type, east, id, spriteSet, warchief, done);
            children.add(node);
        }

        public TextureRegion[] getSpriteSet(UnitTemplate template, WeaponType type, Orientation or, boolean warchief, boolean done, SpriteSetId id){
            TextureRegion[] res = null;
            for(int i = 0; i < children.size; i++){
                if(children.get(i).template == template){
                    res = children.get(i).getSpriteSet(type, or, warchief, done, id);
                }
            }
            return res;
        }

        public String toString(){
            String str = "\n|| Promoted Node : "+promoted;
            for(int i = 0; i < children.size;i++){
                str += children.get(i).toString();
            }
            return str;
        }

    }

    static class UnitTemplateNode{
        private final UnitTemplate template;
        private final Array<WeaponTypeNode> children;


        public UnitTemplateNode(UnitTemplate template){
            this.template = template;
            this.children = new Array<WeaponTypeNode>();
        }

        public boolean isChildExist(WeaponType weaponType){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).type == weaponType){
                    return true;
                }
            }
            return false;
        }

        public WeaponTypeNode getChild(WeaponType type){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).type == type){
                    return children.get(i);
                }
            }
            return null;
        }

        public void populate(WeaponType type, boolean east, SpriteSetId id, TextureRegion[] spriteSet, boolean warchief, boolean done){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).type == type){
                    children.get(i).populate(east, id, warchief, done, spriteSet);
                    return;
                }
            }
            WeaponTypeNode node = new WeaponTypeNode(type);
            node.populate(east, id, warchief, done, spriteSet);
            children.add(node);
        }

        public TextureRegion[] getSpriteSet(WeaponType type, Orientation or, boolean warchief, boolean done, SpriteSetId id){
            TextureRegion[] res = null;
            for(int i = 0; i < children.size; i++){
                if(children.get(i).type == type){
                    res = children.get(i).getSpriteSet(or, id, warchief, done);
                }
            }
            return res;
        }

        public String toString(){
            String str = "\n||| Template Node : "+template.name();
            for(int i = 0; i < children.size;i++){
                str += children.get(i).toString();
            }
            return str;
        }
    }

    static class WeaponTypeNode{
        private final WeaponType type;
        private final Array<OrientationNode> children;

        public WeaponTypeNode(WeaponType type){
            this.type = type;
            this.children = new Array<OrientationNode>();
        }

        public boolean isChildExist(boolean east){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).east == east){
                    return true;
                }
            }
            return false;
        }

        public void populate(boolean east, SpriteSetId id, boolean warchief, boolean done, TextureRegion[] spriteSet){
            for(int i = 0; i < children.size; i++){
                if(children.get(i).east == east){
                    children.get(i).populate(id, spriteSet, warchief, done);
                    return;
                }
            }
            OrientationNode node = new OrientationNode(east);
            node.populate(id, spriteSet, warchief, done);
            children.add(node);
        }

        public void populate(boolean east, SpriteSetId id, TextureRegion[] spriteSet){
            populate(east, id, false, false, spriteSet);
        }

        public TextureRegion[] getSpriteSet(Orientation or, SpriteSetId id, boolean warchief, boolean done){
            TextureRegion[] res = null;
            for(int i = 0; i < children.size; i++){
                if(children.get(i).isCorrect(or)){
                    res = children.get(i).getSpriteSet(id, warchief, done);
                }
            }
            return res;
        }

        public String toString(){
            String str = "\n|||| Template Node : "+type.name();
            for(int i = 0; i < children.size;i++){
                str += children.get(i).toString();
            }
            return str;
        }
    }

    static class OrientationNode{
        private final boolean east;
        private final Array<SpriteSetLeaf> spriteSetLeaves;

        public OrientationNode(boolean east){
            this.east = east;
            this.spriteSetLeaves = new Array<SpriteSetLeaf>();
        }

        public boolean isChildExist(SpriteSetId id, boolean warchief, boolean done){
            for(int i = 0; i < spriteSetLeaves.size; i++){
                if(spriteSetLeaves.get(i).isCorrect(id, warchief, done)){
                    return true;
                }
            }
            return false;
        }

        public boolean isCorrect(Orientation or){
            return ((or == Orientation.NORTH || or == Orientation.EAST) && east) || ((or == Orientation.SOUTH || or == Orientation.WEST) && !east);
        }

        public void populate(SpriteSetId id, TextureRegion[] spriteSet, boolean warchief, boolean done){
            for(int i = 0; i < spriteSetLeaves.size; i++){
                if(spriteSetLeaves.get(i).isCorrect(id, warchief, done)){
                    spriteSetLeaves.get(i).spriteset = spriteSet;
                    return;
                }
            }
            spriteSetLeaves.add(SpriteSetLeaf.create(id, spriteSet, warchief, done));
        }

        public TextureRegion[] getSpriteSet(SpriteSetId id, boolean warchief, boolean done) {
            TextureRegion[] res = null;
            for(int i = 0; i < spriteSetLeaves.size; i++){
                if(spriteSetLeaves.get(i).isCorrect(id, warchief, done)){
                    res = spriteSetLeaves.get(i).getSpriteSet();
                }
            }
            return res;
        }

        public String toString(){
            String str = "\n||||| Orientation Node : West? "+ east;
            for(int i = 0; i < spriteSetLeaves.size;i++){
                str += spriteSetLeaves.get(i).toString();
            }
            return str;
        }
    }

    static class SpriteSetLeaf{
        protected final SpriteSetId id;
        protected TextureRegion[] spriteset;

        public SpriteSetLeaf(SpriteSetId id, TextureRegion[] spriteset) {
            this.id = id;
            this.spriteset = spriteset;
        }

        public static SpriteSetLeaf create(SpriteSetId id, TextureRegion[] spriteset, boolean warchief, boolean done){
            return (id == SpriteSetId.REST) ? new RestSpriteSetLeaf(spriteset, warchief, done) : new SpriteSetLeaf(id, spriteset);
        }

        public boolean isCorrect(SpriteSetId id, boolean warchief, boolean done){
            return this.id == id;
        }

        public TextureRegion[] getSpriteSet() {
            return spriteset;
        }

        public String toString(){
            return "\n|||||| Animation ID : "+id;
        }
    }

    static class RestSpriteSetLeaf extends SpriteSetLeaf {
        protected boolean warchief;
        protected boolean done;

        public RestSpriteSetLeaf(TextureRegion[] spriteset, boolean warchief, boolean done) {
            super(SpriteSetId.REST, spriteset);
            this.warchief = warchief;
            this.done = done;
        }

        public boolean isCorrect(SpriteSetId id, boolean warchief, boolean done){
            return this.id == id && this.warchief == warchief && this.done == done;
        }

        public String toString(){
            String str = "\n|||||| Animation ID : "+id;
            str += (warchief) ? " WC" : " _";
            str += (done) ? " done" : " _";
            return str;
        }

    }

}
