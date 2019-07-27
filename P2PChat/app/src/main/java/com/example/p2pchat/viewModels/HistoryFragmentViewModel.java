package com.example.p2pchat.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.p2pchat.data.DataDao;
import com.example.p2pchat.data.Database;
import com.example.p2pchat.data.model.Session;

import java.util.List;

public class HistoryFragmentViewModel extends ViewModel {
    private LiveData<List<Session>> sessionsListLiveData;
    private DataDao dao;

    public void init(){
        dao = Database.getInstance().dataDao();
        this.sessionsListLiveData = dao.getSessions();
    }

    public LiveData<List<Session>> getSessionsListLiveData() {
        return sessionsListLiveData;
    }

    public void deleteAll(){
        dao.clearSessions();
    }

    public void deleteSingle(Session s){
        dao.deleteSession(s);
    }
}
