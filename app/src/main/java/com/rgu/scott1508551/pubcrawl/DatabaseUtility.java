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

    public void putCrawl(String crawlName, String json, String pontos){
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CRAWLNAME, crawlName);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_JSON, json);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_JSON, pontos);

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

    public ArrayList getCrawl(String name){

        ArrayList json = null;

        String[] projection = {
                FeedReaderContract.FeedEntry.COLUMN_NAME_JSON,
                FeedReaderContract.FeedEntry.COLUMN_NAME_PONTOS
        };

        // Filter results WHERE "crawlname" = 'name'
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_CRAWLNAME + " = ?";
        String[] selectionArgs = { name };

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            json.add(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_JSON)));
            json.add(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_PONTOS)));
        }

        return json;
    }
}
