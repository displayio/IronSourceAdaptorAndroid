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

    private static final String APP_KEY = "1dc3f8185";
    public static final String INTERSTITIAL_AD_UNIT_ID = "p6g336rjv5poqikz";
    public static final String INFEED_AD_UNIT_ID = "q6ksnqakrnj7f4sm";
    public static final String INTERSCROLLER_AD_UNIT_ID = "kuq0ay1tewbl178z";
    public static final String NATIVE_AD_UNIT_ID = "jdhoydzd301ctp5c";
    public static final String REWARDED_AD_UNIT_ID = "dfmt05knamyyvgg5";
    public static final String TAG = "IS_Integration";
    public static final String IS_VER = "IronSource SDK Version: " + LevelPlay.getSdkVersion();
    public static final String DIO_VER = "DisplayIO SDK Version: " + Controller.getInstance().getVer();
    public static volatile boolean sdkInitialized = false;
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
                R.id.navigation_interstitial, R.id.navigation_interscroller, R.id.navigation_infeed)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        TextView versionIS = binding.versionIsTxt;
        versionIS.setText(IS_VER);
        TextView versionDIO = binding.versionDioTxt;
        versionDIO.setText(DIO_VER);
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