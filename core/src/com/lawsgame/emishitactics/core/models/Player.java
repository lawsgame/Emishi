package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.models.interfaces.IArmy;

public class Player {
    private Inventory inventory;
    private IArmy army;


    public Player(Inventory inventory, IArmy army) {
        this.inventory = inventory;
        this.army = army;
    }

    public Player() {
        this(new Inventory(), new Army(Data.Allegeance.ALLY, true));
    }


    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public IArmy getArmy() {
        return army;
    }

    public void setArmy(IArmy army) {
        this.army = army;
    }
}
