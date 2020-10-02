<?php

require_once 'Response.php';

class User {

    private $username;
    private $password;
    private $full_name;



    public function __construct($username, $password, $full_name) {

       $this->username = $username;
       $this->password = $password;
       $this->full_name = $full_name;

    }

    public function create($connection) {

        $response = new Response(0, "iniciando creación de usuario", 0, [], null);


        try {

            $sql = "INSERT INTO user(username, password, full_name) ";
            $sql .= "VALUES(:username, :password, :full_name)";

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
                return $response;
            }


            $success = $stmt->execute(array(
                ":username" => $this->username,
                ":password" => $this->password,
                ":full_name" => $this->full_name
                ));

            if($success){

                $response->id_message = 1;
                $response->server_message = "Se ha creado el usuario correctamente";
                $response->metadata = "".$connection->lastInsertId();
				
				
				$data = [];
				
				array_push($data, ["username" => $this->username, "password" => $this->password, "full_name" => $this->full_name]);
				
				$response->data = $data;


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

    public static function getAllBySkipStep($connection, $skip, $step) {

        $response = new Response(0, "iniciando get de usuarios con skip-step", 0, [], null);


        try {

			$sql = "SELECT * FROM user limit $skip, $step";
			

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

    public static function auth($connection, $username, $password) {

        $response = new Response(0, "iniciando auth de usuario", 0, [], null);


        try {

			$sql = "SELECT * FROM user WHERE username=:username AND password=:password";
			

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
                return $response;
            }


            $success = $stmt->execute(array(
                ":username" => $username,
                ":password" => $password,
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

    public static function search($connection, $search_method, $search_value) {

        $response = new Response(0, "iniciando get search", 0, [], null);


        try {
            $sql = "SELECT * FROM user WHERE ";
            
            if($search_method == 0){
                $sql .= "username ";
            }
            else if($search_method == 1){
                $sql .= "full_name ";
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

        $response = new Response(0, "iniciando actualización de usuario", 0, [], null);

        try {
			
            $sql = "UPDATE user SET password=:password, full_name=:full_name WHERE username=:username";

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
                return $response;
            }


            $success = $stmt->execute(array(
                ":password" => $this->password,
                ":full_name" => $this->full_name,
                ":username" => $this->username
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

        $response = new Response(0, "iniciando eliminación de usuario", 0, [], null);

        try {
			
            $sql = "DELETE FROM user WHERE username=:username";

            $stmt = $connection->prepare($sql);

            if(!$stmt){
                $response->id_message = 4;
                $response->server_message = "Ha ocurrido un error";
                $response->error  =  "Ocurrió un error al preparar la consulta sql: $sql. ";
                return $response;
            }


            $success = $stmt->execute(array(
                ":username" => $this->username
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
