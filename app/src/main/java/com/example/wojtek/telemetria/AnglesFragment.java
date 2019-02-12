package com.example.wojtek.telemetria;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by wojtek on 30.11.17.
 */

public class AnglesFragment extends Fragment {


    MainActivity activity;
    XYPlot plot;
    //PlotUpdater plotUpdater;

    Number[] rollSeriesVals = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    Number[] pitchSeriesVals = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    TextView roll;
    TextView pitch;

    String rollReceived = "0";
    String pitchReceived = "0";
    double rollValReceived = 0.0;
    double pitchValReceived = 0.0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity)activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View myInflatedView = inflater.inflate(R.layout.angles_tab, container, false);

        roll = myInflatedView.findViewById(R.id.rollOnScreen);
        pitch = myInflatedView.findViewById(R.id.pitchOnScreen);

        plot = myInflatedView.findViewById(R.id.plot);

        return myInflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //plotUpdater = new PlotUpdater(plot);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    rollReceived = ((DataFromMain) activity).getRollFromActivity();
                    pitchReceived = ((DataFromMain) activity).getPitchFromActivity();
                    rollValReceived = ((DataFromMain) activity).getRollValueFromActivity();
                    pitchValReceived = ((DataFromMain) activity).getPitchValueFromActivity();

                } catch (ClassCastException e) {
                    roll.setText(e.getMessage());
                }

                System.arraycopy(rollSeriesVals, 1, rollSeriesVals, 0, (rollSeriesVals.length) - 1);
                rollSeriesVals[(rollSeriesVals.length) - 1] = rollValReceived;

                System.arraycopy(pitchSeriesVals, 1, pitchSeriesVals, 0, (pitchSeriesVals.length) - 1);
                pitchSeriesVals[(pitchSeriesVals.length) - 1] = pitchValReceived;

                XYSeries series1 = new SimpleXYSeries(
                        Arrays.asList(rollSeriesVals), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Roll");

                XYSeries series2 = new SimpleXYSeries(
                        Arrays.asList(pitchSeriesVals), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Pitch");


                LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, null, null);
                LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.BLUE, Color.GRAY, null, null);

                plot.clear();
                plot.addSeries(series1, series1Format);
                plot.addSeries(series2, series2Format);

                /*plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                    @Override
                    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                        int i = Math.round(((Number) obj).floatValue());
                        return toAppendTo.append(domainLabels[i]);
                    }
                    @Override
                    public Object parseObject(String source, ParsePosition pos) {
                        return null;
                    }
                });*/
                plot.redraw();
                roll.setText(rollReceived);
                pitch.setText(pitchReceived);

                handler.postDelayed(this,500);

            }
        });

    }

    private class PlotUpdater implements Observer {

        Plot plot;

        public PlotUpdater(Plot plot) {
            this.plot = plot;
        }

        @Override
        public void update(Observable observable, Object o) {
            plot.redraw();
        }
    }

}
