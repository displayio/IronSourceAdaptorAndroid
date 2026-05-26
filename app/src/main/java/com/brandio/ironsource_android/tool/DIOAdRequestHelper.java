package com.brandio.ironsource_android.tool;

import static com.ironsource.adapters.custom.dio.DIOCustomAdapter.DIO_AD_REQUEST;

import com.brandio.ads.request.AdRequest;
import com.brandio.ads.request.AdRequestBuilder;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.segment.LevelPlaySegment;

import org.json.JSONObject;

public class DIOAdRequestHelper {
    // here is the example how to add customized ad request to Display.io network
    public static void addCustomAdRequestData() {
        JSONObject adRequest = new AdRequestBuilder(new AdRequest())
                .setBidFloor(55.8)
                .setUserId("USER_123")
                .body();

        LevelPlay.setMetaData(DIO_AD_REQUEST, adRequest.toString());
        LevelPlay.setNetworkData(DIO_AD_REQUEST, adRequest);
        LevelPlay.setDynamicUserId("setDynamicUserId");

        LevelPlaySegment segment = new LevelPlaySegment();
        segment.setCustom(DIO_AD_REQUEST, adRequest.toString());
        LevelPlay.setSegment(segment);
    }
}
