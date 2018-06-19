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

    public static String getRegionTile(Props.TileType tileType) {
        return tileType.name().toLowerCase();
    }

    public enum UIAssetId{
        SELECTED_AREA_SELECTED_UNIT,
        SELECTED_AREA_MOVE_RANGE,
        SELECTED_AREA_ATTACK_RANGE,
        SELECTED_AREA_BANNER,
        SELECTED_AREA_SAME_SQUAD,
        SELECTED_AREA_SAME_ARMY,
        SELECTED_AREA_ALLY,
        SELECTED_AREA_FOE,
        SELECTED_AREA_DEPLOYMENT,
        BLACK_BACKGROUND
    }

    public static String getRegionUI(UIAssetId id){
        return id.name().toLowerCase();
    }

    public static final String REGION_UNIT_SPRITES = "unit_sprites_tempo";

}
