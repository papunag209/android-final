package com.example.p2pchat.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.sql.Time;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(  entity = Session.class,
        parentColumns = "SessionId",
        childColumns = "SessionId",
        onDelete = CASCADE
))
public class Message {
    @PrimaryKey(autoGenerate = true)
    Long MessageId;

    Long SessionId;

    Time MessageTime;

    String MessageStatus;
}
