/*
  This example scans nearby BLE peripherals and prints the peripherals found.

  created Mar 2017 by MediaTek Labs
*/

#include <LWiFi.h>
#include <LBLE.h>
#include <LBLEPeriphral.h>
#include <LBLECentral.h>
#include <PubSubClient.h>
#include <LWatchDog.h>
#include <WiFiClient.h>
#include <EEPROM.h>
#include <hal_wdt.h>

/* Set these to your desired credentials. */
const char *ssid = "Simple Care";
const char *password = "12345678";

bool _wasConnected;

WiFiServer server(80);

int flag0=1,flag1=0,flag1_1=1,flag2=0,flag3=0,flag31=0,flag32=0,flag33=0,flag_lighting1=1,flag_lighting2=0,flag_lighting3=0;
int i=0;    
int pinLed = 14;
int PWM = 9; // PWM pin
int dimming = 40;  // Dimming level (0-128)  0 = ON, 128 = OFF
int status = WL_IDLE_STATUS;

WiFiClient MT7697Client;
PubSubClient client(MT7697Client);

char str[]={},str1[]={},*array[5]; 
const char* Topic = "hello/add_data";
const char* SubTopic = "hello/world";
const char* mqtt_server = "192.168.2.101"; //your cloud server IP, if using localhost, please check CMD ipconfig.

String str2;

String EEPROM_read(int _page = 0, int _length = 50) // 讀取資料，1頁 50 bytes
{
  int _address = _page * 52;
  char _str;
  String read_buffer = "";

  if (_length > 51) {                         // 超出頁面
    Serial.println("Out Of Pages");
  }
  else {
    for ( int _i = 0; _i < _length; _i++ ) {
      _str = EEPROM.read(_address + _i);
      read_buffer += (String)_str;
    }
  }
  return read_buffer;
} // end of EEPROM_read()

bool EEPROM_write(char* _str, int _page, int _length) // 寫入資料，1頁 50 bytes
{
  int _address = _page * 52;
  if (_length > 51) {                // 超出頁面
    Serial.println("Out Of Pages");
    return false;
  }
  else {
   // Serial.print("Writing data：");
    for ( int _i = 0; _i < _length; _i++ ) {
      EEPROM.update(_i, _str[_i]);
     // Serial.print(_str[_i]);
    }
   // Serial.println();
    return true;
  } // end if
} // end of EEPROM_write()


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

void callback(char* topic, byte* payload, unsigned int length) { //MQTT控制Linkit7697燈座亮滅
 // LWatchDog.feed();
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();


   if(flag_lighting1==1 && (char)payload[0] == 'l') //當收到MQTT發來的"l"時亮或滅
  {
  

  payload[0] = 'k';


  flag31 = 1;
  flag32 = 0;


  flag_lighting1=0;
  flag_lighting2=1;
  
     }

     if(flag_lighting2==1 && (char)payload[0] == 'l')
  { 
    
 
  payload[0] = 'k';

  flag32 = 1;
  flag31 = 0;


  flag_lighting2=0;
  flag_lighting1=1;

 
  }


   
   else { 
    
  // for smart home security 暫留
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
      //client.publish(Topic, "hello world");
      // ... and resubscribe
      client.subscribe(SubTopic);
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
     Serial.println(" try again in 5 seconds");
      // Wait 4 seconds before retrying// if connecting faile, the flash led by every 1 sec
      digitalWrite(LED_BUILTIN, HIGH);   // turn the LED on (HIGH is the voltage level)
      delay(5000);
      
    }
  }
}

/*********************************************************************
 * @fn      calculateAccuracy, for indoor location RSSI換成距離(公尺)
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

    pinMode(6, INPUT);
    Serial.begin(115200);
    analogWrite(pinLed, 0);
    analogWrite(PWM, 55);  //setting PWM speed
    LBLE.begin();
    while (!LBLE.ready()) {
    delay(100); 
    }

    Serial.println("");
    Serial.println("BLE Ready!");


    str2 = LBLE.getDeviceAddress().toString();
    str2.toCharArray(str, str2.length()+1);

 
   // LWatchDog.begin(23);
}

void loop()
{

  if (digitalRead(6)) //如果Linkit7697的按紐按下，重新設定AP帳密
    {

        Serial.println("Reset WiFi...");
        flag1=1;
        flag1_1=1;
        flag3=0;
    }

  if (flag0==1) //一開始就查EEPROM是否有訯定帳密
{

String page_11 = EEPROM_read(0, 50); // 把第0頁 前50個byte都讀出來

char charBuf11[50];
page_11.toCharArray(charBuf11, 50);
    
 char *p = strtok (charBuf11, "\\");
    while (p != NULL)
    {
        array[i] = p;
        p = strtok (NULL, "\\");
       // Serial.println(array[i]);//for string to char
        i++;
    }
 i=0;

String myString = String(array[0]);

 if(myString=="v") //前面有個v才代表eeprom已驗證有設過資料

 {
   Serial.println("vertified");

   Serial.println(array[1]);
   Serial.println(array[2]);

   connectWiFi(array[1], array[2]); //撈EEPROM的帳號密碼後連線AP
  
    flag0 = 0;
    flag1 = 0;
    flag2 = 1;
   
   LWatchDog.begin(25); //增加watch dog，如果系統當機就從這裡開始
  }
else
{
  Serial.println("not vertified");//for string to char
    flag0 = 0;
    flag1 = 1;
  
}

}

  if (flag1==1)
{
 //flag1跳進來後，flag1_1只執行一次，讓7696變成AP mode，待使用者連線
 if (flag1_1==1)
{

Serial.print("Configuring access point...");

    /* You can remove the password parameter if you want the AP to be open. */
    WiFi.softAP(ssid, password);
    IPAddress myIP = WiFi.softAPIP();
    Serial.println("AP ready.");
    Serial.print("Connect to AP ");
    Serial.print(ssid);
    Serial.print(" and visit http://");
    Serial.println(myIP);

    Serial.print("AP MAC=");
    Serial.println(WiFi.softAPmacAddress());

    server.begin();
    

  flag1_1=0;
}

      delay(1000);

    // listen for incoming clients
    WiFiClient client = server.available();
    if (client) {
     Serial.println("Someone connected!");

      while (client.connected()) {
       if (client.available()) {
            
        String req = client.readString();
        char charBuf[50];//for string to char
        int req_len = req.length() + 1; //for string to char
        req.toCharArray(charBuf, req_len);//for string to char

        Serial.println(charBuf);//for string to char
 
        int _length = strlen(charBuf);  // 取得長度

        EEPROM_write(charBuf, 0, _length); //EEPROM_write(char* _str, int _page, int _length)
          delay(500);

        String page_1 = EEPROM_read(0, 50); // 把第0頁 前50個byte都讀出來

        char charBuf1[55];
       page_1.toCharArray(charBuf1, 50);
    
       char *p = strtok (charBuf1, "\\");
         while (p != NULL)
         {
               array[i] = p;
                p = strtok (NULL, "\\");
      
               i++;
          }
              i=0;
             client.stop();
  
           flag1 = 0;
           flag2 = 1;

           reset(); //因為Linkit7697沒有像ESP8266把AP mode關掉的function，只好重新啟動，直接變station mode
          
            }
        }
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
  LWatchDog.feed();//watchdog feed的進入點
//printWifiData();
  
  digitalWrite(LED_BUILTIN, HIGH);   // turn the LED on (HIGH is the voltage level)

   if (WiFi.status() != WL_CONNECTED) //如果沒連線，就先讓wifi斷線，然後再連線
        {
          WiFi.disconnect();
          connectWiFi(array[1], array[2]);
          }
  
   Topic = "hello/update_BLE_data";
   SubTopic = "hello/world";

        if (!client.connected()) {
         reconnect();
            }
         client.loop();

    LBLECentral.stopScan();
    LBLECentral.scan();
  
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
  double a = calculateAccuracy( -60, LBLECentral.getRSSI(i) ); //理論上1米的RSSI大約為55
  //實測s1是-61，s2是-53，s3是-61  //每個都要設定自己的rssi
  //s1 -70 s2 -75 s3 -54
  //s1 -70 s2 -65 s3 -54
   //s1 -60 s2 -60 s3 -60
  
  sprintf(str1, "%f", a);

  strcat(buf3,"\\");
  strcat(buf3,str);
  strcat(buf3,"\\");
  strcat(buf1,"\\");
  strcat(buf1,buf2);
  strcat(buf3,buf1);
  strcat(buf3,"\\");
  strcat(buf3,str1);
 // strcat(buf3,"\\");
  
  Serial.print(buf3);
  client.publish(Topic, buf3);
  Serial.println();

if (flag31 == 1)
{
  Serial.println("3131");
  analogWrite(pinLed, 55);//開啟燈泡

  }

  if (flag32 == 1)
  {
    Serial.println("3232");
    analogWrite(pinLed, 0);//關閉燈泡

    }

  flag3 = 1;
  digitalWrite(LED_BUILTIN, LOW);   // turn the LED on (HIGH is the voltage level)
  }
}
}

void reset()
{
  Serial.println("Restarting");
  hal_wdt_config_t wdt_config;
  wdt_config.mode = HAL_WDT_MODE_RESET;
  wdt_config.seconds = 0;
  hal_wdt_init(&wdt_config); //set WDT as t0 mode.
  hal_wdt_enable(HAL_WDT_ENABLE_MAGIC);
  while (1)
  {
    Serial.println("Restarting");
    hal_wdt_software_reset();
    delay(100);
  }
}


void printWifiData() {
  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);
  Serial.println(ip);

  // print your MAC address:
  byte mac[6];
  WiFi.macAddress(mac);
  Serial.print("MAC address: ");
  Serial.print(mac[5], HEX);
  Serial.print(":");
  Serial.print(mac[4], HEX);
  Serial.print(":");
  Serial.print(mac[3], HEX);
  Serial.print(":");
  Serial.print(mac[2], HEX);
  Serial.print(":");
  Serial.print(mac[1], HEX);
  Serial.print(":");
  Serial.println(mac[0], HEX);

}

