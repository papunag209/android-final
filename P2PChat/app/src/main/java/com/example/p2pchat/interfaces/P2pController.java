package com.example.p2pchat.interfaces;

import android.net.wifi.p2p.WifiP2pDevice;

import androidx.lifecycle.LiveData;

import java.util.Collection;

public interface P2pController {

        LiveData<Collection<WifiP2pDevice>> getPeerLiveData();

        LiveData<WifiP2pDevice> getConnectedDevice();


        void connectToDevice(WifiP2pDevice device);

}