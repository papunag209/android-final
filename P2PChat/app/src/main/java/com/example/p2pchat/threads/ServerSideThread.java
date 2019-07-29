package com.example.p2pchat.threads;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSideThread extends Thread {
    ServerSocket serverSocket;
    Socket socket;
    SendAndReceive sendAndReceive;
    Handler handler;

    private static final String TAG = "ServerSideThread";
    
    public ServerSideThread(SendAndReceive sendAndReceive, Handler handler){
        this.sendAndReceive = sendAndReceive;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8080);
            socket = serverSocket.accept();
            this.sendAndReceive = new SendAndReceive(socket,handler);
            sendAndReceive.start();
            if(sendAndReceive != null) {
                Log.d(TAG, "onClick: SENDING MESSAGE");
                sendAndReceive.write("HELLO".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
