package com.brandio.ironsource_android.ui.infeed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InfeedViewModel extends ViewModel {

    private final MutableLiveData<Boolean> loadState;
    private final MutableLiveData<Boolean> showState;

    public InfeedViewModel() {
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