package com.i360.estimotedemo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class AllDemosActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_demos);
		
		findViewById(R.id.distance_demo_button).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AllDemosActivity.this, ListBeaconsActivity.class);
				intent.putExtra(ListBeaconsActivity.EXTRAS_TARGET_ACTIVITY, DistanceBeaconActivity.class.getName());
				startActivity(intent);
			}
		});
		
		
		findViewById(R.id.notify_demo_button).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AllDemosActivity.this, ListBeaconsActivity.class);
				intent.putExtra(ListBeaconsActivity.EXTRAS_TARGET_ACTIVITY, NotifyDemoActivity.class.getName());
				startActivity(intent);
			}
		});
		
		findViewById(R.id.characterstics_demo_button).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AllDemosActivity.this,ListBeaconsActivity.class);
				intent.putExtra(ListBeaconsActivity.EXTRAS_TARGET_ACTIVITY, CharacteristicsDemoActivity.class.getName());
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.all_demos, menu);
		return true;
	}

}
