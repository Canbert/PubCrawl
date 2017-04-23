package com.rgu.scott1508551.pubcrawl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

public class SavedActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private ListView savedList;
    private ArrayAdapter adapter;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        savedList = (ListView)this.findViewById(R.id.listViewSaved);
        searchView = (SearchView)this.findViewById(R.id.searchView);

        ArrayList testArray = new ArrayList();
        testArray.add("Hwell");
        testArray.add("sttff");

        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,testArray);
        savedList.setAdapter(adapter);

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
}
