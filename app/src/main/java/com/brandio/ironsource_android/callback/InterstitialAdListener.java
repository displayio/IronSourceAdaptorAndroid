package com.brandio.ironsource_android.callback;


import android.util.Log;

import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;

public class InterstitialAdListener implements LevelPlayInterstitialListener {
    private final String TAG = InterstitialAdListener.class.getSimpleName();

    private final UIControllerInterface controllerInterface;

    public InterstitialAdListener(UIControllerInterface controllerInterface) {
        this.controllerInterface = controllerInterface;
    }

    /**
     Called after an interstitial has been loaded
     @param adInfo The info of the ad.
     */
    @Override
    public void onAdReady(AdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
        controllerInterface.switchLoadButton(false);
        controllerInterface.switchShowButton(true);
    }

    /**
     Called after an interstitial has attempted to load but failed.
     @param ironSourceError The reason for the error
     */
    @Override
    public void onAdLoadFailed(IronSourceError ironSourceError) {
        Log.e(TAG, "error = " + ironSourceError);
        controllerInterface.switchLoadButton(true);
        controllerInterface.switchShowButton(false);
    }

    /**
     Called after an interstitial has been opened.
     This is the indication for impression.
     @param adInfo The info of the ad.
     */
    @Override
    public void onAdOpened(AdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
        controllerInterface.switchLoadButton(true);
        controllerInterface.switchShowButton(false);    }

    /**
     Called after an interstitial has been displayed on the screen.
     This callback is not supported by all networks, and we recommend using it
     only if it's supported by all networks you included in your build.
     @param adInfo The info of the ad.
     */
    @Override
    public void onAdShowSucceeded(AdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
        controllerInterface.switchLoadButton(true);
        controllerInterface.switchShowButton(false);
    }

    /**
     Called after an interstitial has attempted to show but failed.
     @param ironSourceError The reason for the error.
     @param adInfo The info of the ad.
     */
    @Override
    public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
        Log.e(TAG, "error = " + ironSourceError + " | adInfo = " + adInfo);
        controllerInterface.switchLoadButton(true);
        controllerInterface.switchShowButton(false);
    }

    /**
     Called after an interstitial has been clicked.
     @param adInfo The info of the ad.
     */
    @Override
    public void onAdClicked(AdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
    }

    /**
     Called after an interstitial has been dismissed.
     @param adInfo The info of the ad.
     */
    @Override
    public void onAdClosed(AdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
    }
}
