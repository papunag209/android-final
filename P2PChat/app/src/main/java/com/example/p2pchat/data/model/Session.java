package com.example.p2pchat.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Time;
import java.sql.Timestamp;

@Entity
public class Session {

    @PrimaryKey(autoGenerate = true)
    Long SessionId;

    String SessionStartTime;

    String PeerPhoneName;
}
