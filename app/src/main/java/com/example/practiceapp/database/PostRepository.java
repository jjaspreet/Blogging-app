package com.example.practiceapp.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PostRepository {

    private PostDao noteDao;
    private List<PostEntity> allNotes;

    public PostRepository(Application application) {
        PostDatabase database = PostDatabase.getInstance(application);
        noteDao = database.postDao();
        allNotes = noteDao.getAllNotes();

    }

    public void insert(PostEntity post){

        new InsertPostAsyncTask(noteDao).execute(post);
    }

    public void delete(PostEntity post){

        new DeletePostAsyncTask(noteDao).execute(post);

    }

    public void update(PostEntity post){

        new UpdatePostAsyncTask(noteDao).execute(post);
    }

    public void deleteAllPosts(){

        new DeleteAllPostAsyncTask(noteDao).execute();
    }

    public List<PostEntity> getAllNotes(){
        return allNotes;
    }

    public static class InsertPostAsyncTask extends AsyncTask<PostEntity, Void, Void> {
        private PostDao postDao;
        private InsertPostAsyncTask (PostDao noteDao){
            this.postDao = noteDao;
        }

        @Override
        protected Void doInBackground(PostEntity... notes) {
            postDao.insert(notes[0]);
            return null;
        }
    }

    public static class UpdatePostAsyncTask extends AsyncTask<PostEntity, Void, Void> {
        private PostDao postDao;
        private UpdatePostAsyncTask (PostDao noteDao){
            this.postDao = noteDao;
        }

        @Override
        protected Void doInBackground(PostEntity... notes) {
            postDao.update(notes[0]);
            return null;
        }

    }

    public static class DeletePostAsyncTask extends AsyncTask<PostEntity, Void, Void> {
        private PostDao postDao;
        private DeletePostAsyncTask (PostDao noteDao){
            this.postDao = noteDao;
        }

        @Override
        protected Void doInBackground(PostEntity... notes) {
            postDao.update(notes[0]);
            return null;
        }

    }

    public static class DeleteAllPostAsyncTask extends AsyncTask<Void, Void, Void> {
        private PostDao postDao;
        private DeleteAllPostAsyncTask (PostDao noteDao){
            this.postDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... notes) {
            postDao.deleteAllNotes();
            return null;
        }

    }






}
