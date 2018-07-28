package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Data.WeaponTemplate;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public class Weapon extends Observable implements Item{

    public static final Weapon FIST = new Weapon(WeaponTemplate.FIST);

    protected int durability;
    protected boolean stealable;
    protected Data.WeaponTemplate template;

    public Weapon (Data.WeaponTemplate template, boolean stealable){
        this.template = template;
        this.durability = template.getDurabilityMax();
        this.stealable = stealable;
    }

    public Weapon(Data.WeaponTemplate template){
        this(template, true);
    }

    public int getDurability() {
        return durability;
    }

    public Data.WeaponTemplate getTemplate() {
        return template;
    }

    public String getName(){
        return template.name().toLowerCase();
    }

    public void setStealable(boolean stealble) {
        this.stealable = stealble;
    }

    @Override
    public boolean isStealable() {
        return stealable;
    }
}
