package com.example.p2pchat.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.p2pchat.data.DataDao;
import com.example.p2pchat.data.Database;
import com.example.p2pchat.data.model.Message;
import com.example.p2pchat.data.model.Session;

import java.util.Calendar;
import java.util.List;

public class ChatFragmentViewModel extends ViewModel {
    private static final String TAG = "ChatFragmentViewModel";
    DataDao dao;
    LiveData<List<Message>> messagesListLiveData;
    LiveData<Session> sessionLiveData;
    Long sessionId;

    public Long getSessionId() {
        return sessionId;
    }

    public void init(Long sessionId) {
        this.sessionId = sessionId;
        dao = Database.getInstance().dataDao();
        sessionLiveData = dao.getSessionById(sessionId);
        messagesListLiveData = dao.getMessages(sessionId);
    }

    public LiveData<List<Message>> getMessages() {
        return messagesListLiveData;
    }

    public LiveData<Session> getSession() {
        return sessionLiveData;
    }

    public void sendMessage(String m) {
        Message messageToSend = new Message();
        messageToSend.setMessageText(m);
        messageToSend.setMessageTime(Calendar.getInstance().getTime().toString());
//        messageToSend.setMessageId(sessionLiveData.getValue().getSessionId());
        messageToSend.setSessionId(sessionId);
//        Log.d(TAG, "sendMessage: session data: " + sessionLiveData.getValue());
//        Log.d(TAG, "sendMessage: message data:" + messagesListLiveData.getValue());

        dao.insertMessage(messageToSend);
    }
}
