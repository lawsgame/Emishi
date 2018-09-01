package com.lawsgame.emishitactics.core.models;

public class Player {
    private Inventory inventory;


    public Player(Inventory inventory) {
        this.inventory = inventory;
    }

    public Player() {
        this(new Inventory());
    }


    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

}
