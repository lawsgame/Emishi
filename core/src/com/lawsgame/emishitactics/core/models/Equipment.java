package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.models.Data.EquipmentTemplate;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public class Equipment extends Observable implements Item{
    protected boolean stealable;
    protected boolean droppable;
    protected Data.EquipmentTemplate template;

    public Equipment(Data.EquipmentTemplate template, boolean stealable){
        this.template = template;
        this.stealable = stealable;
    }

    public Equipment(Data.EquipmentTemplate template){
        this(template, true);
    }


    //----------------------- PROCESS --------------------------------------------

    @Override
    public String getName(I18NBundle bundle) {
        return bundle.get(template.name());
    }

    @Override
    public String getName() {
        return template.name().toLowerCase();
    }


    @Override
    public int getDropRate() {
        return template.getDropRate();
    }




    //------------------ GETTERS & SETTERS --------------------------------------


    public EquipmentTemplate getTemplate() {
        return template;
    }


    @Override
    public boolean isStealable() {
        return stealable;
    }


    public void setStealable(boolean stealable){
        this.stealable = stealable;
    }

    @Override
    public boolean isDroppable() { return droppable; }


    public void setDroppable(boolean droppable) {
        this.droppable = droppable;
    }


    @Override
    public String toString() {
        return getTemplate().name().toLowerCase();
    }
}
