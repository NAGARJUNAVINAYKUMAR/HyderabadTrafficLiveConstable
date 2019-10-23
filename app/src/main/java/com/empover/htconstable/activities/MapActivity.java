package com.empover.htconstable.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.empover.htconstable.R;
import com.empover.htconstable.constants.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
	
	private GoogleMap map;
	private double latitude;
	private double longitude;
	private String address;
	private LatLng latLng;
	SupportMapFragment supportMapFragment;
	MapFragment mapFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		/*supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_fragment);
		supportMapFragment.getMapAsync(this);*/
		mapFragment=(MapFragment)getFragmentManager().findFragmentById(R.id.map_fragment);
		mapFragment.getMapAsync(this);


		//map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();

		
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			try{
				address = bundle.getString(Constants.KEY_ADDRESS);
				latitude = Double.valueOf(bundle.getString(Constants.KEY_LATITUDE));
				longitude = Double.valueOf(bundle.getString(Constants.KEY_LONGITUDE));
				latLng = new LatLng(latitude, longitude);
				MarkerOptions options = new MarkerOptions();
				options.position(latLng);
				options.title(address);
				googleMap.addMarker(options);
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

			}catch(Exception e){

			}
		}
	}
}
