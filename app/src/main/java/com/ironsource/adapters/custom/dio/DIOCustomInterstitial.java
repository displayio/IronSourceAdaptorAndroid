package com.ironsource.adapters.custom.dio;

import static com.ironsource.adapters.custom.dio.DIOCustomAdapter.DIO_AD_REQUEST;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.brandio.ads.Controller;
import com.brandio.ads.ads.Ad;
import com.brandio.ads.ads.Interstitial;
import com.brandio.ads.exceptions.DIOError;
import com.brandio.ads.listeners.AdEventListener;
import com.brandio.ads.listeners.AdRequestListener;
import com.brandio.ads.placements.InterstitialPlacement;
import com.brandio.ads.placements.Placement;
import com.brandio.ads.request.AdRequest;
import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors;
import com.ironsource.mediationsdk.model.NetworkSettings;

import java.util.Map;

public class DIOCustomInterstitial extends BaseInterstitial<DIOCustomAdapter> {
    private Activity activity;
    private Ad interstitialAd;

    public DIOCustomInterstitial(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull InterstitialAdListener listener) {
        this.activity = activity;
        Controller controller = Controller.getInstance();
        if (!controller.isInitialized()) {
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, 0, "DIO SDK not initialized");
            return;
        }
        String placementID = adData.getString("dio_placement_id");
        if (placementID == null || placementID.isEmpty()) {
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, 0, "DIO SDK Missing placement ID");
            return;
        }

        Placement placement;
        try {
            placement = Controller.getInstance().getPlacement(placementID);
        } catch (Exception e) {
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, 0,
                    "DIO SDK Inactive placement or wrong placement ID");
            return;
        }
        if (!(placement instanceof InterstitialPlacement)) {
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, 0,
                    "DIO SDK Trying to load an interstitial ad from non-interstitial placement");
            return;
        }

        Map<String, Object> map1 = adData.getAdUnitData();
        Map<String, Object> map2 = adData.getConfiguration();
        String adRequestString = adData.getString(DIO_AD_REQUEST);
        String serverData = adData.getServerData();

        AdRequest adRequest = placement.newAdRequest();
        adRequest.setAdRequestListener(new AdRequestListener() {
            @Override
            public void onAdReceived(Ad ad) {
                interstitialAd = ad;
                listener.onAdLoadSuccess();
            }

            @Override
            public void onNoAds(DIOError dioError) {
                listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, 0,
                        "DIO SDK No fill");
            }

            @Override
            public void onFailedToLoad(DIOError dioError) {
                listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, 0,
                        "DIO SDK Failed to load ad");
            }
        });
        adRequest.requestAd();
    }

    @Override
    public void showAd(@NonNull AdData adData, @NonNull InterstitialAdListener listener) {
        if (interstitialAd == null || activity == null) {
            listener.onAdShowFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, "Missing Ad or Context object");
        }
        interstitialAd.setEventListener(new AdEventListener() {
            @Override
            public void onShown(Ad ad) {
                listener.onAdOpened();
                listener.onAdShowSuccess();
                listener.onAdVisible();
                if (ad instanceof Interstitial.InterstitialVideo) {
                    listener.onAdStarted();
                }
            }

            @Override
            public void onFailedToShow(Ad ad) {
                listener.onAdShowFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, "Failed to show ad");
            }

            @Override
            public void onClicked(Ad ad) {
                listener.onAdClicked();
            }

            @Override
            public void onClosed(Ad ad) {
                listener.onAdClosed();
                if (ad instanceof Interstitial.InterstitialVideo) {
                    listener.onAdEnded();
                }
            }

            @Override
            public void onAdCompleted(Ad ad) {
                if (ad instanceof Interstitial.InterstitialVideo) {
                    listener.onAdEnded();
                }
            }
        });
        interstitialAd.showAd(activity);

    }

    @Override
    public boolean isAdAvailable(@NonNull AdData adData) {
        return interstitialAd != null && !interstitialAd.isImpressed();
    }
}
