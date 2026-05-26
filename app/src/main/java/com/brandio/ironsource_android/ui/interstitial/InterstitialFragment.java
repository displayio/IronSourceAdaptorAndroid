package com.brandio.ironsource_android.ui.interstitial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.brandio.ironsource_android.MainActivity;
import com.brandio.ironsource_android.callback.InterstitialAdListener;
import com.brandio.ironsource_android.callback.UIControllerInterface;
import com.brandio.ironsource_android.databinding.FragmentInterstitialBinding;
import com.google.android.material.button.MaterialButton;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd;

public class InterstitialFragment extends Fragment {

    private FragmentInterstitialBinding binding;
    private MaterialButton loadInterstitial;
    private MaterialButton showInterstitial;
    private InterstitialViewModel interstitialViewModel;
    private LevelPlayInterstitialAd interstitialAd;

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
                if (!MainActivity.sdkInitialized) {
                    Toast.makeText(requireContext(), "IronSource SDK not initialized yet", Toast.LENGTH_SHORT).show();
                    return;
                }
                LevelPlay.setAdaptersDebug(true);
                interstitialAd.loadAd();
            }
        });
        showInterstitial = binding.showInterstitial;
        interstitialViewModel.getShowState().observe(getViewLifecycleOwner(), showInterstitial::setEnabled);
        showInterstitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd.isAdReady()) {
                    interstitialAd.showAd(requireActivity());
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interstitialAd = new LevelPlayInterstitialAd(MainActivity.INTERSTITIAL_AD_UNIT_ID);
        interstitialAd.setListener(new InterstitialAdListener(new UIControllerInterface() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        interstitialAd = null;
    }
}
