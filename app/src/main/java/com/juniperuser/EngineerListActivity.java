package com.juniperuser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.juniperuser.Common.Common;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;


public class EngineerListActivity extends AppCompatActivity {

    RecyclerView mRecyclerview;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer_list);

        //actiobar
        ActionBar mActionbar = getSupportActionBar();
        //set title
        // getActionBar().setTitle("Engineer List");

        //back button
        mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setDisplayShowHomeEnabled(true);

        mRecyclerview = findViewById(R.id.recylerView);
        mRecyclerview.setHasFixedSize(true);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference(Common.user_engineer_tbl);
    }

    //search data
    private void firebaseSearch(String searchText) {
        Query firebaseSearchQuery = mRef.orderByChild("type").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<ModelFirebase, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ModelFirebase, ViewHolder>(
                        ModelFirebase.class,
                        R.layout.raw,
                        ViewHolder.class,
                        firebaseSearchQuery
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, ModelFirebase model, int position) {
                        viewHolder.setDetails(getApplicationContext(), model.getID(), model.getName(), model.getEmail(), model.getImage(), model.getPhone(),model.getType(),model.getDistance());
                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);

                        viewHolder.setonCLickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                //views
                                TextView mengId = view.findViewById(R.id.IDeng);
                                TextView mNameEng = view.findViewById(R.id.nameEng);
                                TextView mPhoneEng = view.findViewById(R.id.phoneEng);
                                TextView mQualEng = view.findViewById(R.id.qualEng);
                                TextView mTypeEng=view.findViewById(R.id.typeEng);
                                TextView mDistanceEng=view.findViewById(R.id.distanceEng);
                                ImageView mImage = view.findViewById(R.id.mainIcon);

                                //get data from views
                                String mID = mengId.getText().toString();
                                String mName = mNameEng.getText().toString();
                                String mPhone = mPhoneEng.getText().toString();
                                String mQual = mQualEng.getText().toString();
                                String mType = mTypeEng.getText().toString();
                                String mDistance=mDistanceEng.getText().toString();
                                Drawable mDrawable = mImage.getDrawable();
                                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();

                                //pass this data to new activity
                                Intent intent = new Intent(view.getContext(), EngineeraFirebaseDetails.class);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] bytes = stream.toByteArray();
                                intent.putExtra("id",mID);//put engID
                                intent.putExtra("image", bytes);//put bitmap image as array of bytes
                                intent.putExtra("name", mName);//put name
                                intent.putExtra("phone", mPhone);//put
                                intent.putExtra("qual", mQual);
                                intent.putExtra("type", mType);
                                intent.putExtra("distance", mDistance);

                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                //TODO do your own IMplementation on long item click
                            }
                        });
                        return viewHolder;
                    }

                };
        //set Adapter to recyclerVIew
        mRecyclerview.setAdapter(firebaseRecyclerAdapter);
    }

    //load data into recycler view onStart
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ModelFirebase, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ModelFirebase, ViewHolder>(
                        ModelFirebase.class,
                        R.layout.raw,
                        ViewHolder.class,
                        mRef
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, ModelFirebase model, int position) {
                        viewHolder.setDetails(getApplicationContext(),model.getID(), model.getName(), model.getEmail(), model.getImage(), model.getPhone(),model.getType(),model.getDistance());
                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);

                        viewHolder.setonCLickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                //views
                                TextView mengId = view.findViewById(R.id.IDeng);
                                TextView mNameEng = view.findViewById(R.id.nameEng);
                                TextView mPhoneEng = view.findViewById(R.id.phoneEng);
                                TextView mQualEng = view.findViewById(R.id.qualEng);
                                TextView mTypeEng=view.findViewById(R.id.typeEng);
                                TextView mDistanceEng=view.findViewById(R.id.distanceEng);
                                ImageView mImage = view.findViewById(R.id.mainIcon);

                                //get data from views
                                String mID = mengId.getText().toString();
                                String mName = mNameEng.getText().toString();
                                String mPhone = mPhoneEng.getText().toString();
                                String mQual = mQualEng.getText().toString();
                                String mType = mTypeEng.getText().toString();
                                String mDistance=mDistanceEng.getText().toString();
                                Drawable mDrawable = mImage.getDrawable();
                                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();

                                //pass this data to new activity
                                Intent intent = new Intent(view.getContext(), EngineeraFirebaseDetails.class);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] bytes = stream.toByteArray();
                                intent.putExtra("id",mID);//put engID
                                intent.putExtra("image", bytes);//put bitmap image as array of bytes
                                intent.putExtra("name", mName);//put name
                                intent.putExtra("phone", mPhone);//put
                                intent.putExtra("qual", mQual);
                                intent.putExtra("type", mType);
                                intent.putExtra("distance", mDistance);

                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                //TODO do your own IMplementation on long item click
                            }
                        });
                        return viewHolder;
                    }

                };

        //set Adapter to recyclerVIew
        mRecyclerview.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //TODO
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //onBAckPRessed

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(EngineerListActivity.this, Home.class);
        startActivity(intent);
        finish();
        //onBackPressed();
        return true;
    }

}
