package com.lawsgame.emishitactics.engine.patterns;

public interface Command {
	void execute();
	void undo();
	void redo();

}
