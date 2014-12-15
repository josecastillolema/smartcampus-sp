package usp.pad.lumesi.smartcampussp;

import usp.pad.lumesi.smartcampussp.util.CONSTS;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SmartService extends Service {	
	private int timeInterval;
	private ThreadService ts;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void onCreate() {
		
	    // Create an Intent that will open the main Activity
	    // if the notification is clicked.
		int NOTIFICATION_ID = 1;
		Intent intent = new Intent(this, SmartCampusSpAct.class);
	    PendingIntent pi = PendingIntent.getActivity(this, 1, intent, 0);
	    Intent stopIntent = new Intent(this, SmartService.class);
		stopIntent.setAction("ups.pad.lumesi.STOP");
		PendingIntent pstopIntent = PendingIntent.getService(this, 0,
				stopIntent, 0);
	    // Set the Notification UI parameters
	    Notification notification = new Notification.Builder(this)
	    .setContentTitle("SmartCampusSp")
	    .setTicker("SmartCampusSp")
	    .setContentText("SmartService")
	    .setContentIntent(pi)
	    .setOngoing(false)
	    .addAction(android.R.drawable.ic_media_pause, "Stop Service", pstopIntent)  
	    .setSmallIcon(R.drawable.ic_launcher).build();
	    
	    notification.flags = notification.flags |
                		Notification.FLAG_ONGOING_EVENT;

	    // Move the Service to the Foreground
	    startForeground(NOTIFICATION_ID, notification);
	}

	
	@Override
	/**
	 * Start SmartService. Use intent to configure service.
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		configService(intent);
		Toast.makeText(getBaseContext(), "Service running", Toast.LENGTH_SHORT).show();
		return START_STICKY;
	}
	
	
	private void configService(Intent intent){
		if (intent == null){
			ts = new ThreadService(getDefaultIntent(), getBaseContext());
		} else if (intent.getAction().equals("ups.pad.lumesi.STOP")){
			ts.stop();
			stopSelf();
		} else{
			Log.d(CONSTS.TAG, CONSTS.SERVER.BLUETOOTH_SERVER);
			timeInterval = intent.getExtras().getInt(Settings.TIME.name(), CONSTS.DEFAULT_TIME_VALUE);
			ts = new ThreadService(intent, getBaseContext());
		}

	} 
	
	private Intent getDefaultIntent(){
		Intent intent = new Intent();
		intent.putExtra(Settings.TIME.name(), CONSTS.DEFAULT_TIME_VALUE);
		intent.putExtra(Settings.BLUETOOTH_MANAGER.name(), true);
		intent.putExtra(Settings.GPS_MANAGER.name(), true);
		intent.putExtra(Settings.ENABLE_EXTERNAL_SENSORS.name(), true);
		intent.putExtra(Settings.ENABLE_INTERNAL_SENSORS.name(), true);
		return intent;
	}
	
//	private void print(String str){
//		Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
//	}

	@Override
	public void onDestroy() {
		ts.stop();
		stopForeground(true);
		stopSelf();
		super.onDestroy();
	}
}
