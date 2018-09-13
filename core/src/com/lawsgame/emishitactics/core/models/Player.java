package com.lawsgame.emishitactics.core.models;

public class Player {
    private Inventory inventory;
    private Army army;


    public Player(Inventory inventory, Army army) {
        this.inventory = inventory;
        this.army = army;
    }

    public static Player create(){
        Army playerArmy = Army.createPlayerArmyTemplate();;

        Unit warlord = new Unit("Aterui", Data.Job.SOLAIRE, 18, Data.WeaponType.SWORD, false, false, false, false);
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        warlord.setLeadership(19);
        Unit soldier1 = new Unit("Taro", Data.Job.SOLAIRE, 5, Data.WeaponType.SWORD, false, false, false, false);
        soldier1.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        Unit soldier2 = new Unit("Maro", Data.Job.SOLAIRE, 5, Data.WeaponType.SWORD, false, false, false, false);
        soldier2.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        Unit warchief1 = new Unit("Azamaru", Data.Job.SOLAIRE, 5, Data.WeaponType.SWORD, false, false, false, false);
        warchief1.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        warchief1.setLeadership(15);

        playerArmy.add(warlord);
        playerArmy.add(warchief1);
        playerArmy.add(soldier1);
        playerArmy.add(soldier2);
        playerArmy.appointWarLord(warlord);
        playerArmy.appointWarChief(warchief1);
        playerArmy.appointSoldier(soldier1, 0);
        playerArmy.appointSoldier(soldier2, 1);

        return new Player(new Inventory(), playerArmy);
    }


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
