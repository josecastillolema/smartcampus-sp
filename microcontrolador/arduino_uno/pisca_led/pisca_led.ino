

const int ledPin =  13;      

void setup() {
  // initialize the LED pin as an output:
  pinMode(ledPin, OUTPUT);      
     
}

void loop(){
    digitalWrite(ledPin, HIGH);  
    delay(5000);   
    digitalWrite(ledPin, LOW); 
    delay(1000); 
  }
