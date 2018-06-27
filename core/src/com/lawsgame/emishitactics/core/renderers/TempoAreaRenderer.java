package com.lawsgame.emishitactics.core.renderers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.renderers.interfaces.AreaRenderer;

import java.util.HashMap;

public class TempoAreaRenderer implements AreaRenderer {
    protected Battlefield battlefield;

    protected Array<int[]> tiles;                   // coordinates of the highlighted tiles
    private Assets.TileHighligthingAssetsId id;     // the id of type of sprite to use
    protected Array<float[]> spriteCoords;          // coordinates to where render the used sprites => 4 sprites = 1 tile
    protected Array<Sprite> spriteRefs;             // sprites to render associated with the coordinates above


    public TempoAreaRenderer(AssetManager asm, Battlefield bf,  Assets.TileHighligthingAssetsId id){
        this.battlefield = bf;
        this.id = id;
        this.spriteCoords = new Array<float[]>();
        this.spriteRefs = new Array<Sprite>();
        this.tiles = new Array<int[]>();
        build(asm);

    }

    public TempoAreaRenderer(AssetManager asm, Battlefield bf, Assets.TileHighligthingAssetsId id, Array<int[]> tiles){
        this(asm, bf, id);
        for(int i = 0; i < tiles.size; i++){
            if(tiles.get(i).length >= 2){
                if(bf.isTileExisted(tiles.get(i)[0], tiles.get(i)[1])){
                    this.tiles.add(new int[]{tiles.get(i)[0], tiles.get(i)[1]});
                }
            }
        }
        build(asm);

    }

    public TempoAreaRenderer(AssetManager asm, Assets.TileHighligthingAssetsId id, Battlefield bf, int rCenter, int cCenter, int rangeMin, int rangeMax){
        this(asm, bf, id, Utils.getEreaFromRange(bf, rCenter, cCenter, rangeMin, rangeMax));
    }

    @Override
    public void reset(){

        tiles.clear();
        spriteCoords.clear();
        spriteRefs.clear();
    }

    @Override
    public void addTile(int r, int c){
        if(battlefield.isTileExisted(r, c)){
            this.tiles.add(new int[]{r, c});
        }
        build();
    }

    @Override
    public void addTiles(Array<int[]> area){
        int r;
        int c;
        for(int i = 0; i <area.size; i++){
            if(area.get(i).length >= 2) {
                r = area.get(i)[0];
                c = area.get(i)[1];
                if (battlefield.isTileExisted(r, c)) {
                    this.tiles.add(new int[]{r, c});
                }
            }
        }
        build();
    }



    @Override
    public void render(SpriteBatch batch) {

        for (int i = 0; i < spriteCoords.size; i++) {
            spriteRefs.get(i).setCenterX(spriteCoords.get(i)[0]);
            spriteRefs.get(i).setCenterY(spriteCoords.get(i)[1]);
            spriteRefs.get(i).draw(batch);
        }

    }

    @Override
    public void update(float dt) {    }

    //-------------- BUILD METHOD AND TOOLS ------------------------


    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> TOP_LEFT_CORNER = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();
    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> TOP_RIGHT_CORNER = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();
    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> BOTTOM_LEFT_CORNER = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();
    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> BOTTOM_RIGHT_CORNER = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();
    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> MIDDLE = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();
    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> WEST_STRAIGHT = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();
    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> NORTH_STRAIGHT = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();
    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> SOUTH_STRAIGHT = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();
    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> EAST_STRAIGHT = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();
    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> TOP_LEFT_ANTICORNER = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();
    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> TOP_RIGHT_ANTICORNER = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();
    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> BOTTOM_LEFT_ANTICORNER = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();
    protected static HashMap<Assets.TileHighligthingAssetsId,Sprite> BOTTOM_RIGHT_ANTICORNER = new HashMap<Assets.TileHighligthingAssetsId, Sprite>();


    private void build(AssetManager asm){

        if(!TOP_LEFT_CORNER.containsKey(id)) {
            Sprite sprite;
            TextureAtlas atlas = asm.get(Assets.ATLAS_UI);
            TextureRegion region = atlas.findRegion(Assets.getTileHighlightingAsset(id));
            TextureRegion[][] assets = region.split(region.getRegionWidth()/2, region.getRegionHeight()/2);

            sprite = new Sprite(assets[0][0]);
            sprite.setSize(0.5f,0.5f);
            TOP_LEFT_CORNER.put(id, sprite);
            sprite = new Sprite(assets[0][0]);
            sprite.setSize(0.5f,0.5f);
            sprite.rotate90(true);
            TOP_RIGHT_CORNER.put(id, sprite);
            sprite = new Sprite(assets[0][0]);
            sprite.rotate90(false);
            sprite.setSize(0.5f,0.5f);
            BOTTOM_LEFT_CORNER.put(id, sprite);
            sprite = new Sprite(assets[0][0]);
            sprite.setSize(0.5f,0.5f);
            sprite.rotate90(true);
            sprite.rotate90(true);
            BOTTOM_RIGHT_CORNER.put(id, sprite);



            sprite = new Sprite(assets[1][0]);
            sprite.setSize(0.5f,0.5f);
            MIDDLE.put(id, sprite);

            sprite = new Sprite(assets[0][1]);
            sprite.setSize(0.5f,0.5f);
            WEST_STRAIGHT.put(id, sprite);
            sprite = new Sprite(assets[0][1]);
            sprite.setSize(0.5f,0.5f);
            sprite.rotate90(true);
            NORTH_STRAIGHT.put(id, sprite);
            sprite = new Sprite(assets[0][1]);
            sprite.rotate90(false);
            sprite.setSize(0.5f,0.5f);
            SOUTH_STRAIGHT.put(id, sprite);
            sprite = new Sprite(assets[0][1]);
            sprite.setSize(0.5f,0.5f);
            sprite.rotate90(true);
            sprite.rotate90(true);
            EAST_STRAIGHT.put(id, sprite);

            sprite = new Sprite(assets[1][1]);
            sprite.setSize(0.5f,0.5f);
            TOP_LEFT_ANTICORNER.put(id, sprite);
            sprite = new Sprite(assets[1][1]);
            sprite.setSize(0.5f,0.5f);
            sprite.rotate90(true);
            TOP_RIGHT_ANTICORNER.put(id, sprite);
            sprite = new Sprite(assets[1][1]);
            sprite.rotate90(false);
            sprite.setSize(0.5f,0.5f);
            BOTTOM_LEFT_ANTICORNER.put(id, sprite);
            sprite = new Sprite(assets[1][1]);
            sprite.setSize(0.5f,0.5f);
            sprite.rotate90(true);
            sprite.rotate90(true);
            BOTTOM_RIGHT_ANTICORNER.put(id, sprite);

        }

        build();

    }

    /**
     *
     * i ^
     *   |
     *   |
     * (r,c)---> j
     *
     */
    private void build(){
        if(tiles != null && tiles.size > 0) {

            // set up the buildmap
            boolean[][] buildmap = new boolean[battlefield.getNbRows()][battlefield.getNbColumns()];
            for(int r = 0; r < buildmap.length ; r++){
                for(int c = 0; c < buildmap[0].length ; c++){
                    //for each tile
                    buildmap[r][c] = Utils.arrayContains(tiles, r, c);
                }
            }

            for(int r = 0; r < buildmap.length ; r++){
                for(int c = 0; c < buildmap[0].length ; c++){
                    //for each tile to highlight
                    if(buildmap[r][c]){


                        boolean utc, rtc, otc, ltc, dtc;

                        // bottom left

                        spriteCoords.add(new float[]{c  + 0.25f, r + 0.25f});

                        otc = battlefield.checkIndexes(r-1, c-1) && buildmap[r-1][c-1];   // opposite tile covered
                        ltc = battlefield.checkIndexes(r, c-1) && buildmap[r][c-1];          // left ...
                        dtc = battlefield.checkIndexes(r-1, c) && buildmap[r-1][c];          // down ...

                        if(!ltc && !dtc){
                            spriteRefs.add(BOTTOM_LEFT_CORNER.get(id));
                        }else if(!(ltc && dtc)){
                            if(ltc) {
                                spriteRefs.add(SOUTH_STRAIGHT.get(id));
                            }else{
                                spriteRefs.add(WEST_STRAIGHT.get(id));
                            }
                        }else{
                            if(otc){
                                spriteRefs.add(MIDDLE.get(id));
                            }else{
                                spriteRefs.add(BOTTOM_LEFT_ANTICORNER.get(id));
                            }
                        }



                        // top left



                        spriteCoords.add(new float[]{c  + 0.25f, r + 0.75f});

                        otc = battlefield.checkIndexes(r+1, c-1) && buildmap[r+1][c-1];
                        ltc = battlefield.checkIndexes(r, c-1) && buildmap[r][c-1];
                        utc = battlefield.checkIndexes(r+1, c) && buildmap[r+1][c]; // up ...

                        if(!ltc && !utc){
                            spriteRefs.add(TOP_LEFT_CORNER.get(id));
                        }else if(!(ltc && utc)){
                            if(ltc) {
                                spriteRefs.add(NORTH_STRAIGHT.get(id));
                            }else{
                                spriteRefs.add(WEST_STRAIGHT.get(id));
                            }
                        }else{
                            if(otc){
                                spriteRefs.add(MIDDLE.get(id));
                            }else{
                                spriteRefs.add(TOP_LEFT_ANTICORNER.get(id));
                            }
                        }



                        // bottom right


                        spriteCoords.add(new float[]{c  + 0.75f, r + 0.25f});

                        otc = battlefield.checkIndexes(r-1, c+1) && buildmap[r-1][c+1]; // opposite tile covered
                        rtc = battlefield.checkIndexes(r, c+1) && buildmap[r][c+1]; // right ...
                        dtc = battlefield.checkIndexes(r-1, c) && buildmap[r-1][c]; // down ...

                        if(!rtc && !dtc){
                            spriteRefs.add(BOTTOM_RIGHT_CORNER.get(id));
                        }else if(!(rtc && dtc)){
                            if(rtc) {
                                spriteRefs.add(SOUTH_STRAIGHT.get(id));
                            }else{
                                spriteRefs.add(EAST_STRAIGHT.get(id));
                            }
                        }else{
                            if(otc){
                                spriteRefs.add(MIDDLE.get(id));
                            }else{
                                spriteRefs.add(BOTTOM_RIGHT_ANTICORNER.get(id));
                            }
                        }

                        // top right


                        spriteCoords.add(new float[]{c  + 0.75f, r + 0.75f});

                        otc = battlefield.checkIndexes(r+1, c+1) && buildmap[r+1][c+1]; // opposite tile covered
                        rtc = battlefield.checkIndexes(r, c+1) && buildmap[r][c+1]; // right ...
                        utc = battlefield.checkIndexes(r+1, c) && buildmap[r+1][c]; // up ...

                        if(!rtc && !utc){
                            spriteRefs.add(TOP_RIGHT_CORNER.get(id));
                        }else if(!(rtc && utc)){
                            if(rtc) {
                                spriteRefs.add(NORTH_STRAIGHT.get(id));
                            }else{
                                spriteRefs.add(EAST_STRAIGHT.get(id));
                            }
                        }else{
                            if(otc){
                                spriteRefs.add(MIDDLE.get(id));
                            }else{
                                spriteRefs.add(TOP_RIGHT_ANTICORNER.get(id));
                            }
                        }


                    }
                }
            }
        }
    }

}
