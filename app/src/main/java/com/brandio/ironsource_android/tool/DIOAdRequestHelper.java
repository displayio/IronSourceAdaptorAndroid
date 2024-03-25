package com.brandio.ironsource_android.tool;

import static com.ironsource.adapters.custom.dio.DIOCustomAdapter.DIO_AD_REQUEST;

import com.brandio.ads.request.AdRequest;
import com.brandio.ads.request.AdRequestBuilder;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceSegment;

import org.json.JSONObject;

public class DIOAdRequestHelper {
    // here is the example how to add customized ad request to Display.io network
    public static void addCustomAdRequestData() {
        JSONObject adRequest = new AdRequestBuilder(new AdRequest())
                .setBidFloor(55.8)
                .setUserId("USER_123")
                .body();

        IronSource.setMetaData(DIO_AD_REQUEST, adRequest.toString());
        IronSource.setNetworkData(DIO_AD_REQUEST, adRequest);
        IronSource.setDynamicUserId("setDynamicUserId");
        IronSource.setUserId("setUserId");

        IronSourceSegment ironSourceSegmen = new IronSourceSegment();
        ironSourceSegmen.setCustom(DIO_AD_REQUEST, adRequest.toString());
        IronSource.setSegment(ironSourceSegmen);
    }
}
