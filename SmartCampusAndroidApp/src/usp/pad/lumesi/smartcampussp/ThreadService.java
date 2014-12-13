package usp.pad.lumesi.smartcampussp;

import usp.pad.lumesi.smartcampussp.util.ArduinoReader;
import usp.pad.lumesi.smartcampussp.util.AudioLevelReader;
import usp.pad.lumesi.smartcampussp.util.CONSTS;
import usp.pad.lumesi.smartcampussp.util.GeoLocReader;
import usp.pad.lumesi.smartcampussp.util.SensorsReader;
import usp.pad.lumesi.smartcampussp.util.WriterJSON;
import android.content.Context;
import android.content.Intent;

public class ThreadService{
	private Intent settings;
	private SensorsReader sR;
	private ArduinoReader aR;
	private AudioLevelReader alR;
	private GeoLocReader glR;
	private WriterJSON wj;

	public ThreadService(Intent settings, Context ctx) {
		this.settings = settings;
		int time = settings.getExtras().getInt(Settings.TIME.name(), CONSTS.DEFAULT_TIME_VALUE);
		if (settings.getExtras().getBoolean(Settings.ENABLE_EXTERNAL_SENSORS.name(), false))
			this.aR = new ArduinoReader(time);
		if (settings.getExtras().getBoolean(Settings.ENABLE_INTERNAL_SENSORS.name(), false)){
			this.sR = new SensorsReader(ctx, time);
			this.glR = new GeoLocReader(ctx, time);
			this.alR = new AudioLevelReader(time, CONSTS.AUDIO.FAST_MODE);
		}
		this.wj = new WriterJSON(time, aR, alR, glR, sR, ctx);  
	}

	public void stop(){
		if (settings.getExtras().getBoolean(Settings.ENABLE_EXTERNAL_SENSORS.name(), false))
			aR.stop();
		if (settings.getExtras().getBoolean(Settings.ENABLE_INTERNAL_SENSORS.name(), false)){
			alR.stop();
			sR.stop();
			glR.stop();
		}
		wj.stop();
	}
}
