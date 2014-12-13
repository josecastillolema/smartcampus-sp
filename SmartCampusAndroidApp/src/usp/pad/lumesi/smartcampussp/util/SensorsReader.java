package usp.pad.lumesi.smartcampussp.util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.format.Time;
import android.util.Log;

public class SensorsReader implements IReader {
	SensorManager sm;
	private float time;
	private Hashtable<String,SensorEvent> lSensorEv = new Hashtable <String,SensorEvent>();	
	

	private MyListener mListener = new MyListener();

	class MyListener implements SensorEventListener{
		@Override
		public synchronized void onSensorChanged(SensorEvent event) {
			SensorEvent oldEvent = null;
			boolean newEvent = true;
			synchronized (lSensorEv) {
				for (SensorEvent ev : Collections.list(lSensorEv.elements())){
					if (ev.sensor.getType() == event.sensor.getType()){
						if(event.timestamp - ev.timestamp > time){
							lSensorEv.put(event.sensor.getName(), event);
						}
						newEvent = false;
						break;
					}
				}
				if (newEvent)
					lSensorEv.put(event.sensor.getName(), event);
			}
		}
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	}
	
	@Override
	public synchronized JSONArray insertFIWAREValues(JSONArray jobj) {
		try {
			synchronized (lSensorEv) {
				for (SensorEvent se : Collections.list(lSensorEv.elements())){
					//Log.d(CONSTS.TAG, se.sensor.getName());
					//Log.d(CONSTS.TAG, getStringType(se.sensor.getType()));
					String values = se.values[0] + "";
					for (int i = 1; i < se.values.length; i++)
						values = values + "," + se.values[i];
					JSONObject leaf = new JSONObject();
					leaf.put(CONSTS.FIWARE.NAME, getStringType(se.sensor.getType()));
					leaf.put(CONSTS.FIWARE.TYPE, "custom");
					leaf.put(CONSTS.FIWARE.VALUE, values);
					jobj.put(leaf);
				}
				Log.d(CONSTS.TAG, "************************************");

			}
		}catch (JSONException je) {
				// TODO: handle exception
		}
		return jobj;
	}

	
	@Override
	public JSONObject insertFIWAREValues(JSONObject jobj) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public SensorsReader(Context ctx, int time){
		this.time = time * CONSTS.MILI_TO_NANO;
		this.sm = (SensorManager) ctx.getSystemService(Service.SENSOR_SERVICE);
		createListeners();
	}
	
	public synchronized void createListeners(){		
	    List<Sensor> listSensor = sm.getSensorList(Sensor.TYPE_ALL);
	    for (Sensor sen : listSensor){
		    this.sm.registerListener(mListener,
		            sm.getDefaultSensor(sen.getType()),
		            SensorManager.SENSOR_DELAY_NORMAL);
	    }
	}
	
	@Override
	public synchronized JSONObject inserValues(JSONObject jobj) {
		try {
			for (SensorEvent se : Collections.list(lSensorEv.elements())){
				JSONArray array = new JSONArray();
					for (double v : se.values)
						array.put(v);
					jobj.put(getStringType(se.sensor.getType()), array);
			}
			jobj.put(CONSTS.DATE_TIME, getDateTime());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobj;
	}
	
	@Override
	public boolean isSensorEnable() {
		return true;
	}
	
	
	@Override
	public synchronized void printDebug() {
	    for (SensorEvent se : Collections.list(lSensorEv.elements())){
    		String values = "";
    		for (float val : se.values)
    			values = values + ", " + val;
    		print("Name: " + se.sensor.getName() + "; Type: " + getStringType(se.sensor.getType()) +
    				"; Values: " + values);
	    }
	}
	
	@Override
	public void setTime(int time) {
		this.time = time * CONSTS.MILI_TO_NANO;
	}
	
	@Override
	public void stop() {
	
		
	}
	
	
	private synchronized SensorEvent getEvent(Sensor sen){
		for (SensorEvent ev : Collections.list(lSensorEv.elements())){
			if (ev.sensor.getName().equals(sen.getName())){
				return ev;
			}
		}
		return null;
	}
	
	
	public synchronized void getListSensors()  {
	    List<Sensor> listSensor = sm.getSensorList(Sensor.TYPE_ALL);
	    for (Sensor s : listSensor){
	    	SensorEvent se = getEvent(s);
	    	if (se != null){
	    		String values = "";
	    		for (float val : se.values)
	    			values = values + ", " + val;
	    		print("Name: " + se.sensor.getName() + "; Type: " + getStringType(se.sensor.getType()) +
	    				"; Values: " + values);
	    	}
	    }
	}
	
	
	public String getStringType(int type){
		switch (type) {
		case Sensor.TYPE_ACCELEROMETER:
			return "TYPE_ACCELEROMETER";
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			return "TYPE_AMBIENTE_TEMPERATURE";
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
			return "TYPE_GAME_ROTATION_VECTOR";
		case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
			return "TYPE_GEOMAGNETIC_ROTATION_VECTOR";
		case Sensor.TYPE_GRAVITY:
			return "TYPE_GRAVITY";
		case Sensor.TYPE_GYROSCOPE:
			return "TYPE_GYROSCOPE";
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			return "TYPE_GYROSCOPE_UNCALIBRATED";
		case Sensor.TYPE_HEART_RATE:
			return "TYPE_HEART_RATE";
		case Sensor.TYPE_LIGHT:
			return "TYPE_LIGHT";
		case Sensor.TYPE_LINEAR_ACCELERATION:
			return "TYPE_LINEAR_ACCELERATION";
		case Sensor.TYPE_MAGNETIC_FIELD:
			return "TYPE_MAGNETIC_FIELD";
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return "TYPE_MAGNETIC_FIELD_UNCALIBRATED";
		case Sensor.TYPE_ORIENTATION:
			return "TYPE_ORIENTATION";
		case Sensor.TYPE_PRESSURE:
			return "TYPE_PRESSURE";
		case Sensor.TYPE_PROXIMITY:
			return "TYPE_PROXIMITY";
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return "TYPE_RELATIVE_HUMIDITY";
		case Sensor.TYPE_ROTATION_VECTOR:
			return "TYPE_ROTATION_VECTOR";
		case Sensor.TYPE_SIGNIFICANT_MOTION:
			return "TYPE_SIGNIFICANT_MOTION";
		case Sensor.TYPE_STEP_COUNTER:
			return "TYPE_STEP_COUNTER";
		case Sensor.TYPE_STEP_DETECTOR:
			return "TYPE_STEP_DETECTOR";
		case Sensor.TYPE_TEMPERATURE:
			return "TYPE_TEMPERATURE";
		default:
			return "TYPE_NULL";
		}
		
	}
	
	public void print (String msg){
		Log.d("[SMARTCAMPUSSP]", msg);		
	}
	
	private String getDateTime(){
		DateFormat fm = DateFormat.getDateTimeInstance();
		Time time = new Time();   
		time.setToNow();
		return fm.format(new Date(time.toMillis(false)));
	}

}
