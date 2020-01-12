package com.juniperuser;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.support.v7.widget.SearchView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CustomSearch extends AppCompatActivity {

    ListView listView;
    ListViewAdapter adapter;
    String[] nameEng;
    String[] typeEng;
    String[] qualitfication;
    int[] icon;
    ArrayList<ModelData> arrayList= new ArrayList<ModelData>();


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

        setContentView(R.layout.activity_custom_search);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Engineer Available");

        nameEng=new String[]{"Jhon M","Max R","Eddy N"};
        typeEng=new String[]{"Software Engineer","Hardware Engineer","Network Engineer"};
        qualitfication=new String[]{"BE-IT","Btech-CE","Bsc-Comp."};
        icon=new int[]{R.drawable.man,R.drawable.engb,R.drawable.enga};

        listView=findViewById(R.id.listView);

        for(int i=0;i<nameEng.length;i++) {
            ModelData modelData = new ModelData(nameEng[i], typeEng[i], qualitfication[i], icon[i]);
            //bind all string in an array
            arrayList.add(modelData);
        }

        //bind adapter to listview
        adapter= new ListViewAdapter(this,arrayList);

        //pass results to listVIewAdapter
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem myActionMenuItem= menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView)myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(TextUtils.isEmpty(s)){
                    adapter.filter("");
                    listView.clearTextFilter();
                }
                else {
                    adapter.filter(s);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.action_settings)
        {
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }
}
