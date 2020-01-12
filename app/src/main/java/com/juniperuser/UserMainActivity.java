package com.juniperuser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.juniperuser.Model.User;
import com.juniperuser.Common.Common;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserMainActivity extends AppCompatActivity {

    Button btnContinue;
    RelativeLayout rootLayout;

    FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference users;

    private final static int REQUEST_CODE = 1000;

    //press ctrl+o
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

        setContentView(R.layout.activity_user_main);

        //Init Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference(Common.user_rider_tbl);

        //Init View
        btnContinue = (Button) findViewById(R.id.btnContinue);
        rootLayout = (RelativeLayout) findViewById(R.id.rootlayout);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithPhone();
            }
        });


        //auto login with fb acc lit for second time
        if (AccountKit.getCurrentAccessToken() != null) {
            //create dialog
            final SpotsDialog waitingDialog = new SpotsDialog(this);
            waitingDialog.show();
            waitingDialog.setMessage("Please wait...");
            waitingDialog.setCancelable(false);

            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    //login
                    users.child(account.getId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Common.currentUser = dataSnapshot.getValue(User.class);
                                    Intent homeIntent = new Intent(UserMainActivity.this, Home.class);
                                    startActivity(homeIntent);

                                    //dismiss dialog
                                    waitingDialog.dismiss();
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });
        }

    }

    private void signInWithPhone() {
        Intent intent = new Intent(UserMainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (result.getError() != null) {
                Toast.makeText(this, "" + result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            } else if (result.wasCancelled()) {
                Toast.makeText(this, "Cancel Login", Toast.LENGTH_SHORT).show();
                return;
            } else if (result.getAccessToken() != null) {
                //show dialog
                final SpotsDialog waitingDialog = new SpotsDialog(this);
                waitingDialog.show();
                waitingDialog.setMessage("Please waiting...");
                waitingDialog.setCancelable(false);

                //get current phone
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {
                        final String userId = account.getId();

                        //check if exist on firebase
                        users.orderByKey().equalTo(account.getId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.child(account.getId()).exists()) {
                                            //create new user and login
                                            final User engineer = new User();
                                            engineer.setPhone(account.getPhoneNumber().toString());
                                            engineer.setName(account.getPhoneNumber().toString());
                                            engineer.setImagUrl("");

                                            //register to firebase
                                            users.child(account.getId())
                                                    .setValue(engineer)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            //login
                                                            users.child(account.getId())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            User loginUser = dataSnapshot.getValue(User.class);
                                                                            Intent homeIntent = new Intent(UserMainActivity.this, Home.class);
                                                                            startActivity(homeIntent);

                                                                            //dismiss dialog
                                                                            waitingDialog.dismiss();
                                                                            finish();
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(UserMainActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else//if user exists,just login
                                        {
                                            //login
                                            users.child(account.getId())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            Common.currentUser = dataSnapshot.getValue(User.class);
                                                            Intent homeIntent = new Intent(UserMainActivity.this, Home.class);
                                                            startActivity(homeIntent);

                                                            //dismiss dialog
                                                            waitingDialog.dismiss();
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        Toast.makeText(UserMainActivity.this, "" + accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }

    }

}
