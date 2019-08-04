package com.example.p2pchat.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.sql.Time;
import java.sql.Timestamp;

@Entity(indices = {@Index(value = {"PeerMAC"},
        unique = true)})
public class Session {

    @PrimaryKey(autoGenerate = true)
    Long SessionId;

    String SessionStartTime;

    public String getPeerMAC() {
        return PeerMAC;
    }

    public void setPeerMAC(String peerMAC) {
        PeerMAC = peerMAC;
    }

    String PeerPhoneName;

    String PeerMAC;

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
        return ""+getSessionId()+"|"+getPeerMAC()+"|"+getPeerPhoneName()+"|"+getSessionStartTime();
    }


}
