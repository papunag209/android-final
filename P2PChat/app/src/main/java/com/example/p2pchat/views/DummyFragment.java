package com.example.p2pchat.views;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.p2pchat.R;
import com.example.p2pchat.data.DataDao;
import com.example.p2pchat.data.Database;
import com.example.p2pchat.data.model.Message;
import com.example.p2pchat.data.model.Session;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;


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
        final EditText text = view.findViewById(R.id.editText);

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateDb(5);
            }
        });
        view.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearDb();
            }
        });

        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long id = Long.parseLong(text.getText().toString());
                Bundle args = new Bundle();
                args.putLong("SessionId", id);
                Navigation.findNavController(view).navigate(R.id.chatFragment, args);
            }
        });
    }

    void populateDb(int numHistoryItems){
        DataDao dao = Database.getInstance().dataDao();
        Log.d(TAG, "populateDb: populated");

        //populate sessions
        Session s = new Session();
        s.setPeerPhoneName("123");
        s.setPeerMAC("zzz");
        s.setSessionStartTime(Calendar.getInstance().getTime().toString());
        dao.inserSessionAsync(s).subscribe(new SingleObserver<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Long aLong) {
                Log.d(TAG, "onSuccess: Session id is: " + aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: ");
            }
        });
//        s = new Session();
//        s.setPeerPhoneName("Cira");
//        s.setSessionStartTime(Calendar.getInstance().getTime().toString());
//        Single<Long> single = dao.inserSessionAsync(s);
//        single.subscribe(new SingleObserver<Long>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onSuccess(Long aLong) {
//                Log.d(TAG, "onSuccess: SingleObserver return value: " + aLong);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e(TAG, "onError: ", e);
//            }
//        });
//
//        Log.d(TAG, "populateDb: session id: "+ sessionId);
//        Log.d(TAG, "populateDb: session id from obj: " + sessionId);
//
//        LiveData<Session> sessionLiveData = dao.getSessionById(sessionId);
//        sessionLiveData.observe(this, new Observer<Session>() {
//            @Override
//            public void onChanged(Session session) {
//                Log.d(TAG, "onChanged: session: " + session);
//            }
//        });
//
//        Message m = new Message();
//        m.setMessageTime(Calendar.getInstance().getTime().toString());
//        m.setSessionId(sessionId);
//        m.setMessageText("Oee sada xar?");
//        dao.insertMessage(m);
//        m.setMessageTime(Calendar.getInstance().getTime().toString());
//        m.setMessageText("aq var brat shen sada xar");
//        dao.insertMessage(m);
//
//        LiveData<List<Message>> messages = dao.getMessages(sessionId);
//        messages.observe(this, new Observer<List<Message>>() {
//            @Override
//            public void onChanged(List<Message> messages) {
//                Log.d(TAG, "onChanged: messages: " + messages);
//            }
//        });
    }

    void clearDb(){
        DataDao dao = Database.getInstance().dataDao();
        dao.clearMessages();
        dao.clearSessions();
        Log.d(TAG, "clearDb: Cleared kinda");
    }
}
