package com.example.p2pchat.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(  entity = Session.class,
        parentColumns = "SessionId",
        childColumns = "SessionId",
        onDelete = CASCADE
))
public class Message implements Serializable {
    @PrimaryKey(autoGenerate = true)
    Long MessageId;

    Long SessionId;

    String MessageTime;

    String MessageStatus;

    String MessageText;

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

    public String getMessageText() {
        return MessageText;
    }

    public void setMessageText(String messageText) {
        MessageText = messageText;
    }

    @Override
    public String toString() {
        return this.getMessageText() + "|" + this.getMessageId() + "|" + this.getMessageTime() + '|' + getSessionId();
    }
}
