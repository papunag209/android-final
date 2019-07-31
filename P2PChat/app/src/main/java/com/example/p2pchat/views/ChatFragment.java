package com.example.p2pchat.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;

import com.example.p2pchat.App;
import com.example.p2pchat.R;
import com.example.p2pchat.adapters.MessagesRecyclerViewAdapter;
import com.example.p2pchat.data.Database;
import com.example.p2pchat.data.model.Message;
import com.example.p2pchat.data.model.Session;
import com.example.p2pchat.data.model.helperModel.SessionWithMessageCount;
import com.example.p2pchat.viewModels.ChatFragmentViewModel;

import java.util.List;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    RecyclerView recyclerView;
    MessagesRecyclerViewAdapter recyclerAdapter;
    ChatFragmentViewModel chatFragmentViewModel;
    Button sendButton;
    EditText messageText;
    ImageButton backButton;
    ImageButton deleteButton;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_chatMessages);
        sendButton = view.findViewById(R.id.button_messageSend);
        messageText = view.findViewById(R.id.editText_messageInput);
        backButton = view.findViewById(R.id.imageButton_chatBack);
        deleteButton = view.findViewById(R.id.imageButton_deleteThisMessage);

        Boolean isHistoryMode = getArguments().getBoolean("HistoryMode");
        if(isHistoryMode){
            messageText.setVisibility(View.GONE);
            sendButton.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatFragmentViewModel = ViewModelProviders.of(this).get(ChatFragmentViewModel.class);

        Long sessionId = getArguments().getLong("SessionId");
        if (sessionId == null){
            Log.d(TAG, "onViewCreated: No Session Given To Fragment");
        } else {
            chatFragmentViewModel.init(sessionId);
            initOnClickListeners();
            initDataObservers();
            initRecyclerView();
        }
    }

    private void initOnClickListeners(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                chatFragmentViewModel.sendMessage(messageText.getText().toString());
//                long start = System.currentTimeMillis(); todo: clear this for stress testing of async calls
//                for (int i=0; i<10000; i++){
//                    chatFragmentViewModel.sendMessage(messageText.getText().toString());
//                }
//                long end = System.currentTimeMillis();
//                Log.d(TAG, "onClick: 10000 items to send in: " + (end-start) + "ms");

                chatFragmentViewModel.sendMessage(messageText.getText().toString());
                messageText.setText("");
            }
        });
    }

    private void initDataObservers(){
        chatFragmentViewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                Log.d(TAG, "onChanged: messages changed to: " + messages);
                recyclerAdapter.updateDataSet(messages);
            }
        });
        chatFragmentViewModel.getSession().observe(this, new Observer<Session>() {
            @Override
            public void onChanged(Session session) {
                Log.d(TAG, "onChanged: session changed to: " + session);
            }
        });
        LiveData<SessionWithMessageCount> s;
        s = Database.getInstance().dataDao().getSessionWithMessageCount(chatFragmentViewModel.getSessionId());
        s.observe(this, new Observer<SessionWithMessageCount>() {
            @Override
            public void onChanged(SessionWithMessageCount sessionWithMessageCount) {
                Log.d(TAG, "onChanged: session with message count: " + sessionWithMessageCount);
            }
        });
    }

    private void initRecyclerView(){
        recyclerAdapter = new MessagesRecyclerViewAdapter(chatFragmentViewModel.getMessages().getValue());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(App.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
