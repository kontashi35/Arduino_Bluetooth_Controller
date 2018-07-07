package co.realmentlabs.bluetoothconnector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = " ";
    private Button mPairBtn;
    private ListView mDeviceList;
    
    private BluetoothAdapter mBlueToothAdapter=null;
    private Set<BluetoothDevice> pairDevices;
    public static String EXTRA_ADDRESS="device_address";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPairBtn=findViewById(R.id.pairbtn);
        mDeviceList=findViewById(R.id.listview);
        mBlueToothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(mBlueToothAdapter==null){
            Toast.makeText(this, "that the device has no bluetooth adapter", Toast.LENGTH_SHORT).show();
            finish();
        }else if(!mBlueToothAdapter.isEnabled()){
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,1);
        }
        mPairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pairDevice();
            }
        });

    }

    private void pairDevice() {
        pairDevices=mBlueToothAdapter.getBondedDevices();
        ArrayList list=new ArrayList();
        if(pairDevices.size()>0){
            BluetoothDevice device;
            for(BluetoothDevice bt :pairDevices){
                String name="Bluetooth Name: ";
                String address="Address: ";
                if(bt.getAddress().equals("98:D3:31:20:6A:2A")){
                    list.add(name+"HUL"+"\n"+address+bt.getAddress());
                }else{
                    list.add(bt.getName()+"\n"+bt.getAddress());
                }
            }
        }else{
            Toast.makeText(this, "No paired device found", Toast.LENGTH_SHORT).show();
        }
        final ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        mDeviceList.setAdapter(adapter);
        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String info=((TextView)view).getText().toString();
                String address=info.substring(info.length()-17);
                Intent intent=new Intent(MainActivity.this,ControlActivity.class);
                intent.putExtra(EXTRA_ADDRESS,address);
                startActivity(intent);
            }
        });

    }
}
