package com.brandio.ironsource_android;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.brandio.ads.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.brandio.ironsource_android.databinding.ActivityMainBinding;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayConfiguration;
import com.unity3d.mediation.LevelPlayInitError;
import com.unity3d.mediation.LevelPlayInitListener;
import com.unity3d.mediation.LevelPlayInitRequest;

public class MainActivity extends AppCompatActivity {

    private static final String APP_KEY = "2899bebd5";
    public static final String INTERSTITIAL_AD_UNIT_ID = "24j8ex6kkizmok0c";
    public static final String BANNER_AD_UNIT_ID = "png3j7cbl3vopz1x";
    public static final String INFEED_AD_UNIT_ID = "82fh448z71tmdi71";
    public static final String INTERSCROLLER_AD_UNIT_ID = "fkh04nw0iei42yl3";
    public static final String NATIVE_AD_UNIT_ID = "fv8mnlt48901zu4z";
    public static final String REWARDED_AD_UNIT_ID = "juhbq4gbwkgiy69h";
    public static final String TAG = "IS_Integration";
    public static final String IS_VER = "IronSource SDK Version: " + LevelPlay.getSdkVersion();
    public static final String DIO_VER = "DisplayIO SDK Version: " + Controller.getInstance().getVer();
    public static volatile boolean sdkInitialized = false;
    public static volatile boolean useFeed = true;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUI();
        initIronSourceSDK();
    }

    private void initUI() {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_interstitial, R.id.navigation_rewarded, R.id.navigation_banner, R.id.navigation_interscroller, R.id.navigation_infeed)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        TextView versionIS = binding.versionIsTxt;
        versionIS.setText(IS_VER);
        TextView versionDIO = binding.versionDioTxt;
        versionDIO.setText(DIO_VER);
        binding.useFeed.setChecked(useFeed);
        binding.useFeed.setOnCheckedChangeListener((buttonView, isChecked) -> useFeed = isChecked);
    }

    private void initIronSourceSDK() {
        LevelPlayInitRequest initRequest = new LevelPlayInitRequest.Builder(APP_KEY)
//                .withUserId("Some_User_ID")
                .build();
        LevelPlayInitListener initListener = new LevelPlayInitListener() {
            @Override
            public void onInitFailed(@NonNull LevelPlayInitError error) {
                Toast.makeText(MainActivity.this, "IronSource SDK init FAILED", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "IronSource SDK init FAILED: " + error.getErrorMessage());
                Log.e(TAG, "IronSource SDK init FAILED: " + error.getErrorCode());
            }
            @Override
            public void onInitSuccess(LevelPlayConfiguration configuration) {
                sdkInitialized = true;
                Toast.makeText(MainActivity.this, "IronSource SDK initialized", Toast.LENGTH_SHORT).show();
                LevelPlay.validateIntegration(MainActivity.this);
            }
        };
        LevelPlay.init(MainActivity.this, initRequest, initListener);
    }
}