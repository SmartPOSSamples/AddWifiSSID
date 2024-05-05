package com.smartpos.addwifissid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Bundle;
import android.os.PatternMatcher;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WifiAdmin wifiAdmin = new WifiAdmin(this);
        wifiAdmin.closeWifi();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            NetworkSpecifier specifier =
                    new WifiNetworkSpecifier.Builder()
                            .setSsidPattern(new PatternMatcher("cloudpos", PatternMatcher.PATTERN_PREFIX))
                            .setWpa2Passphrase("cloudpostest")
                            .build();

            NetworkRequest request =
                    new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .setNetworkSpecifier(specifier)
                            .build();

            ConnectivityManager connectivityManager = (ConnectivityManager)
                    this.getSystemService(Context.CONNECTIVITY_SERVICE);

            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    // do success processing here..
                }

                @Override
                public void onUnavailable() {
                    // do failure processing here..
                }
            };
            connectivityManager.requestNetwork(request, networkCallback);
            // Release the request when done.
            // connectivityManager.unregisterNetworkCallback(networkCallback);
        }


        WifiNetworkSuggestion suggestion1 = null,suggestion2 = null,suggestion3 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            suggestion1 = new WifiNetworkSuggestion.Builder()
                    .setSsid("test111111")
                    .build();
            suggestion2 = new WifiNetworkSuggestion.Builder()
                    .setSsid("test222222")
                    .setWpa2Passphrase("test123456")
                    .build();
            suggestion3 = new WifiNetworkSuggestion.Builder()
                    .setSsid("test333333")
                    .setWpa3Passphrase("test6789")
                    .build();
        }

        final List<WifiNetworkSuggestion> suggestionsList = new ArrayList<WifiNetworkSuggestion>();
        suggestionsList.add(suggestion1);
        suggestionsList.add(suggestion2);
        suggestionsList.add(suggestion3);

        final WifiManager wifiManager =
                (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        int status = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            status = wifiManager.addNetworkSuggestions(suggestionsList);
            if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
// do error handling here…
            }
        }

// Optional (Wait for post connection broadcast to one of your suggestions)
        final IntentFilter intentFilter =
                new IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);

        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getAction().equals(
                        WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                    return;
                }
                // do post connect processing here...
            }
        };
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

}