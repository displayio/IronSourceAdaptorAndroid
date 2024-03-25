package com.brandio.ironsource_android;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.brandio.ads.Controller;
import com.brandio.ads.exceptions.DIOError;
import com.brandio.ads.listeners.SdkInitListener;
import com.brandio.ironsource_android.callback.InterstitialAdListener;
import com.brandio.ironsource_android.callback.UIControllerInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.brandio.ironsource_android.databinding.ActivityMainBinding;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.sdk.InitializationListener;
import com.ironsource.mediationsdk.utils.IronSourceUtils;

public class MainActivity extends AppCompatActivity {

    private static final String APP_KEY = "1dc3f8185";
    public static final String TAG = "IS_Integration";
    public static final String IS_VER = "IronSource SDK Version: " + IronSourceUtils.getSDKVersion();
    public static final String DIO_VER = "DisplayIO SDK Version: " + Controller.getInstance().getVer();
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
        IronSource.init(this, APP_KEY, new InitializationListener() {
            @Override
            public void onInitializationComplete() {
                Log.e(TAG, "IronSource SDK initialized");
                IntegrationHelper.validateIntegration(MainActivity.this);
                Toast.makeText(MainActivity.this, "IronSource SDK initialized", Toast.LENGTH_SHORT).show();

                Controller.getInstance().init(MainActivity.this, "7729", new SdkInitListener() {
                    @Override
                    public void onInit() {
                        Toast.makeText(MainActivity.this, "DIO SDK initialized", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onInitError(DIOError dioError) {
                        Toast.makeText(MainActivity.this, "DIO SDK init error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

}