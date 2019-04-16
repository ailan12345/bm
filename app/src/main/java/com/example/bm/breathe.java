package com.example.bm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class breathe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breathe);


    }

    public void breatheStart(View view) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        RadioGroup radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
        // get selected radio button from radioGroup
        int selectedId = radioGroup.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        RadioButton radioButton = (RadioButton) findViewById(selectedId);

        Toast.makeText(breathe.this,
                "準備計時" + radioButton.getText(), Toast.LENGTH_SHORT).show();
        int reciprocal = 10000; //時數
        switch(radioButton.getId()){
            case R.id.radioButton2:
                reciprocal = 60000;
                break;
            case R.id.radioButton3:
                reciprocal = 120000;
                break;
            case R.id.radioButton4:
                reciprocal = 180000;
                break;
            case R.id.radioButton5:
                reciprocal = 240000;
                break;
        }

        // get selected radio button from radioGroup
        int selectedId2 = radioGroup2.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        RadioButton radioButton2 = (RadioButton) findViewById(selectedId2);

        int frequency = 0; //頻率
        switch(radioButton2.getId()){
            case R.id.s6:
                frequency = 6;
                break;
            case R.id.s8:
                frequency = 8;
                break;
            case R.id.s10:
                frequency = 10;
                break;
            case R.id.s12:
                frequency = 12;
                break;
        }


        Intent gostart = new Intent();
        gostart.setClass(breathe.this  , start.class);
        gostart .putExtra("reciprocal",reciprocal);
        gostart .putExtra("frequency",frequency);
        startActivity(gostart);
    }
}
