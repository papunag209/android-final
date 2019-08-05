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
import com.example.p2pchat.interfaces.BroadcastController;

import static com.example.p2pchat.MainActivity.getDeviceStatus;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiBroadcastReceiver";
    private WifiP2pManager.Channel wChannel;
    private WifiP2pManager wManager;
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    private WifiP2pManager.PeerListListener peerListListener;
    private BroadcastController broadcastController;

    public WifiBroadcastReceiver(BroadcastController broadcastController) {
        this.broadcastController = broadcastController;
        this.wChannel = broadcastController.getChannel();
        this.wManager = broadcastController.getManager();
        this.peerListListener = broadcastController.getPeerListListener();
        this.connectionInfoListener = broadcastController.getConnectionInfoListener();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getAction();

        assert msg != null;

        Log.d(TAG, "onReceive: SOMETHING CAME IN!!!" + msg);

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
                Log.d(TAG, "boradcastReceiver onReceive: DEVICE IS " + device);
                broadcastController.updateOurDevice(device.status);

            }
        }
    }
}
