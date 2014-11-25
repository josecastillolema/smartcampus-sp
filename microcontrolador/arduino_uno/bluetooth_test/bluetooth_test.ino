#include <SoftwareSerial.h> //Software Serial Port

#define RxD 0 //ok
#define TxD 1 //ok
#define Reset 5 
#define PIO11 8
#define Led 13 //ok
#define RTS 4

 
SoftwareSerial blueToothSerial(RxD,TxD);


void setup()
{
 Serial.begin(9600);
 pinMode(RxD, INPUT);
 pinMode(TxD, OUTPUT);
 pinMode(Led, OUTPUT);
 pinMode(PIO11, OUTPUT);
 digitalWrite(PIO11, HIGH);
 pinMode(Reset, OUTPUT);
 digitalWrite(Reset, LOW);
 pinMode(RTS, INPUT);
 setupBlueToothConnection();
}
 
void loop()
{
 char recvChar;
 boolean rtsStatus = true;
 
 while(1){
 blueToothSerial.println("LSI_USP_PAD");
 Serial.println("LSI_USP_PAD");
 delay(500);
 blueToothSerial.print("2014");
 Serial.println("2014");
 delay(500);
 

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
 }
 if(digitalRead(RTS)){
 if(!rtsStatus){
 rtsStatus = true;
 Serial.print("rts changed true");
 }
 }else{
 if(rtsStatus){
 rtsStatus = false;
 Serial.print("rts changed false");
 }
 }
 }
}
 
void setupBlueToothConnection()
{
 enterATMode();
 sendATCommand();
 sendATCommand("UART=57600,0,0"); //57600
 sendATCommand("ROLE=1"); // define como modo master
 sendATCommand("NAME=ARDUINO_MONITOR");
 sendATCommand("PSWD=4242"); // define a senha do modo mestre 
 sendATCommand("PAIR=0018,e4,0c680a"); // VERIFICAR!!!
 sendATCommand("BIND=0018,e4,0c680a"); // endereço MAC
 sendATCommand("CMODE=1"); // CMODE = 1 permite conexao com qualquer endereço
 enterComMode();
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
 blueToothSerial.begin(57600);//57600
}
 
void enterATMode()
{
 blueToothSerial.flush();
 delay(500);
 digitalWrite(PIO11, HIGH);
 resetBT();
 delay(500);
 blueToothSerial.begin(38400);//38400
 
}
 
void sendATCommand(char *command)
{
 blueToothSerial.print("AT");
 if(strlen(command) > 1){
 blueToothSerial.print("+");
 blueToothSerial.print(command);
 delay(1000);
 }
 blueToothSerial.print("\r\n");
}
 
void sendATCommand()
{
 blueToothSerial.print("AT\r\n");
 delay(100);
}

