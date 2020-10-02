<?php
   require("../params.php");
   require_once '../../../models/Gesture.php';

 $context = $_SERVER['PHP_SELF'];


 if ($_SERVER['REQUEST_METHOD'] === 'POST'){

   $json_input = json_decode(file_get_contents('php://input') , true);

    //var_export($json_input);

   $last_id_function = $json_input['last_id_function'];
   $id_function = $json_input['id_function'];
   $username_user = $json_input['username_user'];
   $name = $json_input['name'];
   $samples = $json_input['samples'];
   


   $validation = sonDatosValidos([$last_id_function, $id_function, $username_user, $name]);
   // echo $validation[1]
   if(!$validation[0]){
       exit();
   }
   
   $validation2 = sonDatosValidos($samples);
   // echo $validation[1]
   if(!$validation2[0]){
       exit();
   }

   $database = new Database();
   $connection = $database->getConnection($DATABASE_NAME);




   if($connection == null){
      // $database-> error
       echo json_encode(new Response(-1, "Error interno", 0, [], "Error de conexión"));
       exit();
   }


   $gesture = new Gesture($last_id_function, $username_user, $name);

   $response = $gesture->updateWithSamples($connection, $samples, $id_function);

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
