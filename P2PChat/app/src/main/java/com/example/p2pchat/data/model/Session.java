package com.example.p2pchat.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"peerMac"},
        unique = true)})
public class Session {

    @PrimaryKey(autoGenerate = true)
    Long SessionId;

    String SessionStartTime;

    String PeerPhoneName;

    String peerMac;

    public String getPeerMac() {
        return peerMac;
    }

    public void setPeerMac(String peerMac) {
        this.peerMac = peerMac;
    }


    public Long getSessionId() {
        return SessionId;
    }

    public void setSessionId(Long sessionId) {
        SessionId = sessionId;
    }

    public String getSessionStartTime() {
        return SessionStartTime;
    }

    public void setSessionStartTime(String sessionStartTime) {
        SessionStartTime = sessionStartTime;
    }

    public String getPeerPhoneName() {
        return PeerPhoneName;
    }

    public void setPeerPhoneName(String peerPhoneName) {
        PeerPhoneName = peerPhoneName;
    }

    @Override
    public String toString() {
        return ""+getSessionId()+"|"+ getPeerMac()+"|"+getPeerPhoneName()+"|"+getSessionStartTime();
    }


}
