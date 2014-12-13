package usp.pad.lumesi.smartcampussp.util;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class WriterJSON {
	private Timer timer;
	private int time;
	private ArduinoReader ardR;
	private AudioLevelReader alR;
	private GeoLocReader glR;
	private SensorsReader sR;
	//private JSONObject jsob;
	Context ctx;
	
	public WriterJSON(int time, ArduinoReader ardR, 
			AudioLevelReader alR, GeoLocReader glR, SensorsReader sR, Context ctx){
		this.time = time;
		this.ardR = ardR;
		this.alR = alR;
		this.glR = glR;
		this.sR = sR;	
		this.ctx = ctx;
		timer = new Timer();
		createThreadSender();
	}
	
	public void stop(){
		timer.cancel();
	}

	private void createThreadSender(){
		
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				try {
					JSONObject jsonMsg = createJSONFIWAREMsg();
					HttpResponse rsp = sendMessage(jsonMsg);
//					Log.d(CONSTS.TAG, "Status:  " + rsp.getStatusLine());
				} catch (JSONException e) {
					Log.d(CONSTS.TAG, "Status: Erro JSON  " );

					e.printStackTrace();
				} catch (ClientProtocolException e) {
					Log.d(CONSTS.TAG, "Status:  Erro ClientProtocol" );

					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					Log.d(CONSTS.TAG, "Status: Erro I/O");

					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

//			private JSONObject createJSONFIWARE() throws JSONException{
				
				
//			}
			
			
			private JSONObject createJSONMsg() throws JSONException{
				JSONObject jsonMsg = new JSONObject();
				JSONObject jPhoheChild = new JSONObject();
				JSONObject jSensorsChild = new JSONObject();
				glR.inserValues(jPhoheChild);
				sR.inserValues(jPhoheChild);
				alR.inserValues(jPhoheChild);
				ardR.inserValues(jSensorsChild);
				
				jsonMsg.put(CONSTS.PHONE_SENSOR, jPhoheChild);
				jsonMsg.put(CONSTS.ARDU_SENSOR, jSensorsChild);
				return jsonMsg;
			}
			
			
			private JSONObject createJSONFIWAREMsg() throws JSONException{
				JSONObject jsonMsg = new JSONObject();
				JSONArray jarray = new JSONArray();
				jsonMsg.put(CONSTS.FIWARE.CONTEXT_ELEMENTS, jarray);				
				jsonMsg.put(CONSTS.FIWARE.UPDATE_ACTION, CONSTS.FIWARE.ACTION_APPEND);
	

				if (ardR != null){
					// Create Arduino Node
					JSONObject jard = new JSONObject();
					ardR.insertFIWAREValues(jard);
					jarray.put(jard);
				}

				if (sR != null){
					//Create Smartphone Node
					JSONObject jsmart = new JSONObject();
					createHeaderJsonSmart(jsmart);
					JSONArray jaSmart = new JSONArray();
					alR.insertFIWAREValues(jaSmart);
					glR.insertFIWAREValues(jaSmart);
					sR.insertFIWAREValues(jaSmart);				
					jsmart.put(CONSTS.FIWARE.ATTRIBUTES, jaSmart);
					jarray.put(jsmart);
				}
				return jsonMsg;
			}

			private void createHeaderJsonSmart(JSONObject jo) throws JSONException{
				jo.put(CONSTS.FIWARE.TYPE, "smartphone");
				jo.put(CONSTS.FIWARE.IS_PATTERN, "false");
				TelephonyManager mngr = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE); 
				jo.put(CONSTS.FIWARE.ID,  mngr.getDeviceId());
			}
			
			private HttpResponse sendMessage(JSONObject jo) throws ClientProtocolException, IOException{
	//			Log.d(CONSTS.TAG, "Message sent:");
	//			Log.d(CONSTS.TAG, jo.toString());
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(CONSTS.SERVER.FIWARE_SERVER);
				StringEntity se = new StringEntity(jo.toString());
				se.setContentType("application/json;charset=UTF-8");
				se.setContentEncoding(new 
						BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
				post.setEntity(se);
				//return null;
				return client.execute(post);
			}
		}, 0, this.time);
	}
}
