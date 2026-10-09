package com.brandio.ironsource_android.ui.interscroller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.brandio.ironsource_android.MainActivity;
import com.brandio.ironsource_android.ui.AdFeed;
import com.brandio.ironsource_android.callback.BannerAdListener;
import com.brandio.ironsource_android.callback.UIControllerInterface;
import com.brandio.ironsource_android.databinding.FragmentInterscrollerBinding;
import com.google.android.material.button.MaterialButton;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayAdSize;
import com.unity3d.mediation.banner.LevelPlayBannerAdView;

public class InterscrollerFragment extends Fragment {

    private FragmentInterscrollerBinding binding;
    private MaterialButton loadInterscroller;
    private MaterialButton showInterscroller;
    private InterscrollerViewModel interscrollerViewModel;
    private LevelPlayBannerAdView banner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        interscrollerViewModel =
                new ViewModelProvider(this).get(InterscrollerViewModel.class);

        binding = FragmentInterscrollerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initUI();
        return root;
    }

    private void initUI() {
        loadInterscroller = binding.loadInterscroller;
        interscrollerViewModel.getLoadState().observe(getViewLifecycleOwner(), loadInterscroller::setEnabled);
        loadInterscroller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MainActivity.sdkInitialized) {
                    Toast.makeText(requireContext(), "IronSource SDK not initialized yet", Toast.LENGTH_SHORT).show();
                    return;
                }
                LevelPlay.setAdaptersDebug(true);
                destroyBanner();

                LevelPlayBannerAdView.Config cfg = new LevelPlayBannerAdView.Config.Builder()
                        .setAdSize(LevelPlayAdSize.MEDIUM_RECTANGLE)
                        .build();
                banner = new LevelPlayBannerAdView(requireContext(), MainActivity.INTERSCROLLER_AD_UNIT_ID, cfg);
                banner.setBannerListener(new BannerAdListener(new UIControllerInterface() {
                    @Override
                    public void switchLoadButton(Boolean isEnabled) {
                        interscrollerViewModel.setLoadState(isEnabled);
                    }

                    @Override
                    public void switchShowButton(Boolean isEnabled) {
                        interscrollerViewModel.setShowState(isEnabled);
                    }
                }));
                banner.loadAd();
            }
        });
        showInterscroller = binding.showInterscroller;
        interscrollerViewModel.getShowState().observe(getViewLifecycleOwner(), showInterscroller::setEnabled);
        showInterscroller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interscrollerViewModel.setLoadState(true);
                interscrollerViewModel.setShowState(false);
                if (banner != null) {
                    binding.adContainer.removeAllViews();
                    binding.adContainer.addView(AdFeed.create(requireContext(), banner, null));
                }
            }
        });
    }

    private void destroyBanner() {
        if (binding != null) {
            binding.adContainer.removeAllViews();
        }
        if (banner != null) {
            if (banner.getParent() != null) {
                ((ViewGroup) banner.getParent()).removeView(banner);
            }
            banner.destroy();
            banner = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyBanner();
        binding = null;
    }
}
