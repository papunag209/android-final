package com.example.p2pchat.threads;

import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.example.p2pchat.MainActivity.MESSAGE_READ;

public class SendAndReceive extends Thread {
    private final static int SIZE = 1024;
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private Handler handler;

    public SendAndReceive(Socket socket, Handler handler){
        this.socket = socket;
        this.handler = handler;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void write(byte[] bytes){
        try {
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buff = new byte[SIZE];
        int bytes;

        while(socket != null){
            try {
                bytes = in.read(buff);
                if(bytes > 0){
                    handler.obtainMessage(MESSAGE_READ,bytes,-1,buff).sendToTarget();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
