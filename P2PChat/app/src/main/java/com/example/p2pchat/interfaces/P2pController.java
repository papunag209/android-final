package com.example.p2pchat.interfaces;

import android.net.wifi.p2p.WifiP2pDevice;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;

public interface P2pController {


        void setRecyclerView(RecyclerView recyclerView);

        void connectToDevice(WifiP2pDevice device);

        int getDeviceStatus();

        void discoverPeers();

}