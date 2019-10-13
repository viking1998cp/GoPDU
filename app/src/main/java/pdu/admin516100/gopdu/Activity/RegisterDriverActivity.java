package pdu.admin516100.gopdu.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import pdu.admin516100.gopdu.R;

public class RegisterDriverActivity extends AppCompatActivity {
    private Button btnRegister, btnBack;
    private EditText editName, editBirthDate, editPhone;
    private RadioGroup rdGroupGender, rdGroupService;
    private ImageView imvDriverAvatar;
    private Uri resultUri;
    String name, birthdate, avatar, phone, gender;
    int service;
    private FirebaseAuth mAuth;
    private Boolean check_tontai =false;
    private ArrayList<String> arrayListPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);
        anhxa();
        mAuth = FirebaseAuth.getInstance();
        arrayListPhone = new ArrayList<>();
        editBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(RegisterDriverActivity.this.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(RegisterDriverActivity.this.getCurrentFocus().getWindowToken(),0);
                chonngay();
            }
        });
        imvDriverAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("User").child("Driver");
        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        if(child.getKey().equals("phone")){
                            String phonenumber = child.getValue().toString();
                            if(arrayListPhone.contains(phonenumber) == false){
                                arrayListPhone.add(phonenumber);
                                Log.d("BBB", "onChildAdded: "+phonenumber);
                            }


                        }
                    }
                }
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

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickData();
//                if(until.checkConnect(RegisterDriverActivity.this)){
//                    pickData();
//                    if( name != null && phone != null && birthdate != null && service != -1 && gender != null ){
//
//                        for(int i=0;i<arrayListPhone.size();i++){
//                            if(arrayListPhone.get(i).equals(phone)){
//                                check_tontai = true;
//
//                                break;
//                            }else {
//                                check_tontai = false;
//                            }
//                        }
//
//                        if(check_tontai){
//                            Toast.makeText(RegisterDriverActivity.this,"Đã tồn tại  số điện thoại này trong hệ thống", Toast.LENGTH_LONG).show();
//                        }else {
//                            Customer customer = new Customer(name, phone, birthdate, avatar, gender);
//                            Intent intent = new Intent(RegisterDriverActivity.this, VerifyPhoneNumberRegister.class);
//                            intent.putExtra("Customer",customer );
//                            intent.putExtra("Object","Customer");
//                            startActivity(intent);
//                        }
//
//                    }else {
//                        Toast.makeText(RegisterDriverActivity.this,"Không được để trống thông tin", Toast.LENGTH_LONG).show();
//                    }
//                }else {
//                    Toast.makeText(RegisterDriverActivity.this, "Kiểm tra kết nối internet",Toast.LENGTH_SHORT).show();
//                }
                mAuth.createUserWithEmailAndPassword(name,phone).addOnCompleteListener(RegisterDriverActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegisterDriverActivity.this,"Sign up error ",Toast.LENGTH_LONG).show();
                            Log.d("BBB", "onComplete: "+task.getException().toString());
                        }else {
                            Toast.makeText(RegisterDriverActivity.this,"Registration sucsess ",Toast.LENGTH_LONG).show();
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(userId).child("name");
                            current_user_db.setValue(name);
                        }
                    }
                });


            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickData();
                mAuth.signInWithEmailAndPassword(name,phone).addOnCompleteListener(RegisterDriverActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegisterDriverActivity.this,"Sign up error ",Toast.LENGTH_LONG).show();
                        }else {

                        }
                    }
                });
            }
        });

    }

    private void pickData() {
        birthdate = editBirthDate.getText().toString().trim();
        name = editName.getText().toString().trim();
        phone = editPhone.getText().toString().trim();
        service = -1;
        avatar ="";
        switch (rdGroupGender.getCheckedRadioButtonId()){
            case R.id.rdMale:
                gender ="Male";
                break;
            case R.id.rdFemale:
                gender ="Female";
                break;
        }

        switch (rdGroupService.getCheckedRadioButtonId()){
            case R.id.rdBike:
                service = 1;
                break;
            case R.id.rdCar:
                service = 2;
                break;
            case R.id.rdCarPlus:
                service = 3;
                break;
        }


    }

    private void anhxa() {
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnBack = (Button) findViewById(R.id.btnBack);
        editName = (EditText) findViewById(R.id.editName);
        editBirthDate = (EditText) findViewById(R.id.editBirthdate);
        editPhone = (EditText) findViewById(R.id.editPhone);
        rdGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        rdGroupService = (RadioGroup) findViewById(R.id.radioGroupService);
        imvDriverAvatar = (ImageView) findViewById(R.id.imvDriverAvatar);
    }

    private void chonngay(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterDriverActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editBirthDate.setText(dayOfMonth+"/"+month+"/"+year);
            }
        }, 1999,01,01);
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            imvDriverAvatar.setImageURI(resultUri);
            Log.d("BBB", "onActivityResult: "+resultUri);
        }
    }
}
