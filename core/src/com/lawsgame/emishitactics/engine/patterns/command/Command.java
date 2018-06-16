package com.lawsgame.emishitactics.engine.patterns.command;

public interface Command {
	void execute();
	void undo();
	void redo();

}
