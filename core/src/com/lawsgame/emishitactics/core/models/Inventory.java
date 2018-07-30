package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;

public class Inventory {
    private Array<Equipment> storedItems;

    public Inventory(){
        storedItems = new Array<Equipment>();
    }

    public void storeItem(Equipment item){
        storedItems.add(item);
    }

    public void clearInventory(){
        storedItems.clear();
    }

    public Array<Equipment> getStoredItems() {
        return storedItems;
    }
}
