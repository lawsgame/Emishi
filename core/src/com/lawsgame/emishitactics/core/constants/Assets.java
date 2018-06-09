package com.lawsgame.emishitactics.core.constants;

public class Assets {

    //XML

    public static final String XML_NAME_DB = "xml/name_db.xml";
    public static final String XML_UNITS_DEPLOYMENT = "xml/units.xml";

    //TEXTURES

    public static String ATLAS_MAPS = "textures/maps.pack";
    public static String ATLAS_TILES = "textures/tiles.pack";

    public static final String getRegionMap(int id) {
        return "map"+id;
    }

    public static String getRegionTile(Props.TileType tileType) {
        return tileType.name().toLowerCase();
    }

}
