package com.rgu.scott1508551.pubcrawl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

public class SavedActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ListView.OnItemClickListener{

    private ListView savedList;
    private ArrayAdapter adapter;

    private SearchView searchView;

    private DatabaseUtility db;

    private Bundle data;

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
        Intent in;

        String yourData = (String) db.getCrawls().get(position);
        Log.d("ITEM", String.valueOf(db.getCrawl(yourData)));
        data = db.getCrawl(yourData);

        in = new Intent(SavedActivity.this, CrawlActivity.class);
        in.putExtras(data);
        Log.d("Data Bundle", data.toString());
        startActivity(in);

    }
}
