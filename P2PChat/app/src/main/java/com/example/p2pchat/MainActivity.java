package com.example.p2pchat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.InetAddresses;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import com.example.p2pchat.adapters.PeersRecyclerViewAdapter;
import com.example.p2pchat.receivers.WifiBroadcastReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    NavController navController;
    WifiP2pManager wManager;
    WifiP2pManager.Channel wChannel;
    BroadcastReceiver wReceiver;
    IntentFilter wFilter = new IntentFilter();

    String[] appPerms = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION};

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {

            Log.d(TAG, "onPeersAvailable: " + wifiP2pDeviceList.getDeviceList());
            peers.postValue(wifiP2pDeviceList.getDeviceList());

            //TODO DISPLAY DATA WITH ADAPTER
//                    PeersRecyclerViewAdapter adapter = new PeersRecyclerViewAdapter(peerNames);
        }
    };
    ;

    MutableLiveData<Collection<WifiP2pDevice>> peers = new MutableLiveData<>();

    private static final int PERMISSION_REQ_CODE = 1;

    public MutableLiveData<Collection<WifiP2pDevice>> getPeers() {
        return this.peers;
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
        if(wReceiver != null) {
            registerReceiver(wReceiver, wFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(wReceiver != null) {
            unregisterReceiver(wReceiver);
        }
    }

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                Log.d(TAG, "onConnectionInfoAvailable: YOU ARE THE HOST");
                Toast.makeText(MainActivity.this, "YOU ARE THE HOST", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "onConnectionInfoAvailable: YOU ARE THE CLIENT");
                Toast.makeText(MainActivity.this, "YOU ARE THE CLIENT", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void getConnection(final WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        Log.d(TAG, "getConnection: Will try to connect to" + device.deviceName);
        wManager.connect(wChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Connected to device: " + device.deviceName);
            }

            @Override
            public void onFailure(int i) {
                Log.d(TAG, "Failed to connect to device: " + device.deviceName);
            }
        });
    }

    public static final int MESSAGE_READ = 5;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what){
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) message.obj;
                    String tempMsg = new String(readBuff,0,message.arg1);
                    //TODO MESSAGE ARRIVED
                    Toast.makeText(MainActivity.this, "Message read: " + tempMsg, Toast.LENGTH_SHORT).show();
                    break;

            }
            return true;
        }
    });


    private void startApp() {
//        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        wifiManager.setWifiEnabled(true);
//        wifiManager.setWifiEnabled(false);

        wManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wChannel = wManager.initialize(this, getMainLooper(), null);
        wReceiver = new WifiBroadcastReceiver(wChannel, wManager, peerListListener, connectionInfoListener);

        wFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                discoverPeers();

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navController = Navigation.findNavController(this, R.id.navHostFragment);

    }

    private void discoverPeers() {
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
        } else {
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
            navController.navigate(R.id.mainFragment);
        } else if (id == R.id.nav_history) {
            navController.navigate(R.id.historyFragment);
        } else if (id == R.id.nav_debug) {
            navController.navigate(R.id.dummyFragment);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
