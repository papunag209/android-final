package com.example.p2pchat.views;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.p2pchat.App;
import com.example.p2pchat.MainActivity;
import com.example.p2pchat.R;
import com.example.p2pchat.adapters.PeersRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    RecyclerView recyclerView;
    PeersRecyclerViewAdapter recyclerViewAdapter;
    WifiP2pDevice[] peerLst;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView_chatPeers);
//        recyclerViewAdapter = (PeersRecyclerViewAdapter)recyclerView.getAdapter();
        final MutableLiveData<Collection<WifiP2pDevice>> peers = ((MainActivity) getActivity()).getPeers();
        peers.observe(this, new Observer<Collection<WifiP2pDevice>>() {
            @Override
            public void onChanged(Collection<WifiP2pDevice> s) {
//                Log.d(TAG,""+s);
                ArrayList<String> names = new ArrayList<String>();
                for (WifiP2pDevice device : s) {
                    names.add(device.deviceName);
                }
                ((PeersRecyclerViewAdapter) recyclerView.getAdapter()).setDataSet(names);
                peerLst = peers.getValue().toArray(new WifiP2pDevice[peers.getValue().size()]);

            }
        });
        //TODO NEED TO GIVE PEER LIST HERE
        PeersRecyclerViewAdapter adapter = new PeersRecyclerViewAdapter(null, new PeersRecyclerViewAdapter.OnRecycleItem() {
            @Override
            public void onClick(int position) {
                //GETTING CONNECTION
                final WifiP2pDevice device = peerLst[position];

                MainActivity activity = (MainActivity) ((MainActivity) getActivity());

                activity.getConnection(device);

                Log.d(TAG, "onClick: " + position);

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getContext()));


        Log.d(TAG, "onViewCreated: ACTIVITY" + getActivity());
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
