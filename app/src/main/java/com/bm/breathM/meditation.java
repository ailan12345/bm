package com.bm.breathM;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class meditation extends AppCompatActivity {

    RadioGroup rg;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button btnDisplay;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meditation);

    }

    public void startMeditation(View view) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        // get selected radio button from radioGroup
        int selectedId = radioGroup.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        Toast.makeText(meditation.this,
                "準備計時" + radioButton.getText(), Toast.LENGTH_SHORT).show();

        int reciprocal = 10000; //時數
        switch(radioButton.getId()){
            case R.id.radioButton2:
                reciprocal = 1 * 60 * 1000;
                break;
            case R.id.radioButton3:
                reciprocal = 3 * 60 * 1000;
                break;
            case R.id.radioButton4:
                reciprocal = 5 * 60 * 1000;
                break;
            case R.id.radioButton5:
                reciprocal = 10 * 60 * 1000;
                break;
            case R.id.radioButton6:
                reciprocal = 30 * 60 * 1000;
                break;
        }

        Intent gostart = new Intent();
        gostart.setClass(meditation.this  , start.class);
        gostart .putExtra("reciprocal",reciprocal);
//        gostart .putExtra("frequency",frequency);
        startActivity(gostart);

    }
}
