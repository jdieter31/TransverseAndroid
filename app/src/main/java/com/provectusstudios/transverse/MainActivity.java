package com.provectusstudios.transverse;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.jirbo.adcolony.AdColony;
import com.unity3d.ads.android.IUnityAdsListener;
import com.unity3d.ads.android.UnityAds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;


public class MainActivity extends Activity implements IUnityAdsListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    MainSurfaceView surfaceView;

    public static final String client_options = "version:0.1,store:google";
    public static final String app_id = "appd1c9fabf495a455b80";
    public static final String retry_zone = "vzf4391cb1a9254f5bb7";
    public static final String interstitial_zone_id = "vz224f92177b4d4a4baf";

    public static final String unity_id = "1069658";

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;

    private GoogleApiClient gClient;

    private IInAppBillingService mService;

    private String developerPayload;

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            updatePurchase();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        AdColony.configure(this, client_options, app_id, retry_zone, interstitial_zone_id);
        UnityAds.init(this, unity_id, this);
        UnityAds.setTestMode(false);

        gClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add other APIs and scopes here as needed
                .build();

        surfaceView = new MainSurfaceView(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(surfaceView);

        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
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
    protected void onStart() {
        super.onStart();
        gClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gClient.disconnect();
    }

    //Leave all methods empty because ad sdk needs ad listener
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

    //End ad methods

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        mResolvingConnectionFailure  = true;
        if (!BaseGameUtils.resolveConnectionFailure(this,
                    gClient, connectionResult,
                    RC_SIGN_IN, "There was an issue with sign-in, please try again later.")) {
                mResolvingConnectionFailure = false;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        gClient.connect();
    }

    public GoogleApiClient getGClient() {
        return gClient;
    }

    private void updatePurchase() {
        Bundle ownedItems;
        try {
            ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
        } catch (RemoteException e) {
            e.printStackTrace();
            return;
        }
        int response = ownedItems.getInt("RESPONSE_CODE");
        if (response == 0) {
            List<String> ownedIDs = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");

            for (int i = 0; i < ownedIDs.size(); ++i) {
                String productID = ownedIDs.get(i);
                if (productID.equals("remove_ads")) {
                    setNoAds();
                    return;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    String intentDevPayload = jo.getString("developerPayload");
                    if (intentDevPayload.equals(developerPayload) && sku.equals("remove_ads")) {
                        setNoAds();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == RC_SIGN_IN) {
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                gClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.signin_failure);
            }
        }
    }

    public void purchaseNoAds() {
        developerPayload = random();
        Bundle buyIntentBundle;
        try {
            buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                    "remove_ads", "inapp", developerPayload);
        } catch (RemoteException e) {
            e.printStackTrace();
            return;
        }
        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
        try {
            startIntentSenderForResult(pendingIntent.getIntentSender(),
                    1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                    Integer.valueOf(0));
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(100);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private void setNoAds() {
        surfaceView.setNoAds();
    }
}
