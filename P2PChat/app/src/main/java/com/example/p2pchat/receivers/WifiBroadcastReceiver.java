package com.example.p2pchat.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.example.p2pchat.MainActivity;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiBroadcastReceiver";
    private WifiP2pManager.Channel wChannel;
    private WifiP2pManager wManager;
    WifiP2pManager.PeerListListener peerListListener;


    public WifiBroadcastReceiver(WifiP2pManager.Channel wChannel, WifiP2pManager wManager, WifiP2pManager.PeerListListener peerListListener) {
        this.wChannel = wChannel;
        this.wManager = wManager;
        this.peerListListener = peerListListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getAction();

        assert msg != null;

        if(msg.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)) {
            Log.d(TAG, "onReceive: ");
        }else if(msg.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){

        }else if(msg.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)){
            //update peers
            Log.d(TAG, "onReceive: peers changed");
            if(wManager!= null){
                wManager.requestPeers(wChannel,peerListListener);
            }
        }else if(msg.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)){

        }


    }
}
