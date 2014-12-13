#include <Wire.h>
#include <AirQuality_v2.h>
#include <Adafruit_BMP085.h>
#include <SoftwareSerial.h> //Software Serial Port
#include <dht.h>
#include <avr/wdt.h>

#define RxD 0
#define TxD 1

#define DHT11_PIN 2 
#define Q_Ar_PIN A0
#define Gas_PIN A1
#define Audio_PIN A2
#define Chuva_PIN A3

 
SoftwareSerial blueToothSerial(RxD,TxD);
dht DHT;
Adafruit_BMP085 bmp;
int volumeGas;
int volumeAudio;
int volumeChuva;
AirQuality_v2 airqualitysensor;
int current_quality =-1;
int chk;
int pressao, altitude;

void setup()
{
 bmp.begin();  
 pinMode(RxD, INPUT);
 pinMode(TxD, OUTPUT);
 pinMode(DHT11_PIN,INPUT);
 airqualitysensor.init(Q_Ar_PIN);
 setupBlueToothConnection();
}
 

void loop()
{
 //umidade, temperatura, gás, qualidade de ar, pressão atmosférica, altitude,chuva

 //wdt_enable(WDTO_8S);
 
 current_quality=airqualitysensor.slope();

 chk = DHT.read11(DHT11_PIN);
 volumeGas=analogRead(Gas_PIN);
 //volumeAudio=analogRead(Audio_PIN);
 volumeChuva=analogRead(Chuva_PIN);
 
  if (volumeGas>100){
   volumeGas = 1;
  }else{ 
    volumeGas=0;
  }
 
 
 if (volumeChuva<600){
    volumeChuva = 1;
 }else{ 
    volumeChuva = 0;
 }
 pressao = bmp.readPressure();
 altitude = bmp.readAltitude(101325); //101325 pressão atmosférica ao nível do mar

 blueToothSerial.print(DHT.humidity, 1);//umidade
 blueToothSerial.print(",\t");
 blueToothSerial.print(DHT.temperature,1);//temperatura
 blueToothSerial.flush();
 blueToothSerial.print(",\t");
 blueToothSerial.print(volumeGas);//verifica se há gás explosivo ou fumaça
 //blueToothSerial.print(",\t");
 //blueToothSerial.print(volumeAudio);
 blueToothSerial.print(",\t");
 blueToothSerial.print(current_quality);//qualidade do ar
 blueToothSerial.print(",\t");
 blueToothSerial.print(pressao);//pressao altmosferica Pa
 blueToothSerial.print(",\t");
 blueToothSerial.print(altitude);//altitude aproximada em metros
 blueToothSerial.print(",\t");
 blueToothSerial.println(volumeChuva);//verifica se há chuva
 //wdt_reset();
 delay(60000);//1 minuto

}
 
void setupBlueToothConnection()
{
 enterATMode();
 sendATCommand();
 sendATCommand("UART=9600,0,0"); //57600
 sendATCommand("ROLE=1"); // define como modo master
 sendATCommand("NAME=MONITOR_ARDUINO_1");
 sendATCommand("PSWD=4242"); // define a senha do modo mestre 
 //sendATCommand("PAIR=0018,e4,0c680a"); // VERIFICAR!!!
 //sendATCommand("BIND=0018,e4,0c680a"); // endereço MAC
 sendATCommand("CMODE=1"); // CMODE = 1 permite conexao com qualquer endereço
 enterComMode();
 

}
 
/*void resetBT()
{
 digitalWrite(Reset, LOW);
 delay(2000);
 digitalWrite(Reset, HIGH);
}*/
 
void enterComMode()
{
 blueToothSerial.flush();
 delay(500);
// digitalWrite(PIO11, LOW);
// resetBT();
 delay(500);
 blueToothSerial.begin(9600);//57600
}
 
void enterATMode()
{
 blueToothSerial.flush();
 delay(500);
 //digitalWrite(PIO11, HIGH);
 //resetBT();
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

ISR(TIMER2_OVF_vect)
{
    if(airqualitysensor.counter==122)//set 2 seconds as a detected duty
    {
        airqualitysensor.last_vol=airqualitysensor.first_vol;
        airqualitysensor.first_vol=analogRead(A0);
        airqualitysensor.counter=0;
        airqualitysensor.timer_index=1;
        PORTB=PORTB^0x20;
    }
    else
    {
        airqualitysensor.counter++;
    }
}
