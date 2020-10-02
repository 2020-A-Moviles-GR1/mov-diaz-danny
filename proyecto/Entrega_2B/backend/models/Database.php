<?php

class Database{

    private $host = "localhost";
    private $username = "root";
    private $password = "";


    public $conn;
    public $error;

    public function getConnection($db_name){

        $this->conn = null;
        $this->db_name = $db_name;

        try{
            $this->conn = new PDO("mysql:host=" . $this->host . ";dbname=" . $this->db_name.";charset=utf8", $this->username, $this->password);
        }catch(PDOException $exception){
            $this->error = "Connection error: " . $exception->getMessage();
        }

        return $this->conn;
    }

     /**
     * close the database connection
     */
    public function __destruct() {
        // close the database connection
        $this->conn = null;
    }
}
?>
