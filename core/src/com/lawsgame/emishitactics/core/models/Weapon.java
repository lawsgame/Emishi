package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data.WeaponTemplate;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public class Weapon extends Observable implements Item{

    public static final Weapon FIST = new Weapon(WeaponTemplate.FIST, false, false);

    protected int durability;
    protected boolean stealable;
    protected boolean droppable;
    protected Data.WeaponTemplate template;

    public Weapon (Data.WeaponTemplate template, boolean stealable, boolean droppable){
        this.template = template;
        this.durability = template.getDurabilityMax();
        this.stealable = stealable;
    }

    public Weapon(Data.WeaponTemplate template){
        this(template, true, true);
    }

    public int getDurability() {
        return durability;
    }

    public Data.WeaponTemplate getTemplate() {
        return template;
    }

    public String getName(I18NBundle bundle){
        return bundle.get(template.name());
    }

    public void setStealable(boolean stealble) {
        this.stealable = stealble;
    }

    @Override
    public boolean isStealable() {
        return stealable;
    }

    @Override
    public boolean isDroppable() {
        return droppable;
    }

    @Override
    public int getDropRate() {
        return template.getDropRate();
    }

    public void setDroppable(boolean droppable) {
        this.droppable = droppable;
    }

    @Override
    public String toString() {
        return getTemplate().name().toLowerCase();
    }
}
