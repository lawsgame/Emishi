package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.constants.StringKey;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BattlefieldTest {
    private Battlefield battlefield;

    public static Battlefield createPlainBF(int rows, int cols){
        Battlefield battlefield = new Battlefield(0, rows,cols);
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                battlefield.setTile(r, c, Data.TileType.PLAIN, false);
            }
        }
        return battlefield;
    }

    @Before
    public void before(){
        battlefield = createPlainBF(9,9);
    }

}
