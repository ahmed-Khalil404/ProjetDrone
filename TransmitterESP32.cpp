#include <TinyGPS++.h>  
#include <SoftwareSerial.h> 
#include <Wire.h>
#include "PubSubClient.h" // Connect and publish to the MQTT broker
#include "WiFi.h" // Enables the ESP32 to connect to the local network (via WiFi)

static const int RXPin = 16, TXPin = 17;
static const uint32_t GPSBaud = 9600;

#define heartbeat 34 
#define choc 4

// The TinyGPS++ object
TinyGPSPlus gps;

// The serial connection to the GPS device
SoftwareSerial ss(RXPin, TXPin);

// WiFi
const char* ssid = "iPhoneM";// Your personal network SSID
const char* wifi_password = "Mx33/11!on"; 
const char* mqtt_server = "172.20.10.3";  // IP of the MQTT broker

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

void setup(){
  Serial.begin(9600);
  ss.begin(GPSBaud);
  pinMode(heartbeat, INPUT);
  pinMode(choc, INPUT);
  connect_MQTT();
}

void loop(){

  if (ss.available() > 0){
    gps.encode(ss.read());
  if (gps.location.isUpdated()){
    int val=0;
   val = digitalRead(choc);
   Serial.println(val);
   String valt= String(val);
  /* if (client.publish(lspi_topic, String(val).c_str())) {
    Serial.println("choc have been send!");
  }
  // Again, client.publish will return a boolean value depending on whether it succeded or not.
  // If the message failed to send, we will try again, as the connection may have broken.
  else {
    Serial.println("choc failed to send. Reconnecting to MQTT Broker and trying again");
    
  }*/
  float lat=gps.location.lat();
  float log=gps.location.lng();

   String lats = String(lat,6);
   String logs = String(log,6);
   String send=lats+"\n"+logs;
  
  Serial.print("Latitude="); 
  Serial.println(lat, 6);
  Serial.println("-------------");
  Serial.print("Longitude="); 
  Serial.println(log, 6);

    
    int a= analogRead(heartbeat); 
    Serial.println(a);
    String cardiact=String(a);
    delay(500);
   


  delay(5000);
  String lspi_text=valt +"\n" +lats+ "\n" +logs+"\n"+cardiact;
  Serial.println(lspi_text);
   if (client.publish(lspi_topic, String(lspi_text).c_str())) {
    Serial.println("All the data have been send!");
  }
 //   }
  //  }
  // Again, client.publish will return a boolean value depending on whether it succeded or not.
  // If the message failed to send, we will try again, as the connection may have broken.
  else {
    Serial.println("Data failed to send. Reconnecting to MQTT Broker and trying again");
    
  }
     }
  }
  //choco();
  //cardiac();
  
 
}
