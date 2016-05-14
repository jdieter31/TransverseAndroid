package com.provectusstudios.transverse;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.jirbo.adcolony.AdColony;
import com.unity3d.ads.android.IUnityAdsListener;
import com.unity3d.ads.android.UnityAds;


public class MainActivity extends Activity implements IUnityAdsListener {

    MainSurfaceView surfaceView;

    public static final String client_options = "version:0.1,store:google";
    public static final String app_id = "appd1c9fabf495a455b80";
    public static final String retry_zone = "vzf4391cb1a9254f5bb7";

    public static final String unity_id = "1069658";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdColony.configure(this, client_options, app_id, retry_zone);
        UnityAds.init(this, unity_id, this);
        //UnityAds.setDebugMode(true);
        UnityAds.setTestMode(true);

        surfaceView = new MainSurfaceView(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(surfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AdColony.pause();
        surfaceView.onPause();
    }

    @Override
    protected  void onResume() {
        super.onResume();
        AdColony.resume(this);
        UnityAds.changeActivity(this);
        surfaceView.onResume();
    }

    @Override
    public void onHide() {

    }

    @Override
    public void onShow() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoCompleted(String itemKey, boolean skipped) {

    }

    @Override
    public void onFetchCompleted() {

    }

    @Override
    public void onFetchFailed() {

    }
}
