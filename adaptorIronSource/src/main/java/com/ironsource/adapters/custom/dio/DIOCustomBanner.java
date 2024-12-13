package com.ironsource.adapters.custom.dio;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.brandio.ads.Controller;
import com.brandio.ads.ads.Ad;
import com.brandio.ads.ads.AdUnit;
import com.brandio.ads.containers.BannerContainer;
import com.brandio.ads.containers.InfeedContainer;
import com.brandio.ads.containers.InterscrollerContainer;
import com.brandio.ads.containers.MediumRectangleContainer;
import com.brandio.ads.exceptions.DIOError;
import com.brandio.ads.listeners.AdEventListener;
import com.brandio.ads.listeners.AdRequestListener;
import com.brandio.ads.placements.BannerPlacement;
import com.brandio.ads.placements.InfeedPlacement;
import com.brandio.ads.placements.InterscrollerPlacement;
import com.brandio.ads.placements.MediumRectanglePlacement;
import com.brandio.ads.placements.Placement;
import com.brandio.ads.request.AdRequest;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.adunit.adapter.BaseBanner;
import com.ironsource.mediationsdk.adunit.adapter.listener.BannerAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;

public class DIOCustomBanner extends BaseBanner<DIOCustomAdapter> {
    private Ad bannerDIOAd;
    private Ad mrectDIOAd;
    private Ad infeedDIOAd;
    private Ad intersrollerDIOAd;

    public DIOCustomBanner(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull ISBannerSize isBannerSize, @NonNull BannerAdListener listener) {

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

        AdRequest adRequest = placement.newAdRequest();
        final String adRequestId = adRequest.getId();

        adRequest.setAdRequestListener(new AdRequestListener() {
            @Override
            public void onAdReceived(Ad ad) {
                ViewGroup adView = null;
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        AdUnit.getPxToDp(300), AdUnit.getPxToDp(250));
                if (placement instanceof BannerPlacement) {
                    bannerDIOAd = ad;
                    BannerContainer bannerContainer = ((BannerPlacement) placement).getContainer(adRequestId);
                    adView = BannerContainer.getAdView(activity);
                    bannerContainer.bindTo(adView);
                    layoutParams = new FrameLayout.LayoutParams(
                            AdUnit.getPxToDp(320), AdUnit.getPxToDp(50));
                } else if (placement instanceof MediumRectanglePlacement) {
                    mrectDIOAd = ad;
                    MediumRectangleContainer mediumRectangleContainer = ((MediumRectanglePlacement) placement)
                            .getContainer(adRequestId);
                    adView = BannerContainer.getAdView(activity);
                    mediumRectangleContainer.bindTo(adView);
                } else if (placement instanceof InfeedPlacement) {
                    infeedDIOAd = ad;
                    adView = InfeedContainer.getAdView(activity);
                    InfeedContainer infeedContainer =
                            ((InfeedPlacement) placement).getContainer(adRequestId);
                    infeedContainer.bindTo(adView);
                } else if (placement instanceof InterscrollerPlacement) {
                    intersrollerDIOAd = ad;
                    adView = InterscrollerContainer.getAdView(activity);
                    adView.setId(Integer.parseInt(ad.getPlacementId()));
                    InterscrollerContainer interscrollerContainer =
                            ((InterscrollerPlacement) placement).getContainer(adRequestId);
                    try {
                        interscrollerContainer.bindTo(adView);
                    } catch (Exception e) {
                        listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, 0,
                                "DIO SDK Failed to create ad view");
                        e.printStackTrace();
                    }
                    layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                }

                if (adView != null) {
                    ad.setEventListener(
                            new AdEventListener() {
                                @Override
                                public void onShown(Ad ad) {
                                    listener.onAdOpened();
                                    listener.onAdScreenPresented();
                                }

                                @Override
                                public void onFailedToShow(Ad ad) {
                                    listener.onAdScreenDismissed();
                                }

                                @Override
                                public void onClicked(Ad ad) {
                                    listener.onAdClicked();
                                }

                                @Override
                                public void onClosed(Ad ad) {
                                    listener.onAdScreenDismissed();
                                    listener.onAdLeftApplication();
                                }
                            }
                    );
                    listener.onAdLoadSuccess(adView, layoutParams);
                } else {
                    listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, 0,
                            "DIO SDK Failed to load ad");
                }
            }

            @Override
            public void onFailedToLoad(DIOError dioError) {
                listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, 0,
                        "DIO SDK Failed to load ad");
            }

            @Override
            public void onNoAds(DIOError dioError) {
                listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, 0,
                        "DIO SDK No fill");
            }
        });
        adRequest.requestAd();
    }

    @Override
    public void destroyAd(@NonNull AdData adData) {
        if (bannerDIOAd != null && bannerDIOAd.isImpressed()) {
            bannerDIOAd.close();
            bannerDIOAd = null;
        }
        if (mrectDIOAd != null && mrectDIOAd.isImpressed()) {
            mrectDIOAd.close();
            mrectDIOAd = null;
        }
        if (infeedDIOAd != null && infeedDIOAd.isImpressed()) {
            infeedDIOAd.close();
            infeedDIOAd = null;
        }
        if (intersrollerDIOAd != null && intersrollerDIOAd.isImpressed()) {
            intersrollerDIOAd.close();
            intersrollerDIOAd = null;
        }
    }
}
