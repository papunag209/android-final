package com.example.p2pchat.interfaces;

import android.net.wifi.p2p.WifiP2pDevice;

import androidx.lifecycle.LiveData;

import java.util.Collection;

public interface P2pController {

        LiveData<Collection<WifiP2pDevice>> getPeerLiveData();

        void setConnectedDevice(WifiP2pDevice connectedDevice);

        void connectToDevice(WifiP2pDevice device);

}