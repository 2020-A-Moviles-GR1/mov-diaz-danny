<?php

$username = $json_input['username']?? null;
$password = $json_input['password']?? null;
$full_name = $json_input['full_name']?? null;

if(isNotNullData([$username, $password, $full_name])){
    $user = new User($username, $password, $full_name);
    $response = $user->create($connection);  
}else{
    exit();
}