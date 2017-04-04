package com.rgu.scott1508551.pubcrawl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    Button generateButton, savedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        generateButton = (Button)this.findViewById(R.id.btnGenerate);
        generateButton.setOnClickListener(this);

        savedButton = (Button)this.findViewById(R.id.btnSaved);
        savedButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Intent in;

        switch (v.getId()){
            case R.id.btnGenerate:
                in = new Intent(this,GenerateActivity.class);
                startActivity(in);
                break;
            case R.id.btnSaved:
                in = new Intent(this,SavedActivity.class);
                startActivity(in);
                break;
        }
    }
}
