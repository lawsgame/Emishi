package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.interfaces.Item;

public class Inventory {
    private Array<Item> storedItems;

    public Inventory(){
        storedItems = new Array<Item>();
    }


    //------------------ PROCESS --------------------------------

    public void storeItem(Item item){
        storedItems.add(item);
    }

    public void clearInventory(){
        storedItems.clear();
    }


    // ---------------- GETTERS & SETTERS ----------------------

    public Array<Item> getStoredItems() {
        return storedItems;
    }
}
