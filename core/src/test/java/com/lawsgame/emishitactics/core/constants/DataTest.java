package com.lawsgame.emishitactics.core.constants;

import com.badlogic.gdx.utils.Array;

import org.junit.BeforeClass;
import org.junit.Test;
import com.lawsgame.emishitactics.core.constants.Data.*;

public class DataTest {

    @BeforeClass
    public static void beforeAll(){
        System.out.println("\n---+++$$$ Data TEST $$$+++---\n");
    }

    @Test
    public void testOffensiveAbilityGetOrientedArea(){
        Data.ActionChoice ability = Data.ActionChoice.USE_SWIRLING_BLOW;

        for(Orientation or : Orientation.values()) {
            Array<int[]> orientedArea = ability.getOrientedImpactArea(or);
            //System.out.println("\n"+or.name());
            switch (or){
                case WEST:
                    Utils.arrayContains(orientedArea, new int[]{-1,1});
                    Utils.arrayContains(orientedArea, new int[]{1,1});
                    break;
                case NORTH:
                    Utils.arrayContains(orientedArea, new int[]{-1,1});
                    Utils.arrayContains(orientedArea, new int[]{-1,-1});
                    break;
                case SOUTH:
                    Utils.arrayContains(orientedArea, new int[]{1,-1});
                    Utils.arrayContains(orientedArea, new int[]{1,1});
                    break;
                case EAST:
                    Utils.arrayContains(orientedArea, new int[]{-1,-1});
                    Utils.arrayContains(orientedArea, new int[]{1,-1});
                    break;
            }
        }
    }
}
