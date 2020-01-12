package com.juniperuser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.TextView;

public class EngineerDeatilsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer_deatils);

        ActionBar acitonBar=getSupportActionBar();
        TextView textView=findViewById(R.id.textview);

        Intent intent=getIntent();
        String mActionBarTitle=intent.getStringExtra("actionBarTitle");
        String mContent=intent.getStringExtra("Engineer");

        acitonBar.setTitle(mActionBarTitle);
        textView.setText(mContent);

    }
}
