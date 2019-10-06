package com.lawsgame.emishitactics.core.constants;

import com.lawsgame.emishitactics.core.helpers.interfaces.SpriteProvider;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.AreaType;
import com.lawsgame.emishitactics.core.models.Data.AnimUnitSSId;

public class Assets {

    // ---***sss  PATH  $$$***----

    public static final String PROJECT_PATH = "com.lawsgame.emishitactics";

    // ---***$$$  STRING  $$$***---

    public static final String STRING_BUNDLE_MAIN = "string/MainBundle";


    // ---***$$$  XML  $$$***----

    public static final String XML_NAME_DB = "xml/name-db.xml";
    public static final String XML_BATTLE_PARAMS = "xml/battlefields-config.xml";


    //  ---***$$$  SKIN  $$$***----

    public static final String SKIN_UI = "skin/uiskin.json";


    //  ---***$$$  TEXTURE  $$$***----

    // atlases

    public static final String ATLAS_MAPS = "textures/maps.pack";
    public static final String ATLAS_BATTLE_ICONS = "textures/battle_icons.pack";
    public static final String TILE_SPRITES_DIR = "textures/tiles";
    public static final String UNIT_SPRITES_DIR = "textures/units";
    public static final String AREA_SPRITES_DIR = "textures/areas";


    // regions

    public static String getRegionMap(int id) {
        return "map"+id;
    }
    public static String getRegionTile(Data.TileSpriteSetId tileType) { return tileType.name().toLowerCase(); }
    public static String getTileHighlighted(AreaType id){ return id.name().toLowerCase(); }
    public static String getRegionUnit(AnimUnitSSId id, boolean east){ return id.name().split("_")[0].toLowerCase()+ "_" +((east)? "east" : "south"); }
    public static String getRegionUnit(Data.AnimUnitSSId id, boolean eastNorth, boolean warchief, SpriteProvider.Flavor flavor){
        return id.name().split("_")[0].toLowerCase()
                + ((eastNorth)? "_east" : "_south")
                + ((flavor == SpriteProvider.Flavor.NORMAL)? "" : "_"+flavor.name().toLowerCase())
                + ((warchief)? "_warchief" :"");
    }
    public static String getWindroseArrowTexture(boolean active, boolean north){ return String.format("arrow_%s_%s", (north) ? "north": "west", (active) ? "active": "inactive"); }
    public static String getSparkleTR(Data.SparkleType sparkleType) { return "sparkle_"+sparkleType.name().toLowerCase();}
    public static final String REGION_TERRAINS_UNDEFINED = "undefined";
    public static final String REGION_UNIT_PORTRAIT = "portrait";
    public static final String REGION_UNIT_GUARD_ICON = "guard_icon";
    public static final String REGION_UNIT_SHADOW = "shadow";
    public static final String UI_BLACK_BACKGROUND = "black_background";
    public static final String UI_BUTTON_UP = "button_up";
    public static final String UI_BUTTON_DOWN = "button_down";


    //  ---***$$$  FONT  $$$***----

    public static final String FONT_MAIN = "fonts/default_font.fnt";
    public static final String FONT_UI = "fonts/defaut_font_ui.fnt";



    //  ---***$$$  SHADER  $$$***----
}
