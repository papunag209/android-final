package com.example.p2pchat.viewModels;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.p2pchat.data.DataDao;
import com.example.p2pchat.data.Database;
import com.example.p2pchat.data.model.Message;
import com.example.p2pchat.data.model.Session;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

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

    public void init(String peerMac, LifecycleOwner lifecycleOwner) {
//        this.sessionId = sessionId;
        dao = Database.getInstance().dataDao();
        sessionLiveData = dao.getSessionByMac(peerMac);
        sessionLiveData.observe(lifecycleOwner, new Observer<Session>() {
            @Override
            public void onChanged(Session session) {
                if(session!=null){
                    sessionId = session.getSessionId();
                    messagesListLiveData = dao.getMessages(sessionId);
                }
            }
        });
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
        messageToSend.setMessageStatus("PENDING");
//        Log.d(TAG, "sendMessage: session data: " + sessionLiveData.getValue());
//        Log.d(TAG, "sendMessage: message data:" + messagesListLiveData.getValue());
        Completable insertDone = dao.insertMessageAsync(messageToSend);
        insertDone.subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: inserted successfully");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: message insert not inserted successfully!", e);
            }
        });
    }

    public void deleteThisSession() {
        dao.deleteSession(sessionLiveData.getValue());
    }
}
