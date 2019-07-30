package com.example.p2pchat.viewModels;

import android.net.wifi.p2p.WifiP2pDevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.p2pchat.data.DataDao;
import com.example.p2pchat.data.Database;
import com.example.p2pchat.data.model.Session;

import java.util.Calendar;
import java.util.Collection;

import io.reactivex.Completable;
import io.reactivex.Single;

public class MainFragmentViewModel extends ViewModel {
    private MutableLiveData<Collection<WifiP2pDevice>> collectionLiveData;
    private DataDao dao;

    public MainFragmentViewModel(MutableLiveData<Collection<WifiP2pDevice>> collectionLiveData) {
        this.collectionLiveData = collectionLiveData;
        this.dao = Database.getInstance().dataDao();
    }

    public LiveData<Collection<WifiP2pDevice>> getCollectionLiveData() {
        return collectionLiveData;
    }

    public void setCollectionLiveData(MutableLiveData<Collection<WifiP2pDevice>> collectionLiveData) {
        this.collectionLiveData = collectionLiveData;
    }

    //should subscribe to returned value and receive session id.
    public Single<Long> registerSession(){
        Session s = new Session();
        s.setPeerPhoneName("papuna");
        s.setSessionStartTime(Calendar.getInstance().getTime().toString());

        return dao.inserSessionAsync(s);
    }
}
