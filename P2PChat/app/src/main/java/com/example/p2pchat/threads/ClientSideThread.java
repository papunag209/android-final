package com.example.p2pchat.threads;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


public class ClientSideThread extends Thread{

    InetAddress address;
    Socket socket;
    SendAndReceive sendAndReceive;
    Handler handler;
    private static final String TAG = "ClientSideThread";


    public SendAndReceive getSendAndReceive() {
        return sendAndReceive;
    }

    public ClientSideThread(InetAddress address, SendAndReceive sendAndReceive, Handler handler){
        socket = new Socket();
        this.address = address;
        this.sendAndReceive = sendAndReceive;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(address.getHostAddress(),8080),1000);
            sendAndReceive = new SendAndReceive(socket,handler);
            Log.d(TAG, "run: INITIALIZED SEND AND RECEIVE" + sendAndReceive);

            sendAndReceive.start();
            Log.d(TAG, "run: sending msg from client");
            sendAndReceive.write("gamadjobaa".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
