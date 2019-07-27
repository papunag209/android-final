package com.example.p2pchat.threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSideThread extends Thread {
    ServerSocket serverSocket;
    Socket socket;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8080);
            socket = serverSocket.accept();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
