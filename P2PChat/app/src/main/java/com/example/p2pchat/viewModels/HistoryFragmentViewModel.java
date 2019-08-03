package com.example.p2pchat.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.p2pchat.data.DataDao;
import com.example.p2pchat.data.Database;
import com.example.p2pchat.data.model.Session;
import com.example.p2pchat.data.model.helperModel.SessionWithMessageCount;

import java.util.List;

public class HistoryFragmentViewModel extends ViewModel {
    private LiveData<List<SessionWithMessageCount>> sessionsListLiveData;
    private DataDao dao;

    public void init() {
        dao = Database.getInstance().dataDao();
        this.sessionsListLiveData = dao.getSessionListWithMessageCount();
    }

    public LiveData<List<SessionWithMessageCount>> getSessionsListLiveData() {
        return sessionsListLiveData;
    }

    public void deleteAll() {
        dao.clearSessions();
    }

    public void deleteSingle(Session s) {
        dao.deleteSession(s);
    }
}
