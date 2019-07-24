package com.example.p2pchat.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Time;

@Entity
public class Session {

    @PrimaryKey(autoGenerate = true)
    Long SessionId;

    Time SessionStartTime;

    String PeerPhoneName;
}
