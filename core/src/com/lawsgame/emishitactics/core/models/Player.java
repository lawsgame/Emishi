package com.lawsgame.emishitactics.core.models;

public class Player {
    private Inventory inventory;
    private Army army;


    public Player(Inventory inventory, Army army) {
        this.inventory = inventory;
        this.army = army;
    }

    public static Player create(){
        Army playerArmy = Army.createPlayerArmyTemplate();

        Unit warlord = new Unit.CharacterUnit("Aterui", "Emishi lord", Data.UnitTemplate.SOLAIRE, 25, Data.WeaponType.SWORD, false, false, false);
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        warlord.setLeadership(3);
        Unit soldier1 = new Unit("Taro", Data.UnitTemplate.SOLAR_KNIGHT, 8, Data.WeaponType.BOW, false, false, false);
        soldier1.addWeapon(new Weapon(Data.WeaponTemplate.HUNTING_BOW));
        Unit soldier2 = new Unit("Rota", Data.UnitTemplate.SOLAR_KNIGHT, 1, Data.WeaponType.POLEARM, false, false, false);
        soldier2.addWeapon(new Weapon(Data.WeaponTemplate.SPEAR));
        Unit soldier3 = new Unit("Rota", Data.UnitTemplate.SOLAR_KNIGHT, 1, Data.WeaponType.AXE, false, false, false);
        soldier3.addWeapon(new Weapon(Data.WeaponTemplate.BROAD_AXE));



        playerArmy.add(warlord);
        playerArmy.add(soldier1);
        playerArmy.add(soldier2);
        playerArmy.add(soldier3);

        playerArmy.appointWarLord(warlord);
        playerArmy.appointSoldier(soldier1, 0);
        //playerArmy.appointSoldier(soldier2, 0);
        //playerArmy.appointSoldier(soldier3, 0);

        warlord.getBanner().increment(Data.BannerBonus.ATTACK_MIGHT);
        warlord.getBanner().increment(Data.BannerBonus.MORAL_SHIELD);
        warlord.getBanner().increment(Data.BannerBonus.MORAL_SHIELD);
        warlord.getBanner().increment(Data.BannerBonus.MORAL_SHIELD);
        warlord.getBanner().increment(Data.BannerBonus.LOOT_RATE);
        warlord.getBanner().increment(Data.BannerBonus.AP_COST);
        warlord.getBanner().setMode(Data.BBMode.OFFENSIVE);


        warlord.takeDamage(1, false,  1f);
        soldier1.takeDamage(1, false,  1f);
        soldier2.takeDamage(1, false,  1f);

        return new Player(new Inventory(), playerArmy);
    }


    //---------------------- GETTERS & SETTERS ------------------------------


    public Army getArmy() {
        return army;
    }

    public void setArmy(Army army) {
        this.army = army;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

}
