<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class App extends CI_Controller
{
	// ===============================================================================================================
	// 初始化設置
	// ---------------------------------------------------------------------------------------------------------------
    function __construct()
    {
        parent::__construct();
        
		// 載入資料庫方法模組
		$this->load->database();
    }
	// ===============================================================================================================
	// 預設介面
	// ---------------------------------------------------------------------------------------------------------------
	public function index()
	{
		
	}

	// ===============================================================================================================
	// 新增使用者帳號主檔
	// ---------------------------------------------------------------------------------------------------------------
	public function add_user()
	{
		$date_now = date("Y-m-d H:i:s");
		$Writer = "API";
		
		$UserContry = isset($_REQUEST["UserCountry"])?$_REQUEST["UserCountry"]:"";
		$UserPassword = isset($_REQUEST["UserPassword"])?$_REQUEST["UserPassword"]:"";
		$UserName = isset($_REQUEST["UserName"])?$_REQUEST["UserName"]:"";
		$UserPhone = isset($_REQUEST["UserPhone"])?$_REQUEST["UserPhone"]:"";
		$UserEmail = isset($_REQUEST["UserEmail"])?$_REQUEST["UserEmail"]:"";
		
		$data_array = array();
		$data_array["UserCountry"] = $UserCountry;
		$data_array["UserPassword"] = $UserPassword;
		$data_array["UserName"] = $UserName;
		$data_array["UserPhone"] = $UserPhone;
		$data_array["UserEmail"] = $UserEmail;
		
		$data_array["DatetimeCreate"] = $date_now;
		$data_array["DatetimeEdit"] = $date_now;
		$data_array["WriterCreate"] = $Writer;
		$data_array["WriterEdit"] = $Writer;
		
		$return_array = array();
		
		if($UserEmail==""||$UserPassword=="")
		{
			$return_array["result"] = 0;
			$return_array["datetime"] = $date_now;
		}
		else
		{
			$success = $this->db->insert('Data_AppUser', $data_array);
		    $insert_id = $this->db->insert_id();
			$data_array["ID"] = $insert_id;
			
		    $return_array["result"] = $success?1:0;
		    $return_array["datetime"] = $date_now;
		    $return_array["detail"] = $data_array;
		}
		
		$json_string = json_encode($return_array);
		echo $json_string;
	}

		// ===============================================================================================================
	// 新增使用者帳號主檔
	// ---------------------------------------------------------------------------------------------------------------
	public function get_reg_user()
	{
		$date_now = date("Y-m-d H:i:s");
		$Writer = "API";
		
		$email = isset($_REQUEST["email"])?$_REQUEST["email"]:"";
		$password = isset($_REQUEST["password"])?$_REQUEST["password"]:"";
		
		$data_array = array();
		$data_array["email"] = $email;
		$data_array["password"] = $password;

		
		$return_array = array();
		$result = mysql_query("SELECT COUNT(*) FROM data_appuser WHERE UserEmail = '$email'");
			if($result){
		 if(mysql_result($result, 0) > 0){
		 		$result = mysql_query("SELECT UserPassword FROM data_appuser WHERE UserEmail = '$email'");
	
				if (!$result) {
			   		$return_array["result"] = 0;
					$return_array["message"] = "Cannot Login. Please try again";

					echo json_encode($return_array);
			    	exit;
				}else{
					$row = mysql_fetch_row($result);

					if($row[0]==$password){
						$return_array["result"] = 1;
						$return_array["message"] = "Login is successfull";


						$sql="SELECT * FROM data_appuser WHERE UserEmail='$email'";
						$result = mysql_query($sql) or die(mysql_error());
	
						if (mysql_num_rows($result)>0) {

						$return_array["info"] = array();
						while ($row=mysql_fetch_array($result)) {
							$files=array();
							$files["location"]=$row["UserContry"];
							$files["name"]=$row["UserName"];
							$files["number"]=$row["UserPhone"];

							array_push($return_array["info"], $files);

						}
					}
						echo json_encode($return_array);
					}else{
						$return_array["result"] = 0;
						$return_array["message"] = "Login email and Password do not match. Please try again.";
						echo json_encode($return_array);
					}

				}
		 }else{
		 	$return_array["result"] = 0;
			$return_array["message"] = "Cannot Login. Email not found";
			echo json_encode($return_array);
			exit();
		 }
	}
	else{

	$return_array["result"] = 0;
	$return_array["message"] = "Required field(s) is missing";
	// echoing JSON response
	echo json_encode($return_array);

}

		

	}




}

/* End of file app.php */
/* Location: controllers/api/app.php */