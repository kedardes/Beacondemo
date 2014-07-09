package com.i360.estimotedemo;

import java.util.Collections;
import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.utils.L;

import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ListBeaconsActivity extends Activity {

	private static final String TAG = ListBeaconsActivity.class.getSimpleName();
	
	public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
	public static final String EXTRAS_BEACON = "extrasBeacon";
	
	private static final int REQUEST_ENABLE_BT = 1234;
	private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid",null,null,null);
	
	private BeaconManager beaconManager;
	private LeDeviceListAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		adapter = new LeDeviceListAdapter(this);
		ListView list = (ListView) findViewById(R.id.device_list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(createOnClickListener());
		
		L.enableDebugLogging(true);
		
		beaconManager = new BeaconManager(this);
		beaconManager.setRangingListener(new BeaconManager.RangingListener() {
			@Override
			public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
					 getActionBar().setSubtitle("Found beacons" + beacons.size());
					 adapter.replaceWith(beacons);
					}
				});
			}
		});
		
	}

	private AdapterView.OnItemClickListener createOnClickListener() {
		// TODO Auto-generated method stub
		return new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
			
				if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY) != null)
				{
					try {
						Class<?> clazz = Class.forName(getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY));
						Intent intent = new Intent(ListBeaconsActivity.this, clazz);
						intent.putExtra(EXTRAS_BEACON, adapter.getItem(position));
						startActivity(intent);
					}
					catch (ClassNotFoundException e)
					{
						Log.e(TAG, "Finding class by name failed",e);
					}
				}
			}
			
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan_menu, menu);
		MenuItem refreshItem = menu.findItem(R.id.refresh);
		refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		beaconManager.disconnect();
		super.onDestroy();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onStart() {
		super.onStart();
		
		StringBuffer buf = new StringBuffer();

	    buf.append("VERSION.RELEASE {"+Build.VERSION.RELEASE+"}");
	    buf.append("\\nVERSION.INCREMENTAL {"+Build.VERSION.INCREMENTAL+"}");
	    buf.append("\\nVERSION.SDK {"+Build.VERSION.SDK+"}");
	    buf.append("\\nBOARD {"+Build.BOARD+"}");
	    buf.append("\\nBRAND {"+Build.BRAND+"}");
	    buf.append("\\nDEVICE {"+Build.DEVICE+"}");
	    buf.append("\\nFINGERPRINT {"+Build.FINGERPRINT+"}");
	    buf.append("\\nHOST {"+Build.HOST+"}");
	    buf.append("\\nID {"+Build.ID+"}");

	    Log.d("build",buf.toString()); 
	    
	    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
	    if (mBluetoothAdapter == null) {
	    	Toast.makeText(this,"Device does not support Bluetooth", Toast.LENGTH_LONG).show();
	    } else {
	        if (!mBluetoothAdapter.isEnabled()) {
	        	Toast.makeText(this,"Blue tooth is not enabled", Toast.LENGTH_LONG).show();
	        }
	        else
	        {
	        	Toast.makeText(this,"Device supports Bluetooth", Toast.LENGTH_LONG).show();
	        }
	        	
	    }
	    
	    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
	        Toast.makeText(this, "BLE NOT SUPPORTED ON THIS DEVICE", Toast.LENGTH_SHORT).show();
	        finish();
	    }
	    
		if (beaconManager.checkPermissionsAndService())
			Toast.makeText(this,"All the permissions are correctly set", Toast.LENGTH_LONG).show();
		
		if (!beaconManager.hasBluetooth()) {
			Toast.makeText(this,"Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (!beaconManager.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
		}
		else {
			connectToService();
		}
		
	}
	
	@Override
	protected void onStop()
	{
		try {
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
		}
		catch (RemoteException e)
		{
			Log.d(TAG,"Error while stopping ranging", e);
		}
		
		super.onStop();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				connectToService();
			}
			else {
				Toast.makeText(this,"BlueTooth not Enabled",Toast.LENGTH_LONG).show();
				getActionBar().setSubtitle("BlueTooth not enabled");
			}
		}
	}
	
	
	private void connectToService() {
		getActionBar().setSubtitle("Scanning ......");
		adapter.replaceWith(Collections.<Beacon>emptyList());
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			
			@Override
			public void onServiceReady() {
				// TODO Auto-generated method stub
				try {
				beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
				}
				catch  (RemoteException e){
					Toast.makeText(ListBeaconsActivity.this,"Cannot start ranging, something terrible happened", Toast.LENGTH_LONG).show();
					Log.e(TAG, "Cannot start ranging", e);
				}
				}
			
		});
	}
}
