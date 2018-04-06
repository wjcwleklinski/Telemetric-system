package com.example.wojtek.telemetria;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    volatile TextView roll;
    volatile TextView pitch;
    volatile TextView velocity;
    volatile TextView temperature;
    volatile TextView xAxisOverload;
    volatile TextView yAxisOverload;
    volatile TextView zAxisOverload;
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

    public static final String EXTRA_MESSAGE = "my first app";

    private DataInputStream mmInStream;
    private byte[] mmBuffer;

    public ConnectThread connection;

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







////discovering (not sure if it works) devices which are not paired
////I am going to test this section when the whole app will be operational
        IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, mFilter);
        //make random debugThis
        mBluetoothAdapter.startDiscovery();


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

        ////BUTTON IS HERE
        bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goHome();
                sendMessage(v);
            }
        });

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
            connection = new ConnectThread(theDevice);

            connection.start();//

        }
        roll.setText("Resuming");




    }


    public class ConnectThread extends Thread {

        private BluetoothDevice myDevice;

        public ConnectThread(BluetoothDevice device) {

            BluetoothSocket tmp = null;
            myDevice = device;
            try
            {
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_SECURE);
            } catch (IOException e) {
                debugThis("connection not secured");
            }
            theSocket = tmp;
            /////////////////////////
            InputStream tmpIn = null;

            try {
                tmpIn = theSocket.getInputStream();
            } catch (IOException e) {
                debugThis("Imputstream not obtained");
            }
            mmInStream = new DataInputStream(tmpIn);

        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();

            try {
                theSocket.connect();
            } catch (IOException e) {
                try {
                    theSocket.close();
                } catch (IOException e1) {
                    debugThis("unable to close socket");
                }
                debugThis("socket closed");
                return;
            }
        /////////////////////////////////////////////////////
            mmBuffer = new byte[32];
            int numBytes;

            while(true) {
                try {
                    numBytes = mmInStream.read(mmBuffer);
                    decodeAndDisplayFrame(mmBuffer, numBytes);
                    //final String msg = new String(mmBuffer);
                    /*final byte myvale0 = mmBuffer[0];
                    final byte myvale1 = mmBuffer[1];
                    final byte myvale2 = mmBuffer[2];
                    final byte myvale3 = mmBuffer[3];
                    final String nums = Integer.toString(numBytes);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roll.setText(nums);
                            pitch.setText(Byte.toString(myvale0) + Byte.toString(myvale1) + Byte.toString(myvale2) + Byte.toString(myvale3));
                            pitch.invalidate();
                        }
                    });*/
                    //pitch.setText(msg);
                } catch (IOException e) {
                    debugThis("unable to read");
                }
            }
        }
    }


    /**
     * Creates socket from device. Device is created from adapter using mac address.
     */
    /*public class ConnectThread extends Thread
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

                try {
                    theSocket.connect();
                } catch (IOException connectException) {
                    try {
                        theSocket.close();
                    } catch (IOException closeException) {
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
            //private DataInputStream mmInStream;
            //private byte[] mmBuffer;


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
                        //runOnUiThread(new Runnable() {
                          //  @Override
                           // public void run() {
                             //   roll.setText(String.valueOf(readMessage));

                            //}
                        });

                    }
                    catch(IOException e)
                    {
                        debugThis("Unable to read.");
                    }
                }

            }


        }




    }*/

    public void decodeAndDisplayFrame(byte[] inBuffer, int numOfBytes)
    {
        int markVal;
        int code;
        String value;


        //frame = 1cmvvvv
        //


        if (numOfBytes > 3)
        {
            code = (int)(Math.floor(inBuffer[1] / 10));
            markVal = inBuffer[1] - 10 * code;
            value = Byte.toString(inBuffer[2]) + "." + Byte.toString(inBuffer[3]);
        }
        else
        {
            code = (int)(Math.floor(inBuffer[0] / 10));
            markVal = inBuffer[0] - 10 * code;
            value = Byte.toString(inBuffer[1]) + "." + Byte.toString(inBuffer[2]);
        }


        if (markVal == 1)
        {
            value = "-" + value;
        }



        switch (code){
            case 1:
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
            case 2:
                temperatureToDisplay = value;
                break;
            case 3:
                pitchToDisplay = value;
                break;
            case 4:
                rollToDisplay = value;
                break;
            case 5:
                xAxisOverloadToDisplay = value;
                break;
            case 6:
                yAxisOverloadToDisplay = value;
                break;
            case 7:
                zAxisOverloadToDisplay = value;
                break;
            case 8:
                velocityToDisplay = value;
                break;
        }

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


    @Override
    public void onPause() {
        super.onPause();

        //roll.setText("Paused");
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


    public void sendMessage(View view)
    {

        Intent intent = new Intent(this, PitchRollChart.class);
        //EditText editText = (EditText)findViewById(R.id.editTextOnScreen);
        //String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, pitchToDisplay);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //finish();
        //moveTaskToBack(true);

        startActivity(intent);
    }

}


