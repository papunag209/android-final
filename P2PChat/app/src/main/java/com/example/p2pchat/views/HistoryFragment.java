package com.example.p2pchat.views;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.p2pchat.App;
import com.example.p2pchat.R;
import com.example.p2pchat.adapters.HistoryRecyclerViewAdapter;
import com.example.p2pchat.adapters.OnItemAction;
import com.example.p2pchat.data.model.Session;
import com.example.p2pchat.viewModels.HistoryFragmentViewModel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements OnItemAction <Session>{
    private static final String TAG = "HistoryFragment";
    RecyclerView recyclerView;
    Button clearHistoryButton;
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
        this.clearHistoryButton = view.findViewById(R.id.button_clearHistory);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        historyFragmentViewModel = ViewModelProviders.of(this).get(HistoryFragmentViewModel.class);
        historyFragmentViewModel.init();

        initRecyclerView();
        initObservers();
        initListeners();
    }

    private void initListeners() {
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyFragmentViewModel.deleteAll();
            }
        });
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
        historyRecyclerViewAdapter = new HistoryRecyclerViewAdapter(historyFragmentViewModel.getSessionsListLiveData().getValue(), this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(App.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(historyRecyclerViewAdapter);
    }

    @Override
    public void onLongPress(final Session item) {
        popUpDialogue("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: yes" + i);
                        historyFragmentViewModel.deleteSingle(item);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: nop" + i);
                    }
                });
    }

    public AlertDialog popUpDialogue(String positiveLabel,
                                     DialogInterface.OnClickListener positiveOnClick,
                                     DialogInterface.OnClickListener negativeOnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setMessage("Do you really want to Delete this session from history");
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton("Cancel", negativeOnClick);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }
}
