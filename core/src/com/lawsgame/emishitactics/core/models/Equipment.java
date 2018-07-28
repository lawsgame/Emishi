package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public class Equipment extends Observable implements Item{
    protected boolean stealable;
    protected Data.EquipmentTemplate template;

    public Equipment(Data.EquipmentTemplate template, boolean stealable){
        this.template = template;
        this.stealable = stealable;
    }

    public Equipment(Data.EquipmentTemplate template){
        this(template, true);
    }

    public Data.EquipmentTemplate getTemplate() {
        return template;
    }

    public void setStealable(boolean stealable){
        this.stealable = stealable;
    }

    @Override
    public boolean isStealable() {
        return stealable;
    }

    @Override
    public String getName() {
        return template.name().toLowerCase();
    }
}
