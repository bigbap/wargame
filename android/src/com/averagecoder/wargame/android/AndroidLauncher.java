package com.averagecoder.wargame.android;

import android.os.Bundle;
import android.util.DisplayMetrics;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.averagecoder.wargame.WargameCampaign;

public class AndroidLauncher extends AndroidApplication {

    DisplayMetrics metrics;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        float scaleFactor = metrics.density;

        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new WargameCampaign(widthDp, heightDp), config);
	}
}
