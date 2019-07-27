package com.example.p2pchat.threads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSideThread extends Thread{

    InetAddress address;
    Socket socket;


    public ClientSideThread(InetAddress address){
        socket = new Socket();
        this.address = address;
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(address.getHostAddress(),8080),1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
