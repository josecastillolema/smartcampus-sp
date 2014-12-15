package usp.pad.lumesi.smartcampussp.util;

import java.util.UUID;

import android.content.Context;
import android.location.LocationManager;
import android.media.AudioFormat;

public interface CONSTS {

	public static final int DEFAULT_TIME_VALUE = 5000;
	public static final String TAG = "[SMARTCAMPUSSP]";
	public static final long INTERVAL_SENSORS = 200;
	public static final int INTERVAL_BLUE = 2000;
	public static final int GPS_UPDT = 1000;
	public static final int GPS_DIST = 1;
	public static final int BUFFER_SIZE_BD = 100;
	public static final float MILI_TO_NANO = 1000000;
	// String JSon
	
	public static final String SOUND = "sound";
	public static final String PHONE_SENSOR = "smartphone";
	public static final String ARDU_SENSOR = "arduino";
	public static final String OTHER_SENSOR = "OTHER_SENSOR";	
	public static final String DATE_TIME = "Date/Time";
	
	
	public static final String TOKEN_DELIM_ARDUINO = ",";
	

	public class SERVER{
		//UUID for Arduino
		public static final UUID Uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		public static String FIWARE_SERVER = "http://130.206.85.233:1026/v1/updateContext";
		public static String BLUETOOTH_SERVER = "HC-05";
		public static String TEMP_HUM = "HC-05";
	}

	public static final String LOC_PROVIDER = LocationManager.NETWORK_PROVIDER;
	public static final String LOC_SERVICE = Context.LOCATION_SERVICE;
	
	
	
	
	// Audio Constants
	public interface AUDIO{
		public static final int FREQUENCY = 44100;
		//public static final int FREQUENCY = 16000;
		public static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
		public static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
		public static final double P0 = 0.000002;	
		public static final double P1 = 0.000002;	
		public static final int CALIB_DEFAULT = -80;
		public static final String SLOW_MODE = "SLOW";
		public static final String FAST_MODE = "FAST";
		public static final double EMA_FILTER = 0.6;
	}

	
	
	public interface PHONE_SENSOR_FIELDS{
		public static final String LOC = "localization";
		public static final String SPEED = "speed";
		public static final String HEIGHT = "height";
	}
	
	public interface FIWARE{
		String CONTEXT_ELEMENTS = "contextElements";
		String TYPE = "type";
		String IS_PATTERN = "isPattern";
		String ID = "id";
		String ATTRIBUTES = "attributes";
		String NAME = "name";
		String VALUE = "value";
		String UPDATE_ACTION = "updateAction";
		String ACTION_APPEND = "APPEND";
	}	
}
