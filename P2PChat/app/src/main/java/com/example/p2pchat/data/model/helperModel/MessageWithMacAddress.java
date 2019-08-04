package com.example.p2pchat.data.model.helperModel;

import androidx.room.Entity;

import com.example.p2pchat.data.model.Message;

import java.io.Serializable;

@Entity
public class MessageWithMacAddress extends Message {

    String peerMac;

    public String getPeerMac() {
        return peerMac;
    }

    public void setPeerMac(String peerMac) {
        this.peerMac = peerMac;
    }

    @Override
    public String toString() {
        return super.toString() + '|' + getPeerMac();
    }
}
