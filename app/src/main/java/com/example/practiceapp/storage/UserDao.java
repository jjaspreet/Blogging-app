package com.example.practiceapp.storage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {


    @Query("SELECT * FROM userentity")
    List<UserEntity> getAllUser();

    @Insert
    void insertAll(UserEntity... users);

    @Delete
    void delete(UserEntity user);
}
