

//BMPDeclare
//#include <Wire.h>
//#include <SFE_BMP180.h>
//SFE_BMP180 bmp180;

// DHT11
#include "DHT.h" 
#define DHTPIN A1     
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

//MQ3 
#define         MQ_PIN                       (0)     
#define         RL_VALUE                     (10)    
#define         RO_CLEAN_AIR_FACTOR          (9.21)  
#define         CALIBARAION_SAMPLE_TIMES     (50)    
#define         CALIBRATION_SAMPLE_INTERVAL  (500)   
#define         READ_SAMPLE_INTERVAL         (50)    
#define         READ_SAMPLE_TIMES            (5)   
#define         GAS_H2                      (0) 
float           H2Curve[3]  =  {2.3, 0.93,-1.44};    
float           Ro           =  10;               

String mq3_ppm = "0";//ppm
String mq3_kohm = "0";//Kohm
String bmp_pressure = "0";//hPa
String bmp_temperature  = "0";//Celcius
String dht_humidity = "0";//Humidity
String dht_celcius = "0";//Celcius
String dht_fahrenheit = "0";//Fahrenheit
String dht_heatindex = "0";//Heat index
void setup() { 

  Serial.begin(9600);  
//  if(initbmp180()){
//    initdht11();
//    initmq3(); 
//  }  

  initdht11();
  initmq3(); 
}

void initmq3(){     
  Serial.print("=");   //calibrating               
  Ro = MQCalibration(MQ_PIN);    
  mq3_kohm = String(Ro,2);//ppm
}

//boolean initbmp180(){   
//  boolean ret = bmp180.begin();
////  if (ret) {
////    Serial.println("BMP180 init success");
////  }
//  return ret;
//}

void initdht11(){  
  dht.begin();
}
 
void loop() {
  //BMP
//  loopbmp180();
  loopdht11();
  loopmq3(); 

  String sendata =mq3_ppm+","+mq3_kohm+","+bmp_pressure+","+bmp_temperature+","+dht_humidity+","+dht_celcius+","+dht_fahrenheit+","+dht_heatindex+",-";
  Serial.println(sendata);
}

//void loopbmp180(){   
//  char status;
//  double T, P;  
//  status = bmp180.startTemperature();
//  if (status != 0) {
//    delay(status); 
//    status = bmp180.getTemperature(T);
//    if (status != 0) { 
//      status = bmp180.startPressure(3);
//      if (status != 0) {
//        delay(status); 
//        status = bmp180.getPressure(P, T);
//        if (status != 0) { 
//          bmp_pressure = String(P,2);//" hPa"
//          bmp_temperature = String(T,2); //Celcius
//        }
//      }
//    } 
//  }
//}

void loopdht11(){ 
  float h = dht.readHumidity(); 
  float t = dht.readTemperature();  
  float f = dht.readTemperature(true); 
  if (isnan(h) || isnan(t) || isnan(f)) {
    Serial.println("Failed to read from DHT sensor!");
    return;
  }  
  float hi = dht.computeHeatIndex(f, h); 
 
  dht_humidity = String(h,2);//Humidity
  dht_celcius = String(t,2);//Celcius
  dht_fahrenheit = String(f,2);//Fahrenheit
  dht_heatindex = String(hi,2);//Heat index 
}

void loopmq3(){
   int ret = MQGetGasPercentage(MQRead(MQ_PIN)/Ro,GAS_H2);//ppm
   mq3_ppm = String(ret);//ppm  
}




////////////////////////////////////////////////////////////////////////////////////////////////-------------------------------
float MQResistanceCalculation(int raw_adc) {
  return ( ((float)RL_VALUE*(1023-raw_adc)/raw_adc));
} 

float MQCalibration(int mq_pin) {
  int i;
  float val=0; 
  for (i=0;i<CALIBARAION_SAMPLE_TIMES;i++) {           
    val += MQResistanceCalculation(analogRead(mq_pin));
    delay(CALIBRATION_SAMPLE_INTERVAL);
  }
  val = val/CALIBARAION_SAMPLE_TIMES;                  
  val = val/RO_CLEAN_AIR_FACTOR;                      
  return val; 
}

float MQRead(int mq_pin) {
  int i;
  float rs=0;
 
  for (i=0;i<READ_SAMPLE_TIMES;i++) {
    rs += MQResistanceCalculation(analogRead(mq_pin));
    delay(READ_SAMPLE_INTERVAL);
  }
 
  rs = rs/READ_SAMPLE_TIMES;
 
  return rs;  
}

int MQGetGasPercentage(float rs_ro_ratio, int gas_id) {
  if ( gas_id == GAS_H2) {
     return MQGetPercentage(rs_ro_ratio,H2Curve);
  }  
  return 0;
} 

int  MQGetPercentage(float rs_ro_ratio, float *pcurve) {
  return (pow(10,( ((log(rs_ro_ratio)-pcurve[1])/pcurve[2]) + pcurve[0])));
}
