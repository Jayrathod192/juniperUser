package com.juniperuser;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.juniperuser.Common.Common;
import com.juniperuser.Model.FCMResponse;
import com.juniperuser.Model.Notification;
import com.juniperuser.Model.Sender;
import com.juniperuser.Model.Token;
import com.juniperuser.Model.User;
import com.juniperuser.Remote.IFCMService;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;

import javax.net.ssl.SSLEngineResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.juniperuser.Common.Common.EngineerID;
import static com.juniperuser.Common.Common.mLastLocation;

public class EngineeraFirebaseDetails extends AppCompatActivity {

    TextView FnameEng, FtypeEng, FqualEng, FengID,FphoneEng,FdistanceEng;
    ImageView FmainIcon;
    Button mCallbtn, mAppCallBtn,mPayBtn;
    IFCMService mService;
    int finalAmount=210;
    String EngineerID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineera_firebase_details);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("Engineer Info");
        //back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mService = Common.getFCMService();

        mCallbtn = (Button) findViewById(R.id.btnCall);
        mPayBtn=(Button)findViewById(R.id.btnPay);

        mAppCallBtn = (Button) findViewById(R.id.btnAppCall);
        FnameEng = (TextView) findViewById(R.id.FnameEng);
        FphoneEng = (TextView) findViewById(R.id.FphoneEng);
        FdistanceEng = (TextView) findViewById(R.id.FdistanceEng);
        FtypeEng = (TextView) findViewById(R.id.FtypeEng);
        FqualEng = (TextView) findViewById(R.id.FqualEng);
        FengID = (TextView) findViewById(R.id.FengID);
        FmainIcon = (ImageView) findViewById(R.id.FmainIcon);

        //get data from intent
        byte[] bytes = getIntent().getByteArrayExtra("image");
        final String Id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        final String phone = getIntent().getStringExtra("phone");
        String type=getIntent().getStringExtra("type");
        String distance=getIntent().getStringExtra("distance");
        String qual = getIntent().getStringExtra("qual");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        //set data to views
        FmainIcon.setImageBitmap(bmp);
        FengID.setText(Id);
        FnameEng.setText(name);
        FphoneEng.setText(phone);
        FtypeEng.setText(type);
        FdistanceEng.setText(distance);
        FqualEng.setText(qual);

        mCallbtn.setText("Call Engineer : " + phone);

        mPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EngineeraFirebaseDetails.this,Paypal.class);
                intent.putExtra("finalAmount",finalAmount);
                startActivity(intent);
                finish();
            }
        });

        mCallbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone));
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(intent);
            }
        });

        mAppCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sendRequestToEngineer(EngineerID);
            }
        });
    }


    private void sendRequestToEngineer(String engineerID) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.pickup_request_tbl);

        tokens.orderByKey().equalTo(engineerID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Token token = postSnapshot.getValue(Token.class);

                            User user = new User();
                            String json_lat_lng = new Gson().toJson(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                            String customerToken = FirebaseInstanceId.getInstance().getToken();
                            String info = Common.address + "-- Wants Service";
                            Notification nf = new Notification(customerToken, json_lat_lng);
                            Sender content = new Sender(token.getToken(),nf);

                            mService.sendMessage(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                            try {
                                                if (response.body().success == 1)
                                                    Toast.makeText(EngineeraFirebaseDetails.this, "Request sent", Toast.LENGTH_SHORT).show();

                                                else
                                                    Toast.makeText(EngineeraFirebaseDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                                            } catch (NullPointerException ignored) {
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }

                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    //onBAckPRessed

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
