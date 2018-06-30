package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;

public class Inventory {
    private Array<Data.Item> storedItems;

    public Inventory(){
        storedItems = new Array<Data.Item>();
    }

    public void storeItem(Data.Item item){
        storedItems.add(item);
    }

    public void clearInventory(){
        storedItems.clear();
    }

    public Array<Data.Item> getStoredItems() {
        return storedItems;
    }
}
