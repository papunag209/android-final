package com.example.p2pchat.viewModels;

import android.net.wifi.p2p.WifiP2pDevice;

import androidx.lifecycle.LiveData;

import java.util.Collection;

public class MainFragmentViewModel {
    LiveData<Collection<WifiP2pDevice>> collectionLiveData;

}
