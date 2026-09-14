package com.example.indiegamecommunity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class UserProfileDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_COLLECTIONS = "collections";
    private static final String TABLE_LIKES = "likes";
    private static final String TABLE_VOTES = "user_votes";
    private static final String TABLE_SUPPORTS = "user_supports";
    private static final String TABLE_MY_GAMES = "my_games";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_POST_ID = "post_id";
    private static final String COLUMN_POST_TITLE = "post_title";
    private static final String COLUMN_GAME_NAME = "game_name";
    private static final String COLUMN_GAME_ID = "game_id";
    private static final String COLUMN_CREATE_TIME = "create_time";

    private String databaseName;

    public UserProfileDatabaseHelper(Context context, String username) {
        super(context, "profile_" + username + ".db", null, DATABASE_VERSION);
        this.databaseName = "profile_" + username + ".db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COLLECTIONS_TABLE = "CREATE TABLE " + TABLE_COLLECTIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POST_ID + " INTEGER UNIQUE,"
                + COLUMN_POST_TITLE + " TEXT,"
                + COLUMN_CREATE_TIME + " INTEGER"
                + ")";

        String CREATE_LIKES_TABLE = "CREATE TABLE " + TABLE_LIKES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POST_ID + " INTEGER UNIQUE,"
                + COLUMN_CREATE_TIME + " INTEGER"
                + ")";

        String CREATE_USER_VOTES_TABLE = "CREATE TABLE " + TABLE_VOTES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_GAME_ID + " INTEGER UNIQUE,"
                + COLUMN_CREATE_TIME + " INTEGER"
                + ")";

        String CREATE_USER_SUPPORTS_TABLE = "CREATE TABLE " + TABLE_SUPPORTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_GAME_ID + " INTEGER UNIQUE,"
                + COLUMN_CREATE_TIME + " INTEGER"
                + ")";

        String CREATE_MY_GAMES_TABLE = "CREATE TABLE " + TABLE_MY_GAMES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_GAME_ID + " INTEGER,"
                + COLUMN_GAME_NAME + " TEXT,"
                + COLUMN_CREATE_TIME + " INTEGER"
                + ")";

        db.execSQL(CREATE_COLLECTIONS_TABLE);
        db.execSQL(CREATE_LIKES_TABLE);
        db.execSQL(CREATE_USER_VOTES_TABLE);
        db.execSQL(CREATE_USER_SUPPORTS_TABLE);
        db.execSQL(CREATE_MY_GAMES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LIKES + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_POST_ID + " INTEGER UNIQUE,"
                    + COLUMN_CREATE_TIME + " INTEGER"
                    + ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_VOTES + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_GAME_ID + " INTEGER UNIQUE,"
                    + COLUMN_CREATE_TIME + " INTEGER"
                    + ")");
        }
        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SUPPORTS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_GAME_ID + " INTEGER UNIQUE,"
                    + COLUMN_CREATE_TIME + " INTEGER"
                    + ")");
        }
    }

    public long addCollection(int postId, String postTitle) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_ID, postId);
        values.put(COLUMN_POST_TITLE, postTitle);
        values.put(COLUMN_CREATE_TIME, System.currentTimeMillis());
        long id = db.insert(TABLE_COLLECTIONS, null, values);
        db.close();
        return id;
    }

    public boolean removeCollection(int postId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_COLLECTIONS, COLUMN_POST_ID + " = ?", new String[]{String.valueOf(postId)});
        db.close();
        return rows > 0;
    }

    public boolean isCollected(int postId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_COLLECTIONS, null, COLUMN_POST_ID + " = ?",
                new String[]{String.valueOf(postId)}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public List<Integer> getCollectionPostIds() {
        List<Integer> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_COLLECTIONS, new String[]{COLUMN_POST_ID}, null, null, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POST_ID)));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<String> getCollectionPostTitles() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_COLLECTIONS, new String[]{COLUMN_POST_TITLE}, null, null, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_TITLE)));
        }
        cursor.close();
        db.close();
        return list;
    }

    public long addLike(int postId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_ID, postId);
        values.put(COLUMN_CREATE_TIME, System.currentTimeMillis());
        long id = db.insert(TABLE_LIKES, null, values);
        db.close();
        return id;
    }

    public boolean isLiked(int postId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_LIKES, null, COLUMN_POST_ID + " = ?",
                new String[]{String.valueOf(postId)}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public long addVote(int gameId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME_ID, gameId);
        values.put(COLUMN_CREATE_TIME, System.currentTimeMillis());
        long id = db.insert(TABLE_VOTES, null, values);
        db.close();
        return id;
    }

    public boolean isVoted(int gameId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_VOTES, null, COLUMN_GAME_ID + " = ?",
                new String[]{String.valueOf(gameId)}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public long addSupport(int projectId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME_ID, projectId);
        values.put(COLUMN_CREATE_TIME, System.currentTimeMillis());
        long id = db.insert(TABLE_SUPPORTS, null, values);
        db.close();
        return id;
    }

    public boolean isSupported(int projectId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SUPPORTS, null, COLUMN_GAME_ID + " = ?",
                new String[]{String.valueOf(projectId)}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public long addMyGame(int gameId, String gameName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME_ID, gameId);
        values.put(COLUMN_GAME_NAME, gameName);
        values.put(COLUMN_CREATE_TIME, System.currentTimeMillis());
        long id = db.insert(TABLE_MY_GAMES, null, values);
        db.close();
        return id;
    }

    public boolean removeMyGame(int gameId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_MY_GAMES, COLUMN_GAME_ID + " = ?", new String[]{String.valueOf(gameId)});
        db.close();
        return rows > 0;
    }

    public List<String> getMyGameNames() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_MY_GAMES, new String[]{COLUMN_GAME_NAME}, null, null, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAME_NAME)));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Integer> getMyGameIds() {
        List<Integer> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_MY_GAMES, new String[]{COLUMN_GAME_ID}, null, null, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GAME_ID)));
        }
        cursor.close();
        db.close();
        return list;
    }
}