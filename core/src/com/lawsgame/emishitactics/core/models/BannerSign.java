package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.interfaces.Item;

public class BannerSign implements Item{
    private Data.BannerSignTemplate template;
    private boolean droppable;
    private boolean stealable;

    public BannerSign(Data.BannerSignTemplate template, boolean droppable, boolean stealable) {
        this.template = template;
        this.droppable = droppable;
        this.stealable = stealable;
    }

    @Override
    public String getName(I18NBundle bundle) {
        return template.getName(bundle);
    }

    @Override
    public boolean isStealable() {
        return stealable;
    }

    @Override
    public boolean isDroppable() {
        return droppable;
    }

    public void setDroppable(boolean droppable) {
        this.droppable = droppable;
    }

    public void setStealable(boolean stealable) {
        this.stealable = stealable;
    }

    @Override
    public int getDropRate() {
        return template.getDropFactor();
    }

    public Data.BannerSignTemplate getTemplate() {
        return template;
    }
}
