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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.p2pchat.App;
import com.example.p2pchat.MainActivity;
import com.example.p2pchat.R;
import com.example.p2pchat.adapters.PeersRecyclerViewAdapter;
import com.example.p2pchat.data.model.dataholder.PeerStatusHolder;
import com.example.p2pchat.threads.SendAndReceive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.p2pchat.MainActivity.getDeviceStatus;


public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    RecyclerView recyclerView;
    PeersRecyclerViewAdapter recyclerViewAdapter;
    WifiP2pDevice[] peerLst;
    Button btn;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_chatPeers);
        btn = view.findViewById(R.id.button4_sendMessage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendAndReceive sendAndReceive = ((MainActivity)getActivity()).getSendAndReceive();
                Log.d(TAG, "onclick: sendAndReceive : " + sendAndReceive);

                if(sendAndReceive != null) {
                    Log.d(TAG, "onClick: SENDING MESSAGE");
                    sendAndReceive.write("HELLO".getBytes());
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        recyclerViewAdapter = (PeersRecyclerViewAdapter)recyclerView.getAdapter();
        final MutableLiveData<Collection<WifiP2pDevice>> peers = ((MainActivity) getActivity()).getPeers();
        peers.observe(this, new Observer<Collection<WifiP2pDevice>>() {
            @Override
            public void onChanged(Collection<WifiP2pDevice> s) {
                Log.d(TAG, "onChanged: SOMETHING CHANGED!!!");
                ArrayList<PeerStatusHolder> peerStatuses = new ArrayList<PeerStatusHolder>();
                for (WifiP2pDevice device : s) {
                    peerStatuses.add(new PeerStatusHolder(device.deviceName,getDeviceStatus(device.status)));
//                    boolean isConnected = false;
//                    if(device.status == WifiP2pDevice.CONNECTED){
//                        isConnected = true;
//                    }
//                    MainActivity activity = (MainActivity) getActivity();
//                    if(isConnected && activity.getClient()== null && activity.getServer() == null){
//                        activity.removeConnection();
//                    }
                }
                ((PeersRecyclerViewAdapter) recyclerView.getAdapter()).setDataSet(peerStatuses);
                peerLst = peers.getValue().toArray(new WifiP2pDevice[peers.getValue().size()]);

            }
        });
        //TODO NEED TO GIVE PEER LIST HERE
        PeersRecyclerViewAdapter adapter = new PeersRecyclerViewAdapter(null, new PeersRecyclerViewAdapter.OnRecycleItem() {
            @Override
            public void onClick(int position) {
                //GETTING CONNECTION
                final WifiP2pDevice device = peerLst[position];

                MainActivity activity =(MainActivity) getActivity();

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
