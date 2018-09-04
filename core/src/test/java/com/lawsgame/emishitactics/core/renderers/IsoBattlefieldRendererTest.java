package com.lawsgame.emishitactics.core.renderers;

import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class IsoBattlefieldRendererTest {
    private Battlefield battlefield;
    private IsoBFR bfr;

    @Before
    public void before(){
        int bfSize = 3;
        battlefield = new Battlefield(bfSize,bfSize);
        for(int r = 0; r < bfSize; r++){
            for(int c = 0; c < bfSize; c++){
                battlefield.getTiles()[r][c] = Data.TileType.PLAIN;
            }
        }
        this.bfr = new IsoBFR(battlefield);
    }

    @Test
    public void getTileCoordsFromGameCoordsTest(){
        float xCenter = 1.5f;
        float yCenter = 0.25f;

        // r = 0 & c = 0
        assertTrue(bfr.getRowFrom(xCenter, yCenter) == 0);
        assertTrue(bfr.getColFrom(xCenter, yCenter) == 0);

        xCenter = 2.5f;
        yCenter = 0.75f;

        // r = 0 & c = 2
        assertTrue(bfr.getRowFrom(xCenter, yCenter) == 0);
        assertTrue(bfr.getColFrom(xCenter, yCenter) == 2);

        xCenter = 2.0f;
        yCenter = 1.0f;

        assertTrue(bfr.getRowFrom(xCenter, yCenter) == 1);
        assertTrue(bfr.getColFrom(xCenter, yCenter) == 2);
    }
}
