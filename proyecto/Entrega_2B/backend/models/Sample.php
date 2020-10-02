<?php

require_once 'Response.php';

class Sample {



    private $id;
    private $id_function;
    private $username_user;
    private $is_augmented_data;
    private $data_accelerometer_x;
    private $data_accelerometer_y;
    private $data_accelerometer_z;
    private $data_gyroscope_x;
    private $data_gyroscope_y;
    private $data_gyroscope_z;


    public function __construct($id, $id_function, $username_user, $is_augmented_data,
                                $data_accelerometer_x, $data_accelerometer_y, $data_accelerometer_z,
                                $data_gyroscope_x, $data_gyroscope_y, $data_gyroscope_z) {

       $this->id = $id;
       $this->id_function = $id_function;
       $this->username_user = $username_user;
       $this->is_augmented_data = ($is_augmented_data)? 1:0;
       $this->data_accelerometer_x = $data_accelerometer_x;
       $this->data_accelerometer_y = $data_accelerometer_y;
       $this->data_accelerometer_z = $data_accelerometer_z;
       $this->data_gyroscope_x = $data_gyroscope_x;
       $this->data_gyroscope_y = $data_gyroscope_y;
       $this->data_gyroscope_z = $data_gyroscope_z;

    }

    public function create($connection) {

        $response = new Response(0, "iniciando creación de sample para un gesto", 0, [], null);


        try {

            $sql = "INSERT INTO sample(id_function, username_user, is_augmented_data, data_accelerometer_x, 	data_accelerometer_y, 	data_accelerometer_z, 	data_gyroscope_x, 	data_gyroscope_y, 	data_gyroscope_z ) ";
            $sql .= "VALUES(:id_function, :username_user, :is_augmented_data, :data_accelerometer_x, 	:data_accelerometer_y, 	:data_accelerometer_z, 	:data_gyroscope_x, 	:data_gyroscope_y, 	:data_gyroscope_z)";

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
                return $response;
            }


            $success = $stmt->execute(array(
                ":id_function" => $this->id_function,
                ":username_user" => $this->username_user,
                ":is_augmented_data" => $this->is_augmented_data,
                ":data_accelerometer_x" => $this->data_accelerometer_x,
                ":data_accelerometer_y" => $this->data_accelerometer_y,
                ":data_accelerometer_z" => $this->data_accelerometer_z,
                ":data_gyroscope_x" => $this->data_gyroscope_x,
                ":data_gyroscope_y" => $this->data_gyroscope_y,
                ":data_gyroscope_z" => $this->data_gyroscope_z
                ));

            if($success){

                $response->id_message = 1;
                $response->server_message = "Se ha creado correctamente";
                $response->metadata = "".$connection->lastInsertId();


            }else{
                $response->id_message = 4;  // 4 es error de servidor
                $response->server_message  = "No se pudo crear";
                $response->error = "Error al ejecutar el sql: $sql\n ";
            }


        } catch (Exception $e) {
            $response->id_message = 4;  // 4 es error de servidor
            $response->server_message  = "Error del servidor";
            $response->error = $e->getMessage();
        }

        /*$this->conn->beginTransaction();
        if($respuesta['error']  == null){
            $this->conn->commit();
        }else{
            $this->conn->rollBack();
        }*/

        return $response;

    }
	
	
	public static function getAllSamplesOfGesture($connection, $id_function, $username_user) {

        $response = new Response(0, "iniciando get", 0, [], null);


        try {

            $sql = "SELECT id, s.data_accelerometer_x, s.data_accelerometer_y, s.data_accelerometer_z, s.data_gyroscope_x, s.data_gyroscope_y, s.data_gyroscope_z FROM sample as s ";
			$sql .= "WHERE id_function=:id_function AND username_user=:username_user ORDER BY id ASC ";

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
                return $response;
            }


            $success = $stmt->execute(array(
                ":id_function" => $id_function,
                ":username_user" => $username_user,
                ));

            if($success){

                $response->id_message = 1;
                $response->server_message = "Se ha consultado correctamente";
				
				// $data = [];
				$output_id = "[";
				
				$output_data_accelerometer_x = "[";
				$output_data_accelerometer_y = "[";
				$output_data_accelerometer_z = "[";
				
				$output_data_gyroscope_x = "[";
				$output_data_gyroscope_y = "[";
				$output_data_gyroscope_z = "[";
				
				$counter = 0;
				$quantity = $stmt->rowCount();
				
				while($row = $stmt->fetch(PDO::FETCH_ASSOC)){
					$output_id .= $row["id"];
					
					$output_data_accelerometer_x .= $row["data_accelerometer_x"];
					$output_data_accelerometer_y .= $row["data_accelerometer_y"];
					$output_data_accelerometer_z .= $row["data_accelerometer_z"];
					
					$output_data_gyroscope_x .= $row["data_gyroscope_x"];
					$output_data_gyroscope_y .= $row["data_gyroscope_y"];
					$output_data_gyroscope_z .= $row["data_gyroscope_z"];
                    // array_push($data, $row);
					$counter = $counter + 1;
					if($counter < $quantity){
						$output_id .= "; ";
						
						$output_data_accelerometer_x .= "; ";
						$output_data_accelerometer_y .= "; ";
						$output_data_accelerometer_z .= "; ";
						
						$output_data_gyroscope_x .= "; ";
						$output_data_gyroscope_y .= "; ";
						$output_data_gyroscope_z .= "; ";
					}else{
						$output_id .= "];\n";
						
						$output_data_accelerometer_x .= "];\n";
						$output_data_accelerometer_y .= "];\n";
						$output_data_accelerometer_z .= "];\n";
						
						$output_data_gyroscope_x .= "];\n";
						$output_data_gyroscope_y .= "];\n";
						$output_data_gyroscope_z .= "];";
					}
						
                }
				
				
				// $response->data = $data;
				$response->metadata = $output_id.$output_data_accelerometer_x.$output_data_accelerometer_y.$output_data_accelerometer_z.$output_data_gyroscope_x.$output_data_gyroscope_y.$output_data_gyroscope_z;


            }else{
                $response->id_message = 4;  // 4 es error de servidor
                $response->server_message  = "No se pudo crear";
                $response->error = "Error al ejecutar el sql: $sql\n ";
            }


        } catch (Exception $e) {
            $response->id_message = 4;  // 4 es error de servidor
            $response->server_message  = "Error del servidor";
            $response->error = $e->getMessage();
        }

        /*$this->conn->beginTransaction();
        if($respuesta['error']  == null){
            $this->conn->commit();
        }else{
            $this->conn->rollBack();
        }*/

        return $response;

    }

    public static function getAllSamplesForGesture($connection, $id_function, $username_user) {

        $response = new Response(0, "iniciando get para gesture", 0, [], null);


        try {

            $sql = "SELECT * FROM sample ";
			$sql .= "WHERE id_function=:id_function AND username_user=:username_user ORDER BY id ASC ";

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
                return $response;
            }


            $success = $stmt->execute(array(
                ":id_function" => $id_function,
                ":username_user" => $username_user,
                ));

            if($success){

                $response->id_message = 1;
                $response->server_message = "Se ha consultado correctamente";
				
				$data = [];
				$quantity = $stmt->rowCount();
				
				while($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                    $row["is_augmented_data"] = ($row["is_augmented_data"] == "1")? true: false;
                    array_push($data, $row);
                }
                
                $response->data = $data;
				$response->metadata = "$quantity";


            }else{
                $response->id_message = 4;  // 4 es error de servidor
                $response->server_message  = "No se pudo crear";
                $response->error = "Error al ejecutar el sql: $sql\n ";
            }


        } catch (Exception $e) {
            $response->id_message = 4;  // 4 es error de servidor
            $response->server_message  = "Error del servidor";
            $response->error = $e->getMessage();
        }

        return $response;

    }

    public static function search($connection, $search_method, $search_value) {

        $response = new Response(0, "iniciando get search", 0, [], null);
        
        
        try {
            $sql = "SELECT * FROM sample WHERE ";

            
            if($search_method == 0){
                $sql .= "id ";
            }
            else if($search_method == 1){
                $sql .= "datetime ";
            }

            $sql .= "LIKE '%$search_value%'";

			

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
                return $response;
            }


            $success = $stmt->execute();

            if($success){

                $response->id_message = 1;
                $response->server_message = "Se ha consultado correctamente";
				
				$data = [];
				
				$quantity = $stmt->rowCount();
				
				while($row = $stmt->fetch(PDO::FETCH_ASSOC)){
					array_push($data, $row);
                }
				
				
				$response->data = $data;
				$response->metadata = "".$quantity;
				


            }else{
                $response->id_message = 4;  // 4 es error de servidor
                $response->server_message  = "No se pudo crear";
                $response->error = "Error al ejecutar el sql: $sql\n ";
            }


        } catch (Exception $e) {
            $response->id_message = 4;  // 4 es error de servidor
            $response->server_message  = "Error del servidor";
            $response->error = $e->getMessage();
        }

        return $response;

    }

    public function update($connection){

        $response = new Response(0, "iniciando actualización de sample", 0, [], null);

        try {
			
            $sql = "UPDATE sample SET is_augmented_data=:is_augmented_data, ";
            $sql .= "data_accelerometer_x=:data_accelerometer_x, data_accelerometer_y=:data_accelerometer_y, ";
            $sql .= "data_accelerometer_z=:data_accelerometer_z, data_gyroscope_x=:data_gyroscope_x, ";
            $sql .= "data_gyroscope_y=:data_gyroscope_y, data_gyroscope_z=:data_gyroscope_z ";
            $sql .= "WHERE id=:id";
             

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
                return $response;
            }


            $success = $stmt->execute(array(
                ":is_augmented_data" => $this->is_augmented_data,
                ":data_accelerometer_x" => $this->data_accelerometer_x,
                ":data_accelerometer_y" => $this->data_accelerometer_y,
                ":data_accelerometer_z" => $this->data_accelerometer_z,
                ":data_gyroscope_x" => $this->data_gyroscope_x,
                ":data_gyroscope_y" => $this->data_gyroscope_y,
                ":data_gyroscope_z" => $this->data_gyroscope_z,
                ":id" => $this->id
                ));

            if($success){

                $response->id_message = 1;
                $response->server_message = "Se ha actualizado correctamente";	

            }else{
                $response->id_message = 4;  // 4 es error de servidor
                $response->server_message  = "No se pudo actualizar";
                $response->error = "Error al ejecutar el sql: $sql\n ";
            }


        } catch (Exception $e) {
            $response->id_message = 4;  // 4 es error de servidor
            $response->server_message  = "Error del servidor";
            $response->error = $e->getMessage();
        }

        return $response;
    }

    public static function delete($connection, $id){

        $response = new Response(0, "iniciando eliminación de sample", 0, [], null);

        try {
			
            $sql = "DELETE FROM sample WHERE id=:id";

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
                return $response;
            }


            $success = $stmt->execute(array(
                ":id" => $id
                ));

            if($success){

                $response->id_message = 1;
                $response->server_message = "Se ha eliminado correctamente";	

            }else{
                $response->id_message = 4;  // 4 es error de servidor
                $response->server_message  = "No se pudo eliminar";
                $response->error = "Error al ejecutar el sql: $sql\n ";
            }


        } catch (Exception $e) {
            $response->id_message = 4;  // 4 es error de servidor
            $response->server_message  = "Error del servidor";
            $response->error = $e->getMessage();
        }

        return $response;
    }


}
