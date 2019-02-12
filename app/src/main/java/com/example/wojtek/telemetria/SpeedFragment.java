package com.example.wojtek.telemetria;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.util.Arrays;

/**
 * Created by wojtek on 30.11.17.
 */

public class SpeedFragment extends Fragment {

    MainActivity activity;
    Plot plot;
    TextView velocity;

    Number[] velocitySeriesVals = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    String velocityReceived;
    double velocityValReceived;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity)activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View myInflatedView = inflater.inflate(R.layout.speed_tab, container, false);
        velocity = myInflatedView.findViewById(R.id.velocityOnScreen);
        plot = myInflatedView.findViewById(R.id.plot);

        return myInflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    velocityReceived = ((DataFromMain) activity).getVelocityFromActivity();
                    velocityValReceived = ((DataFromMain) activity).getVelocityValueFromActivity();

                } catch (ClassCastException e) {
                    velocity.setText(e.getMessage());
                }

                System.arraycopy(velocitySeriesVals, 1, velocitySeriesVals, 0, (velocitySeriesVals.length) - 1);
                velocitySeriesVals[(velocitySeriesVals.length) - 1] = velocityValReceived;



                XYSeries series1 = new SimpleXYSeries(
                        Arrays.asList(velocitySeriesVals), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Speed");



                LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, null, null);


                plot.clear();
                plot.addSeries(series1, series1Format);

                plot.redraw();

                velocity.setText(velocityReceived);


                handler.postDelayed(this,500);
            }
        });
    }
}
