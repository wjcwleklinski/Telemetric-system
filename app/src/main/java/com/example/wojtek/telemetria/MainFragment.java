package com.example.wojtek.telemetria;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.anastr.speedviewlib.AwesomeSpeedometer;
import com.github.anastr.speedviewlib.Gauge;
import com.github.anastr.speedviewlib.SpeedView;

import java.io.IOException;
import java.util.List;


/**
 * Created by wojtek on 30.11.17.
 */

public class MainFragment extends Fragment {


    MainActivity activity;
    TextView roll;
    TextView pitch;
    TextView velocity;
    TextView xAxisOverload;
    TextView yAxisOverload;
    TextView zAxisOverload;
    TextView temperature;

    String rollReceived = "0";
    String pitchReceived = "0";
    String velocityReceived = "0";
    String xAxisOverloadReceived = "0";
    String yAxisOverloadReceived = "0";
    String zAxisOverloadReceived = "0";
    String temperatureReceived = "0";
    double velocityValReceived;

    Gauge gauge;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity)activity;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View myInflatedView = inflater.inflate(R.layout.main_tab, container, false);

        roll = (TextView) myInflatedView.findViewById(R.id.rollOnScreen);
        pitch = (TextView) myInflatedView.findViewById(R.id.pitchOnScreen);
        velocity = (TextView) myInflatedView.findViewById(R.id.velocityOnScreen);
        xAxisOverload = (TextView) myInflatedView.findViewById(R.id.xValOverloadOnScreen);
        yAxisOverload = (TextView) myInflatedView.findViewById(R.id.yValOverloadOnScreen);
        zAxisOverload = (TextView) myInflatedView.findViewById(R.id.zValOverloadOnScreen);
        temperature = (TextView) myInflatedView.findViewById(R.id.tempOnScreen);
        gauge = (Gauge) myInflatedView.findViewById(R.id.awesomeSpeedometer);
        //gauge.setMaxSpeed(200);
        //gauge.setDecelerate(0);
        //gauge.setMinSpeed(0);


        return myInflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();


        //roll = getActivity().findViewById(R.id.rollOnScreen);




        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    rollReceived = ((DataFromMain) activity).getRollFromActivity();
                    pitchReceived = ((DataFromMain) activity).getPitchFromActivity();
                    velocityReceived = ((DataFromMain) activity).getVelocityFromActivity();
                    temperatureReceived = ((DataFromMain) activity).getTemperatureFromActivity();
                    xAxisOverloadReceived = ((DataFromMain) activity).getXOverloadFromActivity();
                    yAxisOverloadReceived = ((DataFromMain) activity).getYOverloadFromActivity();
                    zAxisOverloadReceived = ((DataFromMain) activity).getZOverloadFromActivity();
                    velocityValReceived = ((DataFromMain) activity).getVelocityValueFromActivity();

                } catch (ClassCastException e) {
                    roll.setText(e.getMessage());
                }
                roll.setText(rollReceived);
                pitch.setText(pitchReceived);
                velocity.setText(velocityReceived);
                temperature.setText(temperatureReceived);
                xAxisOverload.setText(xAxisOverloadReceived);
                yAxisOverload.setText(yAxisOverloadReceived);
                zAxisOverload.setText(zAxisOverloadReceived);
                gauge.speedTo((float)velocityValReceived);

                handler.postDelayed(this,500);
            }
        });



    }

    /*public interface OnMainFragment {
        public String getRollFromActivity();

        public String getPitchFromActivity();

        public String getVelocityFromActivity();

        public String getTemperatureFromActivity();

        public String getXOverloadFromActivity();

        public String getYOverloadFromActivity();

        public String getZOverloadFromActivity();


        public double getRollValueFromActivity();

        public double getPitchValueFromActivity();

        public double getVelocityValueFromActivity();

        public double getXOverloadValueFromActivity();

        public double getYOverloadValueFromActivity();

        public double getZOverloadValueFromActivity();


    }*/


}
