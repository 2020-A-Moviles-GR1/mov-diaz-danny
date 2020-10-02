<?php

$username = $json_input['username']?? null;

if(isNotNullData([$username])){
    $user = new User($username, "", "");
    $response = $user->delete($connection);
}else{
    exit();
}