package com.example.administrator.youtube.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.administrator.youtube.database.VideoContract.VideoHistory;
import com.example.administrator.youtube.database.VideoContract.VideoFavourite;
import com.example.administrator.youtube.database.VideoContract.VideoReleted;
import com.example.administrator.youtube.database.VideoContract.VideoDefault;

import com.example.administrator.youtube.fragment.HomeFragment;
import com.example.administrator.youtube.model.Video;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.version;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by Administrator on 10/4/2017.
 */

public class VideoDbHelper extends SQLiteOpenHelper{

    private final static String LOG_TAG = VideoDbHelper.class.getSimpleName();
    SQLiteDatabase database;
    /*command create table history */
    private static final String SQL_CREATE_TABLE_HISTORY = "CREATE TABLE " + VideoHistory.TABLE_NAME + " (" +
            VideoHistory._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+
            VideoHistory.COLUMN_ID_VIDEO + " TEXT NOT NULL, "+
            VideoHistory.COLUMN_TITLE +" TEXT ,"+
            VideoHistory.COLUMN_CHANNEL_ID+ " TEXT , "+
            VideoHistory.COLUMN_TITLE_CHANNEL+ " TEXT , "+
            VideoHistory.COLUMN_THUMBNAIL+ " TEXT , "+
            VideoHistory.COLUMN_DURATION+ " TEXT , "+
            VideoHistory.COLUMN_ESLAPSED_TIME+" TEXT , "+
            VideoHistory.COLUMN_VIEW_COUNT+" TEXT )";
    private static final String SQL_DELETE_TABLE_HISTORY = "DROP TABLE IF EXISTS " +VideoHistory.TABLE_NAME;

    /*command create table favourite*/
    private static final String SQL_CREATE_TABLE_FAVOURITE = "CREATE TABLE " + VideoFavourite.TABLE_NAME + " (" +
            VideoFavourite._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+
            VideoFavourite.COLUMN_ID_VIDEO + " TEXT , "+
            VideoFavourite.COLUMN_TITLE +" TEXT ,"+
            VideoFavourite.COLUMN_CHANNEL_ID+ " TEXT , "+
            VideoFavourite.COLUMN_TITLE_CHANNEL+ " TEXT , "+
            VideoFavourite.COLUMN_THUMBNAIL+ " TEXT , "+
            VideoFavourite.COLUMN_DURATION+ " TEXT , "+
            VideoFavourite.COLUMN_ESLAPSED_TIME+" TEXT , "+
            VideoFavourite.COLUMN_VIEW_COUNT+" TEXT )";
    private static final String SQL_DELETE_TABLE_FAVOURITE = "DROP TABLE IF EXISTS " + VideoFavourite.TABLE_NAME;

    /*command create table related video*/
    private static final String SQL_CREATE_TABLE_DEFAULT = "CREATE TABLE " + VideoDefault.TABLE_NAME + " (" +
            VideoDefault._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+
            VideoDefault.COLUMN_ID_VIDEO + " TEXT , "+
            VideoDefault.COLUMN_TITLE +" TEXT ,"+
            VideoDefault.COLUMN_CHANNEL_ID+ " TEXT , "+
            VideoDefault.COLUMN_TITLE_CHANNEL+ " TEXT , "+
            VideoDefault.COLUMN_THUMBNAIL+ " TEXT , "+
            VideoDefault.COLUMN_DURATION+ " TEXT , "+
            VideoDefault.COLUMN_ESLAPSED_TIME+" TEXT , "+
            VideoDefault.COLUMN_VIEW_COUNT+" TEXT )";
    private static final String SQL_DELETE_TABLE_DEFAULT = "DROP TABLE IF EXISTS " + VideoDefault.TABLE_NAME;

    /*command create table related video*/
    private static final String SQL_CREATE_TABLE_RELATED = "CREATE TABLE " + VideoReleted.TABLE_NAME + " (" +
            VideoReleted._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+
            VideoReleted.COLUMN_ID_ROOT_VIDEO + " TEXT ,"+
            VideoReleted.COLUMN_ID_VIDEO + " TEXT , "+
            VideoReleted.COLUMN_TITLE +" TEXT ,"+
            VideoReleted.COLUMN_CHANNEL_ID+ " TEXT , "+
            VideoReleted.COLUMN_TITLE_CHANNEL+ " TEXT , "+
            VideoReleted.COLUMN_THUMBNAIL+ " TEXT , "+
            VideoReleted.COLUMN_DURATION+ " TEXT , "+
            VideoReleted.COLUMN_ESLAPSED_TIME+" TEXT , "+
            VideoReleted.COLUMN_VIEW_COUNT+" TEXT )";
    private static final String SQL_DELETE_TABLE_RELATED = "DROP TABLE IF EXISTS " + VideoReleted.TABLE_NAME;

    private static final int DATABASE_VERSION = 14;
    private static final String DATABASE_NAME = "FloatingYoutube.db";

    public VideoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_HISTORY);
        db.execSQL(SQL_CREATE_TABLE_FAVOURITE);
        db.execSQL(SQL_CREATE_TABLE_RELATED);
        Log.v(LOG_TAG,"tao bang default" +SQL_CREATE_TABLE_DEFAULT);
        db.execSQL(SQL_CREATE_TABLE_DEFAULT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_HISTORY);
        db.execSQL(SQL_DELETE_TABLE_FAVOURITE);
        db.execSQL(SQL_DELETE_TABLE_RELATED);
        db.execSQL(SQL_DELETE_TABLE_DEFAULT);
        onCreate(db);
    }
    /*CRUD history*/
    public Cursor historyQuery(Video video) {
            SQLiteDatabase database = this.getReadableDatabase();
            Log.v(LOG_TAG,"co truy van duoc khong");
            String selectQuery = "SELECT * FROM " + VideoHistory.TABLE_NAME +
                                 " WHERE " +VideoHistory.COLUMN_ID_VIDEO + "='" + video.getID() + "'";
            Log.v(LOG_TAG,"sql query " +selectQuery);
            Cursor cursor = database.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            int _id = cursor.getColumnIndex(VideoHistory._ID);
            int column_id = cursor.getColumnIndex(VideoHistory.COLUMN_ID_VIDEO);
            int column_title = cursor.getColumnIndex(VideoHistory.COLUMN_TITLE);
            int id = cursor.getInt(_id);
            String id_video = cursor.getString(column_id);
            String title = cursor.getString(column_title);
            Log.v(LOG_TAG, "idq= " + id + " id_videoq= " + id_video + " titleq=" + title);
        }
            return cursor;
    }

    public Cursor historyQueryAllContacts() {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " +VideoHistory.TABLE_NAME;
        Cursor cursor = database.rawQuery(selectQuery,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int _id = cursor.getColumnIndex(VideoHistory._ID);
            int column_id = cursor.getColumnIndex(VideoHistory.COLUMN_ID_VIDEO);
            int column_title = cursor.getColumnIndex(VideoHistory.COLUMN_TITLE);

            int id = cursor.getInt(_id);
            String id_video = cursor.getString(column_id);
            String title = cursor.getString(column_title);
            Log.v(LOG_TAG,"id= " +id + " id_video= " + id_video +" title=" +title);
            cursor.moveToNext();
        }
        return  cursor;
    }

    /*retrieve data display history fragment*/
    public ArrayList<Video> historyGetData() {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " +VideoHistory.TABLE_NAME + " ORDER BY " + VideoHistory._ID + " DESC";
        Log.v(LOG_TAG,"query desc la gi " +selectQuery);
        database.rawQuery(selectQuery,null);
        Cursor cursor = database.rawQuery(selectQuery,null);
        ArrayList<Video> history = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int col_id = cursor.getColumnIndex(VideoHistory.COLUMN_ID_VIDEO);
            int col_title = cursor.getColumnIndex(VideoHistory.COLUMN_TITLE);
            int col_author = cursor.getColumnIndex(VideoHistory.COLUMN_TITLE_CHANNEL);
            int col_view = cursor.getColumnIndex(VideoHistory.COLUMN_VIEW_COUNT);
            int col_thumb = cursor.getColumnIndex(VideoHistory.COLUMN_THUMBNAIL);

            String id = cursor.getString(col_id);
            Log.v(LOG_TAG,"id= " +id);
            String title = cursor.getString(col_title);
            Log.v(LOG_TAG,"title= " +title);
            String author = cursor.getString(col_author);
            Log.v(LOG_TAG,"author= " +author);
            String view = cursor.getString(col_view);
            String thumb = cursor.getString(col_thumb);
            history.add(new Video(id, title, thumb, author ,view,null,null,null));
            cursor.moveToNext();
        }
        return history;
    }
    public int historyUpdate (Video video,ContentValues values) {
        SQLiteDatabase database = getWritableDatabase();
        String selection = VideoHistory.COLUMN_ID_VIDEO + "=?";
        String selectionArgs [] = new String[]{video.getID()};
        int newUpdate = database.update(VideoHistory.TABLE_NAME,values,selection,selectionArgs);
        return newUpdate;
    }

    public long historyInsert(Video video) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VideoHistory.COLUMN_ID_VIDEO,video.getID());
        values.put(VideoHistory.COLUMN_TITLE,video.getTitle());
        values.put(VideoHistory.COLUMN_THUMBNAIL,video.getThumbnails());
        values.put(VideoHistory.COLUMN_CHANNEL_ID,video.getChannelId());
        values.put(VideoHistory.COLUMN_ESLAPSED_TIME,video.getEslapedTime());
        values.put(VideoHistory.COLUMN_TITLE_CHANNEL,video.getTitleChannel());
        values.put(VideoHistory.COLUMN_VIEW_COUNT,video.getViewCount());
        long newId =  database.insert(VideoHistory.TABLE_NAME,null,values);

        Log.v(LOG_TAG,"co insert khong " +newId);
        return newId;
    }

    public int historyDelete(Video video) {
        SQLiteDatabase database = this.getWritableDatabase();
        String selection = VideoHistory.COLUMN_ID_VIDEO + "=?";
        String selectionArgs [] = new String[]{video.getID()};
        int delId = database.delete(VideoHistory.TABLE_NAME,selection,selectionArgs);
        return delId;
    }


    /*CRUD favourite*/
    public Cursor favouriteQuery(Video video) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + VideoFavourite.TABLE_NAME +
                " WHERE " +VideoFavourite.COLUMN_ID_VIDEO + "='" + video.getID() + "'";
        Log.v(LOG_TAG,"sql query " +selectQuery);
        Cursor cursor = database.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            int _id = cursor.getColumnIndex(VideoFavourite._ID);
            int column_id = cursor.getColumnIndex(VideoFavourite.COLUMN_ID_VIDEO);
            int column_title = cursor.getColumnIndex(VideoFavourite.COLUMN_TITLE);
            int id = cursor.getInt(_id);
            String id_video = cursor.getString(column_id);
            String title = cursor.getString(column_title);
            Log.v(LOG_TAG, "idq= " + id + " id_videoq= " + id_video + " titleq=" + title);
        }
        return cursor;
    }

    public long favouriteInsert(Video video) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VideoFavourite.COLUMN_ID_VIDEO,video.getID());
        values.put(VideoFavourite.COLUMN_TITLE,video.getTitle());
        values.put(VideoFavourite.COLUMN_THUMBNAIL,video.getThumbnails());
        values.put(VideoFavourite.COLUMN_CHANNEL_ID,video.getChannelId());
        values.put(VideoFavourite.COLUMN_DURATION,video.getDurationString());
        values.put(VideoFavourite.COLUMN_ESLAPSED_TIME,video.getEslapedTime());
        values.put(VideoFavourite.COLUMN_TITLE_CHANNEL,video.getTitleChannel());
        values.put(VideoFavourite.COLUMN_VIEW_COUNT,video.getViewCount());
        long newId =  database.insert(VideoFavourite.TABLE_NAME,null,values);

        Log.v(LOG_TAG,"co insert khong " +newId);
        return newId;
    }

    /*retrieve data display favourite fragment*/
    public ArrayList<Video> favouriteGetData() {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " +VideoFavourite.TABLE_NAME + " ORDER BY " + VideoFavourite._ID + " DESC";
        database.rawQuery(selectQuery,null);
        Cursor cursor = database.rawQuery(selectQuery,null);
        ArrayList<Video> favourites = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int col_id = cursor.getColumnIndex(VideoFavourite.COLUMN_ID_VIDEO);
            int col_title = cursor.getColumnIndex(VideoFavourite.COLUMN_TITLE);
            int col_author = cursor.getColumnIndex(VideoFavourite.COLUMN_TITLE_CHANNEL);
            int col_view = cursor.getColumnIndex(VideoFavourite.COLUMN_VIEW_COUNT);
            int col_thumb = cursor.getColumnIndex(VideoFavourite.COLUMN_THUMBNAIL);

            String id = cursor.getString(col_id);
            String title = cursor.getString(col_title);
            String author = cursor.getString(col_author);
            String view = cursor.getString(col_view);
            String thumb = cursor.getString(col_thumb);
            favourites.add(new Video(id, title, thumb, author ,view,null,null,null));
            cursor.moveToNext();
        }
        return favourites;
    }

    public int favouriteDelete(Video video) {
        SQLiteDatabase database = this.getWritableDatabase();
        String selection = VideoFavourite.COLUMN_ID_VIDEO + "=?";
        String selectionArgs [] = new String[]{video.getID()};
        int delId = database.delete(VideoFavourite.TABLE_NAME,selection,selectionArgs);
        return delId;
    }

    /*CRUD releted video */

    public ArrayList<Video> getRelatedList(Video video) {
        ArrayList<Video> reletedList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + VideoReleted.TABLE_NAME +
                " WHERE " +VideoReleted.COLUMN_ID_ROOT_VIDEO + "='" + video.getID() + "'";
        Log.v(LOG_TAG,"sql query " +selectQuery);
        Cursor cursor = database.rawQuery(selectQuery,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int col_id = cursor.getColumnIndex(VideoReleted.COLUMN_ID_VIDEO);
            int col_title = cursor.getColumnIndex(VideoReleted.COLUMN_TITLE);
            int col_author = cursor.getColumnIndex(VideoReleted.COLUMN_TITLE_CHANNEL);
            int col_view = cursor.getColumnIndex(VideoReleted.COLUMN_VIEW_COUNT);
            int col_thumb = cursor.getColumnIndex(VideoReleted.COLUMN_THUMBNAIL);

            String id = cursor.getString(col_id);
            String title = cursor.getString(col_title);
            String author = cursor.getString(col_author);
            String view = cursor.getString(col_view);
            String thumb = cursor.getString(col_thumb);
            reletedList.add(new Video(id, title, thumb, author ,view,null,null,null));
            cursor.moveToNext();
        }
        return reletedList;
    }

    public long relatedInsert(Video relatedVideo,Video root) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VideoReleted.COLUMN_ID_ROOT_VIDEO,root.getID());
        values.put(VideoReleted.COLUMN_ID_VIDEO,relatedVideo.getID());
        values.put(VideoReleted.COLUMN_TITLE,relatedVideo.getTitle());
        values.put(VideoReleted.COLUMN_THUMBNAIL,relatedVideo.getThumbnails());
        values.put(VideoReleted.COLUMN_CHANNEL_ID,relatedVideo.getChannelId());
        values.put(VideoReleted.COLUMN_ESLAPSED_TIME,relatedVideo.getEslapedTime());
        values.put(VideoReleted.COLUMN_TITLE_CHANNEL,relatedVideo.getTitleChannel());
        values.put(VideoReleted.COLUMN_VIEW_COUNT,relatedVideo.getViewCount());
        long newId =  database.insert(VideoReleted.TABLE_NAME,null,values);

        Log.v(LOG_TAG,"co insert khong " +newId);
        return newId;
    }
    /*CRUD table default*/

    public long defaultInsert(ArrayList<Video> videos) {
        long newId = 0;
     for(int i=0; i<videos.size(); i++) {
         Video video = videos.get(i);
         SQLiteDatabase database = getWritableDatabase();
         ContentValues values = new ContentValues();
         values.put(VideoDefault.COLUMN_ID_VIDEO, video.getID());
         values.put(VideoDefault.COLUMN_TITLE, video.getTitle());
         values.put(VideoDefault.COLUMN_THUMBNAIL, video.getThumbnails());
         values.put(VideoDefault.COLUMN_CHANNEL_ID, video.getChannelId());
         values.put(VideoDefault.COLUMN_ESLAPSED_TIME, video.getEslapedTime());
         values.put(VideoDefault.COLUMN_TITLE_CHANNEL, video.getTitleChannel());
         values.put(VideoDefault.COLUMN_VIEW_COUNT, video.getViewCount());
         newId = database.insert(VideoDefault.TABLE_NAME, null, values);
     }
        return newId;
    }

    public ArrayList<Video> defaultGetData() {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " +VideoDefault.TABLE_NAME ;
        database.rawQuery(selectQuery,null);
        Cursor cursor = database.rawQuery(selectQuery,null);
        ArrayList<Video> videoDefault = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int col_id = cursor.getColumnIndex(VideoHistory.COLUMN_ID_VIDEO);
            int col_title = cursor.getColumnIndex(VideoHistory.COLUMN_TITLE);
            int col_author = cursor.getColumnIndex(VideoHistory.COLUMN_TITLE_CHANNEL);
            int col_view = cursor.getColumnIndex(VideoHistory.COLUMN_VIEW_COUNT);
            int col_thumb = cursor.getColumnIndex(VideoHistory.COLUMN_THUMBNAIL);

            String id = cursor.getString(col_id);
            Log.v(LOG_TAG,"id= " +id);
            String title = cursor.getString(col_title);
            Log.v(LOG_TAG,"title= " +title);
            String author = cursor.getString(col_author);
            Log.v(LOG_TAG,"author= " +author);
            String view = cursor.getString(col_view);
            String thumb = cursor.getString(col_thumb);
            videoDefault.add(new Video(id, title, thumb, author ,view,null,null,null));
            cursor.moveToNext();
        }
        return videoDefault;
    }
}
