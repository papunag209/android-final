package com.example.p2pchat.threads;

import android.os.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSideThread extends Thread {
    ServerSocket serverSocket;
    Socket socket;
    SendAndReceive sendAndReceive;
    Handler handler;

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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
