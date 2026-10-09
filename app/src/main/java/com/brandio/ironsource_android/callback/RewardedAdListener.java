package com.brandio.ironsource_android.callback;


import android.util.Log;

import androidx.annotation.NonNull;

import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.rewarded.LevelPlayReward;
import com.unity3d.mediation.rewarded.LevelPlayRewardedAdListener;

public class RewardedAdListener implements LevelPlayRewardedAdListener {
    private final String TAG = RewardedAdListener.class.getSimpleName();

    private final UIControllerInterface controllerInterface;

    public RewardedAdListener(UIControllerInterface controllerInterface) {
        this.controllerInterface = controllerInterface;
    }

    @Override
    public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {
        Log.e(TAG, "onAdLoaded adInfo = " + adInfo);
        controllerInterface.switchLoadButton(false);
        controllerInterface.switchShowButton(true);
    }

    @Override
    public void onAdLoadFailed(@NonNull LevelPlayAdError error) {
        Log.e(TAG, "onAdLoadFailed error = " + error);
        controllerInterface.switchLoadButton(true);
        controllerInterface.switchShowButton(false);
    }

    @Override
    public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {
        Log.e(TAG, "onAdDisplayed adInfo = " + adInfo);
        controllerInterface.switchLoadButton(true);
        controllerInterface.switchShowButton(false);
    }

    @Override
    public void onAdDisplayFailed(@NonNull LevelPlayAdError error, @NonNull LevelPlayAdInfo adInfo) {
        Log.e(TAG, "onAdDisplayFailed error = " + error + " | adInfo = " + adInfo);
        controllerInterface.switchLoadButton(true);
        controllerInterface.switchShowButton(false);
    }

    @Override
    public void onAdRewarded(@NonNull LevelPlayReward reward, @NonNull LevelPlayAdInfo adInfo) {
        Log.e(TAG, "onAdRewarded reward = " + reward + " | adInfo = " + adInfo);
    }

    @Override
    public void onAdClicked(@NonNull LevelPlayAdInfo adInfo) {
        Log.e(TAG, "onAdClicked adInfo = " + adInfo);
    }

    @Override
    public void onAdClosed(@NonNull LevelPlayAdInfo adInfo) {
        Log.e(TAG, "onAdClosed adInfo = " + adInfo);
    }
}
