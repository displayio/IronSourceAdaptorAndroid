package com.brandio.ironsource_android.callback;


import android.util.Log;

import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.banner.LevelPlayBannerAdViewListener;

public class BannerAdListener implements LevelPlayBannerAdViewListener {
    private final String TAG = BannerAdListener.class.getSimpleName();

    private final UIControllerInterface controllerInterface;

    public BannerAdListener(UIControllerInterface controllerInterface) {
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
    public void onAdClicked(LevelPlayAdInfo adInfo) {
        Log.e(TAG, "adInfo = " + adInfo);
    }

    @Override
    public void onAdDisplayed(LevelPlayAdInfo adInfo) {
        Log.e(TAG, "onAdDisplayed, adInfo = " + adInfo);
    }

    @Override
    public void onAdDisplayFailed(LevelPlayAdInfo adInfo, LevelPlayAdError error) {
        Log.e(TAG, "onAdDisplayFailed, error = " + error + " | adInfo = " + adInfo);
    }

    @Override
    public void onAdExpanded(LevelPlayAdInfo adInfo) {
        Log.e(TAG, "onAdExpanded, adInfo = " + adInfo);
    }

    @Override
    public void onAdCollapsed(LevelPlayAdInfo adInfo) {
        Log.e(TAG, "onAdCollapsed, adInfo = " + adInfo);
    }

    @Override
    public void onAdLeftApplication(LevelPlayAdInfo adInfo) {
        Log.e(TAG, "onAdLeftApplication, adInfo = " + adInfo);
    }
}
