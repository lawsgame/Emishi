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

        Unit warlord = new Unit.CharacterUnit("Aterui", "Emishi lord", Data.UnitTemplate.SOLAIRE, 6, Data.WeaponType.BOW, false, false, false, false);
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.HUNTING_BOW));
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.HUNTING_BOW));
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.HUNTING_BOW));
        warlord.setLeadership(19);
        Unit soldier1 = new Unit("Taro", Data.UnitTemplate.SOLAR_KNIGHT, 8, Data.WeaponType.SWORD, false, false, false, false);
        soldier1.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));

        /*
        Unit soldier2 = new Unit("Maro", Data.UnitTemplate.SOLAR_KNIGHT, 5, Data.WeaponType.SWORD, false, false, false, false, false);
        soldier2.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        Unit warchief1 = new Unit("Azamaru", Data.UnitTemplate.SOLAR_KNIGHT, 5, Data.WeaponType.SWORD, false, false, false, false, false);
        warchief1.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        warchief1.setLeadership(15);
        warchief1.setExperience(98);
        warchief1.setCurrentHitPoints(3);
        */

        playerArmy.add(warlord);
        playerArmy.add(soldier1);
        //playerArmy.add(warchief1);
        //playerArmy.add(soldier2);

        playerArmy.appointWarLord(warlord);
        playerArmy.appointSoldier(soldier1, 0);
        //playerArmy.appointWarChief(warchief1);
        //playerArmy.appointSoldier(soldier2, 1);

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
