/*
  This example scans nearby BLE peripherals and prints the peripherals found.

  created Mar 2017 by MediaTek Labs
*/

#include <LWiFi.h>
#include <LBLE.h>
#include <LBLEPeriphral.h>
#include <LBLECentral.h>
#include <PubSubClient.h>

bool _wasConnected;
String _ssidString;
String _passString;
LBLEService _periphralService("D709A00C-DA1A-4726-A33D-CF62B8F4C3D6");

int flag1=1,flag2=0,flag3=0,flag4=0,flag5=0;
const char* Topic = "hello/add_data";
const char* SubTopic = "hello/world";

const char* mqtt_server = "192.168.x.x"; //your cloud server IP, if using localhost, please check CMD by ipconfig.

int i=0;    

int status = WL_IDLE_STATUS;

WiFiClient MT7697Client;
PubSubClient client(MT7697Client);

char str[]={}; 
char str1[]={};
String str2;
char str3[]={}; 



/*
 * SSID & password must be sent as UTF-8 String and length 
 * must < 20 bytes due to BLE MTU limitation. 
 * 
 * If password length equals to 0, will connect to SSID as open.
 */
LBLECharacteristicString _ssidRead("61DE21BC-6E02-4631-A0A7-1B6C7AF0DAEE", LBLE_WRITE);
LBLECharacteristicString _passRead("B882467F-77BC-4697-9A4A-4F3366BC6C35", LBLE_WRITE);
LBLECharacteristicString _position("B882467F-77BC-4697-9A4A-4F3366BC6C36", LBLE_WRITE);
LBLECharacteristicString _lon("B882467F-77BC-4697-9A4A-4F3366BC6C37", LBLE_WRITE);
LBLECharacteristicString _lat("B882467F-77BC-4697-9A4A-4F3366BC6C38", LBLE_WRITE);
LBLECharacteristicString _TBD("B882467F-77BC-4697-9A4A-4F3366BC6C39", LBLE_WRITE);
LBLECharacteristicString _usermail("B882467F-77BC-4697-9A4A-4F3366BC6C40", LBLE_WRITE);

void connectWiFi(const String ssidString, const String passString)
{
  if (ssidString.length()) {
        const int ssidLen = ssidString.length() + 1;
        const int passLen = passString.length() + 1;
        char ssidCString[ssidLen];
        char passCString[passLen];
        ssidString.toCharArray(ssidCString, ssidLen);
        passString.toCharArray(passCString, passLen);
        
        Serial.print("Connecting to: [");
        Serial.print(String(ssidCString));
        if (passString.length()) {
            Serial.print("] [");
            Serial.print(String(passCString));
        }
        Serial.println("]");
        if (passString.length()) {
            WiFi.begin(ssidCString, passCString);
        } else {
            WiFi.begin(ssidCString);
        }
    }
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();

  // Switch on the LED if an 1 was received as first character
  if ((char)payload[0] == '1') {
  // for smart home security
  } else {
  // for smart home security
  }

}

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "MT7697Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");
      // Once connected, publish an announcement...
      client.publish(Topic, "hello world");
      // ... and resubscribe
      client.subscribe(SubTopic);
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

/*********************************************************************
 * @fn      calculateAccuracy, for indoor location
 */
double calculateAccuracy( double txPower, double rssi )
{
       if (rssi == 0) {
           return -1.0; // if we cannot determine accuracy, return -1.
       }
       double ratio = rssi * 1.0 / txPower;
       if (ratio < 1.0) {
           return pow(ratio, 10);
       } else {
           double accuracy = (0.42093) * pow(ratio, 6.9476) + 0.54992;
           return accuracy;
       }

}


void setup()
{
    Serial.begin(115200);
    LBLE.begin();
    while (!LBLE.ready()) {
      delay(100); 
    }
    //6 BLE Characteristic
    _periphralService.addAttribute(_ssidRead);
    _periphralService.addAttribute(_passRead);
    _periphralService.addAttribute(_position);
    _periphralService.addAttribute(_lon);
    _periphralService.addAttribute(_lat);
    _periphralService.addAttribute(_TBD);
    _periphralService.addAttribute(_usermail); 
    LBLEPeripheral.addService(_periphralService);
    LBLEPeripheral.begin();
    LBLEAdvertisementData _advertisement;
    _advertisement.configAsConnectableDevice("Simple Care");
    LBLEPeripheral.advertise(_advertisement);
    Serial.println("BLE Ready!");

    str2 = LBLE.getDeviceAddress().toString();
    str2.toCharArray(str, str2.length()+1);
}

void loop()
{

  if (flag1==1)
{
      delay(1000);
    if (_ssidRead.isWritten()) {
        WiFi.disconnect();
        _wasConnected = false;
        _ssidString = _ssidRead.getValue();
        _passString = "";
        Serial.print("New SSID: ");
        Serial.println(_ssidString);
    }
    if (_passRead.isWritten()) {
        WiFi.disconnect();
        _wasConnected = false;
        _passString = _passRead.getValue();
        Serial.print("New Password: ");
        Serial.println(_passString);
        connectWiFi(_ssidString, _passString);

     if (_ssidString.length() &&
        WiFi.status() != WL_CONNECTED) {
        Serial.print("Connecting Status: ");
        Serial.println(WiFi.status());
        connectWiFi(_ssidString, _passString);
    } else if (WiFi.status() == WL_CONNECTED &&
               !_wasConnected) {
        _wasConnected = true;
        Serial.print("Connected to: ");
        Serial.println(_ssidString);
    }
    flag1 = 0;
    flag2 = 1;
        
    }

}
   if (flag2==1)
{
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);

  Serial.println("BLE ready, start scan (wait 2 seconds)");
  LBLECentral.scan();

  delay(1000);

  Serial.println();

    flag2 = 0;
    flag3 = 1;

}
   if (flag3==1)
{
Topic = "hello/add_data";
SubTopic = "hello/world";

    if (!client.connected()) {
    reconnect();
  }
  client.loop();
delay(1000);
  Serial.print("Total ");
  Serial.print(LBLECentral.getPeripheralCount());
  Serial.println(" devices found:"); 

    for (int i = 0; i < LBLECentral.getPeripheralCount(); i++) {
    char  buf1 [100]={'\0'};
    char  buf2 [100]={'\0'};
    char  buf3 [100]={'\0'};

  sprintf (buf1, "%s", LBLECentral.getAddress(i).c_str()); 
  sprintf (buf2, "%03i", LBLECentral.getRSSI(i));
  double a = calculateAccuracy( -55, LBLECentral.getRSSI(i) );
  sprintf(str1, "%f", a);

  
  strcat(buf3,"\\");
  strcat(buf3,str);
  strcat(buf3,"\\");
  strcat(buf1,"\\");
  strcat(buf1,buf2);
  strcat(buf3,buf1);
  strcat(buf3,"\\");
  strcat(buf3,str1);
  strcat(buf3,"\\");
  
  Serial.print(buf3);
  client.publish(Topic, buf3);
  Serial.println();
  }

  

}
    
}
