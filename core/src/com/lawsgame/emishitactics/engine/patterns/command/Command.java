package com.lawsgame.emishitactics.engine.patterns.command;

public interface Command {
	void apply();
	void undo();
	void redo();

}
