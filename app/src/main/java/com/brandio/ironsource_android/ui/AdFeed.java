package com.brandio.ironsource_android.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class AdFeed {
    public static final int AD_POSITION = 5;
    private static final int ITEM_COUNT = 40;
    private static final int TYPE_CONTENT = 0;
    private static final int TYPE_AD = 1;

    private AdFeed() {
    }

    public static RecyclerView create(Context context, View adView, ViewGroup.LayoutParams adParams) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == TYPE_AD) {
                    if (adView.getParent() != null) {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }
                    if (adParams == null) {
                        return new RecyclerView.ViewHolder(adView) {
                        };
                    }
                    FrameLayout slot = new FrameLayout(context);
                    slot.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(adParams.width, adParams.height, Gravity.CENTER_HORIZONTAL);
                    slot.addView(adView, lp);
                    return new RecyclerView.ViewHolder(slot) {
                    };
                }
                View view = new View(context);
                view.setBackgroundColor(Color.LTGRAY);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                lp.setMargins(10, 10, 10, 10);
                view.setLayoutParams(lp);
                return new RecyclerView.ViewHolder(view) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            }

            @Override
            public int getItemViewType(int position) {
                return position == AD_POSITION ? TYPE_AD : TYPE_CONTENT;
            }

            @Override
            public int getItemCount() {
                return ITEM_COUNT;
            }
        });
        return recyclerView;
    }
}
