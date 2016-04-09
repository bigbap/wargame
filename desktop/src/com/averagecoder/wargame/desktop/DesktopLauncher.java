package com.averagecoder.wargame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.averagecoder.wargame.WargameCampaign;

public class DesktopLauncher {
	static final float WIDTH = 1024.0f;
    static final float HEIGHT = 820.0f;

    public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = (int)WIDTH;
        config.height = (int)HEIGHT;
        config.title = "Wargame Campaign";
        config.resizable = false;
		new LwjglApplication(new WargameCampaign(WIDTH, HEIGHT), config);
	}
}
