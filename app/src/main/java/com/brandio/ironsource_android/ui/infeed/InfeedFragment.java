package com.brandio.ironsource_android.ui.infeed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.brandio.ironsource_android.MainActivity;
import com.brandio.ironsource_android.callback.BannerAdListener;
import com.brandio.ironsource_android.callback.UIControllerInterface;
import com.brandio.ironsource_android.databinding.FragmentInfeedBinding;
import com.google.android.material.button.MaterialButton;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayAdSize;
import com.unity3d.mediation.banner.LevelPlayBannerAdView;

public class InfeedFragment extends Fragment {

    private FragmentInfeedBinding binding;
    private MaterialButton loadInfeed;
    private MaterialButton showInfeed;
    private InfeedViewModel infeedViewModel;
    private LevelPlayBannerAdView banner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        infeedViewModel = new ViewModelProvider(this).get(InfeedViewModel.class);

        binding = FragmentInfeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initUI();
        return root;
    }

    private void initUI() {
        loadInfeed = binding.loadInfeed;
        infeedViewModel.getLoadState().observe(getViewLifecycleOwner(), loadInfeed::setEnabled);
        loadInfeed.setOnClickListener(new View.OnClickListener() {
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
                banner = new LevelPlayBannerAdView(requireContext(), MainActivity.INFEED_AD_UNIT_ID, cfg);
                banner.setBannerListener(new BannerAdListener(new UIControllerInterface() {
                    @Override
                    public void switchLoadButton(Boolean isEnabled) {
                        infeedViewModel.setLoadState(isEnabled);
                    }

                    @Override
                    public void switchShowButton(Boolean isEnabled) {
                        infeedViewModel.setShowState(isEnabled);
                    }
                }));
                banner.loadAd();
            }
        });
        showInfeed = binding.showInfeed;
        infeedViewModel.getShowState().observe(getViewLifecycleOwner(), showInfeed::setEnabled);
        showInfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infeedViewModel.setLoadState(true);
                infeedViewModel.setShowState(false);
                if (banner != null) {
                    if (banner.getParent() != null) {
                        ((ViewGroup) banner.getParent()).removeView(banner);
                    }

                    float density = getResources().getDisplayMetrics().density;
                    int widthPx = (int) (300 * density);
                    int heightPx = (int) (250 * density);
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(widthPx, heightPx);
                    params.topToTop = binding.getRoot().getId();
                    params.bottomToBottom = binding.getRoot().getId();
                    params.leftToLeft = binding.getRoot().getId();
                    params.rightToRight = binding.getRoot().getId();
                    binding.getRoot().addView(banner, params);
                }
            }
        });
    }

    private void destroyBanner() {
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
        binding = null;
        destroyBanner();
    }
}
