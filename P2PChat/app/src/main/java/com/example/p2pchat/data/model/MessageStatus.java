package com.example.p2pchat.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MessageStatus {
    @PrimaryKey(autoGenerate = true)
    Long MessageStatusId;
}
