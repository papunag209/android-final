package com.example.p2pchat.views;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.p2pchat.R;
import com.example.p2pchat.data.DataDao;
import com.example.p2pchat.data.Database;
import com.example.p2pchat.data.model.Message;
import com.example.p2pchat.data.model.Session;

import java.util.Calendar;
import java.util.List;


//TODO: remove this class with its xml
/**
 * A simple {@link Fragment} subclass.
 */
public class DummyFragment extends Fragment {
    private static final String TAG = "DummyFragment";

    public DummyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dummy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateDb(5);
            }
        });
        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearDb();
            }
        });
    }

    void populateDb(int numHistoryItems){
        DataDao dao = Database.getInstance().dataDao();

        //populate sessions
        Session s = new Session();
        s.setPeerPhoneName("papuna");
        s.setSessionStartTime(Calendar.getInstance().getTime().toString());
        Long sessionId = dao.insertSession(s);

        Log.d(TAG, "populateDb: session id: "+ sessionId);
        Log.d(TAG, "populateDb: session id from obj: " + sessionId);

        Message m = new Message();
        m.setMessageTime(Calendar.getInstance().getTime().toString());
        m.setSessionId(sessionId);
        m.setMessageText("Oee sada xar?");
        dao.inserMessage(m);
        m.setMessageTime(Calendar.getInstance().getTime().toString());
        m.setMessageText("aq var brat shen sada xar");
        dao.inserMessage(m);

        LiveData<List<Message>> messages = dao.getMessages(sessionId);
        messages.observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                Log.d(TAG, "onChanged: messages: " + messages);
            }
        });
    }

    void clearDb(){
        DataDao dao = Database.getInstance().dataDao();
        dao.clearMessages();
        dao.clearSessions();
        Log.d(TAG, "clearDb: Cleared kinda");
    }
}
