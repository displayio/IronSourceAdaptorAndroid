package com.brandio.ironsource_android.ui.interstitial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.brandio.ironsource_android.callback.InterstitialAdListener;
import com.brandio.ironsource_android.callback.UIControllerInterface;
import com.brandio.ironsource_android.databinding.FragmentInterstitialBinding;
import com.brandio.ironsource_android.tool.DIOAdRequestHelper;
import com.google.android.material.button.MaterialButton;
import com.ironsource.mediationsdk.IronSource;

import org.json.JSONException;
import org.json.JSONObject;

public class InterstitialFragment extends Fragment {

    private FragmentInterstitialBinding binding;
    private MaterialButton loadInterstitial;
    private MaterialButton showInterstitial;
    private InterstitialViewModel interstitialViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        interstitialViewModel =
                new ViewModelProvider(this).get(InterstitialViewModel.class);

        binding = FragmentInterstitialBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initUI();
        return root;
    }

    private void initUI() {
        loadInterstitial = binding.loadInterstitial;
        interstitialViewModel.getLoadState().observe(getViewLifecycleOwner(), loadInterstitial::setEnabled);
        loadInterstitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DIOAdRequestHelper.addCustomAdRequestData();
                IronSource.setAdaptersDebug(true);
                IronSource.loadInterstitial();
            }
        });
        showInterstitial = binding.showInterstitial;
        interstitialViewModel.getShowState().observe(getViewLifecycleOwner(), showInterstitial::setEnabled);
        showInterstitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IronSource.showInterstitial();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IronSource.setLevelPlayInterstitialListener(new InterstitialAdListener(new UIControllerInterface() {
            @Override
            public void switchLoadButton(Boolean isEnabled) {
                interstitialViewModel.setLoadState(isEnabled);
            }

            @Override
            public void switchShowButton(Boolean isEnabled) {
                interstitialViewModel.setShowState(isEnabled);
            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}