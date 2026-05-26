package com.brandio.ironsource_android.ui.interscroller;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brandio.ironsource_android.MainActivity;
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
                    if (banner.getParent() != null) {
                        ((ViewGroup) banner.getParent()).removeView(banner);
                    }

                    RecyclerView recyclerView = new RecyclerView(requireActivity());
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                    recyclerView.setAdapter(new RecyclerView.Adapter() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            if (viewType != 20) {
                                View view = new View(requireActivity());
                                view.setBackgroundColor(Color.LTGRAY);
                                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                                lp.setMargins(10, 10, 10, 10);
                                view.setLayoutParams(lp);
                                return new RecyclerView.ViewHolder(view) {
                                };
                            } else {
                                return new RecyclerView.ViewHolder(banner) {
                                };
                            }
                        }

                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

                        }

                        @Override
                        public int getItemViewType(int position) {
                            return position;
                        }

                        @Override
                        public int getItemCount() {
                            return 40;
                        }
                    });

                    binding.getRoot().addView(recyclerView);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) recyclerView.getLayoutParams();
                    params.bottomToBottom = binding.getRoot().getId();
                    params.topToTop = binding.getRoot().getId();
                    params.leftToLeft = binding.getRoot().getId();
                    params.rightToRight = binding.getRoot().getId();
                    recyclerView.setLayoutParams(params);
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
