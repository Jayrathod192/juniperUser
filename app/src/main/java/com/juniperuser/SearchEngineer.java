package com.juniperuser;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.juniperuser.Model.Engineer;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchEngineer extends AppCompatActivity {

    private RecyclerView recyclerView;
    Context context;
    private List<engineer> engineers;
    Button searchBtn;
    private int[] images= {R.drawable.man,R.drawable.enga,R.drawable.engb,R.drawable.girl,R.drawable.profile_dp};

    private  RecyclerAdapter recyclerAdapter;

    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Before setContentView
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_search_engineer);

        recyclerView= findViewById(R.id.recylerView);

        searchBtn=(Button)findViewById(R.id.search_btn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SearchEngineer.this,"Search Engineer",Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayoutManager layoutManager= new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        //layoutManager=new GridLayoutManager(this,2);
        recyclerView.setHasFixedSize(true);
       // recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(engineers);
       // recyclerView.setAdapter(recyclerAdapter);

        initializeData();
        initializeAdapter();

    }

    private void initializeAdapter() {
        RecyclerAdapter adapter=new RecyclerAdapter(engineers);
        recyclerView.setAdapter(adapter);
    }

    private void initializeData() {
        engineers = new ArrayList<>();
        engineers.add(new engineer("Jhon M","Software Engineer","BE-IT",R.drawable.man));
        engineers.add(new engineer("Max R","Harware Engineer","Btech-CE",R.drawable.enga));
        engineers.add(new engineer("Eddy N","Network Engineer","Bsc-Comp",R.drawable.engb));

    }
    class engineer
    {
        String name;
        String Type;
        String qualification;
        int imgId;

        engineer(String name,String Type,String qualification,int imgId)
        {
            this.name=name;
            this.qualification=qualification;
            this.Type=Type;
            this.imgId=imgId;
        }
    }
}
