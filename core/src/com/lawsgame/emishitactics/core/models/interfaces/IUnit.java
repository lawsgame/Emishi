package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.Allegeance;
import com.lawsgame.emishitactics.core.models.Data.Behaviour;
import com.lawsgame.emishitactics.core.models.Data.DamageType;
import com.lawsgame.emishitactics.core.models.Data.Job;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.Banner;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Equipment;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public abstract class IUnit extends Observable {

    public abstract String getName();
    public abstract void setName(String name);
    public abstract Job getJob();
    public abstract WeaponType getWeaponType();
    public abstract int getLevel();

    /**
     * @return an array of stat enhancement
     */
    public abstract int[] levelup();
    public abstract boolean isPromoted();
    public abstract boolean isRightHanded();
    public abstract boolean setRightHanded(boolean righthanded);
    public abstract boolean isStandardBearer();
    public abstract void setStandardBearer(boolean standardBearer);
    public abstract boolean isHorseman();
    public abstract void setHorseman(boolean horseman);
    public abstract boolean isHorsemanUponPromotion();
    public abstract void setHorsemanUponPromotion(boolean horseman);
    public abstract Banner getBanner();

    // WEAPON related
    public abstract boolean addWeapon(Weapon weapon);
    public abstract Weapon removeWeapon(int index);
    public abstract Weapon replace(int index, Weapon weapon);
    public abstract Array<Weapon> removeAllWeapons();
    public abstract Array<Weapon> getWeapons();
    public abstract Weapon getCurrentWeapon();
    public abstract void switchWeapon(int index);


    // STAT related
    public abstract int getBaseHitpoints();
    public abstract int getAppHitpoints();
    public abstract int getCurrentHP();
    public abstract void setCurrentHitPoints(int hitPoints);
    public abstract void resetCurrentMoral();
    public abstract int getAppMoral();
    public abstract int getCurrentMoral();
    public abstract void setCurrentMoral(int moral);
    public abstract int getExperience();
    public abstract void setExperience(int experience);
    public abstract int[] addExpPoints(int exp);
    public abstract int getLeadershipExperience();
    public abstract void setLeadershipExperience(int experience);
    public abstract boolean addLdExpPoints(int exp);

    public abstract int getBaseCharisma();
    public abstract int getAppCharisma();
    public abstract int getBaseLeadership();
    public abstract int getAppLeadership();
    public abstract int getBaseStrength();
    public abstract int getAppStrength();
    public abstract int getBaseArmor(DamageType damageType);
    public abstract int getAppArmor(DamageType damageType);
    public abstract int getBaseAgility();
    public abstract int getAppAgility();
    public abstract int getBaseDexterity();
    public abstract int getAppDexterity();
    public abstract int getBaseSkill();
    public abstract int getAppSkill();
    public abstract int getBaseBravery();
    public abstract int getAppBravery();
    public abstract int getBaseMobility();
    public abstract int getAppMobility();


    //ITEMS & ABILITIES
    public abstract boolean has(Data.Ability ability);
    public abstract Array<Data.Ability> getAbilities();
    public abstract boolean has(Equipment item);
    public abstract boolean addEquipment(Equipment item);
    public abstract Array<Equipment> disequipAllEquipment();
    public abstract Array<Equipment> getEquipments();
    public abstract Equipment removeEquipment(int index);
    public abstract Equipment replaceEquipment(int index, Equipment item);
    public abstract void setStealable(boolean weapon, int index, boolean stealable);
    public abstract boolean isStealable();
    public abstract Item getStealable();
    public abstract Item getDroppableItem();

    //ENCOUNTER RESOLUTION
    public abstract int getAppWeaponRangeMin();
    public abstract int getCurrentWeaponRangeMin(int rowUnit, int colUnit, Battlefield battlefield);
    public abstract int getAppWeaponRangeMax();
    public abstract int getCurrentWeaponRangeMax(int rowUnit, int colUnit, Battlefield battlefield);
    public abstract int getAppAttackAccuracy();
    public abstract int getAppAttackMight();
    public abstract int getAppDefense(DamageType damageType);
    public abstract int getAppAvoidance();
    public abstract int getAppOATriggerRate();
    public abstract int getCurrentOATriggerRate(int rowUnit, int colUnit, Battlefield battlefield);
    public abstract void setOAChargingBarPoints(int barProgression);
    public abstract void addOAChargingBarPoints(int points);
    public abstract int getOAChargingBarPoints();


    //ARMY
    public abstract boolean isMobilized();
    public abstract boolean isWarChief();
    public abstract boolean isWarlord();
    public abstract Allegeance getAllegeance();
    public abstract int getMaxSoldiersAs(boolean warlord);
    public abstract int getMaxWarChiefs();

    /**
     * NOT to be used outsite an implementation of an implementaiont of an IArmy class
     * @param army
     */
    public abstract void setArmy(IArmy army);

    /**
     *
     * @param allegeance
     * @return true if ally, false if foe
     */
    public abstract boolean isAllyWith(Allegeance allegeance);
    public abstract Array<IUnit> getSquad(boolean stillFighting);
    public abstract IArmy getArmy();
    public abstract boolean sameSquadAs(IUnit unit);
    public abstract boolean sameArmyAs(IUnit unit);
    public abstract void setLeadership(int leadership);
    public abstract int getChiefCharisma();
    public abstract int getChiefMoralBonus();


    //BATTLE PROCEEDING
    public abstract void setOrientation(Data.Orientation orientation);
    public abstract Orientation getOrientation();
    public abstract void setBehaviour(Data.Behaviour behaviour);
    public abstract Behaviour getBehaviour();
    public abstract boolean hasActed();
    public abstract boolean hasMoved();
    public abstract void setActed(boolean acted);
    public abstract void setMoved(boolean moved);
    public abstract boolean isDone();
    public abstract boolean isWounded();
    public abstract boolean isOutOfAction();
    public abstract boolean isDead();
    public abstract boolean treated(int healPower);
    public abstract Array<Notification.ApplyDamage> applyDamage(int damageTaken, boolean moralDamageOnly);

}
