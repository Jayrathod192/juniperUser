package com.juniperuser;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.juniperuser.Common.Common;
import com.juniperuser.Helper.CustomeInfoWindow;
import com.juniperuser.Model.Engineer;
import com.juniperuser.Model.FCMResponse;
import com.juniperuser.Model.Notification;
import com.juniperuser.Model.Sender;
import com.juniperuser.Model.Token;
import com.juniperuser.Model.User;
import com.juniperuser.Remote.IFCMService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import static com.juniperuser.Common.Common.sendRequestToEngineer;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnInfoWindowClickListener {

    SupportMapFragment mapFragment;

    //update inforamtion
    CircleImageView imageAvatar;
    TextView txtUserName,txtStars;

    //firebase storage
    FirebaseStorage storage;
    StorageReference storageReference;

    //Location
    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 2598;
    private static final int PLAY_SERVICE_RES_REQUEST = 1998;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference ref;

    Marker mUserMarker;

    //bottomSheet
    ImageView imgExpandable;
    BottomSheetUserFragment mBottomSheet;
    Button btnPickupRequest;

    boolean isEngineerFound = false;
    String engineerId = "";
    int radius = 1;//1km
    int distance = 1;
    private static final int LIMIT = 3;

    IFCMService mservice;
    DatabaseReference engineerAvailable;
    GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mservice = Common.getFCMService();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeaderView = navigationView.getHeaderView(0);
        txtUserName=navigationHeaderView.findViewById(R.id.txtUserName);
        txtUserName.setText(String.format("%s",Common.currentUser.getName()));
        txtStars=navigationHeaderView.findViewById(R.id.txtStars);
        txtStars.setText(String.format("%s",Common.currentUser.getRates()));
        imageAvatar=navigationHeaderView.findViewById(R.id.imageAvatar);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Init view
        imgExpandable = (ImageView) findViewById(R.id.imgExpandable);
        mBottomSheet = (BottomSheetUserFragment) BottomSheetUserFragment.newInstance("User Bottom Sheet");
        imgExpandable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheet.show(getSupportFragmentManager(), mBottomSheet.getTag());
            }
        });

        //init storage
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        btnPickupRequest = (Button) findViewById(R.id.btnPickupRequest);

        btnPickupRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isEngineerFound)
                {
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            requestPickupHere(account.getId());
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {

                        }
                    });
                }
                else
                    sendRequestToEngineer(engineerId);
            }
        });
        setUpLocation();
        updateFirebaseToken();


    }

    private void updateFirebaseToken() {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference tokens = db.getReference(Common.token_tbl);

                Token token = new Token(FirebaseInstanceId.getInstance().getToken());
                tokens.child(account.getId())
                        .setValue(token);
            }

            @Override
            public void onError(AccountKitError accountKitError) {

            }
        });
    }
    private void sendRequestToEngineer(String engineerId) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.pickup_request_tbl);

        tokens.orderByKey().equalTo(engineerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Token token = postSnapshot.getValue(Token.class);

                            User user = new User();
                            String json_lat_lng = new Gson().toJson(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                            //String customerToken = FirebaseInstanceId.getInstance().getToken();
                            Notification data = new Notification("Juniper", json_lat_lng);
                            Sender content = new Sender(token.getToken(),data);

                            mservice.sendMessage(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                            try {
                                                if (response.body().success == 1)
                                                    Toast.makeText(Home.this, "Request sent", Toast.LENGTH_SHORT).show();

                                                else
                                                    Toast.makeText(Home.this, "Failed", Toast.LENGTH_SHORT).show();
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
    private void requestPickupHere(String uid) {
        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference(Common.pickup_request_tbl);
        GeoFire mGeoFire = new GeoFire(dbRequest);
        mGeoFire.setLocation(uid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (mUserMarker.isVisible()) {
                    mUserMarker.remove();

                    //add new marker
                    mUserMarker = mMap.addMarker(new MarkerOptions()
                            .title("Pickup here")
                            .snippet("")
                            .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    mUserMarker.showInfoWindow();

                    btnPickupRequest.setText("Getting your Engineer...");

                    findEngineer();
                }
            }
        });

    }

    private void findEngineer() {
        final DatabaseReference engineers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
        GeoFire gfEngineers = new GeoFire(engineers);

        GeoQuery geoQuery = gfEngineers.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude())
                , radius);

        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //if found
                if (!isEngineerFound) {
                    isEngineerFound = true;
                    engineerId = key;
                    btnPickupRequest.setText("CALL ENGINEER");
                    Toast.makeText(Home.this, "" + key, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //if still not found engineer,increase distance
                if (!isEngineerFound) {
                    radius++;
                    findEngineer();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
                break;
        }
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request runtime permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            //presence system
            engineerAvailable = FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
            engineerAvailable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    loadAllAvailableEngineer();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
            LatLng myCoordinate = new LatLng(latitude, longitude);
            String cityname = getCityName(myCoordinate);

            //add marker
            if (mUserMarker != null)
                mUserMarker.remove();
            mUserMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("UserLocation")
                    .snippet("Address: " + Common.address));
            //move camera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

            loadAllAvailableEngineer();

            Log.d("Juniper", String.format("Your location was changed : @f / @f", latitude, longitude));
        } else
            Log.d("ERROR", "Cannot get your location");

    }

    private String getCityName(LatLng myCoordinate) {
        String mycity = "";
        Geocoder geocoder = new Geocoder(Home.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(myCoordinate.latitude, myCoordinate.longitude, 1);
            Common.address = addresses.get(0).getAddressLine(0);
            mycity = addresses.get(0).getLocality();
            Log.v("mylog", "complete address: " + addresses.toString());
            Log.v("mylog", "Address: " + Common.address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mycity;
    }

    private void loadAllAvailableEngineer() {

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                .title("You"));


        //load all available eng.
        DatabaseReference engineerLocation = FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
        GeoFire gf = new GeoFire(engineerLocation);

        GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), distance);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                //use key to get email from table Engineer
                FirebaseDatabase.getInstance().getReference(Common.user_engineer_tbl)
                        .child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Engineer engineer = null;
                                //add engineer to map
                                try {
                                    engineer = dataSnapshot.getValue(Engineer.class);
                                    mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(location.latitude, location.longitude))
                                            .flat(true)
                                            .title(engineer.getPhone())
                                            .snippet("Name : " + engineer.getName()+"\nEngineerID : " + dataSnapshot.getKey()+"\nType : " + engineer.getType())
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.engineer)));

                                } catch (NullPointerException ignored) {

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance <= LIMIT) {
                    distance++;
                    loadAllAvailableEngineer();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
            else {
                Toast.makeText(this, "this device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_update_information) {
            showUpdateInformationDialog();
            // Handle the camera action
        } else if (id == R.id.nav_payment) {
            PaymentRedirect();

        } else if (id == R.id.search_engineer) {
            searchEngineer();

        } else if (id == R.id.custom_search) {
            customSearch();

        } else if (id == R.id.nav_sign_out_user) {
            signOutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showUpdateInformationDialog() {
    }

    private void searchEngineer() {
        Intent intent=new Intent(Home.this,EngineerListActivity.class);
        startActivity(intent);
        finish();
    }
    private void customSearch() {
        Intent intent=new Intent(Home.this,CustomSearch.class);
        startActivity(intent);
        finish();
    }

    private void PaymentRedirect() {
        Intent intent=new Intent(Home.this,Paypal.class);
        startActivity(intent);
        finish();
    }

    private void signOutUser() {
        AccountKit.logOut();
        Intent intent = new Intent(Home.this, UserMainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new CustomeInfoWindow(this));
        mMap.setOnInfoWindowClickListener(this);

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (!marker.getTitle().equals("You")) {
            Intent intent = new Intent(Home.this, CallEngineer.class);
            intent.putExtra("EngineerID", marker.getSnippet());
            intent.putExtra("phone",marker.getTitle());
            intent.putExtra("lat", mLastLocation.getLatitude());
            intent.putExtra("lng", mLastLocation.getLongitude());
            startActivity(intent);
        }
    }
}
