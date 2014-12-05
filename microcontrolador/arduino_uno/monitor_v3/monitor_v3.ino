#include <Wire.h>
#include <AirQuality_v2.h>
#include <Adafruit_BMP085.h>
#include <SoftwareSerial.h> //Software Serial Port
#include <dht.h>
#include <avr/wdt.h>


#define RL_VALUE (5) //define the load resistance on the board, in kilo ohms
#define RO_CLEAN_AIR_FACTOR (9.83) //RO_CLEAR_AIR_FACTOR=(Sensor resistance in clean air)/RO,
//which is derived from the chart in datasheet
/***********************Software Related Macros**********************************/
#define CALIBARAION_SAMPLE_TIMES (50) //define how many samples you are going to take in the calibration phase
#define CALIBRATION_SAMPLE_INTERVAL (500) //define the time interal(in milisecond) between each samples in the
//cablibration phase
#define READ_SAMPLE_INTERVAL (50) //define how many samples you are going to take in normal operation
#define READ_SAMPLE_TIMES (5) //define the time interal(in milisecond) between each samples in
//normal operation
/**********************Application Related Macros*******************************/
#define GAS_LPG (0)
#define GAS_CO (1)
#define GAS_SMOKE (2)

float LPGCurve[3] = {2.3,0.21,-0.47}; //two points are taken from the curve.
//with these two points, a line is formed which is "approximately equivalent"
//to the original curve.
//data format:{ x, y, slope}; point1: (lg200, 0.21), point2: (lg10000, -0.59)
float COCurve[3] = {2.3,0.72,-0.34}; //two points are taken from the curve.
//with these two points, a line is formed which is "approximately equivalent"
//to the original curve.
//data format:{ x, y, slope}; point1: (lg200, 0.72), point2: (lg10000, 0.15)
float SmokeCurve[3] ={2.3,0.53,-0.44}; //two points are taken from the curve.
//with these two points, a line is formed which is "approximately equivalent"
//to the original curve.
//data format:{ x, y, slope}; point1: (lg200, 0.53), point2: (lg10000, -0.22)
float Ro = 10; //Ro is initialized to 10 kilo ohms

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
int ppmCO,ppmSmoke,ppmGLP;


void setup()
{
 pinMode(RxD, INPUT);
 pinMode(TxD, OUTPUT);
 pinMode(DHT11_PIN,INPUT);
 Ro = MQCalibration(Gas_PIN);
 bmp.begin();  
 airqualitysensor.init(Q_Ar_PIN);
 setupBlueToothConnection();
}
 

void loop()
{
 //umidade, temperatura, gás, qualidade de ar, pressão atmosférica, altitude,chuva

 //wdt_enable(WDTO_8S);
 
 current_quality=airqualitysensor.slope();

 chk = DHT.read11(DHT11_PIN);

 volumeChuva=analogRead(Chuva_PIN);
 
 if (volumeChuva<600){
    volumeChuva = 1;
 }else{ 
    volumeChuva = 0;
 
 }
 pressao = bmp.readPressure();
 altitude = bmp.readAltitude(101325); //101325 pressão atmosférica ao nível do mar
 
 
 ppmGLP = MQGetGasPercentage(MQRead(Gas_PIN)/Ro,GAS_LPG);
  if (ppmGLP<0) ppmGLP=0;

 ppmSmoke = MQGetGasPercentage(MQRead(Gas_PIN)/Ro,GAS_SMOKE);
  if (ppmSmoke<0) ppmSmoke=0;

 ppmCO = MQGetGasPercentage(MQRead(Gas_PIN)/Ro,GAS_CO);
  if (ppmCO<0)  ppmCO=0;
 
 
 blueToothSerial.print(DHT.humidity, 1);//umidade
 blueToothSerial.print(",\t");
 blueToothSerial.print(DHT.temperature,1);//temperatura
 blueToothSerial.flush();
 blueToothSerial.print(",\t");
 blueToothSerial.print(ppmGLP);
 blueToothSerial.print(",\t");
 blueToothSerial.print(ppmSmoke);
 blueToothSerial.print(",\t");
 blueToothSerial.print(ppmCO);
 blueToothSerial.print(",\t");
 blueToothSerial.print(current_quality);//qualidade do ar
 blueToothSerial.print(",\t");
 blueToothSerial.print(pressao);//pressao altmosferica Pa
 blueToothSerial.print(",\t");
 blueToothSerial.print(altitude);//altitude aproximada em metros
 blueToothSerial.print(",\t");
 blueToothSerial.println(volumeChuva);//verifica se há chuva
 //wdt_reset();
 delay(2000);//2 segundos

}
 
//********************Incio BlueTooth****************************// 
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
//******************* Fim Bluetooth***********************//

//******************Sensor qualidade de ar****************//
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
//***********************Fim Sensor Qaulidade de Ar************************


//************************ Inicio Funções MQ-2*****************************

float MQResistanceCalculation(int raw_adc)
{
return ( ((float)RL_VALUE*(1023-raw_adc)/raw_adc));
}
/***************************** MQCalibration ****************************************
Input: mq_pin - analog channel
Output: Ro of the sensor
Remarks: This function assumes that the sensor is in clean air. It use
MQResistanceCalculation to calculates the sensor resistance in clean air
and then divides it with RO_CLEAN_AIR_FACTOR. RO_CLEAN_AIR_FACTOR is about
10, which differs slightly between different sensors.
************************************************************************************/
float MQCalibration(int mq_pin)
{
int i;
float val=0;
for (i=0;i<CALIBARAION_SAMPLE_TIMES;i++) { //take multiple samples
val += MQResistanceCalculation(analogRead(mq_pin));
delay(CALIBRATION_SAMPLE_INTERVAL);
}
val = val/CALIBARAION_SAMPLE_TIMES; //calculate the average value

val = val/RO_CLEAN_AIR_FACTOR; //divided by RO_CLEAN_AIR_FACTOR yields the Ro
//according to the chart in the datasheet
return val;
}
/***************************** MQRead *********************************************
Input: mq_pin - analog channel
Output: Rs of the sensor
Remarks: This function use MQResistanceCalculation to caculate the sensor resistenc (Rs).
The Rs changes as the sensor is in the different consentration of the target
gas. The sample times and the time interval between samples could be configured
by changing the definition of the macros.
************************************************************************************/
float MQRead(int mq_pin)
{
int i;
float rs=0;
for (i=0;i<READ_SAMPLE_TIMES;i++) {
rs += MQResistanceCalculation(analogRead(mq_pin));
delay(READ_SAMPLE_INTERVAL);
}
rs = rs/READ_SAMPLE_TIMES;
return rs;
}
/***************************** MQGetGasPercentage **********************************
Input: rs_ro_ratio - Rs divided by Ro
gas_id - target gas type
Output: ppm of the target gas
Remarks: This function passes different curves to the MQGetPercentage function which
calculates the ppm (parts per million) of the target gas.
************************************************************************************/
int MQGetGasPercentage(float rs_ro_ratio, int gas_id)
{
if ( gas_id == GAS_LPG ) {
return MQGetPercentage(rs_ro_ratio,LPGCurve);
} else if ( gas_id == GAS_CO ) {
return MQGetPercentage(rs_ro_ratio,COCurve);
} else if ( gas_id == GAS_SMOKE ) {
return MQGetPercentage(rs_ro_ratio,SmokeCurve);
}
return 0;
}
/***************************** MQGetPercentage **********************************
Input: rs_ro_ratio - Rs divided by Ro
pcurve - pointer to the curve of the target gas
Output: ppm of the target gas
Remarks: By using the slope and a point of the line. The x(logarithmic value of ppm)
of the line could be derived if y(rs_ro_ratio) is provided. As it is a
logarithmic coordinate, power of 10 is used to convert the result to non-logarithmic
value.
************************************************************************************/
int MQGetPercentage(float rs_ro_ratio, float *pcurve)
{
return (pow(10, (((log(rs_ro_ratio)-pcurve[1])/pcurve[2]) + pcurve[0])));
}
