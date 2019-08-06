package com.example.p2pchat.views;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.p2pchat.App;
import com.example.p2pchat.MainActivity;
import com.example.p2pchat.R;
import com.example.p2pchat.adapters.PeersRecyclerViewAdapter;
import com.example.p2pchat.data.Database;
import com.example.p2pchat.data.model.dataholder.PeerStatusHolder;
import com.example.p2pchat.interfaces.P2pController;
import com.example.p2pchat.threads.SendAndReceive;
import com.example.p2pchat.viewModels.MainFragmentViewModel;

import java.util.ArrayList;
import java.util.Collection;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import static com.example.p2pchat.MainActivity.getDeviceStatus;


public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    RecyclerView recyclerView;
    NavController navController;
    MainFragmentViewModel mainFragmentViewModel;
    Button btn;
    P2pController p2pController;
    Button cancelSearchButton;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p2pController = (P2pController) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_chatPeers);
        cancelSearchButton = view.findViewById(R.id.button_cancelLoading);
        if(cancelSearchButton != null) {
            cancelSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navController.navigate(R.id.historyFragment);
                }
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(this.getView());
        mainFragmentViewModel = ViewModelProviders.of(this).get(MainFragmentViewModel.class);

        final PeersRecyclerViewAdapter adapter = new PeersRecyclerViewAdapter(null);
        adapter.setRecycleItem(new PeersRecyclerViewAdapter.OnRecycleItem() {
            @Override
            public void onClick(int position) {
                //GETTING CONNECTION
                if(adapter.getDataset() != null){
                    final WifiP2pDevice device = adapter.getDataset().get(position);

                    p2pController.connectToDevice(device);

                    Log.d(TAG, "onClick: " + position);
                }
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getContext()));
        p2pController.setRecyclerView(recyclerView);

        Log.d(TAG, "onViewCreated: ACTIVITY" + getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        p2pController.discoverPeers();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
