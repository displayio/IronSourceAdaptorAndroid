package com.brandio.ironsource_android.ui.banner;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.brandio.ironsource_android.MainActivity;
import com.brandio.ironsource_android.ui.AdFeed;
import com.brandio.ironsource_android.callback.BannerAdListener;
import com.brandio.ironsource_android.callback.UIControllerInterface;
import com.brandio.ironsource_android.databinding.FragmentBannerBinding;
import com.google.android.material.button.MaterialButton;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayAdSize;
import com.unity3d.mediation.banner.LevelPlayBannerAdView;

public class BannerFragment extends Fragment {

    private FragmentBannerBinding binding;
    private MaterialButton loadBanner;
    private MaterialButton showBanner;
    private BannerViewModel bannerViewModel;
    private LevelPlayBannerAdView banner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bannerViewModel = new ViewModelProvider(this).get(BannerViewModel.class);

        binding = FragmentBannerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initUI();
        return root;
    }

    private void initUI() {
        loadBanner = binding.loadBanner;
        bannerViewModel.getLoadState().observe(getViewLifecycleOwner(), loadBanner::setEnabled);
        loadBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MainActivity.sdkInitialized) {
                    Toast.makeText(requireContext(), "IronSource SDK not initialized yet", Toast.LENGTH_SHORT).show();
                    return;
                }
                LevelPlay.setAdaptersDebug(true);
                destroyBanner();

                LevelPlayBannerAdView.Config cfg = new LevelPlayBannerAdView.Config.Builder()
                        .setAdSize(LevelPlayAdSize.BANNER)
                        .build();
                banner = new LevelPlayBannerAdView(requireContext(), MainActivity.BANNER_AD_UNIT_ID, cfg);
                banner.setBannerListener(new BannerAdListener(new UIControllerInterface() {
                    @Override
                    public void switchLoadButton(Boolean isEnabled) {
                        bannerViewModel.setLoadState(isEnabled);
                    }

                    @Override
                    public void switchShowButton(Boolean isEnabled) {
                        bannerViewModel.setShowState(isEnabled);
                    }
                }));
                banner.loadAd();
            }
        });
        showBanner = binding.showBanner;
        bannerViewModel.getShowState().observe(getViewLifecycleOwner(), showBanner::setEnabled);
        showBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bannerViewModel.setLoadState(true);
                bannerViewModel.setShowState(false);
                if (banner != null) {
                    float density = getResources().getDisplayMetrics().density;
                    showAd((int) (320 * density), (int) (50 * density));
                }
            }
        });
    }

    private void showAd(int widthPx, int heightPx) {
        binding.adContainer.removeAllViews();
        if (banner.getParent() != null) {
            ((ViewGroup) banner.getParent()).removeView(banner);
        }
        if (MainActivity.useFeed) {
            binding.adContainer.addView(AdFeed.create(requireContext(), banner, new ViewGroup.LayoutParams(widthPx, heightPx)));
        } else {
            binding.adContainer.addView(banner, new FrameLayout.LayoutParams(widthPx, heightPx, Gravity.CENTER_HORIZONTAL));
        }
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
