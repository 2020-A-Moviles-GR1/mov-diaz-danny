<?php
   require("../params.php");
   require_once '../../../models/FunctionAssociated.php';

 $context = $_SERVER['PHP_SELF'];


 if ($_SERVER['REQUEST_METHOD'] === 'GET'){


   $database = new Database();
   $connection = $database->getConnection($DATABASE_NAME);

	$option = 3;
	
	if(isset($_GET['option'])) {
		$option = $_GET["option"];
		if($option != 0 && $option != 1 && $option != 2 && $option != 3){
			exit();
		}
	}
	
	


   if($connection == null){
      // $database-> error
       echo json_encode(new Response(-1, "Error interno", 0, [], "Error de conexión"));
       exit();
   }



   $response = FunctionAssociated::getAllFunctions($connection, $option);

   if($response->error != null){

     if(!$DEBUG_MODE)
       $response->error = "Error interno";
   }


   echo json_encode($response);
   $database = null;

}else{
   echo $MSG_ACCESO_DENEGADO;
}



?>
