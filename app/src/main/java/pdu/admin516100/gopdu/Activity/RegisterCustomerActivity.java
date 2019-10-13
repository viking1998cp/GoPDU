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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import pdu.admin516100.gopdu.Object.Customer;
import pdu.admin516100.gopdu.R;
import pdu.admin516100.gopdu.until.until;

public class RegisterCustomerActivity extends AppCompatActivity {

    private EditText editName, editPhone, editBirthdate;
    private RadioGroup rdGroupGender;
    private Button btnRegister, btnBack;
    private ImageView imvAvatar;
    private String name, birthdate, gender, phone, avatar;
    private ArrayList<String> arrayListPhone ;
    private Boolean check_tontai = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);
        anhxa();
        arrayListPhone = new ArrayList<>();
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("User").child("Customer");

        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        if(child.getKey().equals("phone")){
                            String phonenumber = child.getValue().toString();
                            if(phonenumber != null){
                                if(arrayListPhone.contains(phonenumber) == false){
                                    arrayListPhone.add(phonenumber);
                                    Log.d("BBB", "onChildAdded: "+phone);
                                }
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

        imvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        editBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chonngay();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(until.checkConnect(RegisterCustomerActivity.this)){
                    pickdata();
                    Log.d("BBB", "onClick: "+name+"/"+gender+"/"+birthdate+"/"+phone);
                    if(name!= null && phone != null && birthdate != null & gender != null){
                        for(int i=0;i<arrayListPhone.size();i++){
                            if(arrayListPhone.get(i).equals(phone)){
                                check_tontai = true;
                                break;
                            }else {
                                check_tontai = false;
                            }
                        }
                        if(check_tontai){
                            Toast.makeText(RegisterCustomerActivity.this,"Đã tồn tại có số điện thoại này trong hệ thống", Toast.LENGTH_LONG).show();

                        }else {
                            Customer customer = new Customer(name, phone, birthdate, avatar, gender);
                            Intent intent = new Intent(RegisterCustomerActivity.this, VerifyPhoneNumberRegister.class);
                            intent.putExtra("Customer",customer );
                            intent.putExtra("Object","Customer");
                            startActivity(intent);
                        }

                    }else {
                        Toast.makeText(RegisterCustomerActivity.this, "Không được để trống thông tin", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(RegisterCustomerActivity.this, "Kiểm tra kết nối internet",Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void pickdata() {
        birthdate = editBirthdate.getText().toString().trim();
        name = editName.getText().toString().trim();
        phone = editPhone.getText().toString().trim();
        avatar ="";
        switch (rdGroupGender.getCheckedRadioButtonId()){
            case R.id.rdMale:
                gender ="Male";
                break;
            case R.id.rdFemale:
                gender ="Female";
                break;
        }
    }


    private void anhxa (){
        editName = (EditText) findViewById(R.id.editName);
        editPhone= (EditText) findViewById(R.id.editPhone);
        editBirthdate= (EditText) findViewById(R.id.editBirthdate);
        rdGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        imvAvatar = (ImageView) findViewById(R.id.imvCustomerAvatar);

    }

    private void chonngay(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterCustomerActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editBirthdate.setText(dayOfMonth+"/"+month+"/"+year);
            }
        }, 1999,01,01);
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            imvAvatar.setImageURI(imageUri);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        check_tontai = false;
    }
}
