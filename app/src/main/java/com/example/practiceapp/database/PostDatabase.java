package com.example.practiceapp.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.practiceapp.storage.UserDao;

@Database(entities = {PostEntity.class}, version = 1)
public abstract class PostDatabase extends RoomDatabase {

    private static PostDatabase instance;

    public abstract PostDao postDao();

    public static synchronized PostDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PostDatabase.class, "note-database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomcallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomcallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new Populatedbasynctask(instance).execute();
        }
    };

    private static class Populatedbasynctask extends AsyncTask<Void,Void , Void> {
        private PostDao noteDao;
        private Populatedbasynctask(PostDatabase db){
            noteDao= db.postDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
           noteDao.insert(new PostEntity("Jaspreet", "This is my Test description", "-MI5-fX53YjlonHnWABn", "https://firebasestorage.googleapis.com/v0/b/myblogapp-33e27.appspot.com/o/Post_Image%2F10105?alt=media&token=ca16ef9c-11b5-4571-8b80-c06e31b4b006", "hIRH3GyrrANexaPjrxWS3gFRVzu2", "https://firebasestorage.googleapis.com/v0/b/myblogapp-33e27.appspot.com/o/users_photo%2F10093?alt=media&token=a1b6d55a-d24e-4732-a784-59b39fd27005"));
           noteDao.insert(new PostEntity("Jaspreet", "This is my Test description", "-MI5-fX53YjlonHnWABn", "https://firebasestorage.googleapis.com/v0/b/myblogapp-33e27.appspot.com/o/Post_Image%2F10105?alt=media&token=ca16ef9c-11b5-4571-8b80-c06e31b4b006", "hIRH3GyrrANexaPjrxWS3gFRVzu2", "https://firebasestorage.googleapis.com/v0/b/myblogapp-33e27.appspot.com/o/users_photo%2F10093?alt=media&token=a1b6d55a-d24e-4732-a784-59b39fd27005"));
            return null;
        }
    }
}
