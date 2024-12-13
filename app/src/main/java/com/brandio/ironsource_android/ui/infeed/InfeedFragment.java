package com.brandio.ironsource_android.ui.infeed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.brandio.ironsource_android.callback.BannerAdListener;
import com.brandio.ironsource_android.callback.InterstitialAdListener;
import com.brandio.ironsource_android.callback.UIControllerInterface;
import com.brandio.ironsource_android.databinding.FragmentInfeedBinding;
import com.brandio.ironsource_android.tool.DIOAdRequestHelper;
import com.google.android.material.button.MaterialButton;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class InfeedFragment extends Fragment {

    private FragmentInfeedBinding binding;
    private MaterialButton loadInfeed;
    private MaterialButton showInfeed;
    private InfeedViewModel infeedViewModel;
    private IronSourceBannerLayout banner;

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
                IronSource.setAdaptersDebug(true);
                if (banner != null && !banner.isDestroyed()) {
                    if (banner.getParent() != null) {
                        ((ViewGroup)banner.getParent()).removeView(banner);
                        IronSource.destroyBanner(banner);
                    }
                }

//                DIOAdRequestHelper.addCustomAdRequestData();
                banner = IronSource.createBanner(getActivity(), ISBannerSize.RECTANGLE);
                banner.setLevelPlayBannerListener(new BannerAdListener(new UIControllerInterface() {
                    @Override
                    public void switchLoadButton(Boolean isEnabled) {
                        infeedViewModel.setLoadState(isEnabled);
                    }

                    @Override
                    public void switchShowButton(Boolean isEnabled) {
                        infeedViewModel.setShowState(isEnabled);
                    }
                }));
                IronSource.loadBanner(banner);
            }
        });
        showInfeed = binding.showInfeed;
        infeedViewModel.getShowState().observe(getViewLifecycleOwner(), showInfeed::setEnabled);
        showInfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infeedViewModel.setLoadState(true);
                infeedViewModel.setShowState(false);
                if (banner != null && !banner.isDestroyed()) {
                    if (banner.getParent() != null) {
                        ((ViewGroup)banner.getParent()).removeView(banner);
                    }

                    binding.getRoot().addView(banner);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) banner.getLayoutParams();
                    params.bottomToBottom = binding.getRoot().getId();
                    params.leftToLeft = binding.getRoot().getId();
                    params.rightToRight = binding.getRoot().getId();
                    banner.setLayoutParams(params);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (banner != null && !banner.isDestroyed()) {
            if (banner.getParent() != null) {
                ((ViewGroup)banner.getParent()).removeView(banner);
            }
            IronSource.destroyBanner(banner);
            banner = null;
        }
    }
}