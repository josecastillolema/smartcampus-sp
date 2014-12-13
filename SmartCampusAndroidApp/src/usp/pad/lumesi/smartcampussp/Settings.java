package usp.pad.lumesi.smartcampussp;

public enum Settings {
	
	TIME("TIME"),
	BLUETOOTH_MANAGER("BLUETOOTH_STATUS"),
	GPS_MANAGER("GPS_STATUS"),
	INTERNET_MANAGER("INTERNET_STATUS"),
	ENABLE_EXTERNAL_SENSORS("ENABLE_EXTERNAL_SENSORS"),
	ENABLE_INTERNAL_SENSORS("ENABLE_INTERNAL_SENSORS");
	
	String setting;
	
	Settings(String setting){
		this.setting = setting;
		
	}
	
}
