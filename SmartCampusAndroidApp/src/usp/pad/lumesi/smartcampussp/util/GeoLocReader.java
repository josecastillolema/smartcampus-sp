package usp.pad.lumesi.smartcampussp.util;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class GeoLocReader implements IReader {
	private Context ctx;
	LocationManager lm;
	private static boolean statusOn;
	private double[]values = {0.0, 0.0, 0.0, 0.0};

	private Address address;
	private LocationListener ll;
	private int time;
	
	public GeoLocReader(Context ctx, int time){
		this.ctx = ctx;
		this.time = time;
		initMembers();
		createListeners();		
	}
	
	private void initMembers(){
		lm = (LocationManager) ctx.getSystemService(CONSTS.LOC_SERVICE);
		statusOn = lm.isProviderEnabled(CONSTS.LOC_PROVIDER);
		address = new Address(Locale.getDefault());
	}
	
	@Override
	public JSONArray insertFIWAREValues(JSONArray jobj) {
		
		JSONObject[] leafs = new JSONObject[3];
		for (int i = 0; i < 3; i++){
			leafs[i] = new JSONObject();
		}
		try {					
			leafs[0].put(CONSTS.FIWARE.NAME, "localization");
			leafs[0].put(CONSTS.FIWARE.TYPE, "long/lat");
			leafs[0].put(CONSTS.FIWARE.VALUE, values[0] + "," + values[1]);
			leafs[1].put(CONSTS.FIWARE.NAME, "speed");
			leafs[1].put(CONSTS.FIWARE.TYPE, "km/h");
			leafs[1].put(CONSTS.FIWARE.VALUE, values[2]);
			leafs[2].put(CONSTS.FIWARE.NAME, "altitude");
			leafs[2].put(CONSTS.FIWARE.TYPE, "meters");
			leafs[2].put(CONSTS.FIWARE.VALUE, values[3]);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < 3; i++)
			jobj.put(leafs[i]);
		return jobj;
	}
	
	@Override
	public JSONObject insertFIWAREValues(JSONObject jobj) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void createListeners(){
		ll = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				if (status == LocationProvider.AVAILABLE)
					statusOn = true;
				statusOn = false;
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				if(provider.equals(CONSTS.LOC_PROVIDER))
					statusOn = true;
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				if(provider.equals(CONSTS.LOC_PROVIDER))
					statusOn = false;						
			}
			
			@Override
			public void onLocationChanged(Location location) {
				values[0] = location.getLongitude();
				values[1] = location.getLatitude();
				values[2] = location.getAltitude();
				values[3] = location.getSpeed();
//				Geocoder geo = new Geocoder(ctx, Locale.getDefault());
//				try {
//					address = geo.getFromLocation(values[0], values[1], 1).get(0);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}	
		};
		
		lm.requestSingleUpdate(CONSTS.LOC_PROVIDER, ll, null);
		lm.addGpsStatusListener(new Listener() {
			@Override
			public void onGpsStatusChanged(int event) {
				if (event == GpsStatus.GPS_EVENT_STOPPED)
					statusOn = false;
				statusOn = true;
			}
		});
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, this.time, CONSTS.GPS_DIST, ll);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, this.time, CONSTS.GPS_DIST, ll);
	}
	

	
	@Override
	public JSONObject inserValues(JSONObject jobj) {
		JSONArray array = new JSONArray();
		try {
			array.put(values[0]);
			array.put(values[1]);
			//array.put(address);
			jobj.put(CONSTS.PHONE_SENSOR_FIELDS.LOC, array);
			jobj.put(CONSTS.PHONE_SENSOR_FIELDS.HEIGHT, values[2]);
			jobj.put(CONSTS.PHONE_SENSOR_FIELDS.SPEED, values[3]);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobj;
	}

	@Override
	public boolean isSensorEnable() {
		return lm.isProviderEnabled(CONSTS.LOC_PROVIDER);
	}
	
	@Override
	public void printDebug() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
			sb.append(address.getAddressLine(i)).append(", ");
		
		Log.d(CONSTS.TAG, ": " + values[0] + ", " + values[1] + ", " + sb.toString() + ", " + values[2] + ", " + values[3]);
	}
	
	@Override
	public void stop() {
		
	}
	
	@Override
	public void setTime(int time) {
		this.time = time;
	}
}
