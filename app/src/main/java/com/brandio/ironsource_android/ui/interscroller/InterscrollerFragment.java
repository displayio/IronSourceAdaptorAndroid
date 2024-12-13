package com.brandio.ironsource_android.ui.interscroller;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brandio.ironsource_android.callback.BannerAdListener;
import com.brandio.ironsource_android.callback.UIControllerInterface;
import com.brandio.ironsource_android.databinding.FragmentInfeedBinding;
import com.brandio.ironsource_android.databinding.FragmentInterscrollerBinding;
import com.brandio.ironsource_android.tool.DIOAdRequestHelper;
import com.brandio.ironsource_android.ui.infeed.InfeedViewModel;
import com.google.android.material.button.MaterialButton;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class InterscrollerFragment extends Fragment {

    private FragmentInterscrollerBinding binding;
    private MaterialButton loadInterscroller;
    private MaterialButton showInterscroller;
    private InterscrollerViewModel interscrollerViewModel;
    private IronSourceBannerLayout banner;

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
                IronSource.setAdaptersDebug(true);
                if (banner != null && !banner.isDestroyed()) {
                    if (banner.getParent() != null) {
                        ((ViewGroup) banner.getParent()).removeView(banner);
                        IronSource.destroyBanner(banner);
                    }
                }
//                DIOAdRequestHelper.addCustomAdRequestData();
                banner = IronSource.createBanner(getActivity(), ISBannerSize.RECTANGLE);
                banner.setLevelPlayBannerListener(new BannerAdListener(new UIControllerInterface() {
                    @Override
                    public void switchLoadButton(Boolean isEnabled) {
                        interscrollerViewModel.setLoadState(isEnabled);
                    }

                    @Override
                    public void switchShowButton(Boolean isEnabled) {
                        interscrollerViewModel.setShowState(isEnabled);
                    }
                }));
                IronSource.loadBanner(banner);
            }
        });
        showInterscroller = binding.showInterscroller;
        interscrollerViewModel.getShowState().observe(getViewLifecycleOwner(), showInterscroller::setEnabled);
        showInterscroller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interscrollerViewModel.setLoadState(true);
                interscrollerViewModel.setShowState(false);
                if (banner != null && !banner.isDestroyed()) {
                    if (banner.getParent() != null) {
                        ((ViewGroup) banner.getParent()).removeView(banner);
                    }

                    RecyclerView recyclerView = new RecyclerView(getActivity());
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(new RecyclerView.Adapter() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            if (viewType != 20) {
                                View view = new View(getActivity());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (banner != null && !banner.isDestroyed()) {
            if (banner.getParent() != null) {
                ((ViewGroup) banner.getParent()).removeView(banner);
            }
            IronSource.destroyBanner(banner);
            banner = null;
        }
    }
}