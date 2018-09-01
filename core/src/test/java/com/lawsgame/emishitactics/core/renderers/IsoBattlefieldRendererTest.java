package com.lawsgame.emishitactics.core.renderers;

import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBattlefieldRenderer;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class IsoBattlefieldRendererTest {
    private Battlefield battlefield;
    private IsoBattlefieldRenderer bfr;

    @Before
    public void before(){
        int bfSize = 3;
        battlefield = new Battlefield(bfSize,bfSize);
        for(int r = 0; r < bfSize; r++){
            for(int c = 0; c < bfSize; c++){
                battlefield.getTiles()[r][c] = Data.TileType.PLAIN;
            }
        }
        bfr = new IsoBattlefieldRenderer(battlefield, 0.5f);
    }

    @Test
    public void getTileCoordsFromGameCoordsTest(){
        float xCenter = 1f;
        float yCenter = 1f;
        // r = 2 & c = 1

        assertTrue(bfr.getRowFromGameCoords(xCenter, yCenter) == 2);
        assertTrue(bfr.getColFromGameCoords(xCenter, yCenter) == 1);

        xCenter = 2.5f;
        yCenter = 0.75f;
        // r = 0 & c = 2
        assertTrue(bfr.getRowFromGameCoords(xCenter, yCenter) == 0);
        assertTrue(bfr.getColFromGameCoords(xCenter, yCenter) == 2);
    }
}
