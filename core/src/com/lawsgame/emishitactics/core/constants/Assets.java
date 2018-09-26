package com.lawsgame.emishitactics.core.constants;

import com.lawsgame.emishitactics.core.models.Data.AreaType;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.Data.SpriteSetId;

public class Assets {

    // STRING

    public static final String STRING_BUNDLE_MAIN = "string/MainBundle";

    //XML

    public static final String XML_NAME_DB = "xml/name_db.xml";
    public static final String XML_BATTLE_PARAMS = "xml/battle_params.xml";


    //SKIN

    public static final String SKIN_UI = "skin/ui_skin.json";


    //TEXTURES

    // atlases

    public static final String ATLAS_TEMPO_UNITS = "textures/tempo/unit_sprites.pack";
    public static final String ATLAS_TEMPO_UI = "textures/tempo/ui_skin.pack";
    public static final String ATLAS_TEMPO_TILES = "textures/tempo/tiles.pack";
    public static final String ATLAS_MAPS = "textures/maps.pack";
    public static final String ATLAS_TILES = "textures/tiles.pack";
    public static final String UNIT_SPRITES_DIR = "textures/units";


    // regions

    public static String getRegionMap(int id) {
        return "map"+id;
    }
    public static String getRegionTile(TileType tileType) { return tileType.name().toLowerCase(); }
    public static String getTileHighlighted(AreaType id){
        return id.name().toLowerCase();
    }

    public static String getRegionUnitAction(SpriteSetId id, boolean east){
        return id.name().split("_")[0].toLowerCase()+ "_" +((east)? "east" : "south");
    }

    public static final String REGION_TERRAINS_UNDEFINED = "undefined";
    public static final String REGION_TEMPO_UNIT_SPRITES = "unit_sprites_tempo";
    public static final String REGION_UNIT_PORTRAIT = "portrait";

    public static final String UI_BLACK_BACKGROUND = "black_background";
    public static final String UI_BUTTON_UP = "button_up";
    public static final String UI_BUTTON_DOWN = "button_down";


    //FONT

    public static final String FONT_MAIN = "fonts/main_text_font.fnt";
    public static final String FONT_ITALIC_MAIN = "fonts/italic_main_text_font.fnt";
    public static final String FONT_GREEN_MAIN = "fonts/green_text_font.fnt";
    public static final String FONT_UI = "fonts/ui_font.fnt";


    //SHADERS
}
