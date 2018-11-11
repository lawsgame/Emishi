package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.models.interfaces.Model;

/**
 * Immutable class
 */
public class Tile extends Model{

    protected TileType type;
    protected boolean fragile;

    public Tile(TileType type){
        this.type = type;
        this.fragile = false;
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

    public String toString(){
        return type.name();
    }

    public boolean isFragile() {
        return fragile;
    }

    public void setFragile(boolean fragile) {
        this.fragile = fragile;
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
