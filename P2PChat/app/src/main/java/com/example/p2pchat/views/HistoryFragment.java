package com.example.p2pchat.views;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.p2pchat.App;
import com.example.p2pchat.R;
import com.example.p2pchat.adapters.HistoryRecyclerViewAdapter;
import com.example.p2pchat.data.model.Session;
import com.example.p2pchat.viewModels.HistoryFragmentViewModel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    RecyclerView recyclerView;
    HistoryRecyclerViewAdapter historyRecyclerViewAdapter;
    HistoryFragmentViewModel historyFragmentViewModel;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        this.recyclerView = view.findViewById(R.id.recyclerView_historyItems);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        historyFragmentViewModel = ViewModelProviders.of(this).get(HistoryFragmentViewModel.class);
        historyFragmentViewModel.init();

        initRecyclerView();
        initObservers();
    }

    private void initObservers() {
        historyFragmentViewModel.getSessionsListLiveData().observe(this, new Observer<List<Session>>() {
            @Override
            public void onChanged(List<Session> sessions) {
                historyRecyclerViewAdapter.updateDataSet(sessions);
            }
        });
    }

    private void initRecyclerView(){
        historyRecyclerViewAdapter = new HistoryRecyclerViewAdapter(historyFragmentViewModel.getSessionsListLiveData().getValue());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(App.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(historyRecyclerViewAdapter);
    }
}
