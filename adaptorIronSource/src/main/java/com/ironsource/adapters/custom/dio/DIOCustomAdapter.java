package com.ironsource.adapters.custom.dio;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.brandio.ads.Controller;
import com.brandio.ads.exceptions.DIOError;
import com.brandio.ads.listeners.SdkInitListener;
import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors;

public class DIOCustomAdapter extends BaseAdapter {
    public static final String DIO_AD_REQUEST = "dioAdRequest";

    @Override
    public void init(@NonNull AdData adData, @NonNull Context context, @Nullable NetworkInitializationListener networkInitializationListener) {
        if (networkInitializationListener == null) {
            return;
        }
        Controller controller = Controller.getInstance();
        if (controller.isInitialized()) {
            networkInitializationListener.onInitSuccess();
            return;
        }
        String appID = adData.getString("dio_app_id");
        if (appID == null || appID.isEmpty()) {
            networkInitializationListener.onInitFailed(AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS, "Missing App ID");
            return;
        }
        controller.init(context, appID, new SdkInitListener() {
            @Override
            public void onInit() {
                networkInitializationListener.onInitSuccess();
            }

            @Override
            public void onInitError(DIOError dioError) {
                networkInitializationListener.onInitFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, dioError.getMessage());
            }
        });
    }

    @Nullable
    @Override
    public String getNetworkSDKVersion() {
        return Controller.getInstance().getVer();
    }

    @NonNull
    @Override
    public String getAdapterVersion() {
        return Controller.getInstance().getVer();
    }
}
