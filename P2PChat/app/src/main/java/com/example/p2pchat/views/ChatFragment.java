package com.example.p2pchat.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.p2pchat.App;
import com.example.p2pchat.R;
import com.example.p2pchat.adapters.MessagesRecyclerViewAdapter;
import com.example.p2pchat.data.model.Message;
import com.example.p2pchat.viewModels.ChatFragmentViewModel;

import java.util.List;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    RecyclerView recyclerView;
    MessagesRecyclerViewAdapter recyclerAdapter;
    ChatFragmentViewModel chatFragmentViewModel;

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
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView_chatMessages);

        chatFragmentViewModel = ViewModelProviders.of(this).get(ChatFragmentViewModel.class);

        Long sessionId = getArguments().getLong("SessionId");
        if (sessionId == null){
            Log.d(TAG, "onViewCreated: No Session Given To Fragment");
        } else {
            chatFragmentViewModel.init(sessionId);
            initDataObservers();
            initRecyclerView();
        }
    }

    private void initDataObservers(){
        chatFragmentViewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                //todo weired action I thing this shoud change the data set too https://www.youtube.com/watch?v=ijXjCtCXcN4&t=375s
                Log.d(TAG, "onChanged: " + messages);
                recyclerAdapter.updateDataSet(messages);
//                recyclerAdapter.notifyDataSetChanged();
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
