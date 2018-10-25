package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.Affiliation;
import com.lawsgame.emishitactics.core.models.Data.Behaviour;
import com.lawsgame.emishitactics.core.models.Data.DamageType;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.Banner;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Equipment;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

import java.util.Stack;

public abstract class IUnit extends Model {

    public abstract String getName(I18NBundle bundle);
    public abstract String getName();
    public abstract void setName(String namekey);
    public abstract Data.UnitTemplate getTemplate();
    public abstract WeaponType getWeaponType();
    public abstract int getLevel();

    /**
     * @return an array of stat enhancement
     */
    public abstract int[] levelup();
    public abstract boolean isPromoted();
    public abstract boolean isRightHanded();
    public abstract boolean setRightHanded(boolean righthanded);
    public abstract boolean isCharacter();
    public abstract boolean isStandardBearer();
    public abstract boolean isHorseman();
    public abstract boolean isShielbearer();
    public abstract void setHorseman(boolean horseman);
    public abstract boolean isHorsemanUponPromotion();
    public abstract void setHorsemanUponPromotion(boolean horseman);
    public abstract Banner getBanner();
    public abstract String getTitle(I18NBundle bundle);

    // WEAPON related
    public abstract boolean addWeapon(Weapon weapon);
    public abstract Weapon removeWeapon(int index);
    public abstract Weapon replace(int index, Weapon weapon);
    public abstract Array<Weapon> removeAllWeapons();
    public abstract Array<Weapon> getWeapons();
    public abstract Weapon getCurrentWeapon();
    public abstract boolean switchWeapon(int index);
    public abstract Weapon getWeapon(int index);


    // STAT related
    public abstract int getBaseHitpoints();
    public abstract int getAppHitpoints();
    public abstract float getAppGrHitpoints();
    public abstract int getCurrentHP();
    public abstract void setCurrentHitPoints(int hitPoints);
    public abstract void resetCurrentMoral();
    public abstract int getAppMoral();
    public abstract int getCurrentMoral();
    public abstract void setCurrentMoral(int moral);
    public abstract int getExperience();
    public abstract int[] setExperience(int experience);
    public abstract Stack<int[]> addExpPoints(int exp);
    public abstract int getLeadershipExperience();
    public abstract void setLeadershipExperience(int experience);
    public abstract boolean addLdExpPoints(int exp);

    public abstract int getBaseCharisma();
    public abstract int getAppCharisma();
    public abstract float getAppGrCharisma();
    public abstract int getBaseLeadership();
    public abstract int getAppLeadership();
    public abstract int getBaseStrength();
    public abstract int getAppStrength();
    public abstract float getAppGrStrength();
    public abstract int getBaseArmor(DamageType damageType);
    public abstract int getAppArmor(DamageType damageType);
    public abstract float getAppGrArmor(DamageType damageType);
    public abstract int getBaseAgility();
    public abstract int getAppAgility();
    public abstract float getAppGrAgility();
    public abstract int getBaseDexterity();
    public abstract int getAppDexterity();
    public abstract float getAppGrDexterity();
    public abstract int getBaseSkill();
    public abstract int getAppSkill();
    public abstract float getAppGrSkill();
    public abstract int getBaseBravery();
    public abstract int getAppBravery();
    public abstract float getAppGrBravery();
    public abstract int getBaseMobility();
    public abstract int getAppMobility();


    //ITEMS & ABILITIES
    public abstract void addNativeAbility(Data.Ability guard);
    public abstract boolean has(Data.Ability ability);
    public abstract Array<Data.Ability> getAbilities();
    public abstract boolean has(Equipment item);
    public abstract boolean addEquipment(Equipment item);
    public abstract Array<Equipment> disequipAllEquipment();
    public abstract Array<Equipment> getEquipments();
    public abstract Equipment removeEquipment(int index);
    public abstract Equipment replaceEquipment(int index, Equipment item);

    public abstract boolean isStealable();
    public abstract Item getRandomlyStealableItem();
    public abstract Item getRandomlyDroppableItem();
    public abstract Array<Item> getStealableItems();

    //ENCOUNTER RESOLUTION
    public abstract int getAppWeaponRangeMin();
    public abstract int getCurrentWeaponRangeMin(int rowUnit, int colUnit, Battlefield battlefield);
    public abstract int getAppWeaponRangeMax();
    public abstract int getCurrentWeaponRangeMax(int rowUnit, int colUnit, Battlefield battlefield);
    public abstract int getAppAttackAccuracy();
    public abstract int[] getAppAttackMight();
    public abstract int getAppDefense(DamageType damageType);
    public abstract int getAppAvoidance();
    public abstract int getAppAPRecoveryRate();
    public abstract int getCurrentAPRecoveryRate(int rowUnit, int colUnit, Battlefield battlefield);
    public abstract void setActionPoints(int ap);
    public abstract void addActionPoints(int points);
    public abstract int getActionPoints();


    //ARMY
    public abstract boolean isMobilized();
    public abstract boolean isWarChief();
    public abstract boolean isWarlord();
    public abstract int getMaxSoldiersAs(boolean warlord);
    public abstract int getMaxWarChiefs();

    /**
     * NOT to be used outsite an implementation of an implementaiont of an IArmy class
     * @param army
     */
    public abstract void setArmy(IArmy army);

    /**
     *
     * @param affiliation
     * @return true if ally, false if foe
     */
    public abstract boolean isAllyWith(Affiliation affiliation);
    public abstract Array<IUnit> getSquad(boolean stillFighting);
    public abstract IArmy getArmy();
    public abstract boolean sameSquadAs(IUnit unit);
    public abstract boolean sameArmyAs(IUnit unit);
    public abstract void setLeadership(int leadership);
    public abstract IUnit getWarchief();
    public abstract int getChiefCharismaBonus();
    public abstract int getSquadIndex();


    //BATTLE PROCEEDING
    public abstract void setOrientation(Data.Orientation orientation);
    public abstract Orientation getOrientation();
    public abstract void setBehaviour(Data.Behaviour behaviour);
    public abstract Behaviour getBehaviour();
    public abstract boolean hasActed();
    public abstract boolean hasMoved();
    public abstract void setActed(boolean acted);
    public abstract void setMoved(boolean moved);
    public abstract boolean isDisabled();
    public abstract void setDisabled(boolean disabled);
    public abstract boolean isCrippled();
    public abstract void setCrippled(boolean crippled);

    public abstract boolean isDone();
    public abstract boolean isWounded();
    public abstract boolean isOutOfAction();
    public abstract boolean isDead();
    public abstract int getRecoveredHitPoints(int healPower);
    public abstract int getRecoveredMoralPoints(int healPower);
    public abstract boolean treated(int healPower);
    public abstract Array<Notification.ApplyDamage> applyDamage(int damageTaken, boolean moralDamageOnly);


}
