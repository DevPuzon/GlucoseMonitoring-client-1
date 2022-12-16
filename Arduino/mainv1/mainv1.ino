 

int lightPin = 12;
const unsigned long eventInterval = 1000;
unsigned long previousTime = 0;
bool isBlink = true;
bool isCalibrated = false; 
float BGL;
float Volt;
void setup() { 
  Serial.begin(9600);
  pinMode(lightPin, OUTPUT);
  digitalWrite(lightPin, LOW);  
}

// the loop routine runs over and over again forever:
void loop() { 
  unsigned long currentTime = millis(); 
  if(!isCalibrated){ 
    if (currentTime - previousTime >= eventInterval) {  
      previousTime = currentTime;
      if(isBlink){
        digitalWrite(lightPin, HIGH);  
      }else{
        digitalWrite(lightPin, LOW);  
      }
      isBlink = !isBlink;
      if(Volt < 1){
        isCalibrated = true; 
      }
    }
  }else{
    digitalWrite(lightPin, LOW);  
  }
  
  // read the input on analog pin 0:
  int sensorValue = analogRead(A0);
  // print out the value you read:
  Volt = sensorValue*5.0/1024.0;
//  Serial.print("Sensor Reading: ");
//  Serial.print(Volt);
//  Serial.print("V");
//  float BGL = (158.12 * Volt) - 519.35;
  BGL = (91.38 * Volt) + 6.3743;
//  Serial.print("\t\tBGL: ");
//  Serial.print(BGL);
//  Serial.print("mg/dl");
//  Serial.println();
  
  String sendata =String(BGL,2)+","+String(Volt,2)+","+String(isCalibrated)+",-";
  //  String sendata =String(BGL,2)+","+String(Volt,2)+",-";
  Serial.println(sendata);
  delay(1000);        // delay in between reads for stability
}
