package com.lawsgame.emishitactics.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lawsgame.emishitactics.EmishiTacticsGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable  = false;
		config.width = EmishiTacticsGame.SCREEN_PIXEL_WIDTH;
		config.height = EmishiTacticsGame.SCREEN_PIXEL_HEIGHT;
		config.title = EmishiTacticsGame.TITLE;
		new LwjglApplication(new EmishiTacticsGame(), config);
	}
}
