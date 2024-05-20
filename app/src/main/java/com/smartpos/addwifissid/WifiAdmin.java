package com.smartpos.addwifissid;

/**
 * create by rf.w 19-4-12下午5:52
 */

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WifiAdmin {
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfiguration;
    private WifiLock mWifiLock;
    private Context context;

    public WifiAdmin(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        this.context = context;
    }

    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            Toast.makeText(context, "WIFI_STATE_DISABLED", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
            Toast.makeText(context, "WIFI_STATE_DISABLING", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "TRY AGAIN", Toast.LENGTH_SHORT).show();
        }
    }

    // CHECK WIFI STATUS
    public void checkState() {
        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
            Toast.makeText(context, "WIFI_STATE_DISABLING", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            Toast.makeText(context, "WIFI_STATE_DISABLED", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            Toast.makeText(context, "WIFI_STATE_ENABLING", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            Toast.makeText(context, "WIFI_STATE_ENABLED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "NO STATUS", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean getWifiIsOpen() {
        return mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
    }

    public void startScan() {
        boolean scanResult = mWifiManager.startScan();
        Logger.debug("boolean scanResult = " + scanResult);
    }

    public void getScanResults() {
        List<ScanResult> results = mWifiManager.getScanResults();
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
        if (results != null) {
            Logger.debug("startScan results " + results.size());
            mWifiList = new ArrayList<>();
            StringBuilder listResult = new StringBuilder();
            for (ScanResult result : results) {
                if (result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]")) {
                    continue;
                }
                if (!listResult.toString().contains(result.SSID)) {
                    listResult.append(result.SSID).append(";");
                    int level = WifiManager.calculateSignalLevel(result.level, 4);
                    mWifiList.add(result);
                }
            }
            mWifiList.sort(new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult o1, ScanResult o2) {
                    if (WifiManager.calculateSignalLevel(o1.level, 4) >= WifiManager.calculateSignalLevel(o2.level, 4)) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
//            Logger.debug("mWifiList " + mWifiList.size());
        }
    }

    public List<ScanResult> getWifiList() {
//        Logger.debug("mWifiList.size " + mWifiList.size());
        return mWifiList;
    }

    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_").append(Integer.valueOf(i + 1).toString()).append(":");
            stringBuilder.append((mWifiList.get(i)).toString());
        }
        return stringBuilder;
    }

    public String getMacAddress() {
        return (mWifiInfo == null) ? null : mWifiInfo.getMacAddress();
    }

    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    public String getWifiSSID() {
        return (mWifiInfo == null) ? null : mWifiInfo.getSSID();
    }

    public String getWifiInfo() {
        return (mWifiInfo == null) ? null : mWifiInfo.toString();
    }

    // add wifi and connect wifi
    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean enableNetwork = mWifiManager.enableNetwork(wcgID, true);
        Logger.debug("addNetwork wcgID = " + wcgID);
        Logger.debug("addNetwork enableNetwork =" + enableNetwork);
        return enableNetwork;
    }

    // removeWifi
    public void removeWifi(int netId) {
        Logger.debug("removeWifi =" + netId);
        disconnectWifi(netId);
//        mWifiManager.forget(netId, null);
    }

    // disconnectWifi
    public void disconnectWifi(int netId) {
        Logger.debug("disconnectWifi =" + netId);
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    public void removeNetwork(WifiConfiguration tempConfig) {
        if (tempConfig != null) {
            Logger.debug("removeNetwork =" + tempConfig.networkId);
//            mWifiManager.forget(tempConfig.networkId, null);
        }
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = SSID;//no used  "\"" + SSID + "\"";
        if (Type == 1) //WIFICIPHER_NOPASS
        {
//            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;

        }
        if (Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    public WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    Logger.debug("existingConfig.SSID = " + existingConfig.SSID);
                    return existingConfig;
                }
            }
        }
        return null;
    }
}