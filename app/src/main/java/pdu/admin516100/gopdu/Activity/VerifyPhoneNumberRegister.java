package pdu.admin516100.gopdu.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.mukesh.OtpView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import pdu.admin516100.gopdu.Object.Customer;
import pdu.admin516100.gopdu.Object.Driver;
import pdu.admin516100.gopdu.R;
import pdu.admin516100.gopdu.fragment.CustomerMapsFragment;
import pdu.admin516100.gopdu.fragment.DriverMapsFragment;
import pdu.admin516100.gopdu.until.until;

public class VerifyPhoneNumberRegister extends AppCompatActivity {
    private OtpView otp_VerifyPhone;
    private Button btnComfirm;
    String VerifyCationId;
    FirebaseAuth mAuth;
    DatabaseReference mdData;
    private Uri uri;
    private String object;

    FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number_register);
        anhxa();

        mAuth = FirebaseAuth.getInstance();
        object = getIntent().getStringExtra("Object");
        if(object.equals("Driver")){
            Driver driver = (Driver) getIntent().getSerializableExtra("Driver");
            SendVerifyCode(new until().editPhoneNumber(driver.getPhone()));
        }else {
            Customer customer = (Customer) getIntent().getSerializableExtra("Customer");
            SendVerifyCode(new until().editPhoneNumber(customer.getPhone()));
        }


        if(getIntent().getExtras().get("resultUri")==null){
            uri = null;
        }else {
            uri =  Uri.parse(getIntent().getExtras().get("resultUri").toString());
        }

        btnComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otp_VerifyPhone.getText().length()==6){
                    if(until.checkConnect(VerifyPhoneNumberRegister.this)){
                        verifyCode(otp_VerifyPhone.getText().toString());
                    }else {
                        Toast.makeText(VerifyPhoneNumberRegister.this,"Kiểm tra kết nối Internet",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(VerifyPhoneNumberRegister.this, "Bạn chưa nhập đủ mã OTP", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void anhxa() {
        otp_VerifyPhone = (OtpView) findViewById(R.id.otp_VerifyPhone);
        btnComfirm = (Button) findViewById(R.id.btnComfirm);
    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerifyCationId,code); //Kiểm tra mã code được
        SigInWithCredential(credential);
    }
    private void SigInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String userid = mAuth.getUid();

                    Toast.makeText(VerifyPhoneNumberRegister.this,"Đăng kí thành công", Toast.LENGTH_SHORT).show();
                    Map userInfo = new HashMap();
                    if(object.equals("Driver")){
                        mdData = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(userid);
                        Driver driver = (Driver) getIntent().getSerializableExtra("Driver");
                        userInfo.put("name",driver.getName());
                        userInfo.put("phone",driver.getPhone());
                        userInfo.put("gender",driver.getGender());
                        userInfo.put("birthdate",driver.getBirthdate());
                        userInfo.put("service", driver.getService());
                        mdData.updateChildren(userInfo);
                        Intent intent = new Intent(VerifyPhoneNumberRegister.this, DriverMapsFragment.class);
                        startActivity(intent);
                    }else {
                        mdData = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(userid);
                        Customer customer = (Customer) getIntent().getSerializableExtra("Customer");
                        userInfo.put("name",customer.getName());
                        userInfo.put("phone",customer.getPhone());
                        userInfo.put("gender",customer.getGender());
                        userInfo.put("birthdate",customer.getBirthdate());
                        mdData.updateChildren(userInfo);
                        Intent intent = new Intent(VerifyPhoneNumberRegister.this, CustomerMapsFragment.class);
                        startActivity(intent);

                    }




                }else {
                    Toast.makeText(VerifyPhoneNumberRegister.this,"Sai mã xác thực vui lòng thử lại", Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }

    private void SendVerifyCode(String PhoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(PhoneNumber
                ,60
                , TimeUnit.SECONDS
                , TaskExecutors.MAIN_THREAD
                ,mcallback);

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mcallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            VerifyCationId =s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d("BBB", "onVerificationFailed: "+e.toString());
        }
    };
}
