int led = 13;
int threshold = 500; //Change This
int volume;
 
void setup() {                
  Serial.begin(9600); // For debugging
  pinMode(led, OUTPUT);     
}
 
void loop() {
  
  volume = analogRead(A0); // Reads the value from the Analog PIN A0
  /*
    //Debug mode
    Serial.println(volume);
    delay(100);
  */
  
 Serial.println(volume);
  
  if(volume<=threshold){
    digitalWrite(led, HIGH); //Turn ON Led
  }  
  else{
    digitalWrite(led, LOW); // Turn OFF Led
  }
  
  delay(200);
 
}
