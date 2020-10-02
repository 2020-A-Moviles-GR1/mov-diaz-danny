<?php

$id_function = $json_input['id_function']?? null;
$username_user = $json_input['username_user']?? null;
$name = $json_input['name']?? null;

if(isNotNullData([$name, $id_function, $username_user])){
    $gesture = new Gesture($id_function, $username_user, $name);
    $response = $gesture->update($connection);  
}else{
    exit();
}