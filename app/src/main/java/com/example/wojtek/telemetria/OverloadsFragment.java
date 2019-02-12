package com.example.wojtek.telemetria;

import android.app.Activity;
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

import java.util.Arrays;

/**
 * Created by wojtek on 30.11.17.
 */

public class OverloadsFragment extends Fragment {

    MainActivity activity;
    XYPlot plot;

    Number[] xOverloadSeriesVals = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    Number[] yOverloadSeriesVals = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    Number[] zOverloadSeriesVals = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private String xAxisOverloadReceived = "0";
    private String yAxisOverloadReceived = "0";
    private String zAxisOverloadReceived = "0";

    private double xOverloadValReceived = 0.0;
    private double yOverloadValReceived = 0.0;
    private double zOverloadValReceived = 0.0;

    TextView xAxisOverload;
    TextView yAxisOverload;
    TextView zAxisOverload;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (MainActivity)activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View myInflatedView = inflater.inflate(R.layout.overloads_tab, container, false);

        xAxisOverload = myInflatedView.findViewById(R.id.xValOverloadOnScreen);
        yAxisOverload = myInflatedView.findViewById(R.id.yValOverloadOnScreen);
        zAxisOverload = myInflatedView.findViewById(R.id.zValOverloadOnScreen);

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
                    xAxisOverloadReceived = ((DataFromMain) activity).getXOverloadFromActivity();
                    yAxisOverloadReceived = ((DataFromMain) activity).getYOverloadFromActivity();
                    zAxisOverloadReceived = ((DataFromMain) activity).getZOverloadFromActivity();
                    xOverloadValReceived = ((DataFromMain) activity).getXOverloadValueFromActivity();
                    yOverloadValReceived = ((DataFromMain) activity).getYOverloadValueFromActivity();
                    zOverloadValReceived = ((DataFromMain) activity).getZOverloadValueFromActivity();

                } catch (ClassCastException e) {
                    xAxisOverload.setText(e.getMessage());
                }
                System.arraycopy(xOverloadSeriesVals, 1, xOverloadSeriesVals, 0, (xOverloadSeriesVals.length) - 1);
                xOverloadSeriesVals[(xOverloadSeriesVals.length) - 1] = xOverloadValReceived;

                System.arraycopy(yOverloadSeriesVals, 1, yOverloadSeriesVals, 0, (yOverloadSeriesVals.length) - 1);
                yOverloadSeriesVals[(yOverloadSeriesVals.length) - 1] = yOverloadValReceived;

                System.arraycopy(zOverloadSeriesVals, 1, zOverloadSeriesVals, 0, (zOverloadSeriesVals.length) - 1);
                zOverloadSeriesVals[(zOverloadSeriesVals.length) - 1] = zOverloadValReceived;

                XYSeries series1 = new SimpleXYSeries(
                        Arrays.asList(xOverloadSeriesVals), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "X");
                XYSeries series2 = new SimpleXYSeries(
                        Arrays.asList(yOverloadSeriesVals), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Y");
                XYSeries series3 = new SimpleXYSeries(
                        Arrays.asList(zOverloadSeriesVals), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Z");

                LineAndPointFormatter series1Formatter = new LineAndPointFormatter(Color.RED, Color.GREEN, null, null);
                LineAndPointFormatter series2Formatter = new LineAndPointFormatter(Color.BLUE, Color.GRAY, null, null);
                LineAndPointFormatter series3Formatter = new LineAndPointFormatter(Color.YELLOW, Color.MAGENTA, null, null);

                plot.clear();
                plot.addSeries(series1, series1Formatter);
                plot.addSeries(series2, series2Formatter);
                plot.addSeries(series3, series3Formatter);
                plot.redraw();

                xAxisOverload.setText(xAxisOverloadReceived);
                yAxisOverload.setText(yAxisOverloadReceived);
                zAxisOverload.setText(zAxisOverloadReceived);

                handler.postDelayed(this,500);
            }
        });
    }
}
