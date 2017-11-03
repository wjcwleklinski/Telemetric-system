package com.example.wojtek.telemetria;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothServerSocket;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ViewAnimator;
import android.os.Handler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by wojtek on 31.10.17.
 */

public class GetData extends Activity
{
    private BluetoothServerSocket mmServerSocket;
    private BluetoothAdapter mAdapter;
    private BluetoothClass.Device remoteDevice;
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public String message;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        BluetoothSocket socket = null;
        mAdapter = BluetoothAdapter.getDefaultAdapter();

        try{
            mmServerSocket = mAdapter.listenUsingRfcommWithServiceRecord("Serwis",MY_UUID_SECURE);
            socket = mmServerSocket.accept();
        }catch(IOException e)
        {

        }

        byte[] buffer = new byte[256];
        int bytes;

        try
        {
            mmServerSocket.close();
            InputStream tmpIn = null;

            DataInputStream mmInstream = new DataInputStream(tmpIn);

            bytes = mmInstream.read(buffer);
            String readMsg = new String(buffer, 0 , bytes);
            takeData(readMsg);
        }catch (IOException e)
        {

        }


    }
    public void takeData(String msg)
    {
        this.message = msg;

    }
}
