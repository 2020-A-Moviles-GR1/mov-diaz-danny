<?php

$id_function = $json_input['id_function']?? null;
$username_user = $json_input['username_user']?? null;

if(isNotNullData([$id_function, $username_user])){
    $response = Sample::getAllSamplesForGesture($connection, $id_function, $username_user);  
}else{
    exit();
}