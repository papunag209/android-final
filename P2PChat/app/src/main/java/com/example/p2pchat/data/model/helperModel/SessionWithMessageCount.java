package com.example.p2pchat.data.model.helperModel;

import androidx.room.Entity;

import com.example.p2pchat.data.model.Session;

@Entity
public class SessionWithMessageCount extends Session {

    Integer messageCount;

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    @Override
    public String toString() {
        return super.toString()+"|"+getMessageCount();
    }
}
