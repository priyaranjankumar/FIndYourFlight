package com.example.ankit.findyourflight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Blaze extends AppCompatActivity {
Button bt;
EditText et,et1,et2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blaze);
        bt=findViewById(R.id.button);
        et=findViewById(R.id.editText);
        et1=findViewById(R.id.editText2);
        et2=findViewById(R.id.editText3);

    }


    public void next(View view) {


        Intent i=new Intent(this,MainActivity.class);
        i.putExtra("origin",et.getText().toString());
        i.putExtra("destination",et1.getText().toString());
        i.putExtra("on",et2.getText().toString());
        startActivity(i);



    }
}
