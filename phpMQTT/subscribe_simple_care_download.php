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


  $topics_indoor_location['hello/indoor_location'] = array("qos"=>0, "function"=>"procmsg3");



  $mqtt->subscribe($topics_indoor_location,0);

  while($mqtt->proc()){        
  }

  $mqtt->close();


  function procmsg3($topic_indoor_location,$msg){ //Arm
    $date_now = date("Y-m-d H:i:s");
    $NewString = explode('\\', $msg,2);
      //echo "you got it: $msg";
      //mysql_connect("localhost","root","") or die("connect error!");
      //mysql_select_db('ktwhost_sco-oc') or die("db error");

     
     //$nums=mysqli_num_rows($query_result); //判斷是否為空值
      //$nums=mysqli_num_rows($query_result); //判斷是否為空值

     /* echo $NewString[0];

       echo ' ';

      echo 'Insert success ';

      echo ' ';*/

      //echo "123";



      $con=mysqli_connect("localhost","root","","simple_care");

      if($msg == "indoor")
      {
       // echo $NewString[0];
          //7D:03:00:2B:88:8C(RAN) s2  ;  21:0B:00:2B:88:8C(RAN) s1 ;   'DB:2F:00:2B:88:8C(RAN) s3
     //$query_string ="SELECT Distance,RSSI FROM log_peripheralwithcentral WHERE CentralID = '7D:03:00:2B:88:8C(RAN)' ORDER BY DatetimeCreate LIMIT 5";
     $query_string1 ="Select RSSI,Distance From log_peripheralwithcentral WHERE CentralID = '7D:03:00:2B:88:8C(RAN)' AND PeripheralID = 'D8:98:4B:BD:90:C0(RAN)' order BY DatetimeCreate desc LIMIT 5";
     $query_result1 = mysqli_query($con,$query_string1);

     $query_string2 ="Select RSSI,Distance From log_peripheralwithcentral WHERE CentralID = 'DB:2F:00:2B:88:8C(RAN)' AND PeripheralID = 'D8:98:4B:BD:90:C0(RAN)' order BY DatetimeCreate desc LIMIT 5";
     $query_result2 = mysqli_query($con,$query_string2);

     $query_string3 ="Select RSSI,Distance From log_peripheralwithcentral WHERE CentralID = '21:0B:00:2B:88:8C(RAN)' AND PeripheralID = 'D8:98:4B:BD:90:C0(RAN)' order BY DatetimeCreate desc LIMIT 5";
     $query_result3 = mysqli_query($con,$query_string3);

     
      //mysqli_query($con,"SELECT Distance,RSSI FROM log_peripheralwithcentral WHERE CentralID = '7D:03:00:2B:88:8C(RAN)' ORDER BY DatetimeCreate LIMIT 5");
      
      if($query_result1 && $query_result2 && $query_result3 === FALSE) 
{
die(mysql_error()); // TODO: better error handling
}

$i=0;
while($row1=mysqli_fetch_array($query_result1))
{

    //$RSSI = $row['RSSI'];
    //$Distance = $row['Distance'];
    $Distance1[$i] = $row1['Distance'];

    //echo "<br>RSSI :".$RSSI."<br>";
    //echo "<br>Distance :".$Distance."<br>";
   // echo "\\";
   // echo $Distance;

    //Distance_fuction($Distance);
    $i++;
}

$i=0;
while($row2=mysqli_fetch_array($query_result2))
{

    //$RSSI = $row['RSSI'];
    //$Distance = $row['Distance'];
    $Distance2[$i] = $row2['Distance'];

    //echo "<br>RSSI :".$RSSI."<br>";
    //echo "<br>Distance :".$Distance."<br>";
   // echo "\\";
   // echo $Distance;

    //Distance_fuction($Distance);
    $i++;
}

$i=0;
while($row3=mysqli_fetch_array($query_result3))
{

    //$RSSI = $row['RSSI'];
    //$Distance = $row['Distance'];
    $Distance3[$i] = $row3['Distance'];

    //echo "<br>RSSI :".$RSSI."<br>";
    //echo "<br>Distance :".$Distance."<br>";
   // echo "\\";
   // echo $Distance;

    //Distance_fuction($Distance);
    $i++;
}

$temp1 = array_sum($Distance1);
$division1 = $temp1 / 5;

$temp2 = array_sum($Distance2);
$division2 = $temp2 / 5;

$temp3 = array_sum($Distance3);
$division3 = $temp3 / 5;

echo "\\".$division1."\\".$division2."\\".$division3;

$total_string ="\\".$division1."\\".$division2."\\".$division3 ;

/*$Distance_array = "\\".implode("\\", $Distance);
echo $Distance_array ;*/

Distance_fuction($total_string);

     /* echo $Distance[0];
      echo $Distance[1];
      echo $Distance[2];
      echo $Distance[3];
      echo $Distance[4];*/
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