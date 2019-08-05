package com.example.p2pchat.interfaces;

import android.net.wifi.p2p.WifiP2pManager;

public interface BroadcastController {

    void updateOurDevice(int status);

    WifiP2pManager getManager();

    WifiP2pManager.Channel getChannel();

    WifiP2pManager.ConnectionInfoListener getConnectionInfoListener();

    WifiP2pManager.PeerListListener getPeerListListener();

}
