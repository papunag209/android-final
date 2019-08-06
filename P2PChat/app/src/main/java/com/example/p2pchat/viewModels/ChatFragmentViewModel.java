package com.example.p2pchat.viewModels;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.p2pchat.data.DataDao;
import com.example.p2pchat.data.Database;
import com.example.p2pchat.data.model.Message;
import com.example.p2pchat.data.model.MessageStatus;
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
        Log.d(TAG, "init with ID: ");
        dao = Database.getInstance().dataDao();
        sessionLiveData = dao.getSessionById(sessionId);
        messagesListLiveData = dao.getMessages(sessionId);
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public void init(String peerMac, LifecycleOwner lifecycleOwner) {
//        this.sessionId = sessionId;
        dao = Database.getInstance().dataDao();
//        Session session = dao.getSessionByMacSync(peerMac);
//        Log.d(TAG, "init with MAC: session got from db is: " + session);
//        this.sessionId = session.getSessionId();
//        sessionLiveData = dao.getSessionById(sessionId);
//        messagesListLiveData = dao.getMessages(sessionId);

        sessionLiveData = dao.getSessionByMac(peerMac);
        messagesListLiveData = dao.getMessagesByMac(peerMac);

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
        messageToSend.setMessageStatus(MessageStatus.PENDING);
//        messageToSend.setMessageStatus(MessageStatus.RECEIVED);

//        Log.d(TAG, "sendMessage: session data: " + sessionLiveData.getValue());
//        Log.d(TAG, "sendMessage: message data:" + messagesListLiveData.getValue());
        Log.d(TAG, "sendMessage: session is:" + sessionLiveData.getValue() + " message to send is: " + messageToSend );
        new InsertAsync().execute(new Message[]{messageToSend});


    }
    class InsertAsync extends AsyncTask<Message,Void,Void>{
        @Override
        protected Void doInBackground(Message... messages) {
            Log.d(TAG, "doInBackground: DOING IN BACKGROUnd"+messages[0]);
            dao.insertMessage(messages[0]);
            return null;
        }
    }

    public void deleteThisSession() {
        dao.deleteSession(sessionLiveData.getValue());
    }
}
