package com.example.p2pchat.threads;


import android.os.Handler;

import com.example.p2pchat.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.example.p2pchat.MainActivity.MESSAGE_READ;

public class SendAndReceive extends Thread {

    private static final String TAG = "SendAndReceive";

    private final static int SIZE = 1024;
    public Socket socket;
    private OutputStream out;
    private InputStream in;
    private Handler handler;
    MainActivity activity;

    //threading idea taken from internet
    public SendAndReceive(Socket socket, Handler handler, MainActivity activity) {
        this.socket = socket;
        this.handler = handler;
        this.activity = activity;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void write(final byte[] bytes) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    activity.removeConnection(null);
                }
            }
        }).start();
    }

    @Override
    public void run() {
        byte[] buff = new byte[SIZE];
        int bytes;
        //code from google developers docs
        while (socket != null) {
            try {
                bytes = in.read(buff);
                if (bytes > 0) {
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buff).sendToTarget();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
