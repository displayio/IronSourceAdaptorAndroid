package com.brandio.ironsource_android.ui.rewarded;

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
import com.brandio.ironsource_android.callback.RewardedAdListener;
import com.brandio.ironsource_android.callback.UIControllerInterface;
import com.brandio.ironsource_android.databinding.FragmentRewardedBinding;
import com.google.android.material.button.MaterialButton;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.rewarded.LevelPlayRewardedAd;

public class RewardedFragment extends Fragment {

    private FragmentRewardedBinding binding;
    private MaterialButton loadRewarded;
    private MaterialButton showRewarded;
    private RewardedViewModel rewardedViewModel;
    private LevelPlayRewardedAd rewardedAd;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rewardedViewModel =
                new ViewModelProvider(this).get(RewardedViewModel.class);

        binding = FragmentRewardedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initUI();
        return root;
    }

    private void initUI() {
        loadRewarded = binding.loadRewarded;
        rewardedViewModel.getLoadState().observe(getViewLifecycleOwner(), loadRewarded::setEnabled);
        loadRewarded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MainActivity.sdkInitialized) {
                    Toast.makeText(requireContext(), "IronSource SDK not initialized yet", Toast.LENGTH_SHORT).show();
                    return;
                }
                LevelPlay.setAdaptersDebug(true);
                rewardedAd.loadAd();
            }
        });
        showRewarded = binding.showRewarded;
        rewardedViewModel.getShowState().observe(getViewLifecycleOwner(), showRewarded::setEnabled);
        showRewarded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardedAd.isAdReady()) {
                    rewardedAd.showAd(requireActivity());
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rewardedAd = new LevelPlayRewardedAd(MainActivity.REWARDED_AD_UNIT_ID);
        rewardedAd.setListener(new RewardedAdListener(new UIControllerInterface() {
            @Override
            public void switchLoadButton(Boolean isEnabled) {
                rewardedViewModel.setLoadState(isEnabled);
            }

            @Override
            public void switchShowButton(Boolean isEnabled) {
                rewardedViewModel.setShowState(isEnabled);
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
        rewardedAd = null;
    }
}
