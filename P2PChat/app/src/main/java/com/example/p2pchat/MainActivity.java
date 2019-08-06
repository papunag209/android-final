package com.example.p2pchat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import com.example.p2pchat.adapters.PeersRecyclerViewAdapter;
import com.example.p2pchat.data.DataDao;
import com.example.p2pchat.data.Database;
import com.example.p2pchat.data.model.MessageStatus;
import com.example.p2pchat.data.model.Session;
import com.example.p2pchat.data.model.helperModel.MessageWithMacAddress;
import com.example.p2pchat.interfaces.BroadcastController;
import com.example.p2pchat.interfaces.P2pController;
import com.example.p2pchat.interfaces.ToolBarActions;
import com.example.p2pchat.receivers.WifiBroadcastReceiver;
import com.example.p2pchat.threads.ClientSideThread;
import com.example.p2pchat.threads.SendAndReceive;
import com.example.p2pchat.threads.ServerSideThread;
import com.example.p2pchat.views.ChatFragment;
import com.example.p2pchat.views.MainFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.Toast;

import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, P2pController, BroadcastController, ToolBarActions {
    private static final String TAG = "MainActivity";
    NavController navController;
    WifiP2pManager wManager;
    WifiP2pManager.Channel wChannel;
    BroadcastReceiver wReceiver;
    IntentFilter wFilter = new IntentFilter();
    ClientSideThread client;
    WifiP2pDevice connectedDevice = null;
    Toolbar toolbar;

    @Override
    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    public ClientSideThread getClient() {
        return client;
    }

    public void setClient(ClientSideThread client) {
        this.client = client;
    }

    public ServerSideThread getServer() {
        return server;
    }

    public void setServer(ServerSideThread server) {
        this.server = server;
    }

    private RecyclerView recyclerView;
    private static String myMacAddress;
    private int myDeviceStatus;

    ServerSideThread server;


    String[] appPerms = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION};

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {


            Log.d(TAG, "onPeersAvailable: " + wifiP2pDeviceList.getDeviceList());
            peers.postValue(wifiP2pDeviceList.getDeviceList());


        }
    };
    ;
    //TODO place init in oncreate
    MutableLiveData<Collection<WifiP2pDevice>> peers = new MutableLiveData<>();

    private static final int PERMISSION_REQ_CODE = 1;


    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPermissions()) {
            startApp();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: SHEMOVEDI ONRESUMESHI");
        registerReceiver(wReceiver, wFilter);
    }

    private void closeSockets(){
        if (server != null) {
            if(server.getServerSocket() != null){
                try {
                    server.getServerSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(server.getSocket() != null){
                try {
                    server.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (client != null && client.getSocket() != null) {
            try {
                client.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeConnection(final ChatFragment.ConnectionListener listener) {
        if(wManager != null && wChannel != null) {
            wManager.removeGroup(wChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    closeSockets();
                    Log.d(TAG, "onSuccess: DETACHED FROM PEER");
                    Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                    if(listener!=null) {
                        listener.onDisconnect();
                    }
                }

                @Override

                public void onFailure(int i) {
                    Log.d(TAG, "onFailure: FAILED TO DETACH FROM PEER");
                    closeSockets();

                    if(listener!=null) {
                        listener.onDisconnect();
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wReceiver != null) {
            try {
                unregisterReceiver(wReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getDeviceStatus(int status) {
        if (status == WifiP2pDevice.AVAILABLE) {
            return "AVAILABLE";
        } else if (status == WifiP2pDevice.INVITED) {
            return "INVITED";
        } else if (status == WifiP2pDevice.FAILED) {
            return "FAILED";
        } else if (status == WifiP2pDevice.UNAVAILABLE) {
            return "UNAVAILABLE";
        } else if (status == WifiP2pDevice.CONNECTED) {
            return "CONNECTED";
        }
        return "UNKNOWN STATUS";
    }


    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            Log.d(TAG, "onConnectionInfoAvailable: TRYING TO REINIT SERVER AND CLIENT");
            closeSockets();

            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                Log.d(TAG, "onConnectionInfoAvailable: REQUESTING GROUP INFO");

                Log.d(TAG, "onConnectionInfoAvailable: YOU ARE THE HOST");

                server = new ServerSideThread(handler, MainActivity.this);
                Log.d(TAG, "onConnectionInfoAvailable:Server THREAD CREATED!!!");
                server.start();
                Log.d(TAG, "onConnectionInfoAvailable:Server THREAD STARTED");
            } else {
                Log.d(TAG, "onConnectionInfoAvailable: REQUESTING GROUP INFO");
                Log.d(TAG, "onConnectionInfoAvailable: YOU ARE THE CLIENT");
                Log.d(TAG, "onConnectionInfoAvailable: GROUP OWNDER ADDRESS: " + groupOwnerAddress);
                client = new ClientSideThread(groupOwnerAddress, handler, MainActivity.this);
                Log.d(TAG, "onConnectionInfoAvailable:Client THREAD CREATED!!!");
                client.start();
                Log.d(TAG, "onConnectionInfoAvailable:Client THREAD STARTED!!!");
            }
        }
    };

    public SendAndReceive getSendAndReceive() {
        if (this.server != null && this.server.getSendAndReceive() != null) {
            return this.server.getSendAndReceive();
        } else if (this.client != null && this.client.getSendAndReceive() != null) {
            return this.client.getSendAndReceive();
        }
        return null;
    }

    @Override
    public void connectToDevice(final WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        Log.d(TAG, "connectToDevice: Will try to connect to" + device.deviceName);
        wManager.connect(wChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Connected to device: " + device.deviceName);
                Log.d(TAG, "onSuccess: CONNECTED TO DEVICE: " + device);
            }

            @Override
            public void onFailure(int i) {
                Log.d(TAG, "Failed to connect to device: " + device.deviceName);
            }
        });
    }

    @Override
    public int getDeviceStatus() {
        return myDeviceStatus;
    }

    public static final int MESSAGE_READ = 5;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            Log.d(TAG, "MESSAGE CAME IN: " + message);


            switch (message.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) message.obj;
                    String tempMsg = new String(readBuff, 0, message.arg1);
                    //TODO MESSAGE ARRIVED

                    Log.d(TAG, "handleMessage: RECEIVED MSG: " + readBuff);
                    Log.d(TAG, "handleMessage: TRYING TO DESERIALIZE MSG AS MESSAGE ");
                    MessageWithMacAddress msg = null;
                    try {
                        msg = (MessageWithMacAddress) SerializationUtils.deserialize(readBuff);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d(TAG, "handleMessage: FAILED TO DESERIALIZE MSG");
                    }

                    if(msg != null && connectedDevice != null){
                        Log.d(TAG, "handleMessage: DESERIALIZED MSG: " + msg);
                        //TODO MODIFY INSERT
                        DataDao dao = Database.getInstance().dataDao();
                        Session session = dao.getSessionByMacSync(connectedDevice.deviceAddress);
                        final com.example.p2pchat.data.model.Message m = new com.example.p2pchat.data.model.Message();
                        m.setMessageStatus(MessageStatus.RECEIVED);
                        m.setSessionId(session.getSessionId());
                        m.setMessageText(msg.getMessageText());
                        m.setMessageTime(msg.getMessageTime());
                        dao.insertMessageAsync(m).subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete: message inserted: " + m);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
                        Toast.makeText(MainActivity.this, m.getMessageText(), Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(MainActivity.this, "Someone without our app is trying to send data. Aborting Connection", Toast.LENGTH_SHORT).show();
                        removeConnection(null);
                    }
                    break;

            }
            return true;
        }
    });

    private void startApp() {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        if(!wifiManager.isWifiEnabled()){
            popWifiDialogue();
        }

        myMacAddress = wifiManager.getConnectionInfo().getMacAddress();


        wManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wChannel = wManager.initialize(this, getMainLooper(), null);
        wReceiver = new WifiBroadcastReceiver(this);

        wFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        removeConnection(null);
//        testSerialization();

        peers.observe(this, new Observer<Collection<WifiP2pDevice>>() {
            @Override
            public void onChanged(Collection<WifiP2pDevice> wifiP2pDevices) {
                Log.d(TAG, "onChanged: SOMETHING CHANGED!!!");
                if(wifiP2pDevices == null || wifiP2pDevices.size() ==0){
                    ConstraintLayout overlay = findViewById(R.id.constraintLayout_mainFragmentOverlay);
                    if(overlay != null) {
                        overlay.setVisibility(View.VISIBLE);
                    }

                }else {
                    ConstraintLayout overlay = findViewById(R.id.constraintLayout_mainFragmentOverlay);
                    if(overlay != null) {
                        overlay.setVisibility(View.INVISIBLE);
                    }
                    ArrayList<WifiP2pDevice> peerList = new ArrayList<WifiP2pDevice>();
                    for (WifiP2pDevice device : wifiP2pDevices) {
                        peerList.add(device);

                        Log.d(TAG, "onChanged: DEVICE STATUS IS: " + device.status);
                        if (device.status == WifiP2pDevice.CONNECTED && getDeviceStatus() == WifiP2pDevice.CONNECTED) {
                            Log.d(TAG, "onChanged: DEVICE IS CONNECTED");
                            registerSessionForDevice(device);
                        }
                    }
                    ((PeersRecyclerViewAdapter) recyclerView.getAdapter()).setDataSet(peerList);
                }
            }


        });



        Database.getInstance().dataDao().getPendingMessages().observe(this, new Observer<List<MessageWithMacAddress>>() {
            @Override
            public void onChanged(List<MessageWithMacAddress> messageWithMacAddresses) {
                Log.d(TAG, "onChanged: GOT PENDING MSG LIST:" + messageWithMacAddresses);
                Log.d(TAG, "onChangedMyStatus:" + myDeviceStatus);
                if(connectedDevice == null) {
                    removeConnection(null);
                }else if(connectedDevice.status != WifiP2pDevice.CONNECTED){
                    removeConnection(null);
                }

                for(final MessageWithMacAddress msg : messageWithMacAddresses){
//                    Log.d(TAG, "onChanged: msg:" + msg);
//                    Log.d(TAG, "onChanged: msg.peermac:" + msg.getPeerMac());
                    if(connectedDevice != null && msg != null && msg.getPeerMac().equals(connectedDevice.deviceAddress)){
                        Log.d(TAG, "onChanged: msg mac is:" + msg.getPeerMac());
                        if(connectedDevice != null) {
                            msg.setMessageStatus(MessageStatus.SENT);
                            Database.getInstance().dataDao().updateMessage(msg).subscribe(new CompletableObserver() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }
                                @Override
                                public void onComplete() {
                                    Log.d(TAG, "onComplete: message updated now sending");
                                    sendPendingMessage(msg);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }
                }
            }
        });

        final Toolbar toolbar = findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navController = Navigation.findNavController(this, R.id.navHostFragment);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.d(TAG, "onDestinationChanged: " + destination.getId());
                if(destination.getId() == R.id.chatFragment){
                    toolbar.setVisibility(View.GONE);
                } else {
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void registerSessionForDevice(final WifiP2pDevice device){
        if (device != null) {
            connectedDevice = device;
            Log.d(TAG, "registerSessionForDevice: CONNECTED DEVICE IS: " + device);
            Log.d(TAG, "onChanged:Connected Device Address Is: " + device.deviceAddress);

            Session s = new Session();
            s.setPeerPhoneName(device.deviceName);
            s.setPeerMac(device.deviceAddress);
            s.setSessionStartTime(Calendar.getInstance().getTime().toString());

            Database.getInstance().dataDao().inserSessionAsync(s).subscribe(new SingleObserver<Long>() {
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
                    Log.d(TAG, "onError: ah shit here we go again" + device.status);
                    args.putString("PeerMac", device.deviceAddress);
                    navController.navigate(R.id.chatFragment, args);
                }
            });
        }
    }

    private void sendPendingMessage(MessageWithMacAddress msg){
//        getSendAndReceive().write(msg);
        if(getSendAndReceive() != null) {
            Log.d(TAG, "sendPendingMessage: TRYING TO SEND MESSAGE: " + msg);
            byte[] msgBytes = SerializationUtils.serialize(msg);
            Log.d(TAG, "sendPendingMessage: serializedmsg: " + msgBytes);
            msg.setPeerMac(myMacAddress);
            getSendAndReceive().write(msgBytes);
            Log.d(TAG, "sendPendingMessage: SENT MESSAGE!" + msg);
//            MessageWithMacAddress result = (MessageWithMacAddress) SerializationUtils.deserialize(msgBytes);
//            Log.d(TAG, "sendPendingMessage: DESERIALIZED MSG SHOUULD BE :" + result);
        }else{
            Log.d(TAG, "sendPendingMessage: SEND AND RECEIVE IS NULL!!!");
        }
       }

    @Override
    public void discoverPeers() {
        if(wManager != null && wChannel != null) {
            wManager.discoverPeers(wChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "Started Discovery", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i) {
                    Toast.makeText(MainActivity.this, "Ended Discovery " + i, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean checkPermissions() {
        ArrayList<String> permissionsNeeded = new ArrayList<>();

        for (int i = 0; i < appPerms.length; i++) {
            if (ContextCompat.checkSelfPermission(this, appPerms[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(appPerms[i]);
            }
        }
        if (permissionsNeeded.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), PERMISSION_REQ_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_REQ_CODE) {
            return;
        }
        ArrayList<String> permsMissing = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                permsMissing.add(permissions[i]);
            }
        }
        if (permsMissing.isEmpty()) {
            startApp();
        } else {
            for (int i = 0; i < permsMissing.size(); i++) {
                String permNeeded = permsMissing.get(i);
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permNeeded)) {
                    popUpDialogue("Grant Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            checkPermissions();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
                } else {
                    popUpDialogue("Go to Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Intent goToSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            goToSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(goToSettingsIntent);
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    });

                }
            }
        }
    }

    public AlertDialog popWifiDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("This app needs WIFI enabled to run");
        builder.setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                finish();
            }
        });
        builder.setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }

    public AlertDialog popUpDialogue(String positiveLabel,
                                     DialogInterface.OnClickListener positiveOnClick,
                                     DialogInterface.OnClickListener negativeOnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("This app needs all listed permissions to run");
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton("Exit App", negativeOnClick);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } if (navController.getCurrentDestination().getId() == R.id.chatFragment){
            navController.navigateUp();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Log.d(TAG, "onNavigationItemSelected: my status is: " + (myDeviceStatus == WifiP2pDevice.CONNECTED));
            if (myDeviceStatus == WifiP2pDevice.CONNECTED){
                Bundle args = new Bundle();
                args.putString("PeerMac", connectedDevice.deviceAddress);
                Log.d(TAG, "onNavigationItemSelected: args:" + args);
                navController.navigate(R.id.chatFragment, args);
                Log.d(TAG, "onNavigationItemSelected: navigated to chatFragment");
            } else {
                navController.navigate(R.id.mainFragment);
            }
            toolbar.setTitle("Peers");
        } else if (id == R.id.nav_history) {
            navController.navigate(R.id.historyFragment);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void updateOurDevice(int status) {
        //TODO UPDATED STATUS FOR OUR DEVICE
        Log.d(TAG, "updateOurDevice: device status updated:" + getDeviceStatus(status));
        myDeviceStatus = status;
        if(status != WifiP2pDevice.CONNECTED){
            Log.d(TAG, "updateOurDevice: came in here"  + getDeviceStatus(status));
            if (navController.getCurrentDestination().getId() == R.id.chatFragment){
                navController.navigateUp();
            }
        }
    }

    @Override
    public WifiP2pManager getManager() {
        return wManager;
    }

    @Override
    public WifiP2pManager.Channel getChannel() {
        return wChannel;
    }

    @Override
    public WifiP2pManager.ConnectionInfoListener getConnectionInfoListener() {
        return connectionInfoListener;
    }

    @Override
    public WifiP2pManager.PeerListListener getPeerListListener() {
        return peerListListener;
    }

}

