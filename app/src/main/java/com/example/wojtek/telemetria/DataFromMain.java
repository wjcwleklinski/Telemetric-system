package com.example.wojtek.telemetria;

/**
 * Created by wojtek on 07.12.17.
 */

public interface DataFromMain {
    String getRollFromActivity();

    String getPitchFromActivity();

    String getVelocityFromActivity();

    String getTemperatureFromActivity();

    String getXOverloadFromActivity();

    String getYOverloadFromActivity();

    String getZOverloadFromActivity();


    double getRollValueFromActivity();

    double getPitchValueFromActivity();

    double getVelocityValueFromActivity();

    double getXOverloadValueFromActivity();

    double getYOverloadValueFromActivity();

    double getZOverloadValueFromActivity();



}
