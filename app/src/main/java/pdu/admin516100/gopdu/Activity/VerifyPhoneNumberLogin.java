package pdu.admin516100.gopdu.Activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

import pdu.admin516100.gopdu.R;
import pdu.admin516100.gopdu.fragment.CustomerMapsFragment;
import pdu.admin516100.gopdu.fragment.DriverMapsFragment;
import pdu.admin516100.gopdu.until.until;

public class VerifyPhoneNumberLogin extends AppCompatActivity {
    private Button btnComfirm;
    private FirebaseAuth mAuth;
    private String VerifyCationId;
    private String phone;
    private OtpView otpView;
    private String object;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number_login);
        anhxa();
        object = getIntent().getStringExtra("Object");
        phone = getIntent().getStringExtra("phone");
        Log.d("BBB", "onCreate: "+phone);
        SendVerifyCode(new until().editPhoneNumber(phone));
        mAuth = FirebaseAuth.getInstance();
        btnComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otpView.length()==6){
                    if(until.checkConnect(VerifyPhoneNumberLogin.this)){
                        verifyCode(otpView.getText().toString());
                    }else {
                        Toast.makeText(VerifyPhoneNumberLogin.this,"Kiểm tra kết nối Internet",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(VerifyPhoneNumberLogin.this,"Bạn chưa nhập đủ mã OTP",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void anhxa() {
        btnComfirm = (Button) findViewById(R.id.btnComfirm);
        otpView = (OtpView) findViewById(R.id.otp_view);
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

                    Toast.makeText(VerifyPhoneNumberLogin.this,"Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    if(object.equals("Driver")){
                        Intent intent = new Intent(VerifyPhoneNumberLogin.this, DriverActivityMain.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(VerifyPhoneNumberLogin.this, CustomerActivityMain.class);
                        startActivity(intent);
                    }



                }else {
                    Toast.makeText(VerifyPhoneNumberLogin.this,"Sai mã xác thực vui lòng thử lại", Toast.LENGTH_SHORT).show();
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
