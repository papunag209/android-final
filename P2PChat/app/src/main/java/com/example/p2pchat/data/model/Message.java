package com.example.p2pchat.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


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

    String MessageTime;

    String MessageStatus;

    public Long getMessageId() {
        return MessageId;
    }

    public void setMessageId(Long messageId) {
        MessageId = messageId;
    }

    public Long getSessionId() {
        return SessionId;
    }

    public void setSessionId(Long sessionId) {
        SessionId = sessionId;
    }

    public String getMessageTime() {
        return MessageTime;
    }

    public void setMessageTime(String messageTime) {
        MessageTime = messageTime;
    }

    public String getMessageStatus() {
        return MessageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        MessageStatus = messageStatus;
    }
}
