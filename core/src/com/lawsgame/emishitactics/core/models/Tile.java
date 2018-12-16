package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.math.MathUtils;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.models.interfaces.Model;

/**
 * Immutable class
 */
public class Tile extends Model{

    protected TileType type;
    protected Item loot;
    protected Unit recruit;
    protected boolean fragile;
    protected boolean revealed;

    public Tile(TileType type){
        this.type = type;
        this.fragile = false;
        this.revealed = false;
        this.loot = null;
        this.recruit = null;
    }

    //--------------- GETTERS & SETTERS ---------------------

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public boolean isFragile() { return fragile; }

    public void setFragile(boolean fragile) {
        this.fragile = fragile;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isLooted() {
        return loot == null;
    }

    public Item getLoot(){
        return loot;
    }

    public boolean isHidingRecruit(){
        return recruit != null;
    }

    public Unit getRecruit(){
        return recruit;
    }

    public String toString(){
        return type.name();
    }
}
