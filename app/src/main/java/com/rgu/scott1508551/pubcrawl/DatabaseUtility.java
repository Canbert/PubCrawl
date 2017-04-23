package com.rgu.scott1508551.pubcrawl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DatabaseUtility {
    private SaveCrawlDatabaseHelper saveCrawlDatabaseHelper;
    private SQLiteDatabase db;

    public DatabaseUtility(Context con) {
        this.saveCrawlDatabaseHelper = new SaveCrawlDatabaseHelper(con);
        this.db = saveCrawlDatabaseHelper.getWritableDatabase();
    }

    public void putCrawl(String crawlName, String json){
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CRAWLNAME, crawlName);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_JSON, json);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
    }

    public ArrayList getCrawls(){
        String[] projection = {
                FeedReaderContract.FeedEntry.COLUMN_NAME_CRAWLNAME
        };

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        ArrayList crawls = new ArrayList<>();

        while (cursor.moveToNext()){
            String crawl = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_CRAWLNAME));
            crawls.add(crawl);
        }
        cursor.close();

        return crawls;
    }

    public void dropTable(){
        db.execSQL("DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME);
    }
}
