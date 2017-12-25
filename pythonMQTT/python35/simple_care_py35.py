import paho.mqtt.client as mqtt
import MySQLdb
 
client = mqtt.Client()
client.connect("127.0.0.1", 1883)
 
client.subscribe("hello/indoor_location")
client.subscribe("hello/update_BLE_data")
client.subscribe("hello/add_data")

db = MySQLdb.connect(host="localhost",user="root", passwd="", db="simple_care")
cursor = db.cursor()

 
def on_message_callback(client, userdata, msg):
    if (msg.topic == "hello/indoor_location" and msg.payload=='indoor'):
        cursor1 = db.cursor()
        cursor2 = db.cursor()
        cursor3 = db.cursor()

        cursor1.execute("SELECT SUM(Distance) FROM ( SELECT Distance FROM log_peripheralwithcentral WHERE CentralID = '7D:03:00:2B:88:8C(RAN)' AND PeripheralID = 'D8:98:4B:BD:90:C0(RAN)' ORDER BY DatetimeCreate desc LIMIT 0,5) AS t1 ")
        cursor2.execute("SELECT SUM(Distance) FROM ( SELECT Distance FROM log_peripheralwithcentral WHERE CentralID = 'DB:2F:00:2B:88:8C(RAN)' AND PeripheralID = 'D8:98:4B:BD:90:C0(RAN)' ORDER BY DatetimeCreate desc LIMIT 0,5) AS t1 ")
        cursor3.execute("SELECT SUM(Distance) FROM ( SELECT Distance FROM log_peripheralwithcentral WHERE CentralID = '21:0B:00:2B:88:8C(RAN)' AND PeripheralID = 'D8:98:4B:BD:90:C0(RAN)' ORDER BY DatetimeCreate desc LIMIT 0,5) AS t1 ")
        
        results1 = cursor1.fetchall()
        results2 = cursor2.fetchall()
        results3 = cursor3.fetchall()

        for record1 in results1:
            total1 = float(record1[0])
            
        for record2 in results2:
            total2 = float(record2[0])

        for record3 in results3:
            total3 = float(record3[0])
            
        average1=total1/5
        average2=total2/5
        average3=total3/5
        total_string='\\'+str(average1)+'\\'  + str(average2) +'\\' + str(average3) +'\\' 

        print(total_string)

        Distance_fuction(total_string)

    if (msg.topic == "hello/update_BLE_data" ):
        string1=msg.payload
        string1 = string1.decode()
        #print(string1)
        String2 = string1.split("\\")
        #print(String2[1])
        # insert data
        cursor.execute('INSERT INTO log_peripheralwithcentral (CentralID, PeripheralID, RSSI, Distance)' 'VALUES (%s, %s, %s, %s)',(String2[1],String2[2],String2[3],String2[4]))
        print("Insert_BLE_data success")
        #db.commit()
        #db.close()
        

    if (msg.topic == "hello/add_data" and msg.payload=='add_data'):
        print("add_data")

def Distance_fuction(d_string):
    _g_cst_MQTTTopicName = "phone/indoor_background" #TOPIC name

    client.publish(_g_cst_MQTTTopicName, d_string)

client.on_message = on_message_callback
client.loop_start()
 
while (1):
    continue