package com.example.p2pchat.threads;

import android.os.Handler;
import android.util.Log;

import com.example.p2pchat.MainActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


public class ClientSideThread extends Thread {

    InetAddress address;
    private Socket socket;
    SendAndReceive sendAndReceive;
    Handler handler;
    MainActivity activity;
    private static final String TAG = "ClientSideThread";


    public SendAndReceive getSendAndReceive() {
        return sendAndReceive;
    }

    public Socket getSocket() {
        return socket;
    }

    //threading idea taken from internet
    public ClientSideThread(InetAddress address, Handler handler, MainActivity activity) {
        socket = new Socket();
        this.address = address;
        this.activity = activity;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            if(address != null && socket != null) {
                socket.connect(new InetSocketAddress(address.getHostAddress(), 8080), 1000);
                Log.d(TAG, "run:CLIENT SOCKET INITIALIZED");
                sendAndReceive = new SendAndReceive(socket, handler, activity);
                Log.d(TAG, "run: INITIALIZED SEND AND RECEIVE" + sendAndReceive);

                sendAndReceive.start();
                Log.d(TAG, "run: sending msg from client");
                sendAndReceive.write("gamadjobaa".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
