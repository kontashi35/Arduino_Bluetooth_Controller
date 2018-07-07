package co.realmentlabs.bluetoothconnector;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ControlActivity extends AppCompatActivity {
    private static final String TAG ="" ;
    private SeekBar mControlSekBar;
    private Button mOnBtn;
    String address=null;
    private Button mOffBtn;
    private Button mDisconnectBtn;
    private ProgressDialog mProgress;
    BluetoothAdapter myBluetooth=null;
    BluetoothSocket btSocket=null;
    private boolean isBtconnected=false;
    private Toolbar mToolbar;
    TextView mMaX;
    TextView mMin;
    static  final UUID myUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        address=getIntent().getStringExtra(MainActivity.EXTRA_ADDRESS);
        mOnBtn=findViewById(R.id.on_btn);
        mOffBtn=findViewById(R.id.offbtn);
        mDisconnectBtn=findViewById(R.id.discontbtn);
        mControlSekBar=findViewById(R.id.control_seekbar);
        mToolbar=findViewById(R.id.control_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Control");
        mProgress=new ProgressDialog(this);
        mProgress.setTitle("Connecting");
        mProgress.setMessage("PLease wait while it's connecting");

        new ConnectBT().execute();

        mOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mOnBtn.setBackground(getDrawable(R.drawable.btn_selected));
                    mOffBtn.setBackground(getDrawable(R.drawable.btn_bg));
                    mDisconnectBtn.setBackground(getDrawable(R.drawable.btn_bg));

                }else{
                    mOnBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mOffBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    mDisconnectBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));


                }
                turnOnLed();

            }
        });
        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mOnBtn.setBackground(getDrawable(R.drawable.btn_bg));
                    mOffBtn.setBackground(getDrawable(R.drawable.btn_selected));
                    mDisconnectBtn.setBackground(getDrawable(R.drawable.btn_bg));

                }else{
                    mOnBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    mOffBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mDisconnectBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));


                }
                turnOffLed();

            }
        });
        mDisconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mOnBtn.setBackground(getDrawable(R.drawable.btn_bg));
                    mOffBtn.setBackground(getDrawable(R.drawable.btn_bg));
                    mDisconnectBtn.setBackground(getDrawable(R.drawable.btn_selected));

                }else{
                    mOnBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    mOffBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    mDisconnectBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


                }
                disconnectDevice();

            }
        });
        mControlSekBar.setMax(255);
        mControlSekBar.setVisibility(View.INVISIBLE);


        mControlSekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean status) {
                if(status){
                    try
                    {
                        btSocket.getOutputStream().write(String.valueOf(progress).getBytes());

                    }
                    catch (IOException e)
                    {
                        Toast.makeText(ControlActivity.this, "Error occur", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onStart() {
        turnOffLed();
        super.onStart();
    }

    private void disconnectDevice() {
        if(btSocket!=null){
            try{
                turnOffLed();
                btSocket.close();

            }catch (Exception e){
                msg("Error");
            }
        }
        finish();

    }

    private void turnOffLed() {
        if(btSocket!=null){
            try{
                btSocket.getOutputStream().write("TF".toString().getBytes());
                mControlSekBar.setVisibility(View.INVISIBLE);
            }catch (Exception e){
                msg("Error");
            }
        }
    }

    private void turnOnLed() {
        mControlSekBar.setVisibility(View.VISIBLE);
        if(btSocket!=null){
            try {
                btSocket.getOutputStream().write("TO".toString().getBytes());
                mControlSekBar.setProgress(255);
            } catch (Exception e) {
                msg("Error");
            }
        }
    }

    public class ConnectBT extends AsyncTask<Void,Void,Void>{
        private boolean ConnectSuccess=true;

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

                try {
                    if (btSocket == null || !isBtconnected) {
                        myBluetooth = BluetoothAdapter.getDefaultAdapter();
                        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                        btSocket=dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        btSocket.connect();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(!ConnectSuccess){
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
        }else{
                msg("Connected");
                isBtconnected=true;
            }
            mProgress.dismiss();
    }

    }

    private void msg(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_LONG).show();
    }
}
