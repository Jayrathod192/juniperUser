package com.juniperuser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.juniperuser.Common.Common;
import com.juniperuser.Model.Engineer;
import com.juniperuser.Model.User;
import com.juniperuser.Remote.IFCMService;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallEngineer extends AppCompatActivity {

    CircleImageView engineer_image;
    TextView txtEngName, txtEngPhone, txtRate;
    Button btn_call_engineer, btn_call_engineer_phone;
    String EngineerID;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    Location mLastLocation;
    IFCMService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_engineer);


        mService = Common.getFCMService();
        engineer_image = (CircleImageView) findViewById(R.id.engineerimage);
        txtEngName=(TextView)findViewById(R.id.txt_Name);
        txtEngPhone = (TextView) findViewById(R.id.txt_Phone);
        //txtEngPhone.setText("8511802156");
        txtRate = (TextView) findViewById(R.id.txtrate);
        btn_call_engineer = (Button) findViewById(R.id.btn_call_engineer);
        btn_call_engineer_phone = (Button) findViewById(R.id.btn_call_engineer_phone);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference(Common.user_engineer_tbl);

        btn_call_engineer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (EngineerID != null && !EngineerID.isEmpty())
                   // sendRequestToEngineer(EngineerID);
            }

        });
        btn_call_engineer_phone.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + txtEngPhone.getText().toString()));
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(intent);
            }
        });

        if (getIntent() != null) {
            EngineerID = getIntent().getStringExtra("EngineerID");

            String phone=getIntent().getStringExtra("phone");
            double lat = getIntent().getDoubleExtra("lat", -1.0);
            double lng = getIntent().getDoubleExtra("lng", -1.0);

            Toast.makeText(CallEngineer.this, "" + lat + " " + lng, Toast.LENGTH_SHORT).show();

            mLastLocation = new Location("");
            mLastLocation.setLatitude(lat);
            mLastLocation.setLongitude(lng);

            //  txtRate.setText("4.5");
            txtEngPhone.setText(phone);
            txtEngName.setText(EngineerID);
            //txtEngPhone.setText("8141451021");
        }

    }

    private void loadEngineerInfo(final String EngineerID) {
        //btn_call_engineer.setText(EngineerID);
        FirebaseDatabase.getInstance().getReference(Common.user_engineer_tbl)
                .child(EngineerID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Engineer engineer = null;
                        try {
                            engineer = dataSnapshot.getValue(Engineer.class);

                            txtEngName.setText(engineer.getName());
                            txtEngPhone.setText(engineer.getPhone());
                            txtRate.setText(engineer.getRate());
                        } catch (NullPointerException ignored) {
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.getMessage();
                    }
                });
    }

}
