
#include <SoftwareSerial.h> //Software Serial Port
#include <dht.h>
#include <avr/wdt.h>

#define RxD 0
#define TxD 1
#define Reset 5
#define PIO11 8
#define Led 13
#define RTS 4
#define DHT11_PIN A1
 
SoftwareSerial blueToothSerial(RxD,TxD);
dht DHT;

void setup()
{
 //Serial.begin(9600);
 pinMode(RxD, INPUT);
 pinMode(TxD, OUTPUT);
 pinMode(Led, OUTPUT);
 pinMode(PIO11, OUTPUT);
 digitalWrite(PIO11, HIGH);
 pinMode(Reset, OUTPUT);
 digitalWrite(Reset, LOW);
 pinMode(RTS, INPUT);
 wdt_enable(WDTO_8S);
 setupBlueToothConnection();
 
}
 
void loop()
{
 char recvChar;
 boolean rtsStatus = true;
 int chk; 
 //int cont = 0;
 while(1){
 /*blueToothSerial.print("a");
 delay(500);
 blueToothSerial.print("b");
 delay(500);*/
 
 wdt_enable(WDTO_4S);
 //Serial.print("DHT11, \t");
 blueToothSerial.print("DHT11, \t");
 chk = DHT.read11(DHT11_PIN);
  switch (chk)
  {
    case DHTLIB_OK:  
	        blueToothSerial.print("OK,\t"); 
               //Serial.print("OK,\t"); 
		break;
    case DHTLIB_ERROR_CHECKSUM: 
		//Serial.print("Checksum error,\t"); 
                 blueToothSerial.print("Checksum error,\t");
		break;
    case DHTLIB_ERROR_TIMEOUT: 
		//Serial.print("Time out error,\t");
                blueToothSerial.print("Time out error,\t");
		break;
    default: 
		//Serial.print("Unknown error,\t"); 
                blueToothSerial.print("Unknown error,\t");
		break;
  }
 // DISPLAY DATA
/* Serial.print(DHT.humidity, 1);
 Serial.print(",\t");
 Serial.println(DHT.temperature, 1);
 delay(1000);*/

 //cont++;
 blueToothSerial.print(DHT.humidity, 1);
 blueToothSerial.print(",\t");
 blueToothSerial.println(DHT.temperature,1);
 blueToothSerial.flush();
// blueToothSerial.println(",\t");
 //blueToothSerial.println(cont);
 delay(1000);
 wdt_reset();
 
 
/*
 if(blueToothSerial.available()){//check if there's any data sent from the remote bluetooth shield
 recvChar = blueToothSerial.read();
 if(recvChar == 'a'){
 digitalWrite(Led, HIGH);
 }
 if(recvChar == 'b'){
 digitalWrite(Led, LOW);
 }
 Serial.print(recvChar);
 }
 if(Serial.available()){//check if there's any data sent from the local serial terminal, you can add the other applications here
 recvChar = Serial.read();
 blueToothSerial.print(recvChar);
 }*/

/* if(digitalRead(RTS)){
 if(!rtsStatus){
 rtsStatus = true;
 Serial.print("rts changed true");
 }
 }else{
 if(rtsStatus){
 rtsStatus = false;
 Serial.print("rts changed false");
 }
 }*/
 }
}
 
void setupBlueToothConnection()
{
 enterATMode();
 sendATCommand();
 sendATCommand("UART=9600,0,0"); //57600
 sendATCommand("ROLE=1"); // define como modo master
 wdt_reset();
 wdt_enable(WDTO_8S);
 sendATCommand("NAME=MONITOR_ARDUINO_1");
 sendATCommand("PSWD=4242"); // define a senha do modo mestre 
 //sendATCommand("PAIR=0018,e4,0c680a"); // VERIFICAR!!!
 //sendATCommand("BIND=0018,e4,0c680a"); // endereço MAC
 sendATCommand("CMODE=1"); // CMODE = 1 permite conexao com qualquer endereço
 enterComMode();
 wdt_reset();

}
 
void resetBT()
{
 digitalWrite(Reset, LOW);
 delay(2000);
 digitalWrite(Reset, HIGH);
}
 
void enterComMode()
{
 blueToothSerial.flush();
 delay(500);
 digitalWrite(PIO11, LOW);
 resetBT();
 delay(500);
 blueToothSerial.begin(9600);//57600
}
 
void enterATMode()
{
 blueToothSerial.flush();
 delay(500);
 digitalWrite(PIO11, HIGH);
 resetBT();
 delay(500);
 blueToothSerial.begin(38400);
 
}
 
void sendATCommand(char *command)
{
 blueToothSerial.print("AT");
 if(strlen(command) > 1){
 blueToothSerial.print("+");
 blueToothSerial.print(command);
  delay(1000);//100
 }
 blueToothSerial.print("\r\n");
}
 
void sendATCommand()
{
 blueToothSerial.print("AT\r\n");
 delay(1000);//100
}

