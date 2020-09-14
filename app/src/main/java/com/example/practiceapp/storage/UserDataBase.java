package com.example.practiceapp.storage;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = UserEntity.class, version = 1)
public abstract class UserDataBase extends RoomDatabase {

    private UserDao userDao;
}
