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
    PeersRecyclerViewAdapter recyclerViewAdapter;
    WifiP2pDevice[] peerLst;
    Button btn;
    P2pController p2pController;


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
        btn = view.findViewById(R.id.button4_sendMessage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO temporary shit
                SendAndReceive sendAndReceive = ((MainActivity) getActivity()).getSendAndReceive();
                Log.d(TAG, "onclick: sendAndReceive : " + sendAndReceive);

                if (sendAndReceive != null) {
                    Log.d(TAG, "onClick: SENDING MESSAGE");
                    sendAndReceive.write("HELLO".getBytes());
                } else {
                    ((MainActivity) getActivity()).removeConnection();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(this.getView());
        mainFragmentViewModel = ViewModelProviders.of(this).get(MainFragmentViewModel.class);
        //TODO CHECK ACTIVITY
        mainFragmentViewModel.init(p2pController.getPeerLiveData());
//        recyclerViewAdapter = (PeersRecyclerViewAdapter)recyclerView.getAdapter();
        mainFragmentViewModel.getCollectionLiveData().observe(this, new Observer<Collection<WifiP2pDevice>>() {
            @Override
            public void onChanged(Collection<WifiP2pDevice> s) {
                Log.d(TAG, "onChanged: SOMETHING CHANGED!!!");
                ArrayList<PeerStatusHolder> peerStatuses = new ArrayList<PeerStatusHolder>();
                for (WifiP2pDevice device : s) {
                    peerStatuses.add(new PeerStatusHolder(device.deviceName, getDeviceStatus(device.status)));
                    if(device.status == WifiP2pDevice.CONNECTED){
                        Log.d(TAG, "onChanged: AMASTAN VART DAQONEQTEBULI " + device);
                        registerSessionForDevice(device);
                    }
                }
                ((PeersRecyclerViewAdapter) recyclerView.getAdapter()).setDataSet(peerStatuses);
                peerLst = s.toArray(new WifiP2pDevice[s.size()]);

            }
        });


        //TODO NEED TO GIVE PEER LIST HERE
        PeersRecyclerViewAdapter adapter = new PeersRecyclerViewAdapter(null, new PeersRecyclerViewAdapter.OnRecycleItem() {
            @Override
            public void onClick(int position) {
                //GETTING CONNECTION
                final WifiP2pDevice device = peerLst[position];

                p2pController.connectToDevice(device);

                Log.d(TAG, "onClick: " + position);

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getContext()));


        Log.d(TAG, "onViewCreated: ACTIVITY" + getActivity());
    }

    private void registerSessionForDevice(final WifiP2pDevice device){
        if (device != null) {
            p2pController.setConnectedDevice(device);
            Log.d(TAG, "registerSessionForDevice: CONNECTED DEVICE IS: " + device);
            Log.d(TAG, "onChanged:Connected Device Address Is: " + device.deviceAddress);
            mainFragmentViewModel.registerSession(device.deviceAddress).subscribe(new SingleObserver<Long>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(Long aLong) {
                    Log.d(TAG, "onSuccess: " + Database.getInstance().dataDao().getSessionsSync());
                    Log.d(TAG, "onSuccess: REGISTERED WITH :" + aLong);
                    Log.d(TAG, "onSuccess: session is:" + Database.getInstance().dataDao().getSessionByIdSync(aLong));
                    Bundle args = new Bundle();
                    args.putLong("SessionId", aLong);
                    args.putString("PeerMac", device.deviceAddress);
                    navController.navigate(R.id.chatFragment, args);
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, "onError: REGISTER FAILED");
                    Bundle args = new Bundle();
                    args.putString("PeerMac", device.deviceAddress);
                    navController.navigate(R.id.chatFragment, args);
                }
            });
        }
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
