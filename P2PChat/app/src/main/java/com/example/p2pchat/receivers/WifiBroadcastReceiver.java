package com.example.p2pchat.receivers;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.p2pchat.MainActivity;

import static com.example.p2pchat.MainActivity.getDeviceStatus;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiBroadcastReceiver";
    private WifiP2pManager.Channel wChannel;
    private WifiP2pManager wManager;
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    private WifiP2pManager.PeerListListener peerListListener;
    private MutableLiveData<WifiP2pDevice> connectedDevice;


    public WifiBroadcastReceiver(WifiP2pManager.Channel wChannel, WifiP2pManager wManager, WifiP2pManager.PeerListListener peerListListener, WifiP2pManager.ConnectionInfoListener connectionInfoListener, MutableLiveData<WifiP2pDevice> connectedDevice) {
        this.wChannel = wChannel;
        this.wManager = wManager;
        this.peerListListener = peerListListener;
        this.connectionInfoListener = connectionInfoListener;
        this.connectedDevice = connectedDevice;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getAction();

        assert msg != null;

        Log.d(TAG, "onReceive: SOMETHING CAME IN!!!");

        if (msg.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)) {
            Log.d(TAG, "onReceive: ");

        } else if (msg.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)) {

            NetworkInfo nInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (nInfo.isConnected()) {
                wManager.requestConnectionInfo(wChannel, this.connectionInfoListener);
            } else {
                Log.d(TAG, "onReceive: CONNECTION LOST");
            }


        } else if (msg.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)) {
            //update peers
            Log.d(TAG, "onReceive: peers changed");
            if (wManager != null) {
                wManager.requestPeers(wChannel, peerListListener);
            }
        } else if (msg.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)) {
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            if (device == null) {
                Log.d(TAG, "onReceive: Device is null");
            } else {
                if(device.status == WifiP2pDevice.CONNECTED){
                    connectedDevice.postValue(device);
                }
                Log.d(TAG, "onReceive: DEVICE IS " + device);
            }
        }
    }
}
