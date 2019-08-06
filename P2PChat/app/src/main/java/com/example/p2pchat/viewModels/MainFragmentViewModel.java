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
    private DataDao dao;
    private MutableLiveData<Boolean> loadingOverlayVisible;
    private String loadingOverlayLabel;

    private static final String TAG = "MainFragmentViewModel";

    public MainFragmentViewModel() {
        this.dao = Database.getInstance().dataDao();
        this.loadingOverlayLabel = "";
        this.loadingOverlayVisible = new MutableLiveData<>();
        this.loadingOverlayVisible.setValue(false);
    }



    public void setLoadingOverlayVisible(Boolean loadingOverlayVisible) {
        this.loadingOverlayVisible.postValue(loadingOverlayVisible);
    }

    //should subscribe to returned value and receive session id.
    public Single<Long> registerSession(String addr){
        Session s = new Session();
        s.setPeerPhoneName("");
        s.setPeerMac(addr);
        s.setSessionStartTime(Calendar.getInstance().getTime().toString());

        return dao.inserSessionAsync(s);
    }
}
