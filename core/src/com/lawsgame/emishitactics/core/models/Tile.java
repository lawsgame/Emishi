package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.math.MathUtils;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.Unit;
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



    //--------------- PROCESS -------------------------------

    public boolean collapse(){
        return isFragile() && MathUtils.random() <= Data.CHANCE_OF_COLLAPSING;
    }

    public boolean isLooted() {
        return true;
    }

    public boolean isFragile() {
        return type.isDestructible() && fragile;
    }





    //--------------- GETTERS & SETTERS ---------------------

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public void setFragile(boolean fragile) {
        this.fragile = fragile;
    }

    public String toString(){
        return type.name();
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

    public static class RecruitTile extends LootableTile<Unit>{


        public RecruitTile(TileType type, Unit loot) {
            super(type, loot);
        }
    }

    public static class ItemType extends LootableTile<Item>{

        public ItemType(TileType type, Item loot) {
            super(type, loot);
        }
    }


}
