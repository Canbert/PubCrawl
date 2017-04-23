package com.rgu.scott1508551.pubcrawl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class SavedActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ListView.OnItemClickListener{

    private ListView savedList;
    private ArrayAdapter adapter;

    private SearchView searchView;

    private DatabaseUtility db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        savedList = (ListView)this.findViewById(R.id.listViewSaved);
        searchView = (SearchView)this.findViewById(R.id.searchView);


//        this.deleteDatabase(SaveCrawlDatabaseHelper.DB_NAME);
        db = new DatabaseUtility(this);

        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,db.getCrawls());
        savedList.setAdapter(adapter);

        savedList.setOnItemClickListener(this);

        searchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        SavedActivity.this.adapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String yourData = (String) db.getCrawls().get(position);
//        ArrayList array = db.getCrawl(yourData);
        Log.d("ITEM", String.valueOf(db.getCrawl(yourData)));
    }
}
