<?php
   require("../params.php");
   require_once '../../../models/Sample.php';

 $context = $_SERVER['PHP_SELF'];


 if ($_SERVER['REQUEST_METHOD'] === 'POST'){

   $json_input = json_decode(file_get_contents('php://input') , true);

    //var_export($json_input);

   $id_function = $json_input['id_function'];
   $username_user = $json_input['username_user'];
   $is_augmented_data = $json_input['is_augmented_data'];

   $data_accelerometer_x = $json_input['data_accelerometer_x'];
   $data_accelerometer_y = $json_input['data_accelerometer_y'];
   $data_accelerometer_z = $json_input['data_accelerometer_z'];


   $data_gyroscope_x = $json_input['data_gyroscope_x'];
   $data_gyroscope_y = $json_input['data_gyroscope_y'];
   $data_gyroscope_z = $json_input['data_gyroscope_z'];





   $validation = sonDatosValidos([$id_function, $username_user, $is_augmented_data, $data_accelerometer_x, $data_accelerometer_y,
                                  $data_accelerometer_z, $data_gyroscope_x, $data_gyroscope_y, $data_gyroscope_z]);
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



   $gesture_sample = new Sample(-1, $id_function, $username_user, $is_augmented_data, $data_accelerometer_x,
                                $data_accelerometer_y, $data_accelerometer_z, $data_gyroscope_x, $data_gyroscope_y, $data_gyroscope_z);


   $response = $gesture_sample->create($connection);

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
