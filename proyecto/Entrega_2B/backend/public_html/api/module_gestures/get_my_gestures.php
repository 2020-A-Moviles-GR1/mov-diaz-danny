<?php
   require("../params.php");
   require_once '../../../models/Gesture.php';

 $context = $_SERVER['PHP_SELF'];


 if ($_SERVER['REQUEST_METHOD'] === 'POST'){

	
	$json_input = json_decode(file_get_contents('php://input') , true);
    $username_user = $json_input['username_user'];
	$option = $json_input['option'];
	
    $validation = sonDatosValidos([$username_user, $option]);
    // echo $validation[1]
    if(!$validation[0]){
        exit();
    }

    $database = new Database();
    $connection = $database->getConnection($DATABASE_NAME);




   if($connection == null){
      // $database-> error
       echo json_encode(new Response(-1, "Error interno", 0, [], "Error de conexión"));
       exit();
   }



   $response = Gesture::getAllMyGestures($connection, $username_user, $option);

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
