package com.example.indiegamecommunity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.indiegamecommunity.model.Comment;
import com.example.indiegamecommunity.model.Crowdfunding;
import com.example.indiegamecommunity.model.GamePromo;
import com.example.indiegamecommunity.model.Notification;
import com.example.indiegamecommunity.model.Post;
import com.example.indiegamecommunity.model.Vote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "community.db";
    private static final int DATABASE_VERSION = 13;

    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_COVER_IMAGE = "cover_image";
    private static final String COLUMN_CONTENT_IMAGES = "content_images";
    private static final String COLUMN_ICON_PATH = "icon_path";
    private static final String COLUMN_BANNER_PATH = "banner_path";
    private static final String COLUMN_TARGET_TYPE = "target_type";
    private static final String COLUMN_POST_ID = "post_id";
    private static final String COLUMN_IMAGE_PATH = "image_path";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_AUTHOR_USERNAME = "author_username";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_IS_READ = "is_read";

    private static final String TABLE_POSTS = "posts";
    private static final String TABLE_COMMENTS = "comments";
    private static final String TABLE_GAME_PROMOS = "game_promos";
    private static final String TABLE_CROWDFUNDINGS = "crowdfundings";
    private static final String TABLE_VOTES = "votes";
    private static final String TABLE_NOTIFICATIONS = "notifications";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_LIKE_COUNT = "like_count";
    private static final String COLUMN_COLLECT_COUNT = "collect_count";
    private static final String COLUMN_CREATE_TIME = "create_time";

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_SCREENSHOT = "screenshot";
    private static final String COLUMN_DOWNLOAD_URL = "download_url";

    private static final String COLUMN_TARGET_AMOUNT = "target_amount";
    private static final String COLUMN_CURRENT_AMOUNT = "current_amount";
    private static final String COLUMN_SUPPORTER_COUNT = "supporter_count";

    private static final String COLUMN_GAME_NAME = "game_name";
    private static final String COLUMN_VOTE_COUNT = "vote_count";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_CATEGORY + " TEXT DEFAULT '',"
                + COLUMN_COVER_IMAGE + " TEXT DEFAULT '',"
                + COLUMN_CONTENT_IMAGES + " TEXT DEFAULT '',"
                + COLUMN_LIKE_COUNT + " INTEGER DEFAULT 0,"
                + COLUMN_COLLECT_COUNT + " INTEGER DEFAULT 0,"
                + COLUMN_STATUS + " TEXT DEFAULT 'approved',"
                + COLUMN_CREATE_TIME + " INTEGER"
                + ")";

        String CREATE_COMMENTS_TABLE = "CREATE TABLE " + TABLE_COMMENTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POST_ID + " INTEGER,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_IMAGE_PATH + " TEXT DEFAULT '',"
                + COLUMN_TARGET_TYPE + " TEXT DEFAULT '',"
                + COLUMN_CREATE_TIME + " INTEGER"
                + ")";

        String CREATE_GAME_PROMOS_TABLE = "CREATE TABLE " + TABLE_GAME_PROMOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_SCREENSHOT + " TEXT,"
                + COLUMN_CONTENT_IMAGES + " TEXT DEFAULT '',"
                + COLUMN_DOWNLOAD_URL + " TEXT,"
                + COLUMN_ICON_PATH + " TEXT DEFAULT '',"
                + COLUMN_BANNER_PATH + " TEXT DEFAULT '',"
                + COLUMN_AUTHOR_USERNAME + " TEXT DEFAULT '',"
                + COLUMN_STATUS + " TEXT DEFAULT 'approved',"
                + COLUMN_CREATE_TIME + " INTEGER"
                + ")";

        String CREATE_CROWDFUNDINGS_TABLE = "CREATE TABLE " + TABLE_CROWDFUNDINGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT DEFAULT '',"
                + COLUMN_CONTENT_IMAGES + " TEXT DEFAULT '',"
                + COLUMN_ICON_PATH + " TEXT DEFAULT '',"
                + COLUMN_BANNER_PATH + " TEXT DEFAULT '',"
                + COLUMN_AUTHOR_USERNAME + " TEXT DEFAULT '',"
                + COLUMN_STATUS + " TEXT DEFAULT 'approved',"
                + COLUMN_TARGET_AMOUNT + " REAL,"
                + COLUMN_CURRENT_AMOUNT + " REAL DEFAULT 0,"
                + COLUMN_SUPPORTER_COUNT + " INTEGER DEFAULT 0,"
                + COLUMN_CREATE_TIME + " INTEGER"
                + ")";

        String CREATE_VOTES_TABLE = "CREATE TABLE " + TABLE_VOTES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_GAME_NAME + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT DEFAULT '',"
                + COLUMN_CONTENT_IMAGES + " TEXT DEFAULT '',"
                + COLUMN_ICON_PATH + " TEXT DEFAULT '',"
                + COLUMN_BANNER_PATH + " TEXT DEFAULT '',"
                + COLUMN_AUTHOR_USERNAME + " TEXT DEFAULT '',"
                + COLUMN_STATUS + " TEXT DEFAULT 'approved',"
                + COLUMN_VOTE_COUNT + " INTEGER DEFAULT 0,"
                + COLUMN_CREATE_TIME + " INTEGER"
                + ")";

        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_MESSAGE + " TEXT,"
                + COLUMN_IS_READ + " INTEGER DEFAULT 0,"
                + COLUMN_CREATE_TIME + " INTEGER"
                + ")";

        db.execSQL(CREATE_POSTS_TABLE);
        db.execSQL(CREATE_COMMENTS_TABLE);
        db.execSQL(CREATE_GAME_PROMOS_TABLE);
        db.execSQL(CREATE_CROWDFUNDINGS_TABLE);
        db.execSQL(CREATE_VOTES_TABLE);
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_POSTS + " ADD COLUMN " + COLUMN_CATEGORY + " TEXT DEFAULT ''");
        }
        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COMMENTS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_POST_ID + " INTEGER,"
                    + COLUMN_USERNAME + " TEXT,"
                    + COLUMN_CONTENT + " TEXT,"
                    + COLUMN_CREATE_TIME + " INTEGER"
                    + ")");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_CROWDFUNDINGS + " ADD COLUMN " + COLUMN_DESCRIPTION + " TEXT DEFAULT ''");
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + TABLE_POSTS + " ADD COLUMN " + COLUMN_COVER_IMAGE + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_COMMENTS + " ADD COLUMN " + COLUMN_IMAGE_PATH + " TEXT DEFAULT ''");
        }
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE " + TABLE_POSTS + " ADD COLUMN " + COLUMN_CONTENT_IMAGES + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_GAME_PROMOS + " ADD COLUMN " + COLUMN_CONTENT_IMAGES + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_CROWDFUNDINGS + " ADD COLUMN " + COLUMN_CONTENT_IMAGES + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_VOTES + " ADD COLUMN " + COLUMN_DESCRIPTION + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_VOTES + " ADD COLUMN " + COLUMN_CONTENT_IMAGES + " TEXT DEFAULT ''");
        }
        if (oldVersion < 8) {
            db.execSQL("ALTER TABLE " + TABLE_GAME_PROMOS + " ADD COLUMN " + COLUMN_ICON_PATH + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_CROWDFUNDINGS + " ADD COLUMN " + COLUMN_ICON_PATH + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_VOTES + " ADD COLUMN " + COLUMN_ICON_PATH + " TEXT DEFAULT ''");
        }
        if (oldVersion < 9) {
            db.execSQL("ALTER TABLE " + TABLE_CROWDFUNDINGS + " ADD COLUMN " + COLUMN_BANNER_PATH + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_VOTES + " ADD COLUMN " + COLUMN_BANNER_PATH + " TEXT DEFAULT ''");
        }
        if (oldVersion < 10) {
            db.execSQL("ALTER TABLE " + TABLE_GAME_PROMOS + " ADD COLUMN " + COLUMN_BANNER_PATH + " TEXT DEFAULT ''");
        }
        if (oldVersion < 11) {
            db.execSQL("ALTER TABLE " + TABLE_COMMENTS + " ADD COLUMN " + COLUMN_TARGET_TYPE + " TEXT DEFAULT ''");
        }
        if (oldVersion < 12) {
            db.execSQL("ALTER TABLE " + TABLE_POSTS + " ADD COLUMN " + COLUMN_STATUS + " TEXT DEFAULT 'approved'");
            db.execSQL("ALTER TABLE " + TABLE_GAME_PROMOS + " ADD COLUMN " + COLUMN_AUTHOR_USERNAME + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_GAME_PROMOS + " ADD COLUMN " + COLUMN_STATUS + " TEXT DEFAULT 'approved'");
            db.execSQL("ALTER TABLE " + TABLE_CROWDFUNDINGS + " ADD COLUMN " + COLUMN_AUTHOR_USERNAME + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_CROWDFUNDINGS + " ADD COLUMN " + COLUMN_STATUS + " TEXT DEFAULT 'approved'");
            db.execSQL("ALTER TABLE " + TABLE_VOTES + " ADD COLUMN " + COLUMN_AUTHOR_USERNAME + " TEXT DEFAULT ''");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USERNAME + " TEXT,"
                    + COLUMN_MESSAGE + " TEXT,"
                    + COLUMN_IS_READ + " INTEGER DEFAULT 0,"
                    + COLUMN_CREATE_TIME + " INTEGER"
                    + ")");
        }
        if (oldVersion < 13) {
            db.execSQL("ALTER TABLE " + TABLE_VOTES + " ADD COLUMN " + COLUMN_STATUS + " TEXT DEFAULT 'approved'");
        }
    }

    public List<Post> getApprovedPosts() {
        List<Post> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, null, COLUMN_STATUS + " = ?",
                new String[]{"approved"}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToPost(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Post> searchApprovedPostsByTitle(String keyword) {
        List<Post> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, null,
                COLUMN_TITLE + " LIKE ? AND " + COLUMN_STATUS + " = ?",
                new String[]{"%" + keyword + "%", "approved"}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToPost(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Post> getApprovedPostsByCategory(String category) {
        List<Post> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, null,
                COLUMN_CATEGORY + " = ? AND " + COLUMN_STATUS + " = ?",
                new String[]{category, "approved"}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToPost(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    // ====== Post methods ======

    public long insertPost(Post post) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, post.getUsername());
        values.put(COLUMN_TITLE, post.getTitle());
        values.put(COLUMN_CONTENT, post.getContent());
        values.put(COLUMN_CATEGORY, post.getCategory() != null ? post.getCategory() : "");
        values.put(COLUMN_COVER_IMAGE, post.getCoverImage() != null ? post.getCoverImage() : "");
        values.put(COLUMN_CONTENT_IMAGES, post.getContentImages() != null ? post.getContentImages() : "");
        values.put(COLUMN_LIKE_COUNT, post.getLikeCount());
        values.put(COLUMN_COLLECT_COUNT, post.getCollectCount());
        values.put(COLUMN_STATUS, post.getStatus() != null && !post.getStatus().isEmpty() ? post.getStatus() : "pending");
        values.put(COLUMN_CREATE_TIME, post.getCreateTime());
        long id = db.insert(TABLE_POSTS, null, values);
        db.close();
        return id;
    }

    public List<Post> getAllPosts() {
        List<Post> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, null, null, null, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToPost(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Post> getPostsByUsername(String username) {
        List<Post> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, null, COLUMN_USERNAME + " = ?",
                new String[]{username}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToPost(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Post> searchPostsByTitle(String keyword) {
        List<Post> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, null, COLUMN_TITLE + " LIKE ?",
                new String[]{"%" + keyword + "%"}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToPost(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public Post getPostById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Post post = null;
        if (cursor.moveToFirst()) {
            post = cursorToPost(cursor);
        }
        cursor.close();
        db.close();
        return post;
    }

    public List<Post> getPendingPosts() {
        List<Post> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, null, COLUMN_STATUS + " = ?",
                new String[]{"pending"}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToPost(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public void approvePost(int id) {
        updateStatus(TABLE_POSTS, id, "approved");
    }

    public void rejectPost(int id) {
        updateStatus(TABLE_POSTS, id, "rejected");
    }

    private void updateStatus(String table, int id, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        db.update(table, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public boolean updateLikeCount(int id, int count) {
        return updateColumn(TABLE_POSTS, COLUMN_LIKE_COUNT, id, count);
    }

    public boolean updateCollectCount(int id, int count) {
        return updateColumn(TABLE_POSTS, COLUMN_COLLECT_COUNT, id, count);
    }

    public boolean deletePostByTitle(String title) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_POSTS, COLUMN_TITLE + " = ?", new String[]{title});
        db.close();
        return rows > 0;
    }

    public boolean updatePostContent(int id, String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, content);
        int rows = db.update(TABLE_POSTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean updatePostTitle(int id, String title) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        int rows = db.update(TABLE_POSTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean updatePostStatus(int id, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        int rows = db.update(TABLE_POSTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    private Post cursorToPost(Cursor cursor) {
        Post post = new Post();
        post.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        post.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
        post.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
        post.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
        int coverIdx = cursor.getColumnIndex(COLUMN_COVER_IMAGE);
        post.setCoverImage(coverIdx >= 0 ? cursor.getString(coverIdx) : "");
        int contentImgIdx = cursor.getColumnIndex(COLUMN_CONTENT_IMAGES);
        post.setContentImages(contentImgIdx >= 0 ? cursor.getString(contentImgIdx) : "");
        int catIdx = cursor.getColumnIndex(COLUMN_CATEGORY);
        post.setCategory(catIdx >= 0 ? cursor.getString(catIdx) : "");
        int statusIdx = cursor.getColumnIndex(COLUMN_STATUS);
        post.setStatus(statusIdx >= 0 ? cursor.getString(statusIdx) : "approved");
        post.setLikeCount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIKE_COUNT)));
        post.setCollectCount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COLLECT_COUNT)));
        post.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)));
        return post;
    }

    public List<Post> getPostsByCategory(String category) {
        List<Post> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, null, COLUMN_CATEGORY + " = ?",
                new String[]{category}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToPost(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    // ====== GamePromo methods ======

    public long insertGamePromo(GamePromo promo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, promo.getName());
        values.put(COLUMN_DESCRIPTION, promo.getDescription());
        values.put(COLUMN_SCREENSHOT, promo.getScreenshot());
        values.put(COLUMN_CONTENT_IMAGES, promo.getContentImages() != null ? promo.getContentImages() : "");
        values.put(COLUMN_ICON_PATH, promo.getIconPath() != null ? promo.getIconPath() : "");
        values.put(COLUMN_BANNER_PATH, promo.getBannerPath() != null ? promo.getBannerPath() : "");
        values.put(COLUMN_AUTHOR_USERNAME, promo.getAuthorUsername() != null ? promo.getAuthorUsername() : "");
        values.put(COLUMN_STATUS, promo.getStatus() != null && !promo.getStatus().isEmpty() ? promo.getStatus() : "pending");
        values.put(COLUMN_DOWNLOAD_URL, promo.getDownloadUrl());
        values.put(COLUMN_CREATE_TIME, promo.getCreateTime());
        long id = db.insert(TABLE_GAME_PROMOS, null, values);
        db.close();
        return id;
    }

    public List<GamePromo> getAllGamePromos() {
        List<GamePromo> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_GAME_PROMOS, null, null, null, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToGamePromo(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<GamePromo> getApprovedGamePromos() {
        List<GamePromo> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_GAME_PROMOS, null, COLUMN_STATUS + " = ?",
                new String[]{"approved"}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToGamePromo(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<GamePromo> getGamePromosByAuthor(String username) {
        List<GamePromo> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_GAME_PROMOS, null, COLUMN_AUTHOR_USERNAME + " = ?",
                new String[]{username}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToGamePromo(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public GamePromo getGamePromoById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_GAME_PROMOS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        GamePromo promo = null;
        if (cursor.moveToFirst()) {
            promo = cursorToGamePromo(cursor);
        }
        cursor.close();
        db.close();
        return promo;
    }

    public List<GamePromo> getPendingGamePromos() {
        List<GamePromo> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_GAME_PROMOS, null, COLUMN_STATUS + " = ?",
                new String[]{"pending"}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToGamePromo(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public void approveGamePromo(int id) {
        updateStatus(TABLE_GAME_PROMOS, id, "approved");
    }

    public void rejectGamePromo(int id) {
        updateStatus(TABLE_GAME_PROMOS, id, "rejected");
    }

    private GamePromo cursorToGamePromo(Cursor cursor) {
        GamePromo promo = new GamePromo();
        promo.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        promo.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
        promo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
        promo.setScreenshot(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCREENSHOT)));
        int ciIdx = cursor.getColumnIndex(COLUMN_CONTENT_IMAGES);
        promo.setContentImages(ciIdx >= 0 ? cursor.getString(ciIdx) : "");
        int iconIdx = cursor.getColumnIndex(COLUMN_ICON_PATH);
        promo.setIconPath(iconIdx >= 0 ? cursor.getString(iconIdx) : "");
        int bannerIdx = cursor.getColumnIndex(COLUMN_BANNER_PATH);
        promo.setBannerPath(bannerIdx >= 0 ? cursor.getString(bannerIdx) : "");
        int auIdx = cursor.getColumnIndex(COLUMN_AUTHOR_USERNAME);
        promo.setAuthorUsername(auIdx >= 0 ? cursor.getString(auIdx) : "");
        int statusIdx = cursor.getColumnIndex(COLUMN_STATUS);
        promo.setStatus(statusIdx >= 0 ? cursor.getString(statusIdx) : "approved");
        promo.setDownloadUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOWNLOAD_URL)));
        promo.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)));
        return promo;
    }

    // ====== Crowdfunding methods ======

    public long insertCrowdfunding(Crowdfunding cf) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, cf.getName());
        values.put(COLUMN_DESCRIPTION, cf.getDescription() != null ? cf.getDescription() : "");
        values.put(COLUMN_CONTENT_IMAGES, cf.getContentImages() != null ? cf.getContentImages() : "");
        values.put(COLUMN_ICON_PATH, cf.getIconPath() != null ? cf.getIconPath() : "");
        values.put(COLUMN_BANNER_PATH, cf.getBannerPath() != null ? cf.getBannerPath() : "");
        values.put(COLUMN_AUTHOR_USERNAME, cf.getAuthorUsername() != null ? cf.getAuthorUsername() : "");
        values.put(COLUMN_STATUS, cf.getStatus() != null && !cf.getStatus().isEmpty() ? cf.getStatus() : "pending");
        values.put(COLUMN_TARGET_AMOUNT, cf.getTargetAmount());
        values.put(COLUMN_CURRENT_AMOUNT, cf.getCurrentAmount());
        values.put(COLUMN_SUPPORTER_COUNT, cf.getSupporterCount());
        values.put(COLUMN_CREATE_TIME, cf.getCreateTime());
        long id = db.insert(TABLE_CROWDFUNDINGS, null, values);
        db.close();
        return id;
    }

    public List<Crowdfunding> getAllCrowdfundings() {
        List<Crowdfunding> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CROWDFUNDINGS, null, null, null, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToCrowdfunding(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Crowdfunding> getApprovedCrowdfundings() {
        List<Crowdfunding> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CROWDFUNDINGS, null, COLUMN_STATUS + " = ?",
                new String[]{"approved"}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToCrowdfunding(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Crowdfunding> getCrowdfundingsByAuthor(String username) {
        List<Crowdfunding> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CROWDFUNDINGS, null, COLUMN_AUTHOR_USERNAME + " = ?",
                new String[]{username}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToCrowdfunding(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public Crowdfunding getCrowdfundingById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CROWDFUNDINGS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Crowdfunding cf = null;
        if (cursor.moveToFirst()) {
            cf = cursorToCrowdfunding(cursor);
        }
        cursor.close();
        db.close();
        return cf;
    }

    public List<Crowdfunding> getPendingCrowdfundings() {
        List<Crowdfunding> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CROWDFUNDINGS, null, COLUMN_STATUS + " = ?",
                new String[]{"pending"}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToCrowdfunding(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public void approveCrowdfunding(int id) {
        updateStatus(TABLE_CROWDFUNDINGS, id, "approved");
    }

    public void rejectCrowdfunding(int id) {
        updateStatus(TABLE_CROWDFUNDINGS, id, "rejected");
    }

    public boolean supportCrowdfunding(int id, double amount) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_CROWDFUNDINGS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }

        double currentAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_AMOUNT));
        int supporterCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUPPORTER_COUNT));
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CURRENT_AMOUNT, currentAmount + amount);
        values.put(COLUMN_SUPPORTER_COUNT, supporterCount + 1);
        int rows = db.update(TABLE_CROWDFUNDINGS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean supportCrowdfunding(int id) {
        Crowdfunding cf = getCrowdfundingById(id);
        if (cf == null) return false;
        double amount = cf.getTargetAmount() / 10;
        if (amount < 10) amount = 10;
        return supportCrowdfunding(id, amount);
    }

    private Crowdfunding cursorToCrowdfunding(Cursor cursor) {
        Crowdfunding cf = new Crowdfunding();
        cf.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        cf.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
        cf.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
        int ciIdx = cursor.getColumnIndex(COLUMN_CONTENT_IMAGES);
        cf.setContentImages(ciIdx >= 0 ? cursor.getString(ciIdx) : "");
        int iconIdx = cursor.getColumnIndex(COLUMN_ICON_PATH);
        cf.setIconPath(iconIdx >= 0 ? cursor.getString(iconIdx) : "");
        int bannerIdx = cursor.getColumnIndex(COLUMN_BANNER_PATH);
        cf.setBannerPath(bannerIdx >= 0 ? cursor.getString(bannerIdx) : "");
        int auIdx = cursor.getColumnIndex(COLUMN_AUTHOR_USERNAME);
        cf.setAuthorUsername(auIdx >= 0 ? cursor.getString(auIdx) : "");
        int statusIdx = cursor.getColumnIndex(COLUMN_STATUS);
        cf.setStatus(statusIdx >= 0 ? cursor.getString(statusIdx) : "approved");
        cf.setTargetAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TARGET_AMOUNT)));
        cf.setCurrentAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_AMOUNT)));
        cf.setSupporterCount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUPPORTER_COUNT)));
        cf.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)));
        return cf;
    }

    // ====== Vote methods ======

    public long insertVote(Vote vote) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME_NAME, vote.getGameName());
        values.put(COLUMN_DESCRIPTION, vote.getDescription() != null ? vote.getDescription() : "");
        values.put(COLUMN_CONTENT_IMAGES, vote.getContentImages() != null ? vote.getContentImages() : "");
        values.put(COLUMN_ICON_PATH, vote.getIconPath() != null ? vote.getIconPath() : "");
        values.put(COLUMN_BANNER_PATH, vote.getBannerPath() != null ? vote.getBannerPath() : "");
        values.put(COLUMN_AUTHOR_USERNAME, vote.getAuthorUsername() != null ? vote.getAuthorUsername() : "");
        values.put(COLUMN_STATUS, vote.getStatus() != null && !vote.getStatus().isEmpty() ? vote.getStatus() : "pending");
        values.put(COLUMN_VOTE_COUNT, vote.getVoteCount());
        values.put(COLUMN_CREATE_TIME, vote.getCreateTime());
        long id = db.insert(TABLE_VOTES, null, values);
        db.close();
        return id;
    }

    public List<Vote> getAllVotes() {
        List<Vote> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_VOTES, null, null, null, null, null, COLUMN_VOTE_COUNT + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToVote(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean vote(int id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_VOTES, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }

        int voteCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VOTE_COUNT));
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_VOTE_COUNT, voteCount + 1);
        int rows = db.update(TABLE_VOTES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public Vote getVoteById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_VOTES, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Vote vote = null;
        if (cursor.moveToFirst()) {
            vote = cursorToVote(cursor);
        }
        cursor.close();
        db.close();
        return vote;
    }

    private Vote cursorToVote(Cursor cursor) {
        Vote vote = new Vote();
        vote.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        vote.setGameName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAME_NAME)));
        int descIdx = cursor.getColumnIndex(COLUMN_DESCRIPTION);
        vote.setDescription(descIdx >= 0 ? cursor.getString(descIdx) : "");
        int ciIdx = cursor.getColumnIndex(COLUMN_CONTENT_IMAGES);
        vote.setContentImages(ciIdx >= 0 ? cursor.getString(ciIdx) : "");
        int iconIdx = cursor.getColumnIndex(COLUMN_ICON_PATH);
        vote.setIconPath(iconIdx >= 0 ? cursor.getString(iconIdx) : "");
        int bannerIdx = cursor.getColumnIndex(COLUMN_BANNER_PATH);
        vote.setBannerPath(bannerIdx >= 0 ? cursor.getString(bannerIdx) : "");
        int auIdx = cursor.getColumnIndex(COLUMN_AUTHOR_USERNAME);
        vote.setAuthorUsername(auIdx >= 0 ? cursor.getString(auIdx) : "");
        int statusIdx = cursor.getColumnIndex(COLUMN_STATUS);
        vote.setStatus(statusIdx >= 0 ? cursor.getString(statusIdx) : "approved");
        vote.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VOTE_COUNT)));
        vote.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)));
        return vote;
    }

    public List<Vote> getApprovedVotes() {
        List<Vote> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_VOTES, null, COLUMN_STATUS + " = ?",
                new String[]{"approved"}, null, null, COLUMN_VOTE_COUNT + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToVote(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Vote> getPendingVotes() {
        List<Vote> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_VOTES, null, COLUMN_STATUS + " = ?",
                new String[]{"pending"}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToVote(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public void approveVote(int id) {
        updateStatus(TABLE_VOTES, id, "approved");
    }

    public void rejectVote(int id) {
        updateStatus(TABLE_VOTES, id, "rejected");
    }

    public List<Vote> getVotesByAuthor(String username) {
        List<Vote> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_VOTES, null, COLUMN_AUTHOR_USERNAME + " = ?",
                new String[]{username}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToVote(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    // ====== Notification methods ======

    public long insertNotification(Notification notification) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, notification.getUsername());
        values.put(COLUMN_MESSAGE, notification.getMessage());
        values.put(COLUMN_IS_READ, notification.isRead() ? 1 : 0);
        values.put(COLUMN_CREATE_TIME, notification.getCreateTime());
        long id = db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();
        return id;
    }

    public List<Notification> getNotificationsByUser(String username) {
        List<Notification> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTIFICATIONS, null, COLUMN_USERNAME + " = ?",
                new String[]{username}, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            Notification n = new Notification();
            n.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            n.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            n.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE)));
            n.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_READ)) != 0);
            n.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)));
            list.add(n);
        }
        cursor.close();
        db.close();
        return list;
    }

    public int getUnreadNotificationCount(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS
                + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_IS_READ + " = 0",
                new String[]{username});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public void markNotificationRead(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);
        db.update(TABLE_NOTIFICATIONS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void markAllNotificationsRead(String username) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);
        db.update(TABLE_NOTIFICATIONS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
    }

    private void notifyUser(String username, String message) {
        Notification n = new Notification(username, message);
        insertNotification(n);
    }

    public void notifyPostApproved(String username, String title) {
        notifyUser(username, "你发布的帖子《" + title + "》已通过管理员审核，现在可以在论坛中查看了！");
    }

    public void notifyPostRejected(String username, String title) {
        notifyUser(username, "你发布的帖子《" + title + "》未通过管理员审核。");
    }

    public void notifyPromoApproved(String username, String name) {
        notifyUser(username, "你的宣发《" + name + "》已通过管理员审核！");
    }

    public void notifyPromoRejected(String username, String name) {
        notifyUser(username, "你的宣发《" + name + "》未通过管理员审核。");
    }

    public void notifyCrowdfundingApproved(String username, String name) {
        notifyUser(username, "你的众筹项目《" + name + "》已通过管理员审核！");
    }

    public void notifyCrowdfundingRejected(String username, String name) {
        notifyUser(username, "你的众筹项目《" + name + "》未通过管理员审核。");
    }

    public void notifyVoteApproved(String username, String gameName) {
        notifyUser(username, "你的游戏《" + gameName + "》已通过管理员审核，现在可以在投票榜单中展示！");
    }

    public void notifyVoteRejected(String username, String gameName) {
        notifyUser(username, "你的游戏《" + gameName + "》未通过管理员审核。");
    }

    // ====== Comment methods ======

    public long insertComment(Comment comment) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_ID, comment.getPostId());
        values.put(COLUMN_USERNAME, comment.getUsername());
        values.put(COLUMN_CONTENT, comment.getContent());
        values.put(COLUMN_IMAGE_PATH, comment.getImagePath() != null ? comment.getImagePath() : "");
        values.put(COLUMN_TARGET_TYPE, comment.getTargetType() != null ? comment.getTargetType() : "");
        values.put(COLUMN_CREATE_TIME, comment.getCreateTime());
        long id = db.insert(TABLE_COMMENTS, null, values);
        db.close();
        return id;
    }

    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_COMMENTS, null, COLUMN_POST_ID + " = ?",
                new String[]{String.valueOf(postId)}, null, null, COLUMN_CREATE_TIME + " ASC");
        while (cursor.moveToNext()) {
            Comment comment = new Comment();
            comment.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            comment.setPostId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POST_ID)));
            comment.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            comment.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
            int imgIdx = cursor.getColumnIndex(COLUMN_IMAGE_PATH);
            comment.setImagePath(imgIdx >= 0 ? cursor.getString(imgIdx) : "");
            comment.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)));
            list.add(comment);
        }
        cursor.close();
        db.close();
        return list;
    }

    public int getCommentCount(int postId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_COMMENTS + " WHERE " + COLUMN_POST_ID + " = ?",
                new String[]{String.valueOf(postId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public List<Comment> getCommentsByTarget(String targetType, int targetId) {
        List<Comment> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_COMMENTS, null,
                COLUMN_POST_ID + " = ? AND " + COLUMN_TARGET_TYPE + " = ?",
                new String[]{String.valueOf(targetId), targetType},
                null, null, COLUMN_CREATE_TIME + " ASC");
        while (cursor.moveToNext()) {
            Comment comment = new Comment();
            comment.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            comment.setPostId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POST_ID)));
            comment.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            comment.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
            int imgIdx = cursor.getColumnIndex(COLUMN_IMAGE_PATH);
            comment.setImagePath(imgIdx >= 0 ? cursor.getString(imgIdx) : "");
            int ttIdx = cursor.getColumnIndex(COLUMN_TARGET_TYPE);
            comment.setTargetType(ttIdx >= 0 ? cursor.getString(ttIdx) : "");
            comment.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)));
            list.add(comment);
        }
        cursor.close();
        db.close();
        return list;
    }

    // ====== Admin / Delete methods ======

    public boolean deletePost(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_COMMENTS, COLUMN_POST_ID + " = ?", new String[]{String.valueOf(id)});
        int rows = db.delete(TABLE_POSTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean deleteComment(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_COMMENTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public java.util.List<Comment> getAllComments() {
        java.util.List<Comment> list = new java.util.ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_COMMENTS, null, null, null, null, null, COLUMN_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            Comment comment = new Comment();
            comment.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            comment.setPostId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POST_ID)));
            comment.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            comment.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
            int imgIdx = cursor.getColumnIndex(COLUMN_IMAGE_PATH);
            comment.setImagePath(imgIdx >= 0 ? cursor.getString(imgIdx) : "");
            comment.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATE_TIME)));
            list.add(comment);
        }
        cursor.close();
        db.close();
        return list;
    }

    public Post getPostByIdSimple(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, new String[]{COLUMN_TITLE, COLUMN_USERNAME, COLUMN_CONTENT},
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        Post post = null;
        if (cursor.moveToFirst()) {
            post = new Post();
            post.setId(id);
            post.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            post.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            post.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
        }
        cursor.close();
        db.close();
        return post;
    }

    public boolean deleteGamePromo(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_GAME_PROMOS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean deleteCrowdfunding(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_CROWDFUNDINGS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean deleteVote(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_VOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    private boolean updateColumn(String table, String column, int id, int value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(column, value);
        int rows = db.update(table, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }
}
