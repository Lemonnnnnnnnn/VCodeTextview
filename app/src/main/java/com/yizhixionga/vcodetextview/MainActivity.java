package com.yizhixionga.vcodetextview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.yizhixionga.textview.VCodeTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VCodeTextView vCodeTextView =new VCodeTextView(MainActivity.this);
        vCodeTextView.setVCodeCompleteListener(new VCodeTextView.VCodeCompleteListener() {
            @Override
            public void VCodeComplete(String code) {
                Toast.makeText(MainActivity.this,code,Toast.LENGTH_LONG).show();
            }
        });
    }
}