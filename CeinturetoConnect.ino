//Choc sensor configuration
int valeur=0;
const int buttonPin = 13;     // the number of the pushbutton pin
const int buttonPin1 = 36;     // the number of the pushbutton pin

#define DEEP_SLEEP_TIME 0.2

//End of choc sensor configuration

// Implemented libraries in this project
#include <TinyGPS++.h>  
#include <SoftwareSerial.h> 
#include <Wire.h>
#include "PubSubClient.h" // Connect and publish to the MQTT broker
#include "WiFi.h" // Enables the ESP32 to connect to the local network (via WiFi)


//In and out configuration
static const int RXPin = 16, TXPin = 17;
static const uint32_t GPSBaud = 9600;
#define Btn1_GPIO   32
#define heartbeat 34 
#define stopbutton 23
// The TinyGPS++ object
TinyGPSPlus gps;
// The serial connection to the GPS device
SoftwareSerial ss(RXPin, TXPin);
// WiFi
const char* ssid = "Choufli7al_Ext";// Your personal network SSID
const char* wifi_password = "24342442"; 
const char* mqtt_server = "192.168.1.29";  // IP of the MQTT broker
const char* gps_topic = "gps";
const char* cardio_topic = "cardio";
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



void setup ()
{



Serial.begin(9600);

//Special serial so transfer dosen't bother
ss.begin(GPSBaud);

//GPIOS CONFIGURATIONS
pinMode(heartbeat, INPUT);
pinMode(stopbutton, INPUT);
pinMode(buttonPin, INPUT);
pinMode(buttonPin1, INPUT);




pinMode(Btn1_GPIO, INPUT_PULLDOWN);
attachInterrupt(Btn1_GPIO, Ext_INT1_ISR, RISING);



//MQTT connection set
connect_MQTT();



}


bool value(){
    bool buttonState = digitalRead(buttonPin);
        bool buttonState1 = digitalRead(buttonPin1);

      if (buttonState == HIGH) {
                valeur++;
      }
      if (buttonState1 == HIGH) {
                valeur++;
      }
      if (valeur>0){
        return true;
      }

    


}

void loop ()

{

  if (value()){
  if (ss.available() > 0){
        gps.encode(ss.read());
  if (gps.location.isUpdated()){
    Serial.println("**********");
    Serial.println("Stop Button status");
    int a=0;digitalRead(stopbutton);
    Serial.println(a);
    Serial.println("**********");
    
 Serial.println("**********"); 
 Serial.println("GPS DATA"); 

  float lat=gps.location.lat();
  float log=gps.location.lng();

   String lats = String(lat,6);
   String logs = String(log,6);
   String send=lats+"\n"+logs;

  Serial.print("Latitude="); 
  Serial.println(lat, 6);
  Serial.print("Longitude="); 
  Serial.println(log,6);

    Serial.println("*****"); 
    Serial.println("heartbeat"); 
    int mono= analogRead(heartbeat); 
    Serial.println(mono);
    String cardiact=String(a);
    Serial.println("*****"); 
    delay(3000);
    String lspi_text=lats+" "+logs+" "+cardiact;
    Serial.println(lspi_text);
    if (client.publish(lspi_topic, String(lspi_text).c_str())) {
    Serial.println("All the data have been send!");

  }
  
  // Again, client.publish will return a boolean value depending on whether it succeded or not.
  // If the message failed to send, we will try again, as the connection may have broken.
  else {
    Serial.println("Data failed to send. Reconnecting to MQTT Broker and trying again");
    
  }

     



 }



}
 }
}
