package com.brandio.ironsource_android.callback;


import android.util.Log;

import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;

public class BannerAdListener implements LevelPlayBannerListener {
    private final String TAG = BannerAdListener.class.getSimpleName();

    private final UIControllerInterface controllerInterface;

    public BannerAdListener(UIControllerInterface controllerInterface) {
        this.controllerInterface = controllerInterface;
    }

    // Invoked each time a banner was loaded. Either on refresh, or manual load.
    //  AdInfo parameter includes information about the loaded ad
    @Override
    public void onAdLoaded(AdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
        controllerInterface.switchLoadButton(false);
        controllerInterface.switchShowButton(true);
    }
    // Invoked when the banner loading process has failed.
    //  This callback will be sent both for manual load and refreshed banner failures.
    @Override
    public void onAdLoadFailed(IronSourceError error) {
        Log.e(TAG, "error = " + error);
        controllerInterface.switchLoadButton(true);
        controllerInterface.switchShowButton(false);
    }
    // Invoked when end user clicks on the banner ad
    @Override
    public void onAdClicked(AdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
    }
    // Notifies the presentation of a full screen content following user click
    @Override
    public void onAdScreenPresented(AdInfo adInfo) {
        Log.e(TAG, "onAdScreenPresented, adInfo = " + adInfo);
    }
    // Notifies the presented screen has been dismissed
    @Override
    public void onAdScreenDismissed(AdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
    }
    //Invoked when the user left the app
    @Override
    public void onAdLeftApplication(AdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
    }
}
