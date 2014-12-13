/*
Sketch: Este programa lê o estado de um potenciometro para ascender ou apagar dois leds.
*/

const int led = 13;
const int Potenciomento = 5; // pino analogico

void setup() {
 pinMode(led, OUTPUT);
 Serial.begin(9600); // abre a porta serial
}

void loop() {

 int valorPotenciometro = 0; 
 float valorTensao = 0;
 
 while(1){
   
   valorPotenciometro=analogRead(Potenciomento);
   
 if(valorPotenciometro == 0){
  digitalWrite(led, LOW);
 }
 if(valorPotenciometro == 1023) {
  digitalWrite(led, HIGH);
 }

 if(Serial.available() == 0) {
   valorTensao=(float)valorPotenciometro*5/1023;
  Serial.print("Valor do Potenciometro: ");
  Serial.println(valorPotenciometro);
  Serial.print('\n');
  Serial.print("Valor de Tensao: ");
  Serial.println(valorTensao);
  Serial.print('\n');
  delay(2000);
 }
 }
}
