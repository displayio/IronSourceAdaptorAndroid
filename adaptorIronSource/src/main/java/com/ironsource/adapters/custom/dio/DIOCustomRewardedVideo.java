package com.ironsource.adapters.custom.dio;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.brandio.ads.Controller;
import com.brandio.ads.ads.Ad;
import com.brandio.ads.ads.supers.MediaAd;
import com.brandio.ads.exceptions.DIOError;
import com.brandio.ads.listeners.AdEventListener;
import com.brandio.ads.listeners.AdRequestListener;
import com.brandio.ads.placements.InterstitialPlacement;
import com.brandio.ads.placements.Placement;
import com.brandio.ads.placements.RewardedVideoPlacement;
import com.brandio.ads.request.AdRequest;
import com.brandio.ads.tools.Utils;
import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors;
import com.ironsource.mediationsdk.model.NetworkSettings;

public class DIOCustomRewardedVideo extends BaseRewardedVideo<DIOCustomAdapter> {
    private Ad rewardedAd;
    private boolean rewardGranted;

    public DIOCustomRewardedVideo(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Context context, @NonNull RewardedVideoAdListener listener) {
        Controller controller = Controller.getInstance();
        if (!controller.isInitialized()) {
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, 0, "DIO SDK not initialized");
            return;
        }
        String placementID = adData.getString("dio_placement_id");
        if (placementID == null || placementID.isEmpty()) {
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                    AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS, "DIO SDK Missing placement ID");
            return;
        }

        Placement placement;
        try {
            placement = controller.getPlacement(placementID);
        } catch (Exception e) {
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, 0,
                    "DIO SDK Inactive placement or wrong placement ID");
            return;
        }
        if (!(placement instanceof InterstitialPlacement) && !(placement instanceof RewardedVideoPlacement)) {
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, 0,
                    "DIO SDK Trying to load a rewarded ad from non-interstitial placement");
            return;
        }

        AdRequest adRequest = placement.newAdRequest();
        adRequest.setAdRequestListener(new AdRequestListener() {
            @Override
            public void onAdReceived(Ad ad) {
                if (!(ad instanceof MediaAd)) {
                    Utils.runOnMainThread(ad::close);
                    listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, 0,
                            "DIO SDK Received a non-rewardable creative");
                    return;
                }
                rewardedAd = ad;
                rewardGranted = false;
                listener.onAdLoadSuccess();
            }

            @Override
            public void onNoAds(DIOError dioError) {
                listener.onAdLoadFailed(DIOCustomAdapter.loadErrorType(dioError), 0,
                        DIOCustomAdapter.errorMessage(dioError, "DIO SDK No fill"));
            }

            @Override
            public void onFailedToLoad(DIOError dioError) {
                listener.onAdLoadFailed(DIOCustomAdapter.loadErrorType(dioError), 0,
                        DIOCustomAdapter.errorMessage(dioError, "DIO SDK Failed to load ad"));
            }
        });
        adRequest.requestAd();
    }

    @Override
    public void showAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull RewardedVideoAdListener listener) {
        if (rewardedAd == null) {
            listener.onAdShowFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, "Missing Ad object");
            return;
        }
        rewardedAd.setEventListener(new AdEventListener() {
            @Override
            public void onShown(Ad ad) {
                listener.onAdOpened();
                listener.onAdVisible();
                listener.onAdStarted();
            }

            @Override
            public void onFailedToShow(Ad ad, DIOError error) {
                if (ad != null && ad.isImpressed()) {
                    return;
                }
                listener.onAdShowFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL,
                        DIOCustomAdapter.errorMessage(error, "DIO SDK Failed to show ad"));
            }

            @Override
            public void onClicked(Ad ad) {
                listener.onAdClicked();
            }

            @Override
            public void onAdCompleted(Ad ad) {
                listener.onAdEnded();
                if (!rewardGranted) {
                    rewardGranted = true;
                    listener.onAdRewarded();
                }
            }

            @Override
            public void onClosed(Ad ad) {
                listener.onAdClosed();
            }
        });
        rewardedAd.showAd(activity);
    }

    @Override
    public boolean isAdAvailable(@NonNull AdData adData) {
        return rewardedAd != null && !rewardedAd.isImpressed();
    }

    @Override
    public void destroyAd(@NonNull AdData adData) {
        Ad ad = rewardedAd;
        rewardedAd = null;
        if (ad != null) {
            Utils.runOnMainThread(ad::close);
        }
    }
}
