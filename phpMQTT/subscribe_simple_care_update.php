<?php
  // subscribe.php
date_default_timezone_set('Asia/Taipei');
  require("./phpMQTT.php");

  $host = "127.0.0.1"; 
  $port = 1883;
  $username = ""; 
  $password = ""; 

  $mqtt = new phpMQTT($host, $port, "ClientID".rand()); 

  if(!$mqtt->connect(true,NULL,$username,$password)){
    exit(1);
  }

  //currently subscribed topics
  $topics_update_BLE['hello/update_BLE_data'] = array("qos"=>0, "function"=>"procmsg");
  $topics_add_data['hello/add_data'] = array("qos"=>0, "function"=>"procmsg1");



  $mqtt->subscribe($topics_update_BLE,0);
  $mqtt->subscribe($topics_add_data,0);


  while($mqtt->proc()){        
  }

  $mqtt->close();
  function procmsg($topic_update_BLE,$msg){
    $NewString = explode('\\', $msg,5);

      //echo "you got it: $msg";
      $con=mysqli_connect("localhost","root","","simple_care");

      mysqli_query($con,"insert into log_peripheralwithcentral (CentralID,PeripheralID,RSSI,Distance) values ('$NewString[1]','$NewString[2]','$NewString[3]','$NewString[4]')");

      //mysqli_query($con,"insert into data_central(CentralID,CentralName,GPS_N,GPS_E,UserEmail,DatetimeCreate) values ('$NewString[1]','$NewString[2]','$NewString[3]','$NewString[4]','$NewString[6]','$date_now')");

     // $query_string = "insert into log_peripheralwithcentral (CentralID,PeripheralID,RSSI,Distance) values ('$NewString[1]','$NewString[2]','$NewString[3]','$NewString[4]')";

      //$query_result = mysqli_query($con,$query_string);

     /* echo $NewString[1];
      echo ' ';
      echo $NewString[2];
      echo ' ';
      echo $NewString[3];
      echo ' ';
      echo $NewString[4];
      */

      echo ' ';

      echo $NewString[1];

      echo 'Insert_BLE_data success ';

      echo ' ';
 

      
  }

    function procmsg1($topic_add_data,$msg){ //一開始的註冊
    $date_now = date("Y-m-d H:i:s");
    $NewString = explode('\\', $msg,7);
      //echo "you got it: $msg";
      //mysql_connect("localhost","root","") or die("connect error!");
      //mysql_select_db('ktwhost_sco-oc') or die("db error");

     $con=mysqli_connect("localhost","root","","simple_care");
     
     $query_string ="SELECT * FROM data_central WHERE CentralID = '$NewString[1]'";
     $query_result = mysqli_query($con,$query_string);
     //$nums=mysqli_num_rows($query_result); //判斷是否為空值
      $nums=mysqli_num_rows($query_result); //判斷是否為空值

      if(!$nums)
      {
        mysqli_query($con,"insert into data_central(CentralID,CentralName,GPS_N,GPS_E,UserEmail,DatetimeCreate) values ('$NewString[1]','$NewString[2]','$NewString[3]','$NewString[4]','$NewString[6]','$date_now')");
        echo "INSERT DATA success";
      }
      else
      {
      mysqli_query($con,"UPDATE data_central SET CentralName ='$NewString[2]',GPS_N ='$NewString[3]',GPS_E ='$NewString[4]',UserEmail ='$NewString[6]',DatetimeEdit ='$date_now' WHERE CentralID = '$NewString[1]'");
      echo "UPDATA DATA success";
      }
  }






 

function Distance_fuction($d_string){ 
  $host = "127.0.0.1"; 
  $port = 1883;
  $username = ""; 
  $password = ""; 
  $message = $d_string;

  //MQTT client id to use for the device. "" will generate a client id automatically
  $mqtt = new phpMQTT($host, $port, "ClientID".rand()); 

  if ($mqtt->connect(true,NULL,$username,$password)) {
    $mqtt->publish("phone/indoor_background",$message, 0);
    $mqtt->close();
  }else{
    echo "Fail or time out<br />";
  }
} 

?>