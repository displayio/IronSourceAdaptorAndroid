package com.brandio.ironsource_android.ui.interstitial;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InterstitialViewModel extends ViewModel {

    private final MutableLiveData<Boolean> loadState;
    private final MutableLiveData<Boolean> showState;

    public InterstitialViewModel() {
        loadState = new MutableLiveData<>();
        loadState.setValue(true);
        showState = new MutableLiveData<>();
        showState.setValue(false);
    }

    public LiveData<Boolean> getLoadState() {
        return loadState;
    }
    public LiveData<Boolean> getShowState() {
        return showState;
    }
    public void setLoadState(Boolean state) {
         loadState.setValue(state);
    }
    public void setShowState(Boolean state) {
        showState.setValue(state);
    }
}