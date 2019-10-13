package pdu.admin516100.gopdu.Activity;


import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import pdu.admin516100.gopdu.Adapter.MainViewpager;
import pdu.admin516100.gopdu.R;
import pdu.admin516100.gopdu.until.until;


public class LoginActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tableLayout;
    private Button btnComfirm;
    private EditText editPhone;
    private ArrayList<String> arrayListPhone;
    private Boolean check_tontai=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        anhxa();
        arrayListPhone = new ArrayList<>();
        MainViewpager mainViewpager = new MainViewpager(getSupportFragmentManager());
        mainViewpager.addfragment(new Fragment(),"Customer");
        mainViewpager.addfragment(new Fragment(),"Driver");
        viewPager.setAdapter(mainViewpager);
        tableLayout.setupWithViewPager(viewPager);
        tableLayout.getTabAt(0).setIcon(R.drawable.ic_customer);
        tableLayout.getTabAt(1).setIcon(R.drawable.ic_driver);
        getDataNumberPhone();

        btnComfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(until.checkConnect(LoginActivity.this)){
                    String phone = editPhone.getText().toString().trim();

                    switch (tableLayout.getSelectedTabPosition()){
                        case 0:
                            for(int i=0;i<arrayListPhone.size();i++){
                                if(arrayListPhone.get(i).equals(phone)){
                                    check_tontai = true;
                                    break;
                                }else {
                                    check_tontai = false;
                                }
                            }
                            if(check_tontai){
                                Intent intent_Customer = new Intent(LoginActivity.this, VerifyPhoneNumberLogin.class);
                                intent_Customer.putExtra("phone",phone);
                                intent_Customer.putExtra("Object","Customer");
                                startActivity(intent_Customer);
                            }else {
                                Toast.makeText(LoginActivity.this,"Chưa tồn tại có số điện thoại này trong hệ thống", Toast.LENGTH_LONG).show();
                            }


                            break;
                        case 1:

                            for(int i=0;i<arrayListPhone.size();i++){
                                if(arrayListPhone.get(i).equals(phone)){
                                    check_tontai = true;
                                    break;
                                }else {
                                    check_tontai = false;
                                }
                            }
                            if(check_tontai){
                                Intent intent_Driver = new Intent(LoginActivity.this, VerifyPhoneNumberLogin.class);
                                intent_Driver.putExtra("phone",phone);
                                intent_Driver.putExtra("Object","Driver");
                                startActivity(intent_Driver);
                            }else {
                                Toast.makeText(LoginActivity.this,"Chưa tồn tại có số điện thoại này trong hệ thống", Toast.LENGTH_LONG).show();
                            }


                            break;
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "Kiểm tra kết nối internet",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void getDataNumberPhone() {

        switch (tableLayout.getSelectedTabPosition()) {
            case 0:
                arrayListPhone.clear();
                DatabaseReference mDataCustomer = FirebaseDatabase.getInstance().getReference().child("User").child("Customer");
                mDataCustomer.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Log.d("AAA", "onChildAdded: "+child.getKey());
                                if (child.getKey().equals("phone")) {
                                    String phonenumber = child.getValue().toString();
                                    if (phonenumber != null) {
                                        if (arrayListPhone.contains(phonenumber) == false) {
                                            arrayListPhone.add(phonenumber);
                                            Log.d("BBB", "onChildAdded: " + phonenumber);
                                        }
                                        Log.d("BBB", "onChildAdded: " + phonenumber);
                                    }

                                }
                            }
                        }
                        Log.d("'BBB", "onChildAdded: "+dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("BBB", "onCancelled: " + databaseError.getMessage());
                    }
                });
                break;
            case 1:
                arrayListPhone.clear();
                DatabaseReference mDataDriver = FirebaseDatabase.getInstance().getReference().child("User").child("Driver");
                Log.d("BBB", "onCreate: "+mDataDriver.getKey());
                mDataDriver.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.d("BBB", "onCreate: "+s);
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Log.d("AAA", "onChildAdded: "+child.getKey());
                                if (child.getKey().equals("phone")) {
                                    String phonenumber = child.getValue().toString();
                                    if (phonenumber != null) {
                                        if (arrayListPhone.contains(phonenumber) == false) {
                                            arrayListPhone.add(phonenumber);
                                            Log.d("BBB", "onChildAdded: " + phonenumber);
                                        }
                                        Log.d("BBB", "onChildAdded: " + phonenumber);
                                    }

                                }
                            }
                        }
                        Log.d("'BBB", "onChildAdded: "+dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;

        }
    }

    private void anhxa() {
        viewPager = (ViewPager) findViewById(R.id.viewPagerLogin);
        tableLayout = (TabLayout) findViewById(R.id.tablayoutLogin);
        btnComfirm = (Button) findViewById(R.id.btnComfirm);
        editPhone = (EditText) findViewById(R.id.editPhone);
    }

    @Override
    protected void onResume() {
        super.onResume();
        check_tontai = false;
    }
}
