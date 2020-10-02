<?php

require_once 'Response.php';

class Gesture {



    private $id_function;
    private $username_user;
    private $name;



    public function __construct($id_function, $username_user, $name) {

       $this->id_function = $id_function;
       $this->username_user = $username_user;
       $this->name = $name;

    }

    public function create($connection) {

        $response = new Response(0, "iniciando creación de gesto", 0, [], null);


        try {

            $sql = "INSERT INTO gesture(id_function, username_user, name) ";
            $sql .= "VALUES(:id_function, :username_user, :name)";

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
                ":name" => $this->name
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

    public static function getAllBySkipStep($connection, $skip, $step) {

        $response = new Response(0, "iniciando get de gestures skip-step", 0, [], null);


        try {

			$sql = "SELECT * FROM gesture limit $skip, $step";
			

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
                $response->server_message  = "No se pudo obtener los datos";
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

        $response = new Response(0, "iniciando actualización de gesto", 0, [], null);

        try {
			
            $sql = "UPDATE gesture SET name=:name WHERE id_function=:id_function AND username_user=:username_user";

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
                return $response;
            }


            $success = $stmt->execute(array(
                ":name" => $this->name,
                ":id_function" => $this->id_function,
                ":username_user" => $this->username_user
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

    public function delete($connection){

        $response = new Response(0, "iniciando eliminación de gesto", 0, [], null);

        try {
			
            $sql = "DELETE FROM gesture WHERE id_function=:id_function AND username_user=:username_user";

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

    public static function search($connection, $search_method, $search_value) {

        $response = new Response(0, "iniciando get search", 0, [], null);


        try {
            $sql = "SELECT * FROM gesture WHERE ";

            
            if($search_method == 0){
                $sql .= "id_function ";
            }
            else if($search_method == 1){
                $sql .= "username_user ";
            }
            else if($search_method == 2){
                $sql .= "name ";
            }
            $sql .= "LIKE '%$search_value%' limit 10";

			

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




	
	
	public function createWithSamples($connection, $samples, $with_transaction) {

        $response = new Response(0, "iniciando creación de gesto", 0, [], null);


        try {
			
			if($with_transaction){
				$connection->beginTransaction();
			}

            $sql = "INSERT INTO gesture(id_function, username_user, name) ";
            $sql .= "VALUES(:id_function, :username_user, :name)";

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
				
				if($with_transaction){
					$connection->rollBack();
				}
				
                return $response;
            }


            $success = $stmt->execute(array(
                ":id_function" => $this->id_function,
                ":username_user" => $this->username_user,
                ":name" => $this->name
                ));
				

            if($success){

                $response->id_message = 1;
                $response->server_message = "Se ha creado correctamente el gesto sin samples";
                $response->metadata = $this->id_function."#".$this->username_user;
				
				$success_count = 0;
				
				for($i=0; $i<count($samples); $i++){
					
					$sample = $samples[$i];
					
					
					$sql_sample = "INSERT INTO sample(id_function, username_user, data_accelerometer_x, data_accelerometer_y, data_accelerometer_z, data_gyroscope_x, data_gyroscope_y, data_gyroscope_z) ";
					$sql_sample .= "VALUES(:id_function, :username_user, :data_accelerometer_x, :data_accelerometer_y, :data_accelerometer_z, :data_gyroscope_x, :data_gyroscope_y, :data_gyroscope_z)";
					
					$stmt2 = $connection->prepare($sql_sample);

					if(!$stmt2){
						$response->id_message = 4;
						$response->server_message = "Ha ocurrido un error";
						$response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
						
						if($with_transaction){
							$connection->rollBack();
						}
						return $response;
					}


					$success = $stmt2->execute(array(
						":id_function" => $this->id_function,
						":username_user" => $this->username_user,
						":data_accelerometer_x" => $sample["data_accelerometer_x"],
						":data_accelerometer_y" => $sample["data_accelerometer_y"],
						":data_accelerometer_z" => $sample["data_accelerometer_z"],
						":data_gyroscope_x" => $sample["data_gyroscope_x"],
						":data_gyroscope_y" => $sample["data_gyroscope_y"],
						":data_gyroscope_z" => $sample["data_gyroscope_z"]
						));

					if($success){

						$success_count += 1;
						$response->id_message = 1;
						$response->server_message  = "Se han creado $success_count samples";
						$response->metadata = "".$success_count;


					}else{
						$response->id_message = 4;  // 4 es error de servidor
						$response->server_message  = "No se pudo crear SAMPLE";
						$response->error = "Error al ejecutar el sql: $sql_sample\n ";
						break;
					}
					
				}
				


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


		if($with_transaction){
			if($response->error  == null){
				$connection->commit();
			}else{
				$connection->rollBack();
			}
		}
        

        return $response;

    }
	
	public function updateWithSamples($connection, $samples, $new_id_function) {

        $response = new Response(0, "iniciando creación de gesto", 0, [], null);


        try {
			
			$connection->beginTransaction();
			$response_delete = $this->deleteIt($connection, false);
			$this->id_function = $new_id_function;
			$response = $this->createWithSamples($connection, $samples, false);
			

        } catch (Exception $e) {
            $response->id_message = 4;  // 4 es error de servidor
            $response->server_message  = "Error del servidor";
            $response->error = $e->getMessage();
        }

        if($response->error  == null){
            $connection->commit();
        }else{
            $connection->rollBack();
        }

        return $response;

    }
	
	
	
	public function deleteIt($connection, $with_transaction) {

        $response = new Response(0, "iniciando eliminación de gesto", 0, [], null);

		
		
        try {
			
			if($with_transaction){
				$connection->beginTransaction();
			}

            $sql = "DELETE FROM gesture WHERE id_function=:id_function AND username_user=:username_user";

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
				if($with_transaction){
					$connection->rollBack();
				}
                return $response;
            }


            $success = $stmt->execute(array(
                ":id_function" => $this->id_function,
                ":username_user" => $this->username_user
                ));

            if($success){

                $response->id_message = 1;
                $response->server_message = "Se ha eliminado los samples correctamente";
				
				


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


		if($with_transaction){
			if($response->error == null){
				$connection->commit();
			}else{
				$connection->rollBack();
			}
		}
        
        

        return $response;

    }
	
	public static function getAllMyGestures($connection, $username_user, $option) {

        $response = new Response(0, "iniciando get de functions", 0, [], null);


        try {

            $sql = "SELECT g.id_function, g.name FROM gesture as g, function as f WHERE g.id_function=f.id AND g.username_user=:username_user ";
			
			if($option == 1){
				$sql .= "AND f.function_type_id=1 ";
			}
			else if($option == 2){
				$sql .= "AND f.function_type_id=2 ";
			}
			else if($option == 3){
				$sql .= "AND f.function_type_id=3 ";
			}
			$sql .= "ORDER BY name ASC";
			
            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
                return $response;
            }


            $success = $stmt->execute(array(
                ":username_user" => $username_user
                ));

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
                $response->server_message  = "No se pudo obtener";
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
	
	
	
	
	
	
	


}
