package com.example.p2pchat.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.p2pchat.data.model.Message;
import com.example.p2pchat.data.model.Session;

import java.util.List;

@Dao
public interface DataDao {
    //Session methods
    @Query("select * from Session")
    LiveData<List<Session>> getSessions();

    @Query("select * from Session where SessionId = :id")
    LiveData<Session> getSessionById(Long id);

    @Query("select * from Session where SessionId = :id")
    Session getASession(Long id);

    @Insert
    Long insertSession(Session session);

    @Update
    void updateSession(Session session);

    @Query("delete from Session")
    void clearSessions();

    @Delete
    void deleteSession(Session session);

    //Message methods
    @Query("select * from Message where SessionId = :sessionId")
    LiveData<List<Message>> getMessages(Long sessionId);

    @Insert
    Long insertMessage(Message message);

    @Update
    void updateMessage(Message message);

    @Query("delete from Message")
    void clearMessages();


}
