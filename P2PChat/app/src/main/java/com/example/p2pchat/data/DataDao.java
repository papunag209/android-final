package com.example.p2pchat.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.p2pchat.data.model.Message;
import com.example.p2pchat.data.model.Session;
import com.example.p2pchat.data.model.helperModel.MessageWithMacAddress;
import com.example.p2pchat.data.model.helperModel.SessionWithMessageCount;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface DataDao {
    //Session methods
    @Query("select * from Session")
    LiveData<List<Session>> getSessions();

    @Query("select * from Session")
    List<Session> getSessionsSync();

    @Query("select * from Session where SessionId = :id")
    LiveData<Session> getSessionById(Long id);

    @Query("select * from Session where SessionId = :id")
    Session getSessionByIdSync(Long id);

    @Query("select * from Session where SessionId = :id")
    Session getASession(Long id);

    @Query("select * from Session where peerMac = :mac")
    LiveData<Session> getSessionByMac(String mac);

    @Insert
    Long insertSession(Session session);

    @Insert
    Single<Long> inserSessionAsync(Session session);

    @Update
    void updateSession(Session session);

    @Query("delete from Session")
    void clearSessions();

    @Delete
    void deleteSession(Session session);

    //get session with message count
    @Query("select *, count(*) as messageCount from session s, message m where s.SessionId = :sessionId and s.SessionId = m.SessionId group by m.SessionId")
    LiveData<SessionWithMessageCount> getSessionWithMessageCount(Long sessionId);

    @Query("select *, count(*) as messageCount from session s, message m where s.SessionId = m.SessionId group by m.SessionId")
    LiveData<List<SessionWithMessageCount>> getSessionListWithMessageCount();

    //Message methods
    @Query("select * from Message where SessionId = :sessionId")
    LiveData<List<Message>> getMessages(Long sessionId);

    @Query("select * from Message m, Session s where m.SessionId = s.SessionId and m.MessageStatus='PENDING'")
    LiveData<List<MessageWithMacAddress>> getPendingMessages();

    @Insert
    Long insertMessage(Message message);

    @Update
    Completable updateMessage(Message message);

    @Query("delete from Message")
    void clearMessages();

    @Insert
    Completable insertMessageAsync(Message message);

}
