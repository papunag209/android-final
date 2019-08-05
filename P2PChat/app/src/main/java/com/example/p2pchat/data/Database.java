package com.example.p2pchat.data;


import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.p2pchat.App;
import com.example.p2pchat.data.model.Message;
import com.example.p2pchat.data.model.Session;


@androidx.room.Database(entities = {Message.class, Session.class}, version = 1)
public abstract class Database extends RoomDatabase {

    private static final String DATABASE_NAME = "app_database";

    private static Database INSTANCE;

    private static final Object lock = new Object();

    public abstract DataDao dataDao();

    public static Database getInstance(){
        synchronized (lock){
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                        App.getContext(),
                        Database.class,
                        DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return INSTANCE;
    }

}
