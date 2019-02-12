package com.example.wojtek.telemetria;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.support.v4.app.FragmentTransaction;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements DataFromMain{


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     *  Bluetooth adapter vars
     */
    BluetoothAdapter mBluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 3;

    /**
     * strings and doubles to display in fragments
     */

    public String rollToDisplay = "0.0";
    public String pitchToDisplay = "0.0";
    public String velocityToDisplay = "0.0";
    public String temperatureToDisplay = "0.0";
    public String xAxisOverloadToDisplay = "0.0";
    public String yAxisOverloadToDisplay = "0.0";
    public String zAxisOverloadToDisplay = "0.0";

    public double rollParsedToDouble = 0.0;
    public double pitchParsedToDouble = 0.0;
    public double velocityParsedToDouble = 0.0;
    public double xOverloadParsedToDouble = 0.0;
    public double yOverloadParsedToDouble = 0.0;
    public double zOverloadParsedToDouble = 0.0;

    /**
     * Universal and well known UUID
     */
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private DataInputStream mmInStream;
    private byte[] mmBuffer;
    public BluetoothSocket theSocket;
    public String myOwnTest;
    public String deviceName;
    public String addressMAC;
    public BluetoothDevice theDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //MainFragment.OnMainFragment onMainFragment;
        DataFromMain dataFromMain;

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

/////// get default bluetooth adapter and enables it if its not///////////


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null)
        {
            this.finishAffinity();
        }

        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//////////////////////////METODY Z MOJEGO INTERFEJSU////////////////////
    @Override
    public String getRollFromActivity() { return this.rollToDisplay; }

    @Override
    public String getPitchFromActivity() {
        return this.pitchToDisplay;
    }

    @Override
    public String getVelocityFromActivity() {
        return this.velocityToDisplay;
    }

    @Override
    public String getTemperatureFromActivity() {
        return this.temperatureToDisplay;
    }

    @Override
    public String getXOverloadFromActivity() {
        return this.xAxisOverloadToDisplay;
    }

    @Override
    public String getYOverloadFromActivity() {
        return this.yAxisOverloadToDisplay;
    }

    @Override
    public String getZOverloadFromActivity() { return this.zAxisOverloadToDisplay; }



    @Override
    public double getRollValueFromActivity() {
        return rollParsedToDouble;
    }

    @Override
    public double getPitchValueFromActivity() {
        return pitchParsedToDouble;
    }

    @Override
    public double getVelocityValueFromActivity() {
        return velocityParsedToDouble;
    }

    @Override
    public double getXOverloadValueFromActivity() {
        return xOverloadParsedToDouble;
    }

    @Override
    public double getYOverloadValueFromActivity() {
        return yOverloadParsedToDouble;
    }

    @Override
    public double getZOverloadValueFromActivity() {
        return zOverloadParsedToDouble;
    }


////////////////////////////////////////////////////////////////////////

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }




    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    MainFragment tab1 = new MainFragment();
                    return tab1;
                case 1:
                    AnglesFragment tab2 = new AnglesFragment();
                    return tab2;
                case 2:
                    OverloadsFragment tab3 = new OverloadsFragment();
                    return tab3;
                case 3:
                    SpeedFragment tab4 = new SpeedFragment();
                    return tab4;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        //rollToDisplay = "100";
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                deviceName = device.getName();
                addressMAC = device.getAddress(); ///address is all I need
            }
            //rollToDisplay = addressMAC;///////////////////
        }

        if (mBluetoothAdapter.isEnabled()) {

            theDevice = mBluetoothAdapter.getRemoteDevice(addressMAC);  //device acquired from adapter
            ConnectThread connection = new ConnectThread(theDevice);

            connection.start();//

        }



    }


    /**
     * Debugging tool
     */
    public void debugThis(String str) {
        this.myOwnTest = str;
    }

    /**
     * Separate thread to create socket and receive data
     */

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
                    decodeAndPackFrame(mmBuffer, numBytes);
                } catch (IOException e) {
                    debugThis("unable to read");
                }
            }
        }
    }

    public void decodeAndPackFrame(byte[] inBuffer, int numOfBytes)
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

                break;
            case 2:
                temperatureToDisplay = value;
                break;
            case 3:
                pitchToDisplay = value;
                try {
                    pitchParsedToDouble = Double.parseDouble(pitchToDisplay);
                } catch (NumberFormatException e) {
                    e.getMessage();
                }
                break;
            case 4:
                rollToDisplay = value;
                try {
                    rollParsedToDouble = Double.parseDouble(rollToDisplay);
                } catch (NumberFormatException e) {
                    e.getMessage();
                }
                break;
            case 5:
                xAxisOverloadToDisplay = value;
                xOverloadParsedToDouble = Double.parseDouble(xAxisOverloadToDisplay);
                break;
            case 6:
                yAxisOverloadToDisplay = value;
                yOverloadParsedToDouble = Double.parseDouble(yAxisOverloadToDisplay);
                break;
            case 7:
                zAxisOverloadToDisplay = value;
                zOverloadParsedToDouble = Double.parseDouble(zAxisOverloadToDisplay);
                break;
            case 8:
                velocityToDisplay = value;
                velocityParsedToDouble = Double.parseDouble(velocityToDisplay);
                break;
        }


    }


}
