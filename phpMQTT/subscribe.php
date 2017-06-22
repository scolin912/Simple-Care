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
  function procmsg($topic_update_BLE,$msg){ //for Scan BLE data update
    $NewString = explode('\\', $msg,5);

      //echo "you got it: $msg";
      $con=mysqli_connect("localhost","root","","simple_care");

      mysqli_query("insert into Log_PeripheralWithCentral(PeripheralID,CentralID,RSSI,Distance) values ('$NewString[1]','$NewString[2]','$NewString[3]','$NewString[4]')");

      echo 'Update_BLE_data success ';
  }

    function procmsg1($topic_add_data,$msg){ //for initial register
    $date_now = date("Y-m-d H:i:s");
    $NewString = explode('\\', $msg,7);

     $con=mysqli_connect("localhost","root","","simple_care");
     
     $query_string ="SELECT * FROM data_central WHERE CentralID = '$NewString[1]'";
     $query_result = mysqli_query($con,$query_string);

      $nums=mysqli_num_rows($query_result); //check return is none or not

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
?>
