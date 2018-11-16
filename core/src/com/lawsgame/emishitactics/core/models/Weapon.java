package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data.WeaponTemplate;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.models.Notification.StateChanged;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public class Weapon extends Observable implements Item{

    public static final Weapon FIST = new Weapon(WeaponTemplate.FIST, false, false);

    protected int durability;
    protected boolean stealable;
    protected boolean droppable;
    protected final Data.WeaponTemplate template;

    public Weapon (Data.WeaponTemplate template, boolean stealable, boolean droppable){
        this.template = template;
        this.durability = template.getDurabilityMax();
        this.stealable = stealable;
    }

    public Weapon(Data.WeaponTemplate template){
        this(template, true, true);
    }



    // --------------- PROCESS ----------------------------------------

    public String getName(I18NBundle bundle){
        return (bundle.get(template.name()) != null) ? bundle.get(template.name()) : template.name().toLowerCase();
    }



    //------------- GETTERS & SETTERS --------------------------


    public Data.WeaponTemplate getTemplate() {
        return template;
    }

    public void setDurability(int durability) {
        if(durability >= 0 && durability <= template.getDurabilityMax()) {
            this.durability = durability;
            notifyAllObservers(StateChanged.getInstance());
        }
    }

    public int getDurability() {
        return durability;
    }


    public void setStealable(boolean stealble) {
        this.stealable = stealble;
        notifyAllObservers(StateChanged.getInstance());
    }

    @Override
    public boolean isStealable() {
        return stealable;
    }


    public void setDroppable(boolean droppable) {
        this.droppable = droppable;
        notifyAllObservers(StateChanged.getInstance());
    }


    @Override
    public boolean isDroppable() {
        return droppable;
    }

    @Override
    public int getDropRate() {
        return template.getDropRate();
    }


    @Override
    public String toString() {
        return getTemplate().name().toLowerCase();
    }
}
