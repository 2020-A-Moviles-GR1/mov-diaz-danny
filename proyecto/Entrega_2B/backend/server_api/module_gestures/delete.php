<?php

$id_function = $json_input['id_function']?? null;
$username_user = $json_input['username_user']?? null;

if(isNotNullData([$id_function, $username_user])){
    $gesture = new Gesture($id_function, $username_user, "");
    $response = $gesture->delete($connection);  
}else{
    exit();
}