package com.example.p2pchat.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.p2pchat.data.DataDao;
import com.example.p2pchat.data.Database;
import com.example.p2pchat.data.model.Message;
import com.example.p2pchat.data.model.Session;

import java.util.Calendar;
import java.util.List;

public class ChatFragmentViewModel extends ViewModel {
    LiveData<List<Message>> messagesListLiveData;
    LiveData<Session> sessionLiveData;

    public void init(Long sessionId){
        DataDao dao = Database.getInstance().dataDao();
        sessionLiveData = dao.getSessionById(sessionId);
        messagesListLiveData = dao.getMessages(sessionId);
    }

    public LiveData<List<Message>> getMessages(){
        return messagesListLiveData;
    }

    public LiveData<Session> getSession() {
        return sessionLiveData;
    }

    public void sendMessage(String m){
        Message messageToSend = new Message();
        messageToSend.setMessageText(m);
        messageToSend.setMessageTime(Calendar.getInstance().getTime().toString());
        messageToSend.setMessageId(sessionLiveData.getValue().getSessionId());
    }
}
