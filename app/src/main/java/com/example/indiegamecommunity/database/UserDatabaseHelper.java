package com.example.indiegamecommunity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_AVATAR = "avatar";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IS_ADMIN = "is_admin";
    private static final String COLUMN_CREATE_TIME = "create_time";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_NICKNAME + " TEXT,"
                + COLUMN_AVATAR + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT DEFAULT '',"
                + COLUMN_IS_ADMIN + " INTEGER DEFAULT 0,"
                + COLUMN_CREATE_TIME + " INTEGER"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // 创建默认管理员账户
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "admin");
        values.put(COLUMN_PASSWORD, "admin123");
        values.put(COLUMN_NICKNAME, "管理员");
        values.put(COLUMN_AVATAR, "");
        values.put(COLUMN_IS_ADMIN, 1);
        values.put(COLUMN_CREATE_TIME, System.currentTimeMillis());
        db.insert(TABLE_USERS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_DESCRIPTION + " TEXT DEFAULT ''");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_IS_ADMIN + " INTEGER DEFAULT 0");
            // 为已有数据库创建默认管理员
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, "admin");
            values.put(COLUMN_PASSWORD, "admin123");
            values.put(COLUMN_NICKNAME, "管理员");
            values.put(COLUMN_AVATAR, "");
            values.put(COLUMN_IS_ADMIN, 1);
            values.put(COLUMN_CREATE_TIME, System.currentTimeMillis());
            try { db.insert(TABLE_USERS, null, values); } catch (Exception ignored) {}
        }
    }

    public long registerUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_NICKNAME, username);
        values.put(COLUMN_AVATAR, "");
        values.put(COLUMN_CREATE_TIME, System.currentTimeMillis());
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public boolean usernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USERNAME + " = ?",
                new String[]{username}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public boolean updateUserInfo(String username, String nickname, String avatar) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NICKNAME, nickname);
        values.put(COLUMN_AVATAR, avatar);
        int rows = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
        return rows > 0;
    }

    public String getNickname(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_NICKNAME}, COLUMN_USERNAME + " = ?",
                new String[]{username}, null, null, null);
        String nickname = username;
        if (cursor.moveToFirst()) {
            nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME));
        }
        cursor.close();
        db.close();
        return nickname;
    }

    public String getAvatar(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_AVATAR}, COLUMN_USERNAME + " = ?",
                new String[]{username}, null, null, null);
        String avatar = "";
        if (cursor.moveToFirst()) {
            avatar = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR));
        }
        cursor.close();
        db.close();
        return avatar;
    }

    public String getDescription(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_DESCRIPTION}, COLUMN_USERNAME + " = ?",
                new String[]{username}, null, null, null);
        String desc = "";
        if (cursor.moveToFirst()) {
            desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
        }
        cursor.close();
        db.close();
        return desc;
    }

    public boolean isAdmin(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_IS_ADMIN}, COLUMN_USERNAME + " = ?",
                new String[]{username}, null, null, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1;
        }
        cursor.close();
        db.close();
        return result;
    }

    public java.util.List<String[]> getAllUsers() {
        java.util.List<String[]> list = new java.util.ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME, COLUMN_NICKNAME, COLUMN_IS_ADMIN, COLUMN_CREATE_TIME},
                null, null, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(new String[]{
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME)),
                String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN))),
                String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)))
            });
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_USERS, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
        return rows > 0;
    }

    public boolean setAdminStatus(String username, boolean isAdmin) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_ADMIN, isAdmin ? 1 : 0);
        int rows = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
        return rows > 0;
    }
}