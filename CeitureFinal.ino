//global variables 
String lats;
String logs;
String cardiact;
String shockvaleur;
String macAdress;

bool mlf=false;

//HeartBeatVariables
long instance1=0, timer;
int hrv =0, hr = 72, interval = 0;
int value = 0, count = 0;  
bool flag = 0;
bool statee;

#define shutdown_pin 14 
#define timer_value 10000 // 10 seconds timer to calculate hr
#define thresholdd 100 // to identify R peak
#define analogic 18 // to identify R peak


#define BUTTON_PIN     21 // GIOP21 pin connected to button
#define DEBOUNCE_TIME  50 // the debounce time in millisecond, increase this time if it still chatters

// Variables will change:
unsigned long lastDebounceTime = 0;  // the last time the output pin was toggled
int lastSteadyState = LOW;       // the previous steady state from the input pin
int lastFlickerableState = LOW;  // the previous flickerable state from the input pin
int currentState;                // the current reading from the input pin
bool gst;
bool allow;
//Choc sensor configuration
bool sx=0,lsx=0;
const int chocvalue = 32;     // the number of the pushbutton pin


const int buttonPin1 = 34;     // the number of the pushbutton pin
#define DEEP_SLEEP_TIME 0.2
//End of choc sensor configuration
// Implemented libraries in this project
#include <TinyGPSPlus.h>  
#include <SoftwareSerial.h> 
#include <Wire.h>
#include "PubSubClient.h" // Connect and publish to the MQTT broker
#include "WiFi.h" // Enables the ESP32 to connect to the local network (via WiFi)
//In and out configuration
static const int RXPin = 16, TXPin = 17;
static const uint32_t GPSBaud = 9600;
#define Btn1_GPIO 22
#define stopbutton 23
// The TinyGPS++ object
TinyGPSPlus gps;
// The serial connection to the GPS device
SoftwareSerial ss(RXPin, TXPin);
// WiFi
const char* ssid = "Airbox-1E7E";// Your personal network SSID
const char* wifi_password = "02005324"; 
const char* mqtt_server = "192.168.1.178";  // IP of the MQTT broker
const char* lspi_topic = "lspi";
const char* mqtt_username = "pi"; // MQTT username
const char* mqtt_password = "root"; // MQTT password
const char* clientID = "grimpeur"; // MQTT client ID
// Initialise the WiFi and MQTT Client objects
WiFiClient wifiClient;
// 1883 is the listener port for the Broker
PubSubClient client(mqtt_server, 1883, wifiClient); 

void IRAM_ATTR Ext_INT1_ISR()
{
  esp_sleep_enable_timer_wakeup(DEEP_SLEEP_TIME * 60 * 1000000);
  esp_light_sleep_start(); 
  Serial.println("x");
}

// Custom function to connet to the MQTT broker via WiFi
void connect_MQTT(){
  Serial.print("Connecting to ");
  Serial.println(ssid);
  // Connect to the WiFi
  WiFi.begin(ssid, wifi_password);
  // Wait until the connection has been confirmed before continuing
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  // Debugging - Output the IP Address of the ESP8266
  Serial.println("WiFi connected");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  // Connect to MQTT Broker
  // client.connect returns a boolean value to let us know if the connection was successful.
  // If the connection is failing, make sure you are using the correct MQTT Username and Password (Setup Earlier in the Instructable)
  if (client.connect(clientID, mqtt_username, mqtt_password)) {
    Serial.println("Connected to MQTT Broker!");
  }
  else {
    Serial.println("Connection to MQTT Broker failed...");
  }

}

bool valuee(){
  currentState = digitalRead(BUTTON_PIN);

  // check to see if you just pressed the button
  // (i.e. the input went from LOW to HIGH), and you've waited long enough
  // since the last press to ignore any noise:

  // If the switch/button changed, due to noise or pressing:
  if (currentState != lastFlickerableState) {
    // reset the debouncing timer
    lastDebounceTime = millis();
    // save the the last flickerable state
    lastFlickerableState = currentState;
  }

  if ((millis() - lastDebounceTime) > DEBOUNCE_TIME) {
    // whatever the reading is at, it's been there for longer than the debounce
    // delay, so take it as the actual current state:

    // if the button state has changed:
       if(lastSteadyState == LOW && currentState == HIGH){
      //Serial.println("The button is released");
      gst=!gst;
      }

    // save the the last steady state
    lastSteadyState = currentState;
  }
  return gst;

}  

void IRAM_ATTR semtex(){
  valuee();
}

void setup ()
{ 
//attachInterrupt(BUTTON_PIN, semtex ,CHANGE);
Serial.begin(9600);

//Special serial so transfer dosen't bother
ss.begin(GPSBaud);
//GPIOS CONFIGURATIONS
pinMode(stopbutton, INPUT);
//pinMode(buttonPin1, INPUT_PULLUP);
pinMode(analogic, INPUT);
pinMode(BUTTON_PIN, INPUT_PULLUP);


pinMode(Btn1_GPIO, INPUT_PULLDOWN);
attachInterrupt(Btn1_GPIO, Ext_INT1_ISR, RISING);
pinMode(4, INPUT); // Setup for leads off detection LO +
pinMode(5, INPUT); // Setup for leads off detection LO -
//MQTT connection set
connect_MQTT();
}


//bool valuee(){
//      //ON
//      bool buttonState = digitalRead(buttonPin);
//      //OFF
//      bool buttonState1 = digitalRead(buttonPin1);
//      if (!sx && buttonState == HIGH) {
//                sx=true;
//                return true;
//      }
//      if (sx && buttonState1 == HIGH) {
//                sx=false;
//                return false;
//      }
//}

//bool valuee(){
//      //ONOFF
//     lsx = sx;      // save the last state
//     sx = digitalRead(buttonPin);   // read new state
//     if (lsx == HIGH && sx == LOW) {
//        Serial.println("ON");
//        return true;
//     }
//     else {
//      Serial.println("OFF");
//      return false;
//     }
//    
//}


void loop ()

{
   allow =false;
   if(valuee()){
    allow = true;
   if(0/*(digitalRead(4) == 1)||(digitalRead(0) == 1)*/){
    hrv=0;
    digitalWrite(shutdown_pin, LOW); //standby mode
    instance1 = micros();
    timer = millis();
  }
  else {
    digitalWrite(shutdown_pin, HIGH); //normal mode
    value = analogRead(analogic);
    value = map(value, 250, 400, 0, 100); //to flatten the ecg values a bit
    if((value > thresholdd) && (!flag)) {
      count++;  
      flag = 1;
      interval = micros() - instance1; //RR interval
      instance1 = micros(); 
    }
    else if((value < thresholdd)) {
      flag = 0;
    }
    if ((millis() - timer) > 10000) {
      hr = count*6;
      timer = millis();
      count = 0; 
    }
    //hrv = hr/60 - interval/1000000;
    hrv=random(75,99);
  }
    cardiact=String(hrv);
    int readchoc=analogRead(chocvalue);
    shockvaleur= String (readchoc);

    macAdress=WiFi.macAddress();
    if(readchoc>3000)
    mlf=true;

   
   while (allow && ss.available() > 0 && mlf==true){
    gps.encode(ss.read());
    if (gps.location.isUpdated()){
      float latss=gps.location.lat();  
      float logss=gps.location.lng();  
      lats = String(latss,6);
      logs = String(logss,6);
      String lspi_text=macAdress+" "+lats+" "+logs+" "+cardiact+" "+shockvaleur;
      Serial.println("The value of lspi");
      Serial.println(lspi_text);
      delay(500);
      if (client.publish(lspi_topic, String(lspi_text).c_str())) {
      Serial.println("All the data have been send!");
  }
  // Again, client.publish will return a boolean value depending on whether it succeded or not.
  // If the message failed to send, we will try again, as the connection may have broken.
  else {
    Serial.println("Data failed to send. Reconnecting to MQTT Broker and trying again");    
    }
   
}
  allow=false; 
}
}
else Serial.println("System OFF");
}
