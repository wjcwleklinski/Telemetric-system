package com.example.wojtek.telemetria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PitchRollChart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pitch_roll_chart);


        Intent intent = getIntent();

        String incomingPitch = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        TextView pitch = (TextView)findViewById(R.id.pitchOnScreenChart);
        TextView roll = (TextView)findViewById(R.id.rollOnScreenChart);
        Button bMain = (Button)findViewById(R.id.toMainButtonOnScreen);

        bMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain(v);
            }
        });
        roll.setText("udało sie cos.");
        pitch.setText(incomingPitch);
    }


    public void goToMain(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //finish();
        startActivity(intent);
    }
}
