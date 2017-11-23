package com.example.wojtek.telemetria;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
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
//import java.util.logging.Handler;


public class MainActivity extends Activity {


    private static final int REQUEST_ENABLE_BT = 3;

    public  BluetoothAdapter mBluetoothAdapter;
    public BluetoothDevice theDevice;

    public String myOwnTest = "Begin test.";
    public String deviceName;
    public String addressMAC;

    TextView roll;
    TextView pitch;
    TextView velocity;
    TextView temperature;
    TextView xAxisOverload;
    TextView yAxisOverload;
    TextView zAxisOverload;
    Button bConnect;


    public BluetoothSocket temp = null;
    public BluetoothSocket theSocket;

    private static final UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public String rollToDisplay;
    public String pitchToDisplay;
    public String velocityToDisplay;
    public String temperatureToDisplay;
    public String xAxisOverloadToDisplay;
    public String yAxisOverloadToDisplay;
    public String zAxisOverloadToDisplay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bConnect = (Button)findViewById(R.id.connectBtnOnScreen);
        roll = (TextView)findViewById(R.id.rollOnScreen);
        pitch = (TextView)findViewById(R.id.pitchOnScreen);
        velocity = (TextView)findViewById(R.id.velocityOnScreen);
        xAxisOverload = (TextView)findViewById(R.id.xValOverloadOnScreen);
        yAxisOverload = (TextView)findViewById(R.id.yValOverloadOnScreen);
        zAxisOverload = (TextView)findViewById(R.id.zValOverloadOnScreen);
        temperature = (TextView)findViewById(R.id.tempOnScreen);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        ////CHECKING
        if (mBluetoothAdapter == null)
        {
            goHome();

        }

////BUTTON IS HERE
            bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();

            }
            });





////discovering (not sure if it works) devices which are not paired
////I am going to test this section when the whole app will be operational
        IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, mFilter);
        //make random debugThis
        mBluetoothAdapter.startDiscovery();
////

    }

    @Override
    public void onStart() {
        super.onStart();
        //initialize layout here
////ENABLING
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


    }

    @Override
    public void onResume()
    {
        super.onResume();

////QUERYING paired devices (HC05 is already paired with my phone)
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        //xAxis.setText("here");

        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                deviceName = device.getName();
                addressMAC = device.getAddress(); ///address is all I need
            }
            roll.setText("wiecej niz 1");//works
        }
        velocity.setText(deviceName + " " + addressMAC);


        if (mBluetoothAdapter.isEnabled()) {


            theDevice = mBluetoothAdapter.getRemoteDevice(addressMAC);  //device acquired from adapter
            ConnectThread connection = new ConnectThread(theDevice);
            //connection.run();
            connection.start();//
        }
        roll.setText("Resuming");
        //xAxis.invalidate();

    }


    /**
     * Creates socket from device. Device is created from adapter using mac address.
     */
    private class ConnectThread extends Thread
    {

        private final BluetoothDevice myDevice;

        public  ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            myDevice = device;
            try
            {
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_SECURE);
            }
            catch (IOException e)
            {
                String connectExeption = e.getMessage();
            }
            theSocket = tmp;
            debugThis("connect thread constructor");

        }

        public void run()
        {
            mBluetoothAdapter.cancelDiscovery();
            debugThis("connect thread run before try/catch");

            try
            {
                theSocket.connect();
            }
            catch (IOException connectException)
            {
                try
                {
                    theSocket.close();
                }
                catch(IOException closeException)
                {
                    debugThis("unable to close socket");

                }
                debugThis("socket closed");
                return;
            }
            debugThis("connection established");
            //manageMyConnectedSocket(mySocket);
            ConnectedThread connectedT = new ConnectedThread(theSocket);
            //connectedT.run();
            connectedT.start();//// starts a thread


    }

        private class ConnectedThread extends Thread
        {
            private final BluetoothSocket mmSocket;
            private final DataInputStream mmInStream;
            private byte[] mmBuffer;


            public ConnectedThread(BluetoothSocket socket)
            {
                mmSocket = socket;
                InputStream tmpIn = null;

                try
                {
                    tmpIn = socket.getInputStream();
                }
                catch (IOException e)
                {
                    debugThis("connectedThread constructor exception");
                    //String errInString = e.getMessage();
                }
                mmInStream = new DataInputStream(tmpIn);

            }

            public void run()
            {
                mmBuffer = new byte[32];
                int numBytes;

                while (true)
                {
                    try
                    {
                        numBytes = mmInStream.read(mmBuffer);
                        //final String readMessage = new String(mmBuffer, 0, numBytes);
                        decodeAndDisplayFrame(mmBuffer, numBytes);
                        //final String testVal = String.valueOf(numBytes);
                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                roll.setText(String.valueOf(readMessage));

                            }
                        });*/

                    }
                    catch(IOException e)
                    {
                        debugThis(e.getMessage());
                    }
                }

            }


        }




    }

    public void decodeAndDisplayFrame(byte[] inBuffer, int numOfBytes)
    {
        //final int markVal = inBuffer[8];
        byte markVal;
        //final int code = inBuffer[2];
        byte code;
        String value;
        //double result, overloadResult;

        //frame = 1cmvvvv
        //


        if (numOfBytes > 6)
        {
            value ="" +  (char)inBuffer[3] + (char)inBuffer[4] + "." + (char)inBuffer[5] + (char)inBuffer[6];
            markVal = inBuffer[2];
            code = inBuffer[1];
        }
        else
        {
            value ="" +  (char)inBuffer[2] + (char)inBuffer[3] + "." + (char)inBuffer[4] + (char)inBuffer[5];
            markVal = inBuffer[1];
            code = inBuffer[0];
        }
        //result = Double.parseDouble(value);

        if (markVal == 49)
        {
            //result = -result;
            value = "-" + value;
        }



        switch (code){
            case 49:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        temperature.setText(String.valueOf(temperatureToDisplay));
                        temperature.invalidate();

                        pitch.setText(pitchToDisplay);
                        pitch.invalidate();

                        roll.setText(rollToDisplay);
                        roll.invalidate();

                        xAxisOverload.setText(xAxisOverloadToDisplay);
                        xAxisOverload.invalidate();

                        yAxisOverload.setText(yAxisOverloadToDisplay);
                        yAxisOverload.invalidate();

                        zAxisOverload.setText(zAxisOverloadToDisplay);
                        zAxisOverload.invalidate();

                        velocity.setText(velocityToDisplay);
                        velocity.invalidate();

                    }
                });

                break;
            case 50:
                temperatureToDisplay = value;
                break;
            case 51:
                pitchToDisplay = value;
                break;
            case 52:
                rollToDisplay = value;
            case 53:
                xAxisOverloadToDisplay = value;
                break;
            case 54:
                yAxisOverloadToDisplay = value;
                break;
            case 55:
                zAxisOverloadToDisplay = value;
                break;
            case 56:
                velocityToDisplay = value;
                break;
        }
        //temperatureToDisplay = String.valueOf(code);
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {

                temperature.setText(String.valueOf(temperatureToDisplay));
                temperature.invalidate();

                pitch.setText(pitchToDisplay);
                pitch.invalidate();

                roll.setText(rollToDisplay);
                roll.invalidate();

                xAxisOverload.setText(xAxisOverloadToDisplay);
                xAxisOverload.invalidate();

                yAxisOverload.setText(yAxisOverloadToDisplay);
                yAxisOverload.invalidate();

                zAxisOverload.setText(zAxisOverloadToDisplay);
                zAxisOverload.invalidate();

                velocity.setText(velocityToDisplay);
                velocity.invalidate();

            }
        });*/



    }
    /**
     * Used to discovery device
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            debugThis("intent created");////
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceMAC = device.getAddress();

                debugThis(deviceName);//////
            }
        }
    };

    @Override
    protected  void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    /**
     * Method works like home button on device
     */
    public void goHome()
    {
        Intent toHomeIntent = new Intent(Intent.ACTION_MAIN);
        toHomeIntent.addCategory(Intent.CATEGORY_HOME);
        toHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toHomeIntent);
    }

    public void debugThis(String s)
    {
        myOwnTest = s;
    }


}


