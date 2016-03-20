package com.example.smsinterception;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnWords;
    Button btnSender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        btnWords= (Button) findViewById(R.id.btnWords);
        btnSender= (Button) findViewById(R.id.btnSender);
        btnWords.setOnClickListener(this);
        btnSender.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnWords:
                Intent intentWord=new Intent();
                intentWord.setClass(EditActivity.this,WordsActivity.class);
                startActivity(intentWord);
                break;
            case R.id.btnSender:
                Intent intentSender=new Intent();
                intentSender.setClass(EditActivity.this,NumberActivity.class);
                startActivity(intentSender);
                break;
        }
    }
}
