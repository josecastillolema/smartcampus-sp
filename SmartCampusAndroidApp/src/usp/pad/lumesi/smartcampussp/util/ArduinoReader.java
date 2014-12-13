package usp.pad.lumesi.smartcampussp.util;

import java.io.IOException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ArduinoReader  implements IReader {
	private String[] data = {"umity", "temperature", "gLP Level","smoke level", "carbon dioxide level", "air quality", "atmospheric pressure", "altitude","rain"};
	private String[] dataType = {"percentage", "celsius", "ppm","ppm", "ppm", "custom", "Pa", "meter","custom"};
	
	private BluetoothAdapter ba;
	private BluetoothDevice bd;
	private BluetoothSocket bs;
	private String[] values = {"-1","-1","-1","-1","-1","-1","-1","-1","-1" };
	Timer t;	
	
	public ArduinoReader(int time){
		t = new Timer();
		initBluetooth();
		getSensorValues(time);
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		t.cancel();
	}
	
	public void initBluetooth(){
		this.ba = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> devices = ba.getBondedDevices();
		for (BluetoothDevice dev : devices){
			Log.d(CONSTS.TAG, dev.getName());
			if (dev.getName().equals(CONSTS.SERVER.BLUETOOTH_SERVER)){
				this.bd = dev;
				break;
			}
		}
		
	}
	
	private void getSensorValues(int time){
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					if (bd == null || ba == null){
						initBluetooth();
					}
					if (bd != null && ba != null){
						Log.d(CONSTS.TAG, "Ready to Connect Arduino dev");
						bs = bd.createRfcommSocketToServiceRecord(CONSTS.SERVER.Uuid);
						bs.connect();
						Log.d(CONSTS.TAG, "Conexao realizada");
						byte[] buffer = new byte[CONSTS.BUFFER_SIZE_BD];
						if (bs.getInputStream().read(buffer) != -1){
							String str = new String(buffer);
							values = str.split(CONSTS.TOKEN_DELIM_ARDUINO);
						}
						bs.close();
					}

				} catch (IOException e) {
					Log.d(CONSTS.TAG, "Failure to Connect Arduino Dev");
				}
			}
		}, 0, time);
	}
	
	@Override
	public boolean isSensorEnable() {
		return ba.isEnabled();
	}
	
	@Override
	public JSONObject inserValues(JSONObject jobj) {
		try {
			JSONArray array = new JSONArray();
			for (int i = 1; i < values.length; i++)
				array.put(values[i].trim());
			jobj.put(values[0], array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobj;
	}
	
	@Override
	public JSONObject insertFIWAREValues(JSONObject jobj) {
		try {
			jobj.put(CONSTS.FIWARE.TYPE, "arduino");
			jobj.put(CONSTS.FIWARE.IS_PATTERN, "false");
			jobj.put(CONSTS.FIWARE.ID, "1");
			JSONArray jarray = new JSONArray();
			jobj.put(CONSTS.FIWARE.ATTRIBUTES, jarray);			
			for (int i = 1; i < values.length; i++)
			{
				JSONObject leaf = new JSONObject();
				leaf.put(CONSTS.FIWARE.NAME, data[i]);
				leaf.put(CONSTS.FIWARE.TYPE, dataType[i]);
				leaf.put(CONSTS.FIWARE.VALUE, values[i].trim());
				jarray.put(leaf);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobj;
	}
	
	
	@Override
	public JSONArray insertFIWAREValues(JSONArray jobj) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setTime(int time) {
		t.cancel();
		getSensorValues(time);
	}
	
	@Override
	public void printDebug() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++)
			sb.append(values[i]).append(", ");
		Log.d(CONSTS.TAG, sb.toString());
	}
}
