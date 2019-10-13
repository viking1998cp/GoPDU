package pdu.admin516100.gopdu.Activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pdu.admin516100.gopdu.R;
import pdu.admin516100.gopdu.fragment.CustomerMapsFragment;
import pdu.admin516100.gopdu.fragment.DriverMapsFragment;

public class MainActivity extends AppCompatActivity {
    Button btnSign, btnRegister;
    FirebaseAuth.AuthStateListener mFireAuthListener;
    DatabaseReference mdData;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhxa();
        mdData = FirebaseDatabase.getInstance().getReference().child("User");
        mAuth = FirebaseAuth.getInstance();
        mFireAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String userId = FirebaseAuth.getInstance().getUid();
                if(user != null){
                    mdData.child("Driver").child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                mdData.child("Customer").child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){

                                        }else {
                                            Intent intent = new Intent(MainActivity.this, DriverActivityMain.class);
                                            startActivity(intent);
                                            finish();
                                        }

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
                    mdData.child("Customer").child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                mdData.child("Driver").child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            FragmentManager fragmentManager = getSupportFragmentManager();
                                            dialog_customer_or_driver dialog_customer_or_driver = pdu.admin516100.gopdu.Activity.dialog_customer_or_driver.newInstance("Lựa chọn sử dụng");
                                            dialog_customer_or_driver.show(fragmentManager,"dialog");
                                            dialog_customer_or_driver.setCancelable(false);

                                        }else {
                                            Intent intent = new Intent(MainActivity.this, CustomerActivityMain.class);
                                            startActivity(intent);
                                            finish();
                                        }

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

            }
        };



        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void anhxa() {
        btnSign = (Button) findViewById(R.id.btnSign);
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mFireAuthListener);
    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mFireAuthListener);
    }
}
