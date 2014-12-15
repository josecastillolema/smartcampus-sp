package usp.pad.lumesi.smartcampussp;

import java.util.ArrayList;
import java.util.Set;

import usp.pad.lumesi.smartcampussp.util.CONSTS;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SmartCampusSpAct extends ActionBarActivity {
	// Private Members
	private EditText edtTime;
	private CheckBox chkES;
	private CheckBox chkIS;
	private Button btnSettings;
	private Button btnStop;
	private BluetoothAdapter ba;

	private ArrayList<String> lstBlue = new ArrayList<String>();
	private ArrayAdapter<String> adapBlue;
	private ListView lvBlue;
	
	private Intent intent;
	
	 public static final String PREFS_NAME = "PrefFile";

	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		
		super.onSaveInstanceState(outState);
		outState.putSerializable(Settings.TIME.name(), edtTime.toString());
		outState.putSerializable(Settings.ENABLE_EXTERNAL_SENSORS.name(), chkES.isChecked());
		outState.putSerializable(Settings.ENABLE_INTERNAL_SENSORS.name(), chkIS.isChecked());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		

		//default init calls
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		intent = new Intent("br.usp.lumesi.INIT_SMART");
		getInterfaceElementsReferences();
		recreateUI(savedInstanceState);
		createListeners();
		initBluetooth();
		getPreferences();
	}
	
	/**
	 * Get interface elements references
	 */
	private void getInterfaceElementsReferences(){
		this.edtTime = (EditText) findViewById(R.id.edtTime);
		this.chkES = (CheckBox) findViewById(R.id.chkES);
		this.chkIS = (CheckBox) findViewById(R.id.chkIS);
		this.btnSettings = (Button) findViewById(R.id.btnSettings);
		this.btnStop = (Button) findViewById(R.id.btnStop);
		this.lvBlue = (ListView) findViewById(R.id.lvArd);
		adapBlue = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, lstBlue);
		this.lvBlue.setAdapter(adapBlue);
	}
	
	private void recreateUI(Bundle bun){ 
		if (bun != null){
			edtTime.setText(bun.getString(Settings.TIME.name()));
			chkES.setChecked(bun.getBoolean(Settings.ENABLE_EXTERNAL_SENSORS.name()));
			chkIS.setChecked(bun.getBoolean(Settings.ENABLE_INTERNAL_SENSORS.name()));
		}
	}
	
	private void createListeners(){
		btnSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					String time = edtTime.getText().toString();
					intent.putExtra(Settings.TIME.name(), time.length()==0?CONSTS.DEFAULT_TIME_VALUE:Integer.parseInt(time)*1000);
					intent.putExtra(Settings.ENABLE_EXTERNAL_SENSORS.name(), chkES.isChecked());
					intent.putExtra(Settings.ENABLE_INTERNAL_SENSORS.name(), chkIS.isChecked());
					int pos = lvBlue.getCheckedItemPosition();
					CONSTS.SERVER.BLUETOOTH_SERVER = lvBlue.getItemAtPosition(pos).toString();
					startService(intent);
					finish();
				} catch (NullPointerException npe){
					print("Click on device name and then click on start service");
				}
			}
		});
		
		btnStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				print("Stop Service");
				stopService(intent);
			}
		});
	}
	
	public void initBluetooth(){
		this.ba = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> devices = ba.getBondedDevices();
		for (BluetoothDevice dev : devices){
			lstBlue.add(dev.getName());
		}
	}

	private void getPreferences(){
		// Restore preferences
	    SharedPreferences sets = getSharedPreferences(PREFS_NAME, 0);
	    this.chkES.setChecked(sets.getBoolean(Settings.ENABLE_EXTERNAL_SENSORS.name(), true));
	    this.chkIS.setChecked(sets.getBoolean(Settings.ENABLE_INTERNAL_SENSORS.name(), true));
	    this.edtTime.setText(sets.getString(Settings.TIME.name(), "5"));
	    this.lvBlue.setSelection(sets.getInt(Settings.BLUETOOTH_MANAGER.name(),  0));
	}
	
	private void setPreferences(){
	      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putBoolean(Settings.ENABLE_EXTERNAL_SENSORS.name(),chkES.isChecked());
	      editor.putBoolean(Settings.ENABLE_INTERNAL_SENSORS.name(),chkIS.isChecked());
	      editor.putString(Settings.TIME.name(),edtTime.getText().toString());
	      editor.putInt(Settings.BLUETOOTH_MANAGER.name(), lvBlue.getCheckedItemPosition());
	      editor.commit();
	}
	
	private void print(String str){
		Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
	}
	
	//Generated by Android IDE
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		setPreferences();
		super.onDestroy();
		
	}
}