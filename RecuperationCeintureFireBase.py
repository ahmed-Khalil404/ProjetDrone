import paho.mqtt.client as mqtt
from firebase import firebase

MQTT_ADDRESS = '172.20.10.2'
MQTT_USER = 'pi'
MQTT_PASSWORD = 'root'
MQTT_TOPIC = 'lspi'

FBConn = firebase.FirebaseApplication('https://sleampi-default-rtdb.europe-west1.firebasedatabase.app/', None)

def on_connect(client, userdata, flags, rc):

    client.subscribe(MQTT_TOPIC)


def on_message(client, userdata, msg):
   
    a =str(msg.payload)
    print(a);
    m=a[0:17]
    c=a[18:27]
    x=a[28:37]
    h=a[38:40]
    o=a[41:45]
    print(c)
    print(x)
    print(h)
    print(o)
    z={'Laltitude':c,
       'Longitude':x,
       'Heartbeat':h,
       'Choc':o
        }
    result = FBConn.post('/LSPI/'+m,z)


def main():
    mqtt_client = mqtt.Client()
    mqtt_client.username_pw_set(MQTT_USER, MQTT_PASSWORD)
    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message
    mqtt_client.connect(MQTT_ADDRESS, 1883)
    mqtt_client.loop_forever()
   

if __name__ == '__main__':
    main()
