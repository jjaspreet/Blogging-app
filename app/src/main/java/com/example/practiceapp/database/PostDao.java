package com.example.practiceapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.practiceapp.storage.UserEntity;

import java.util.List;

@Dao
public interface PostDao {

    @Query("SELECT * FROM PostEntity")
    List<PostEntity> getAllNotes();

    @Insert
    void insert(PostEntity postEntity);

    @Delete
    void delete(PostEntity postEntity);

    @Update
    void update(PostEntity postEntity);

    @Query("DELETE FROM PostEntity")
    void deleteAllNotes();

   }
