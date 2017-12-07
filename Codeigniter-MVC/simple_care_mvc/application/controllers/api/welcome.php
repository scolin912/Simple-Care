<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Welcome extends CI_Controller
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
    
	public function index()
	{
		$sql = "
			SELECT *
			FROM `Data_AppUser`
			WHERE 1
		";
		$query = $this->db->query($sql);
		$num_rows = $query->num_rows();
		$result_array = $query->result_array();
		
		//print_r($result_array);
		//$this->load->view('api/welcome');
	}
}

/* End of file welcome.php */
/* Location: controllers/api/welcome.php */