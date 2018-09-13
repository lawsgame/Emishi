package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data.BannerSignTemplate;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public class Banner extends Observable  {
    private final Array<Sign> bannerSigns;

    public Banner() {
        this.bannerSigns = new Array<Sign>();
    }

    public  void addSign(Sign sign, boolean notifyObservers){
        if(hasSign(sign.getTemplate()) < sign.getTemplate().getMaxSignByBanner()) {
            if(notifyObservers)
                notifyAllObservers(null);
        }
    }

    public Sign removeSign(int index, boolean notifyObservers){
        Sign sign = null;
        if(0 <= index && index < bannerSigns.size) {
            sign = bannerSigns.removeIndex(index);
            if (notifyObservers)
                notifyAllObservers(null);
        }
        return sign;
    }

    public void switchSignPosition(int index1, int index2, boolean notifyObservers){
        if(0 <= index1 && index1 < bannerSigns.size && 0 <= index2 && index2 < bannerSigns.size) {
            bannerSigns.swap(index1, index2);
            if (notifyObservers)
                notifyAllObservers(null);
        }
    }

    private int hasSign(BannerSignTemplate sign){
        int numberSigns = 0;
        for(int i = 0; i < bannerSigns.size; i++){
            if(bannerSigns.get(i).getTemplate() == sign)
                numberSigns++;
        }
        return numberSigns;
    }

    public Array<Sign> getBannerSigns() {
        return bannerSigns;
    }

    public int getBonus(Data.BonusType type){
        int bonus = 0;
        for(int i = 0; i < bannerSigns.size; i++){
            if (bannerSigns.get(i).getTemplate().getBonusType() == type){
                bonus += bannerSigns.get(i).getTemplate().getAmount();
            }
        }
        return bonus;
    }

    // --------------------- GETTERS & SETTERS -------------------------------


    public boolean isEmpty() {
        return bannerSigns.size == 0;
    }

    public boolean isDroppable() {
        boolean droppable = false;
        for(int i = 0; i <bannerSigns.size; i++){
            if(bannerSigns.get(i).isDroppable()){
                droppable = true;
            }
        }
        return droppable;
    }

    public boolean isStealable(){
        boolean stealable = false;
        for(int i = 0; i <bannerSigns.size; i++){
            if(bannerSigns.get(i).isStealable()){
                stealable = true;
            }
        }
        return stealable;
    }



    public static class Sign implements Item {
        private Data.BannerSignTemplate template;
        private boolean droppable;
        private boolean stealable;

        public Sign(Data.BannerSignTemplate template, boolean droppable, boolean stealable) {
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

}
