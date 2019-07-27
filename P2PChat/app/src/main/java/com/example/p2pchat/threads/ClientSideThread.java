package com.example.p2pchat.threads;

import android.os.Handler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


public class ClientSideThread extends Thread{

    InetAddress address;
    Socket socket;
    SendAndReceive sendAndReceive;
    Handler handler;

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
            sendAndReceive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
