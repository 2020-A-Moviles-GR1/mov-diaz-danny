<?php

$username = $json_input['username']?? null;
$password = $json_input['password']?? null;

if(isNotNullData([$username, $password])){
    $response = User::auth($connection, $username, $password);
}else{
    exit();
}