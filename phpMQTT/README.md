1.	Please study this AWS tutorial to setup your LAMP server.
    http://docs.aws.amazon.com/zh_cn/AWSEC2/latest/UserGuide/install-LAMP.html
2.	Go to phpmyadmin and add a new database call simple_care then import simple_care.sql for it.
3.	Open putty and connect to AWS Linux, create a folder on the /var/www/html/(phpMQTT folder or something)
4.	Put phpMQTT.php and subscribe.php to this folder.
5.  Try this command line--> php subscribe.php and run it on the AWS Linux system.
6.	Open Terminal or CMD and test MQTT command line, example: mosquitto_pub -h 1.2.3.4(your AWS public IP) -d -t hello/add_data -m "\0xID_20170622\bed\123\789\1\my@gmail.com\"
7.	You’ll receive localhost MQTT publish command line by putty.
8.	Congras! you have your own PHP server and work successfully from AWS!
