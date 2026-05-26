package com.brandio.ironsource_android.callback;


import android.util.Log;

import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener;

public class InterstitialAdListener implements LevelPlayInterstitialAdListener {
    private final String TAG = InterstitialAdListener.class.getSimpleName();

    private final UIControllerInterface controllerInterface;

    public InterstitialAdListener(UIControllerInterface controllerInterface) {
        this.controllerInterface = controllerInterface;
    }

    @Override
    public void onAdLoaded(LevelPlayAdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
        controllerInterface.switchLoadButton(false);
        controllerInterface.switchShowButton(true);
    }

    @Override
    public void onAdLoadFailed(LevelPlayAdError error) {
        Log.e(TAG, "error = " + error);
        controllerInterface.switchLoadButton(true);
        controllerInterface.switchShowButton(false);
    }

    @Override
    public void onAdDisplayed(LevelPlayAdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
        controllerInterface.switchLoadButton(true);
        controllerInterface.switchShowButton(false);
    }

    @Override
    public void onAdDisplayFailed(LevelPlayAdError error, LevelPlayAdInfo adInfo) {
        Log.e(TAG, "error = " + error + " | adInfo = " + adInfo);
        controllerInterface.switchLoadButton(true);
        controllerInterface.switchShowButton(false);
    }

    @Override
    public void onAdClicked(LevelPlayAdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
    }

    @Override
    public void onAdClosed(LevelPlayAdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
    }
}
