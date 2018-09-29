package com.lawsgame.emishitactics.core.constants;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.ActorCommand;

import java.util.Stack;

public class Utils {

    /**
     *
     * @param row
     * @param col
     * @param rowTarget
     * @param colTarget
     * @return the number of tiles between the two given tiles including the target one
     */
    public static int dist(int row, int col, int rowTarget, int colTarget){
        return Math.abs(row  - rowTarget) + Math.abs(col - colTarget);
    }

    public static Array<int[]> getEreaFromRange(Battlefield bf,  int rCenter, int cCenter, int rangeMin, int rangeMax){
        Array<int[]> area = new Array<int[]>();
        for(int r = rCenter - rangeMax ; r <= rCenter + rangeMax; r++){
            for(int c = cCenter - rangeMax ; c <= cCenter + rangeMax; c++){
                if(bf.isTileExisted(r,c)) {
                    int dist = Utils.dist(rCenter, cCenter, r, c);
                    if (dist <= rangeMax && rangeMin <= dist) {
                        area.add(new int[]{r, c});
                    }
                }

            }
        }
        return area;
    }

    public static boolean arrayContains(Array<int[]> intArray, int[] value){
        boolean contained;
        if(value != null) {
            for (int i = 0; i < intArray.size; i++) {
                if (intArray.get(i).length == value.length) {
                    contained = true;
                    for (int j = 0; j < value.length; j++) {
                        contained = contained && value[j] == intArray.get(i)[j];
                    }
                    if (contained) return true;
                }
            }
        }
        return false;
    }

    public static<T> boolean stackContainsAtLeastOneElementOf(Stack<T> checkedStack, Array<T> initialContainer){
        for (int j = 0; j < initialContainer.size; j++) {
            for(int i = 0; i < checkedStack.size(); i++){
                if(checkedStack.get(i) == initialContainer.get(j)){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean arrayContains(Array<int[]> intArray, int r, int c){
        if(intArray != null) {
            for (int i = 0; i < intArray.size; i++) {
                if(intArray.get(i).length >= 2) {
                    if (r == intArray.get(i)[0] && c == intArray.get(i)[1]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     *        NORTH
     *          ^
     *          |
     WEST  -- (0,0) -> EAST
     *          |
     *          |
     *        SOUTH
     *
     *
     * @param rowI
     * @param colI
     * @param rowTarget
     * @param colTarget
     * @return the general orientation of the vector (rowT - rowI, colT - colI)
     */
    public static Data.Orientation getOrientationFromCoords(float rowI, float colI, float rowTarget, float colTarget){
        Data.Orientation resOr;
        float deltaR = rowTarget - rowI;
        float deltaC = colTarget - colI; // light factor to prioritized an orientation over another when both are equally valid option.

        if(deltaR > 0){
            if(deltaC > 0){
                if(deltaR > deltaC) {
                    resOr = Data.Orientation.NORTH;
                }else{
                    resOr = Data.Orientation.EAST;
                }
            }else{
                if(deltaR > -deltaC) {
                    resOr = Data.Orientation.NORTH;
                }else{
                    resOr = Data.Orientation.WEST;
                }
            }
        }else{
            if(deltaC > 0){
                if(-deltaR > deltaC) {
                    resOr = Data.Orientation.SOUTH;
                }else{
                    resOr = Data.Orientation.EAST;
                }
            }else{
                if(-deltaR > -deltaC) {
                    resOr = Data.Orientation.SOUTH;
                }else{
                    resOr = Data.Orientation.WEST;
                }
            }
        }


        return resOr;
    }

    public static int getColor32Bits(int r, int g, int b) {
        return 255 + 256*b + 256*256*g + 256*256*256*r;
    }

    public static int getColor32Bits(int r, int g, int b, int a) {
        return a + 256*b + 256*256*g + 256*256*256*r;
    }

    public static int[] getRGBA(int color32bits){
        int A = (color32bits)&0xFF;
        int B = (color32bits>>8)&0xFF;
        int G = (color32bits>>16)&0xFF;
        int R = (color32bits>>24)&0xFF;
        return new int[]{R,G,B,A};
    }

    public static int getMean(int nbOfDices, int nbFaces){
        int result = 0;
        for(int i = 0; i < nbOfDices; i++){
            result += Data.rand(nbFaces);
        }
        return result /nbOfDices;
    }

    /**
     *
     * @param historic
     * @return true if all commands has been undone, false otherwise
     */
    public static boolean undoCommands(Stack<ActorCommand> historic){
        while(!historic.isEmpty() && historic.peek().getActionChoice().isUndoable()){
            historic.pop().undo();
        }
        return historic.isEmpty();
    }

    public static float getStandardDerivation (float[] array){
        float expectedValue = getExpectedValue(array);
        float standardDerivation = 0;
        float term;
        for(int i = 0; i < array.length; i++){
            term = array[i] - expectedValue;
            standardDerivation += term*term;
        }
        standardDerivation = (array.length > 1) ? standardDerivation / (array.length - 1) : 0;
        return (float)Math.sqrt(standardDerivation);
    }

    public static float getExpectedValue (float[] array){
        float expected = 0;
        for(int i = 0; i < array.length; i++){
            expected += array[i];
        }
        return (array.length > 0) ? expected / array.length : 0;
    }

    public static Texture createDesaturatedTexture(final Texture originalTexture) {
        originalTexture.getTextureData().prepare();
        Pixmap pixmap = originalTexture.getTextureData().consumePixmap();
        int colorInt;
        int average;
        int[] colorArray;
        for(int r = 0; r < pixmap.getHeight(); r++){
            for(int c = 0; c < pixmap.getWidth(); c++){
                colorInt = pixmap.getPixel(r, c);
                colorArray = Utils.getRGBA(colorInt);
                average = (colorArray[0] + colorArray[1] + colorArray[2])/3;
                colorInt = Utils.getColor32Bits(average, average, average, colorArray[3]);
                pixmap.drawPixel(r, c, colorInt);
            }
        }
        pixmap.getPixels().rewind();
        Texture madeupTexture = new Texture(pixmap);
        pixmap.dispose();
        return madeupTexture;
    }
}
