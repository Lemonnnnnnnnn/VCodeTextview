package com.yizhixionga.vcodetextview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.yizhixionga.textview.VerificationCodeView;

public class MainActivity extends AppCompatActivity implements VerificationCodeView.OnVerificationCodeCompleteListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VerificationCodeView vCodeTextView =new VerificationCodeView(MainActivity.this);
        vCodeTextView.setOnVerificationCodeCompleteListener(this);
    }

    @Override
    public void verificationCodeComplete(String verificationCode) {
        Toast.makeText(MainActivity.this,verificationCode,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void verificationCodeIncomplete(String verificationCode) {

    }
}