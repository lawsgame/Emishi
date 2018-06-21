package com.lawsgame.emishitactics.engine;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.engine.math.geometry.Shape;


/**
 * A GameWidget is the combination of a GameElement and a Shape
 * which is integrated in a tree-like dependency relation with other widget regarding the displacement of its parent
 *
 */


public abstract class GameWidget implements GameElement, Shape{
	protected Array<GameWidget> children;

	Rectangle r = new Rectangle();


	public GameWidget(float x, float y) {
		this.setXCenter(x);
		this.setYCenter(y);
		children = new Array<GameWidget>();
	}

	public GameWidget() {
		this.setXCenter(0);
		this.setYCenter(0);
	}

	public Array<GameWidget> getChildren() {
		return children;
	}
	
	@Override
	public void translate(float dx, float dy){
		translate(dx, dy);
		for(int i=0; i<children.size; i++){
			children.get(i).translate(dx, dy);
		}
	}
	
	@Override
	public void setXCenter(float x){
		float dx = x - this.getXCenter();
		for(int i=0; i<children.size; i++){
			children.get(i).translate(dx,0);
		}
		setXCenter(x);
	}
	
	@Override
	public void setYCenter(float y){
		float dy = y - this.getYCenter();
		for(int i=0; i<children.size; i++){
			children.get(i).translate(0, dy);
		}
		setYCenter(y);
	}
	
	public void addChild(GameWidget child){
		children.add(child);
	}
	
	public void removeChild(GameWidget child, boolean useEquals){
		this.children.removeValue(child, !useEquals);
	}
	
	
}
