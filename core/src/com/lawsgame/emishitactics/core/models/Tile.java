package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;

import java.util.HashMap;

/**
 * Immutable class
 */
public class Tile {

    private static HashMap<TileType, Tile> tileTemplates = new HashMap<TileType, Tile>();

    protected TileType type;

    Tile(TileType type){
        this.type = type;
    }

    public static Tile get(TileType type){
        if(!tileTemplates.containsKey(type))
            tileTemplates.put(type, new Tile(type));
        return tileTemplates.get(type);
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public boolean isLooted() {
        return true;
    }



    //------------- SUB CLASS ---------------------


    public static class LootableTile<T> extends Tile{
        protected boolean looted;
        protected T loot;

        public LootableTile(TileType type, T loot) {
            super(type);
            this.loot = loot;
            this.looted = loot == null;
        }

        public void setLoot(T loot){
            this.looted = false;
            this.loot = loot;
        }

        public T getLoot(){
            this.looted = true;
            return loot;
        }
    }

    public static class RecruitTile extends LootableTile<IUnit>{


        public RecruitTile(TileType type, IUnit loot) {
            super(type, loot);
        }
    }

    public static class ItemType extends LootableTile<Item>{

        public ItemType(TileType type, Item loot) {
            super(type, loot);
        }
    }


}
