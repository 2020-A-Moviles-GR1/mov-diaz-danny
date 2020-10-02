<?php
   require("../params.php");
   require_once '../../../models/Sample.php';

 $context = $_SERVER['PHP_SELF'];


 if ($_SERVER['REQUEST_METHOD'] === 'GET'){


   $id_function = $_GET['id_function'];
   $username_user = $_GET['username_user'];

   $validation = sonDatosValidos([$id_function, $username_user]);
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



   $response = Sample::getAllSamplesOfGesture($connection, $id_function, $username_user);

   if($response->error != null){

     if(!$DEBUG_MODE)
       $response->error = "Error interno";
   }


   // echo json_encode($response->metadata);
   $database = null;
   
   $filename = "data,".$username_user.",function".$id_function.".txt";
   //$filename = "data.txt";
   
   header("Content-type: text/csv");
	header("Cache-Control: no-store, no-cache");
	header('Content-Disposition: attachment; filename="'.$filename.'"');
	$file = fopen('php://output','w');
	fwrite($file, $response->metadata);
	fclose($file);

}else{
   echo $MSG_ACCESO_DENEGADO;
}



?>
