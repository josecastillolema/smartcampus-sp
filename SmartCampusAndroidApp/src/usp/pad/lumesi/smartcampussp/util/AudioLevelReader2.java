package usp.pad.lumesi.smartcampussp.util;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioLevelReader2 implements IReader {
	private Timer timer;
	private int BUFFSIZE = 0;
	private int mCaliberationValue = CONSTS.AUDIO.CALIB_DEFAULT;
	private double mMaxValue = 0.0;
	private double dbValue = 0.0;
	private int LOGLIMIT = 50;
	private String mode;
	AudioRecord mRecordInstance = null;
	AudioLevelTask alt;
	
	public AudioLevelReader2(int time, String mode) {
		alt = new AudioLevelTask();
		timer = new Timer();
		setMode(mode);
		createRecordInstance();
		timer.schedule(alt, 0, time);
	}

	private void createRecordInstance(){
		mRecordInstance = new AudioRecord(
				MediaRecorder.AudioSource.MIC,
				CONSTS.AUDIO.FREQUENCY, CONSTS.AUDIO.CHANNEL, 
				CONSTS.AUDIO.ENCODING, BUFFSIZE*2);
//		Log.d(CONSTS.TAG, "AUDIO BLOCK SIZE *******: " + BUFFSIZE);
		
	}
	
	@Override
	public JSONObject inserValues(JSONObject jobj) {
		try {
			jobj.put(CONSTS.SOUND, dbValue);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobj;
	}
	
	
	@Override
	public JSONArray insertFIWAREValues(JSONArray jarray) {
		JSONObject leaf = new JSONObject();
		try {					
			leaf = new JSONObject();
			leaf.put(CONSTS.FIWARE.NAME, "audio level");
			leaf.put(CONSTS.FIWARE.TYPE, "db");
			leaf.put(CONSTS.FIWARE.VALUE, dbValue);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jarray.put(leaf);
		return jarray;
	}
	
	@Override
	public JSONObject insertFIWAREValues(JSONObject jobj) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isSensorEnable() {
		return true;
	}
	
	@Override
	public void printDebug() {
		Log.d(CONSTS.TAG, CONSTS.SOUND + ":" + dbValue);
	}
	
	
	@Override
	public void setTime(int time) {
		timer.cancel();
		timer.schedule(alt, 0, time);
	}
	
	
	public class AudioLevelTask extends TimerTask {
		/**
		 * The main thread. Records audio and calculates the SPL The heart of the
		 * Engine.
		 */
		public void run() {
			try {
				if (mRecordInstance == null)
					createRecordInstance();
				mRecordInstance.startRecording();
				double splValue = Short.MIN_VALUE;
				int SIZE = BUFFSIZE;
				short[] tempBuffer = new short[SIZE];
				mRecordInstance.read(tempBuffer, 0, SIZE);
				Log.d(CONSTS.TAG, splValue + "****SOUND LEVEL INIT");
				for (int i = 0; i < tempBuffer.length; i++) {
					if (tempBuffer[i] > splValue){
						splValue = tempBuffer[i];
					}
				}
				if (splValue == 0.0)
						splValue = 1; 
				Log.d(CONSTS.TAG, splValue + "****SOUND LEVEL END");
				splValue = 20 * Math.log10(splValue / Short.MAX_VALUE) - mCaliberationValue;
				dbValue = splValue;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(mRecordInstance != null){
				mRecordInstance.stop();
				mRecordInstance.release();
				mRecordInstance = null;
			}
		}
	}
	
	
	/**
	 * sets mode as fast or slow	 
	 * @param mode
	 */
	public void setMode(String mode) {
		this.mode = mode;
		if (CONSTS.AUDIO.SLOW_MODE.equals(mode)) {
			BUFFSIZE = AudioRecord.getMinBufferSize(
					CONSTS.AUDIO.FREQUENCY, 
					CONSTS.AUDIO.CHANNEL, 
					CONSTS.AUDIO.ENCODING)*30;			
			LOGLIMIT = 10;
		} else {
			BUFFSIZE = AudioRecord.getMinBufferSize(
					CONSTS.AUDIO.FREQUENCY, 
					CONSTS.AUDIO.CHANNEL, 
					CONSTS.AUDIO.ENCODING)*2;			
			LOGLIMIT = 50;
		}
	}
	
	public double round(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	
	@Override
	public void stop() {
		timer.cancel();	
	}
}
