package com.lawsgame.emishitactics.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lawsgame.emishitactics.TacticsGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable  = false;
		config.width = TacticsGame.SCREEN_PIXEL_WIDTH;
		config.height = TacticsGame.SCREEN_PIXEL_HEIGHT;
		config.title = TacticsGame.TITLE;
		//config.samples = 3;
		new LwjglApplication(new TacticsGame(), config);
	}
}
