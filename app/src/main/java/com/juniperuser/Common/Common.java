package com.juniperuser.Common;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.juniperuser.Home;
import com.juniperuser.Model.DataMessage;
import com.juniperuser.Model.FCMResponse;
import com.juniperuser.Model.Notification;
import com.juniperuser.Model.Sender;
import com.juniperuser.Model.Token;
import com.juniperuser.Model.User;
import com.juniperuser.Remote.FCMClient;
import com.juniperuser.Remote.IFCMService;
import com.juniperuser.Remote.IGoogleApi;
import com.juniperuser.Remote.RetrofitClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Common {
    public static String address;
    public static User currentUser;

    TextView txtEngName, txtEngPhone, txtRate;
    public static String EngineerID="";
    public static Location mLastLocation = null;
    public static final String driver_tbl = "EngineerLocation"; //engineersLocation_tbl="EngineersLocation"
    public static final String user_engineer_tbl = "EngineersInformation"; //engineer_tbl="EngineersInformation";
    public static final String user_rider_tbl = "UsersInformation";//user_tbl="UsersInformation"
    public static final String pickup_request_tbl = "PickupRequest";//Tokens
    public static final String token_tbl = "Token";//Tokens

    public static final String fcmURL = "https://fcm.googleapis.com/";

    public static IFCMService getFCMService() {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

   /*public static void sendRequestToEngineer(String EngineerID, final IFCMService mservice, final Context context, final Location currentLocation) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.pickup_request_tbl);

        tokens.orderByKey().equalTo(EngineerID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Token token = postSnapshot.getValue(Token.class);

                            String userToken= FirebaseInstanceId.getInstance().getToken();

                           Map<String,String> content=new HashMap<>();
                            content.put("customer",userToken);
                            content.put("lat",String.valueOf(currentLocation.getLatitude()));
                            content.put("lng",String.valueOf(currentLocation.getLongitude()));

                           DataMessage dataMessage=new DataMessage(token.getToken(),content);
                            String json_lat_lng = new Gson().toJson(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            String info = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            //sender content = new Sender(nf, token.getToken());

                            mservice.sendMessage(dataMessage)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                                            if (response.body().success == 1)
                                                Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show();

                                            else
                                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();

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
*/
}
