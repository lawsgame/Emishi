package com.lawsgame.emishitactics.core.constants;

public class Assets {

    //XML

    public static final String XML_NAME_DB = "xml/name_db.xml";
    public static final String XML_UNITS_DEPLOYMENT = "xml/units.xml";

    //TEXTURES

    // atlases

    public static String ATLAS_UNITS = "textures/unit_sprites.pack";
    public static String ATLAS_UI = "textures/ui_assets.pack";
    public static String ATLAS_MAPS = "textures/maps.pack";
    public static String ATLAS_TILES = "textures/tiles.pack";

    // regions

    public static final String getRegionMap(int id) {
        return "map"+id;
    }

    public static String getRegionTile(Data.TileType tileType) {
        return tileType.name().toLowerCase();
    }

    public enum HighlightedTile {
        SELECTED_UNIT,
        MOVE_RANGE,
        ACTION_RANGE,
        SAME_SQUAD,
        BANNER_RANGE,
        COVERING_FIRE,
        FOE_ACTION_AREA,
        DEPLOYMENT

    }

    public static String getTileHighlighted(HighlightedTile id){
        return id.name().toLowerCase();
    }
    public static String UI_BLACK_BACKGROUND = "black_background";

    public static final String REGION_UNIT_SPRITES = "unit_sprites_tempo";

    //FONT


}
