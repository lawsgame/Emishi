package com.lawsgame.emishitactics.engine;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.engine.geometry.Shape;


/**
 * A GameWidget is the combination of a GameElement and a Shape
 * which is integrated in a tree-like dependency relation with other widget regarding the displacement of its parent
 *
 */


public abstract class GameWidget implements GameElement, Shape{
	protected Array<GameWidget> children;

	public GameWidget(float x, float y) {
		this.setX(x);
		this.setY(y);
		children = new Array<GameWidget>();
	}

	public GameWidget() {
		this.setX(0);
		this.setY(0);
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
	public void setX(float x){
		float dx = x - this.getX();
		for(int i=0; i<children.size; i++){
			children.get(i).translate(dx,0);
		}
		setX(x);
	}
	
	@Override
	public void setY(float y){
		float dy = y - this.getY();
		for(int i=0; i<children.size; i++){
			children.get(i).translate(0, dy);
		}
		setY(y);
	}
	
	public void addChild(GameWidget child){
		children.add(child);
	}
	
	public void removeChild(GameWidget child, boolean useEquals){
		this.children.removeValue(child, !useEquals);
	}
	
	
}
