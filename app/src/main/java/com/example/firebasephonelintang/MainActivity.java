package com.example.firebasephonelintang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText phone, otp;
    Button btnGenOTP, btnVerify;
    FirebaseAuth mAuth;
    String verificationID;
    ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        btnGenOTP = findViewById(R.id.btngenerateOTP);
        btnVerify =findViewById(R.id.btnverifyOTP);
        mAuth = FirebaseAuth.getInstance();
        bar = findViewById(R.id.bar);
        btnGenOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(TextUtils.isEmpty(phone.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Masukkan Nomor Telepon Anda", Toast.LENGTH_SHORT).show();
                }
                else {
                    String number = phone.getText().toString();
                    bar.setVisibility(View.VISIBLE);
                    sendverificationcode(number);
                }
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(TextUtils.isEmpty(otp.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Kode OTP Salah!", Toast.LENGTH_SHORT).show();
                }
                else
                    verifycode(otp.getText().toString());
            }
        });
    }

    private void sendverificationcode(String phoneNumber)
    {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)  // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential)
        {
            final String code = credential.getSmsCode();
            if(code!=null)
            {
                verifycode(code);
            }
        }
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(MainActivity.this, "Verifikasi Gagal", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token)
        {
            super.onCodeSent(s, token);
            verificationID = s;
            Toast.makeText(MainActivity.this, "Kode terkirim", Toast.LENGTH_SHORT).show();
            btnVerify.setEnabled(true);
            bar.setVisibility(View.INVISIBLE);
        }};
    private void verifycode(String Code)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,Code);
        signinbyCredentials(credential);
    }

    private void signinbyCredentials(PhoneAuthCredential credential)
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null)
        {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
    }
}